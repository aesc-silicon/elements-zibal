mkfile_path := $(abspath $(lastword $(MAKEFILE_LIST)))
current_dir := $(patsubst %/,%,$(dir $(mkfile_path)))
BUILDDIR := ${current_dir}/../../../../build/symbiflow/
ZIBALDIR := ${current_dir}/../../../../build/zibal/
XDCDIR := ${current_dir}/../XDC/

# Set variables with elements
board := Nexys4-DDR
SOC := Hydrogen3
top_module := Nexys4-DDR_top
top_module_name := Nexys4DDR_top
part := xc7a100tcsg324-1
DEVICE := xc7a100t_test
BITSTREAM_DEVICE := artix7

PARTNAME := ${part}
TOP:=${top_module_name}
VERILOG:=${ZIBALDIR}/${SOC}.v ${current_dir}/../${top_module}.v
XDC:=${XDCDIR}//${board}.xdc

all: ${BUILDDIR}/${TOP}.bit

${BUILDDIR}:
	mkdir ${BUILDDIR}

${BUILDDIR}/${TOP}.eblif: | ${BUILDDIR}
	cd ${BUILDDIR} && symbiflow_synth -t ${TOP} -v ${VERILOG} -d ${BITSTREAM_DEVICE} -p ${PARTNAME} -x ${XDC} 2>&1 > symbiflow_synth.log

${BUILDDIR}/${TOP}.net: ${BUILDDIR}/${TOP}.eblif
	cd ${BUILDDIR} && symbiflow_pack -e ${TOP}.eblif -d ${DEVICE} 2>&1 > symbiflow_pack.log

${BUILDDIR}/${TOP}.place: ${BUILDDIR}/${TOP}.net
	cd ${BUILDDIR} && symbiflow_place -e ${TOP}.eblif -d ${DEVICE} -n ${TOP}.net -P ${PARTNAME} 2>&1 > symbiflow_place.log

${BUILDDIR}/${TOP}.route: ${BUILDDIR}/${TOP}.place
	cd ${BUILDDIR} && symbiflow_route -e ${TOP}.eblif -d ${DEVICE} 2>&1 > symbiflow_route.log

${BUILDDIR}/${TOP}.fasm: ${BUILDDIR}/${TOP}.route
	cd ${BUILDDIR} && symbiflow_write_fasm -e ${TOP}.eblif -d ${DEVICE} 2>&1 > symbiflow_write_fasm.log

${BUILDDIR}/${TOP}.bit: ${BUILDDIR}/${TOP}.fasm
	cd ${BUILDDIR} && symbiflow_write_bitstream -d ${BITSTREAM_DEVICE} -f ${TOP}.fasm -p ${PARTNAME} -b ${TOP}.bit 2>&1 >symbiflow_write_bitstream.log
	cp ${BUILDDIR}/${TOP}.bit ${ZIBALDIR}/${TOP}.bit

clean:
	rm -rf ${BUILDDIR}
