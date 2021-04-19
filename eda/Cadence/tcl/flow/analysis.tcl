proc elements_check_design { type } {
	global PATH_REPORT

	switch $type {
	"all" {
		checkDesign -all -outdir ${PATH_REPORT}
	}
	"io" {
		checkDesign -io -outdir ${PATH_REPORT}
	}
	"power" {
		checkDesign -powerGround -outdir ${PATH_REPORT}
	}
	"place" {
		checkDesign -place -outdir ${PATH_REPORT}
	}
	"pastPlace" {
		checkPlace ${PATH_REPORT}/${type}.txt
	}
	"floorplan" {
		checkDesign -floorplan -outdir ${PATH_REPORT}
	}
	}
}

proc elements_analysis_timing {state {check false}} {
	global PATH_REPORT

	setAnalysisMode -analysisType onChipVariation

	switch $state {
	"prePlace" {
		timeDesign -prePlace -outDir ${PATH_REPORT} -expandedViews
	}
	"preCTS" {
		timeDesign -preCTS   -outDir ${PATH_REPORT} -expandedViews
	}
	"postCTS" {
		timeDesign -postCTS  -outDir ${PATH_REPORT} -expandedViews
		timeDesign -postCTS -hold -outDir ${PATH_REPORT} -expandedViews
	}
	"postRoute" {
		timeDesign -postRoute -outDir ${PATH_REPORT} -expandedViews
		timeDesign -postRoute -hold -outDir ${PATH_REPORT} -expandedViews
	}
	"signOff" {
		timeDesign -signOff -outDir ${PATH_REPORT} -expandedViews
		timeDesign -signOff -hold -outDir ${PATH_REPORT} -expandedViews
	}
	}

	if [string is true $check] {
		check_timing -verbose > ${PATH_REPORT}/${state}_check_timing
	}
}

proc elements_opt_design {state} {
	global PATH_REPORT

	switch ${state} {
	"preCTS" {
		optDesign -preCTS -outdir ${PATH_REPORT}
	}
	"postCTS" {
		optDesign -postCTS -hold -outdir ${PATH_REPORT}
	}
	"postRoute" {
		optDesign -postRoute -outdir ${PATH_REPORT}
	}
	"postRouteHold" {
		optDesign -postRoute -hold -outdir ${PATH_REPORT}
	}
	"signOff" {
		signoffOptDesign -hold -outdir ${PATH_REPORT}
	}
	}
}
