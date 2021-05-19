proc elements_verify {} {
	global top_module_name
	global PATH_REPORT

	clearDrc

	verify_drc -report ${PATH_REPORT}/${top_module_name}.drc.rpt
	verifyConnectivity -report ${PATH_REPORT}/${top_module_name}.con.rpt

	verifyMetalDensity -report ${PATH_REPORT}/${top_module_name}.denstiy.rpt
	verifyPowerVia -report ${PATH_REPORT}/${top_module_name}.power.rpt
}

proc elements_verify_endcap {} {
	global top_module_name
	global PATH_REPORT

	verifyEndCap -report ${PATH_REPORT}/${top_module_name}.endcap.rpt
}

proc elements_save postfix {
	global top_module_name
	global PATH_OUTPUT

	saveDesign ${PATH_OUTPUT}/${top_module_name}
}

proc elements_write postfix {
	global top_module_name
	global PATH_OUTPUT
	global PATH_LATEST
	global PATH_PDK
	set pathes $PATH_OUTPUT
	lappend pathes $PATH_LATEST

	foreach path $pathes {
		##-- Write DEF
		set filename [format "%s/%s_%s.def" $path $top_module_name $postfix]
		defOut -floorplan -netlist -routing $filename

		##-- Create snapshots
		set snapshot [format "%s_%s.def" $top_module_name $postfix]
		createSnapshot -dir ${path} -name ${snapshot} -overwrite

		##-- Write GDS2
		set filename [format "%s/%s_%s_fe.gds" $path $top_module_name $postfix]
		streamOut $filename -mapFile ${PATH_PDK}/streamOut.map -libName $top_module_name \
			-structureName $top_module_name -attachInstanceName 13 -attachNetName 13 \
			-units 1000 -mode ALL

		##-- Verilog Netlist
		set filename [format "%s/%s_%s.v" $path $top_module_name $postfix]
		saveNetlist $filename
		##-- Verilog Netlist without core filler cells
		set filler [get_filler_caps]
		lappend filler [get_filler_cells]
		set filename [format "%s/%s_%s_fillcap.v" $path $top_module_name $postfix]
		saveNetlist $filename -excludeLeafCell -includePhysicalInst \
			-excludeCellInst $filler

		##-- Extract detail parasitics
		set filename [format "%s/%s_%s.rcdb" $path $top_module_name $postfix]
		setExtractRCMode -engine postRoute -effortLevel high
		extractRC
		set filename [format "%s/%s_%s.spef" $path $top_module_name $postfix]
		rcOut -spef $filename
	}
}
