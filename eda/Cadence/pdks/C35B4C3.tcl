proc setup_library {} {
	set_db library liberty/c35_3.3V/c35_CORELIBD_TYP.lib

	set_db lef_library [list \
		cds/HK_C35/LEF/c35b4/c35b4.lef \
		cds/HK_C35/LEF/c35b4/CORELIBD.lef \
	]

	set_db cap_table_file cds/HK_C35/LEF/encounter/c35b4-typical.capTable
}
