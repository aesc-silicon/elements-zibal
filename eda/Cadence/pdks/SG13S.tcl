proc setup_library {} {
	set_db library "stdcell/lib/ixc013_stdcell_typ_1p20V_25C.lib iocell/lib/ixc013_iocell_typ_1p2V_3p3V_25C.lib"

	set_db lef_library [list \
		stdcell/lef/ixc013_tech.lef \
		stdcell/lef/ixc013_stdcell_v5p7.lef \
		iocell/lef/ixc013_iocell_v5p8.lef \
	]

	set_db cap_table_file stdcell/lef/captable/SG13.captable
}

proc get_lef_files {} {
	global PATH_PDK

	set lef_files ${PATH_PDK}/stdcell/lef/ixc013_tech.lef
	lappend lef_files ${PATH_PDK}/stdcell/lef/ixc013_stdcell_v5p7.lef
	lappend lef_files ${PATH_PDK}/iocell/lef/ixc013_iocell_v5p8.lef
	lappend lef_files ${PATH_PDK}/iocell/lef/ixc013_iolvds.lef

	return $lef_files
}

proc get_verilog_files {} {
	global PATH_PDK

	set verilog_files ${PATH_PDK}/stdcell/verilog/ixc013_primitives.v
	lappend verilog_files ${PATH_PDK}/stdcell/verilog/ixc013_stdcell.v
	lappend verilog_files ${PATH_PDK}/iocell/verilog/ixc013_iocell.v

	return $verilog_files
}

proc get_power_nets {} {
	return {VDDCORE VDDPAD}
}

proc get_ground_nets {} {
	return {VSSCORE VSSPAD}
}

proc get_max_route_layer {} {
	return 7;
}

proc get_suppress_messages {} {
	return {IMPLF-200 IMPVL-159 IMPFP-3961 TCLCMD-1403 IMPCK-8086 IMPTS-282 IMPDC-1629 IMPFP-325 IMPSR-1253 IMPSR-1254 IMPSP-9531 IMPOPT-6118 IMPSR-4302 IMPREPO-231}
}

proc get_global_core_nets {} {
	return {
		{VDDCORE pgpin VDD! *}
		{VSSCORE pgpin VSS! *}
	}
}

proc get_global_per_nets {} {
	return {
		{VDDPAD pgpin VDDPAD *}
		{VSSPAD pgpin VSSPAD *}
		{VDDCORE pgpin VDDCORE *}
		{VSSCORE pgpin VSSCORE *}
	}
}

proc get_global_tie_nets {} {
	return {
		{VDDCORE tiehi}
		{VSSCORE tielo}
	}
}

proc get_buffer_cells {} {
	return {BUFJILTX1 BUJILTX1 BUFJILTX2 BUJILTX2 BUFJILTX4 BUJILTX4 BUFJILTX8 BUJILTX8 BUFJILTX12 BUJILTX12 BUFJILTX16 BUJILTX16 BUFJILTX20 BUJILTX20}
}

proc get_inverter_cells {} {
	return {INJILTX1 INVJILTX1 INJILTX2 INVJILTX2 INJILTX4 INVJILTX4 INJILTX8 INVJILTX8 INJILTX12 INVJILTX12}
}

proc get_clock_gating_cells {} {
	return {LGCPJILTX1}
}

proc get_filler_ios {} {
	return {filler10u filler4u filler2u filler1u}
}

proc get_filler_caps {} {
	return {DECAP25JILT DECAP10JILT DECAP5JILT DECAP3JILT DECAP2JILT}
}

proc get_filler_cells {} {
	return {FEED25JILT FEED10JILT FEED5JILT FEED3JILT FEED2JILT}
}
