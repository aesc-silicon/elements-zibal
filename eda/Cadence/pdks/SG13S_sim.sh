#!/bin/bash

xmvlog -WORK ${SOC} -CDSLIB ${ELEMENTS_BASE}/cds.lib -LOGFILE xmvlog.log -status \
	${PDK_BASE}/ng-stdcell/verilog/ixc013ng_primitives.v
xmvlog -WORK ${SOC} -CDSLIB ${ELEMENTS_BASE}/cds.lib -LOGFILE xmvlog.log -status \
	${PDK_BASE}/ng-stdcell/verilog/ixc013ng_stdcell.v
xmvlog -WORK ${SOC} -CDSLIB ${ELEMENTS_BASE}/cds.lib -LOGFILE xmvlog.log -status \
	${PDK_BASE}/iocell/verilog/ixc013_iocell.v
