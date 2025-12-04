# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0

proc elements_load_design {} {
	global PROCESS
	global PATH_BUILD_ROOT
	global TOP

	init_design

	defIn ${PATH_BUILD_ROOT}/synthesize/${TOP}.scan.def

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
