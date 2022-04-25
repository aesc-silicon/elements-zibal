mkfile_path := $(abspath $(lastword $(MAKEFILE_LIST)))
current_dir := $(patsubst %/,%,$(dir $(mkfile_path)))
BUILDDIR := ${current_dir}/../../../../../build/${SOC}/${BOARD}/symbiflow/
LOGDIR := ${BUILDDIR}/logs/
ZIBALDIR := ${current_dir}/../../../../../build/${SOC}/${BOARD}/zibal/
XDCDIR := ${ZIBALDIR}

# Set variables with elements
TOP := ${BOARD}Top
BITSTREAM_DEVICE := artix7
VERILOG:=${ZIBALDIR}/${TOP}.v
XDC:=${XDCDIR}/${TOP}.xdc

all: ${BUILDDIR}/${TOP}.bit

${BUILDDIR}:
	mkdir ${BUILDDIR}

${BUILDDIR}/${TOP}.eblif: | ${BUILDDIR}
	cd ${BUILDDIR} && symbiflow_synth -t ${TOP} -v ${VERILOG} -d ${BITSTREAM_DEVICE} -p ${PART} -x ${XDC} 2>&1 > ${LOGDIR}/symbiflow_synth.log

${BUILDDIR}/${TOP}.net: ${BUILDDIR}/${TOP}.eblif
	cd ${BUILDDIR} && symbiflow_pack -e ${TOP}.eblif -d ${DEVICE} 2>&1 > ${LOGDIR}/symbiflow_pack.log

${BUILDDIR}/${TOP}.place: ${BUILDDIR}/${TOP}.net
	cd ${BUILDDIR} && symbiflow_place -e ${TOP}.eblif -d ${DEVICE} -n ${TOP}.net -P ${PART} 2>&1 > ${LOGDIR}/symbiflow_place.log

${BUILDDIR}/${TOP}.route: ${BUILDDIR}/${TOP}.place
	cd ${BUILDDIR} && symbiflow_route -e ${TOP}.eblif -d ${DEVICE} 2>&1 > ${LOGDIR}/symbiflow_route.log

${BUILDDIR}/${TOP}.fasm: ${BUILDDIR}/${TOP}.route
	cd ${BUILDDIR} && symbiflow_write_fasm -e ${TOP}.eblif -d ${DEVICE} 2>&1 > ${LOGDIR}/symbiflow_write_fasm.log

${BUILDDIR}/${TOP}.bit: ${BUILDDIR}/${TOP}.fasm
	cd ${BUILDDIR} && symbiflow_write_bitstream -d ${BITSTREAM_DEVICE} -f ${TOP}.fasm -p ${PART} -b ${TOP}.bit 2>&1 > ${LOGDIR}/symbiflow_write_bitstream.log

clean:
	rm -rf ${BUILDDIR}/*
	mkdir ${BUILDDIR}logs
