# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0

proc setup_library {} {
	set_db library "ng-stdcell/lib/ixc013g2ng_stdcell_typ_1p20V_25C.lib iocell/lib/ixc013g2_iocell_typ_1p2V_3p3V_25C.lib"

	set_db lef_library [list \
		ng-stdcell/lef/ixc013g2ng_tech.lef \
		ng-stdcell/lef/ixc013g2ng_stdcell_v5p7.lef \
		ng-stdcell/lef/ixc013g2ng_phys.lef \
		iocell/lef/ixc013g2_iocell_v5p8.lef \
	]

	set_db qrc_tech_file tech/Assura_SG13/qrc/qrcTechFile
}

proc setup_power {} {
	set_db lp_insert_clock_gating false
	set_db lp_clock_gating_prefix "lp_clk"
}

proc get_lef_files {} {
	global PATH_PDK

	set lef_files ${PATH_PDK}/ng-stdcell/lef/ixc013g2ng_tech.lef
	lappend lef_files ${PATH_PDK}/ng-stdcell/lef/ixc013g2ng_stdcell_v5p7.lef
	lappend lef_files ${PATH_PDK}/ng-stdcell/lef/ixc013g2ng_phys.lef
	lappend lef_files ${PATH_PDK}/iocell/lef/ixc013g2_iocell_v5p8.lef

	return $lef_files
}

proc get_verilog_files {} {
	global PATH_PDK

	set verilog_files ${PATH_PDK}/ng-stdcell/verilog/ixc013g2ng_primitives.v
	lappend verilog_files ${PATH_PDK}/ng-stdcell/verilog/ixc013g2ng_stdcell.v
	lappend verilog_files ${PATH_PDK}/iocell/verilog/ixc013g2_primitives.v
	lappend verilog_files ${PATH_PDK}/iocell/verilog/ixc013g2_iocell.v

	return $verilog_files
}

proc get_lib_min_files {} {
	global PATH_PDK

	set timing_lib ${PATH_PDK}/ng-stdcell/lib/ixc013g2ng_stdcell_fast_1p32V_m40C.lib
	lappend timing_lib ${PATH_PDK}/iocell/lib/ixc013g2_iocell_fast_1p32V_3p6V_m40C.lib

	return $timing_lib
}

proc get_lib_max_files {} {
	global PATH_PDK

	set timing_lib ${PATH_PDK}/ng-stdcell/lib/ixc013g2ng_stdcell_slow_1p08V_125C.lib
	lappend timing_lib ${PATH_PDK}/iocell/lib/ixc013g2_iocell_slow_1p08V_3p0V_125C.lib

	return $timing_lib
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

proc get_suppress_messages { tool } {

	switch $tool {
	"genus" {
		return {LBR-9 VLOGPT-506 VLOGPT-502}
	}
	"innovus" {
		return {IMPLF-200 IMPVL-159 IMPFP-3961 TCLCMD-1403 IMPCK-8086 IMPTS-282 IMPDC-1629 IMPFP-325 IMPSR-1253 IMPSR-1254 IMPSP-9531 IMPOPT-6118 IMPSR-4302 IMPREPO-231}
	}
	}

	return {}
}

proc get_global_core_nets {} {
	return {
		{VDDCORE pgpin VDD *}
		{VSSCORE pgpin VSS *}
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
	return {BUFJIX1 BUJIX1 BUFJIX2 BUJIX2 BUFJIX4 BUJIX4 BUFJIX8 BUJIX8 BUFJIX12 BUJIX12 BUFJIX16 BUJIX16 BUFJIX20 BUJIX20}
}

proc get_inverter_cells {} {
	return {INJIX0 INVJIX0 INJIX1 INVJIX1 INJIX2 INVJIX2 INJIX4 INVJIX4 INJIX8 INVJIX8 INJIX12 INVJIX12 INJIX16 INVJIX16 INJIX20 INVJIX20}
}

proc get_clock_gating_cells {} {
	# LGCPJIX1 -> **WARN: (TECHLIB-302):	No function defined for cell 'LGCPJIX1'.
	return {LGCNJIX1 LSGCNJIX1 LSGCPJIX1 LSOGCNJIX1 LSOGCPJIX1}
}

proc get_filler_ios {} {
	return {filler10u filler4u filler2u filler1u}
}

proc get_filler_caps {} {
	return {DECAP25JI DECAP10JI DECAP5JI DECAP3JI}
}

proc get_filler_cells {} {
	return {FEED25JI FEED10JI FEED5JI FEED3JI FEED2JI FEED1JI}
}
