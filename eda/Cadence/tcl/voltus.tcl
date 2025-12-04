# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0

set TOOL "voltus"

source common/board.tcl
source common/pathes.tcl
source pdks/${PDK}.tcl

foreach {suppress_message} [get_suppress_messages "innovus"] {
	suppressMessage "$suppress_message"
}

start_gui

read_design -physical_data ${PATH_BUILD_ROOT}/place/latest/${TOP}.dat ${TOP}

#read_lib -lef [get_lef_files]
#read_lib -min [get_lib_min_files]
#read_lib -max [get_lib_max_files]
#read_verilog ${PATH_BUILD_ROOT}/place/latest/${TOP}_final_fillcap.v
#set_top_module ${TOP}
#read_sdc ${PATH_RTL}/${TOP}.sdc
#read_def ${PATH_BUILD_ROOT}/place/latest/${TOP}_final.def
#read_spef ${PATH_BUILD_ROOT}/place/latest/${TOP}_final.spef

fit
