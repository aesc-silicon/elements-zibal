#!/bin/bash

# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0

xmvlog -WORK ${SOC} -CDSLIB ${ELEMENTS_BASE}/cds.lib -LOGFILE xmvlog.log -status \
	${PDK_BASE}/stdcell/verilog/ixc013_primitives.v
xmvlog -WORK ${SOC} -CDSLIB ${ELEMENTS_BASE}/cds.lib -LOGFILE xmvlog.log -status \
	${PDK_BASE}/stdcell/verilog/ixc013_stdcell.v
xmvlog -WORK ${SOC} -CDSLIB ${ELEMENTS_BASE}/cds.lib -LOGFILE xmvlog.log -status \
	${PDK_BASE}/iocell/verilog/ixc013_iocell.v
