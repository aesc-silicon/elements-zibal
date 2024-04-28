#!/bin/bash

ZIBAL_BUILD=${BUILD_ROOT}/${SOC}/${BOARD}/zibal/
FPGA_BUILD=${BUILD_ROOT}/${SOC}/${BOARD}/fpga/
TOP=${BOARD}Top

cd ${ZIBAL_BUILD}

mkdir -p ${FPGA_BUILD}

yosys -p "read_verilog ${ZIBAL_BUILD}/${TOP}.v; synth_${FPGA_FAMILY} -top ${TOP} -json ${FPGA_BUILD}/${TOP}.json"
nextpnr-${FPGA_FAMILY} -v --${FPGA_DEVICE} --package ${FPGA_PACKAGE} --json ${FPGA_BUILD}/${TOP}.json --lpf ${ZIBAL_BUILD}/${TOP}.lpf --textcfg ${FPGA_BUILD}/${TOP}.config --freq ${FPGA_FREQUENCY}
ecppack ${FPGA_BUILD}/${TOP}.config ${FPGA_BUILD}/${TOP}.bit
