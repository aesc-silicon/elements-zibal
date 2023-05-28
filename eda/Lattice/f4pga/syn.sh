#!/bin/bash

ZIBAL_BUILD=${BUILD_ROOT}/${SOC}/${BOARD}/zibal/
F4PGA_BUILD=${BUILD_ROOT}/${SOC}/${BOARD}/f4pga/
TOP=${BOARD}Top

cd ${ZIBAL_BUILD}

yosys -p "read_verilog ${ZIBAL_BUILD}/${TOP}.v; synth_${FPGA_FAM} -top ${TOP} -json ${F4PGA_BUILD}/${TOP}.json"
nextpnr-${FPGA_FAM} -v --${FPGA_DEVICE} --package ${FPGA_PACKAGE} --json ${F4PGA_BUILD}/${TOP}.json --lpf ${ZIBAL_BUILD}/${TOP}.lpf --textcfg ${F4PGA_BUILD}/${TOP}.config
ecppack ${F4PGA_BUILD}/${TOP}.config ${F4PGA_BUILD}/${TOP}.bit
