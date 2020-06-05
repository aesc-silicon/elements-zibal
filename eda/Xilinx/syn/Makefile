syn:
	vivado -mode tcl -source tcl/${BOARD}.tcl -log ./output/logs/vivado.log -journal ./output/logs/vivado.jou

sim:
	vivado -mode tcl -source tcl/${BOARD}_sim.tcl -log ./output/logs/vivado.log -journal ./output/logs/vivado.jou
#	vivado -mode tcl -source tcl/sim.tcl -log ./output/logs/vivado.log -journal ./output/logs/vivado.jou
init:
	@mkdir -p output/logs
	@ln -sf ../../../build/${SOC}.v_toplevel_system_onChipRam_ram_symbol0.bin ${SOC}.v_toplevel_system_onChipRam_ram_symbol0.bin
	@ln -sf ../../../build/${SOC}.v_toplevel_system_onChipRam_ram_symbol1.bin ${SOC}.v_toplevel_system_onChipRam_ram_symbol1.bin
	@ln -sf ../../../build/${SOC}.v_toplevel_system_onChipRam_ram_symbol2.bin ${SOC}.v_toplevel_system_onChipRam_ram_symbol2.bin
	@ln -sf ../../../build/${SOC}.v_toplevel_system_onChipRam_ram_symbol3.bin ${SOC}.v_toplevel_system_onChipRam_ram_symbol3.bin

clean:
	echo "Remove unnessecary journal and log files"
	rm -f xsim*.jou
	rm -f webtalk*.jou
	rm -f webtalk*.log
	rm -f xvhdl.pb
	rm -f xvhdl.log
	rm -f xvlog.pb
	rm -f xvlog.log
	rm -f xelab.pb
	rm -f usage_statistics_webtalk.*
	rm -f simulate_xsim.wdb
	rm -f fsm_encoding.os

fresh: clean
	echo "Remove all other datas"
	rm -rf xsim.dir/
	rm -rf .Xil/
	rm -rf output/logs/*
	rm -rf output/*.dcp
	rm -rf output/*.edf
	rm -rf output/*.sdf
	rm -rf output/*.v
	rm -rf output/*.vhd
	rm -rf output/*.bin
	rm -rf output/*.bit
	rm -rf *.v_toplevel_system_onChipRam_ram_symbol*.bin
