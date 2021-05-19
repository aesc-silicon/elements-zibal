proc elements_filler {type} {
	set fillerIOList [get_filler_ios]
	set fillerCapList [get_filler_caps]
	set fillerCellList [get_filler_cells]

	switch ${type} {
	"core" {
		addFiller -cell ${fillerCapList} -prefix CORE_FILLER_CAP
		addFiller -cell ${fillerCellList} -prefix CORE_FILLER
	}
	"per"  {
		addIoFiller -cell ${fillerIOList} -prefix PER_FILLER
	}
	"metal"  {
		addViaFill
		addMetalFill
	}
	"endcaps" {
		setEndCapMode -rightEdge ENDCAPL
		setEndCapMode -leftEdge ENDCAPR
	}
	}

	fit
}
