source $::env(TCL_PATH)/../../common/board.tcl
source $::env(TCL_PATH)/../../common/general.tcl
source $::env(TCL_PATH)/sources.tcl

set wcfg_file "${BOARD}.wcfg"

exec xvlog -sv -work work $::env(TCL_PATH)/../../../testbenches/${BOARD}_tb.sv \
     -log ./logs/${BOARD}_tb_xvhdl.log

exec xelab -debug typical --lib unisims --lib unisims_ver -s ${TOP}_func \
     -log ./logs/${BOARD}_tb_xelab.log --timescale 1ns/1ns ${BOARD}_tb glbl

if {[file exists ${wcfg_file}]} {
	exec xsim ${TOP}_func -gui -wdb simulate_xsim.wdb -view ${wcfg_file} \
	     -log ./logs/${TOP}_xsim.log
} else {
	exec xsim ${TOP}_func -gui -wdb simulate_xsim.wdb \
	     -log ./logs/${TOP}_xsim.log
}
exit
