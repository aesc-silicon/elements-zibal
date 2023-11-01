#!/bin/bash

ZIBAL_BUILD=${BUILD_ROOT}/${SOC}/${BOARD}/zibal/
FPGA_BUILD=${BUILD_ROOT}/${SOC}/${BOARD}/fpga/
TOP=${BOARD}Top
DB_PATH=${NEXTPNR_XILINX_ROOT}/nextpnr-xilinx/xilinx/external/prjxray-db/${FPGA_FAMILY}

cd ${ZIBAL_BUILD}

yosys -l ${FPGA_BUILD}/${TOP}.yosys.log -p "read_verilog ${ZIBAL_BUILD}/${TOP}.v; synth_${FPGA_VENDOR} -flatten -nowidelut -abc9 -arch ${FPGA_ARCH} -top ${TOP}; write_json ${FPGA_BUILD}/${TOP}.json"

nextpnr-${FPGA_VENDOR} --chipdb ${NEXTPNR_XILINX_ROOT}/db/${FPGA_DEVICE}.bin --xdc ${ZIBAL_BUILD}/${TOP}.xdc --json ${FPGA_BUILD}/${TOP}.json --write ${FPGA_BUILD}/${TOP}.routed.json --fasm ${FPGA_BUILD}/${TOP}.fasm --freq ${FPGA_FREQUENCY} --log ${FPGA_BUILD}/${TOP}.nextpnr.log

source ${NEXTPNR_XILINX_ROOT}/prjxray/utils/environment.sh

${NEXTPNR_XILINX_ROOT}/prjxray/utils/fasm2frames.py --db-root ${DB_PATH} --part ${PART} ${FPGA_BUILD}/${TOP}.fasm > ${FPGA_BUILD}/${TOP}.frames
xc7frames2bit --part-file ${DB_PATH}/${PART}/part.yaml --part_name ${PART} --frm_file ${FPGA_BUILD}/${TOP}.frames --output_file ${FPGA_BUILD}/${TOP}.bit
