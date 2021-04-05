proc elements_place how {
	switch $how {
	"ntd" {
		setPlaceMode -timingdriven false -reorderScan false -congEffort medium
		placeDesign -noPrePlaceOpt
		}
	"td" {
		setPlaceMode -timingdriven true -reorderScan false -congEffort medium
		placeDesign -noPrePlaceOpt
		}
	"opt" {
		setPlaceMode -timingdriven true -reorderScan false -congEffort high
		placeDesign -inPlaceOpt -prePlaceOpt
		}
	}

	fit
}

proc elements_cts {} {
	global PATH_REPORT

	set_ccopt_property buffer_cells [get_buffer_cells]
	set_ccopt_property inverter_cells [get_inverter_cells]
	set_ccopt_property clock_gating_cells [get_clock_gating_cells]

	create_ccopt_clock_tree_spec

	ccopt_design -cts -outdir ${PATH_REPORT}
}

proc elements_route {{router wroute}} {
	switch $router {
	"nano" {
		routeDesign -globalDetail
	}
	"wroute" {
		wroute -topLayerLimit 4
	}
	"eco" {
		ecoRoute
	}
	}
}
