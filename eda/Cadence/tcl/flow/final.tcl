# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0

proc elements_verify {} {
	global TOP
	global PATH_REPORT

	clearDrc

	verify_drc -report ${PATH_REPORT}/${TOP}.drc.rpt
	verifyConnectivity -report ${PATH_REPORT}/${TOP}.con.rpt

	verifyPowerVia -report ${PATH_REPORT}/${TOP}.power.rpt
}

proc elements_verify_endcap {} {
	global TOP
	global PATH_REPORT

	verifyEndCap -report ${PATH_REPORT}/${TOP}.endcap.rpt
}

proc elements_save postfix {
	global TOP
	global PATH_OUTPUT
	set pathes $PATH_OUTPUT
	global PATH_LATEST
	lappend pathes $PATH_LATEST

	foreach path $pathes {
		saveDesign ${path}/${TOP}
	}
}

proc elements_write postfix {
	global TOP
	global PATH_OUTPUT
	global PATH_LATEST
	global PATH_PDK
	set pathes $PATH_OUTPUT
	lappend pathes $PATH_LATEST

	foreach path $pathes {
		##-- Write DEF
		set filename [format "%s/%s_%s.def" $path $TOP $postfix]
		defOut -floorplan -netlist -routing $filename

		##-- Create snapshots
		set snapshot [format "%s_%s.def" $TOP $postfix]
		createSnapshot -dir ${path} -name ${snapshot} -overwrite

		##-- Write GDS2
		set filename [format "%s/%s_%s_fe.gds" $path $TOP $postfix]
		streamOut $filename -mapFile ${PATH_PDK}/streamOut.map -libName $TOP \
			-structureName $TOP -attachInstanceName 13 -attachNetName 13 \
			-units 1000 -mode ALL

		##-- Verilog Netlist
		set filename [format "%s/%s_%s.v" $path $TOP $postfix]
		saveNetlist $filename
		##-- Verilog Netlist without core filler cells
		set filler [get_filler_caps]
		lappend filler [get_filler_cells]
		set filename [format "%s/%s_%s_fillcap.v" $path $TOP $postfix]
		saveNetlist $filename -excludeLeafCell -includePhysicalInst \
			-excludeCellInst $filler

		##-- Extract detail parasitics
		set filename [format "%s/%s_%s.rcdb" $path $TOP $postfix]
		setExtractRCMode -engine postRoute -effortLevel high
		extractRC
		set filename [format "%s/%s_%s.spef" $path $TOP $postfix]
		rcOut -spef $filename
	}
}
