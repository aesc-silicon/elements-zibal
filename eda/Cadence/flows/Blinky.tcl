proc floorplan {} {
	elements_analysis_timing prePlace true
	elements_check_design place
	elements_floorplan core 0.8 70

	elements_filler per
	elements_connect_PG per
	elements_connect_PG core
	elements_connect_PG tie

	elements_power_route {{VDDCORE 30} {VSSCORE 30}}
	elements_check_design power
}

proc place {} {
	elements_place ntd
	elements_check_design pastPlace
	elements_check_design floorplan
}

proc cts {} {
	elements_opt_design preCTS
	elements_analysis_timing preCTS true
	elements_cts
	elements_opt_design postCTS
	elements_analysis_timing postCTS true
}

proc route {} {
	elements_route nano
	elements_opt_design postRoute
	elements_opt_design postRouteHold
	elements_analysis_timing postRoute true
	elements_filler core
	elements_filler metal
}

proc signoff {} {
	elements_opt_design signOff
	setMultiCpuUsage -localCpu 2 -remoteHost 1 -cpuPerRemoteHost 8
	elements_analysis_timing signOff true
}

proc verify {} {
	elements_check_design all
	elements_verify
}

proc save {} {
	elements_save final
	elements_write final
}
