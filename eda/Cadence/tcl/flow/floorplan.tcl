# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0

proc elements_floorplan {type util iodist {ratio 1.0}} {

	switch $type {
	"core" {
		floorPlan -r $ratio $util $iodist $iodist $iodist $iodist

		set left [dbGet top.fplan.corebox_llx]
		set left [expr fmod(1 - fmod($left, 1.0), 1.0)]
		set right [dbGet top.fplan.corebox_sizex]
		set right [expr $iodist + $left + 1 - fmod($right, 1.0)]
		puts "Use $right as margin between core and right IO\n"

		set bottom [dbGet top.fplan.corebox_lly]
		set bottom [expr fmod(1 - fmod($bottom, 1.0), 1.0)]
		set top [dbGet top.fplan.corebox_sizey]
		set top [expr $iodist + $bottom + 1 - fmod($top, 1.0)]
		puts "Use $top as margin between core and top IO\n"

		floorPlan -r $ratio $util $iodist $iodist $right $top
	}
	}

	fit
}
