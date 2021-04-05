puts "Hostname : [info hostname]"
set DATE [clock format [clock seconds] -format "%b%d-%T"]

set PATH $::env(ELEMENTS_BASE)
set PATH_PDK $::env(PDK_BASE)
set PATH_RTL ${PATH}/build/${board}/zibal/
set PATH_BUILD_ROOT ${PATH}/build/${board}/cadence
set PATH_BUILD ${PATH_BUILD_ROOT}/${TOOL}/${DATE}
set PATH_LATEST ${PATH_BUILD_ROOT}/${TOOL}/latest
set PATH_OUTPUT ${PATH_BUILD}/output/
set PATH_REPORT ${PATH_BUILD}/reports/
set PATH_LOG ${PATH_BUILD}/logs/

proc createDirectory {path} {
	if {![file exists ${path}]} {
		file mkdir ${path}
		puts "Created directory ${path}"
	}
}

createDirectory ${PATH_BUILD}
createDirectory ${PATH_LATEST}
createDirectory ${PATH_OUTPUT}
createDirectory ${PATH_REPORT}
createDirectory ${PATH_LOG}
