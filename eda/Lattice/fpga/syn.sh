#!/bin/bash

# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0

ZIBAL_BUILD=${BUILD_ROOT}/${SOC}/${BOARD}/zibal/
FPGA_BUILD=${BUILD_ROOT}/${SOC}/${BOARD}/fpga/
TOP=${BOARD}Top

cd ${ZIBAL_BUILD}

mkdir -p ${FPGA_BUILD}

ADDITIONAL_VERILOG=""
if [ -f ${ZIBAL_BUILD}/${TOP}Blackboxes.v ]; then
	ADDITIONAL_VERILOG="read_verilog ${ZIBAL_BUILD}/${TOP}Blackboxes.v;"
fi

yosys -p "${ADDITIONAL_VERILOG} read_verilog ${ZIBAL_BUILD}/${TOP}.v; synth_${FPGA_FAMILY} -top ${TOP} -json ${FPGA_BUILD}/${TOP}.json"
nextpnr-${FPGA_FAMILY} -v --${FPGA_DEVICE} --package ${FPGA_PACKAGE} --json ${FPGA_BUILD}/${TOP}.json --lpf ${ZIBAL_BUILD}/${TOP}.lpf --textcfg ${FPGA_BUILD}/${TOP}.config --freq ${FPGA_FREQUENCY}
ecppack ${FPGA_BUILD}/${TOP}.config ${FPGA_BUILD}/${TOP}.bit
