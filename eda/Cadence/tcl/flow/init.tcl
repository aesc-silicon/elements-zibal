proc elements_load_design {} {
	global PROCESS

	init_design

	setDesignMode -process ${PROCESS}

	setPreference ConstraintUserXGrid 1.0
	setPreference ConstraintUserYGrid 1.0
	setPreference SnapAllCorners 1

	fpiSetSnapRule -for IOP -grid UG
	fpiSetSnapRule -for CORE -grid MG
	fpiSetSnapRule -for DIE -grid MG
}

proc elements_free_design {} {
	freeDesign
}
