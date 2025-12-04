# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0

proc elements_place how {
	switch $how {
	"ntd" {
		setPlaceMode -timingdriven false -reorderScan true -congEffort medium
		placeDesign -noPrePlaceOpt
		}
	"td" {
		setPlaceMode -timingdriven true -reorderScan true -congEffort medium
		placeDesign -noPrePlaceOpt
		}
	"opt" {
		setPlaceMode -timingdriven true -reorderScan true -congEffort high
		placeDesign -inPlaceOpt -prePlaceOpt
		}
	}

	fit
}

proc elements_cts {} {
	global PATH_REPORT

	create_route_type -name leaf_rule -top_preferred_layer 4 -bottom_preferred_layer 3
	create_route_type -name trunk_rule -top_preferred_layer 4 -bottom_preferred_layer 3 -shield_net VSSCORE
	create_route_type -name top_rule -top_preferred_layer 4 -bottom_preferred_layer 3 -shield_net VSSCORE

	set_ccopt_property -net_type leaf route_type leaf_rule
	set_ccopt_property -net_type trunk route_type trunk_rule
	set_ccopt_property -net_type top route_type top_rule

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
