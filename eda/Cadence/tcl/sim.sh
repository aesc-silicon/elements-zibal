#!/bin/bash

# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0

${TCL_PATH}/../pdks/${PDK}_sim.sh

if [ "${SIM_TYPE}" == "generated" ]; then
	xmvlog -WORK ${SOC} -CDSLIB ${ELEMENTS_BASE}/cds.lib -LOGFILE xmvlog.log -status \
		${ELEMENTS_BASE}/build/${BOARD}/zibal/${SOC}.v
	xmvlog -WORK ${SOC} -CDSLIB ${ELEMENTS_BASE}/cds.lib -LOGFILE xmvlog.log -status \
		${ELEMENTS_BASE}/zibal/eda/Cadence/constraints/${SOC}/${SOC}_top.v
else
	xmvlog -WORK ${SOC} -CDSLIB ${ELEMENTS_BASE}/cds.lib -LOGFILE xmvlog.log -status \
		${ELEMENTS_BASE}/build/${BOARD}/cadence/place/${TOP_NAME}.v
fi

xmvlog -WORK ${SOC} -CDSLIB ${ELEMENTS_BASE}/cds.lib -LOGFILE xmvlog.log -status \
	-incdir ${ELEMENTS_BASE}/zibal/eda/testbenches/ \
	-sv ${ELEMENTS_BASE}/zibal/eda/testbenches/${TESTBENCH}.sv

xmelab -WORK ${SOC} -CDSLIB ${ELEMENTS_BASE}/cds.lib -timescale 1ns/10ps -acces +wc -status \
	${SOC}.${TESTBENCH_NAME}:module

xmsim -CDSLIB ${ELEMENTS_BASE}/cds.lib -status -gui ${SOC}.${TESTBENCH_NAME}:module
