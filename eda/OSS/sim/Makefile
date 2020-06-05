SOC = Zibal

TESTBENCHNAME = $(SOC)_tb
TESTBENCHFILE = ../../$(TESTBENCHNAME).sv
TOPBENCHNAME = $(SOC)_top
TOPBENCHFILE = ../$(TOPBENCHNAME).v
SRCS = ../../../$(SOC).v
INCLUDE = ../../

CC = iverilog
CFLAGS = -Wall -g2009
COUT = build/$(SOC).out
SIM = vvp
SFLAGS = -lbuild/$(TESTBENCHNAME).log
VIEWER = gtkwave
VFLAGS = build/config.gtkw
VCD = build/$(TOPBENCHNAME).vcd

all: clean sim view

compile: $(COUT)

$(COUT): $(TOPBENCHFILE) $(TESTBENCHFILE) $(SRCS)
	$(CC) $(CFLAGS) $(TOPBENCHFILE) $(TESTBENCHFILE) $(SRCS) -I$(INCLUDE) -o $(COUT)

sim: init $(COUT)
	$(SIM) $(SFLAGS) $(COUT)

view:
ifneq (,$(wildcard $(VFLAGS)))
	$(VIEWER) $(VCD) $(VFLAGS) &
else
	$(VIEWER) $(VCD) &
endif

init:
	@mkdir -p build/
	@ln -sf ../../../Zibal.v_toplevel_system_onChipRam_ram_symbol0.bin Zibal.v_toplevel_system_onChipRam_ram_symbol0.bin
	@ln -sf ../../../Zibal.v_toplevel_system_onChipRam_ram_symbol1.bin Zibal.v_toplevel_system_onChipRam_ram_symbol1.bin
	@ln -sf ../../../Zibal.v_toplevel_system_onChipRam_ram_symbol2.bin Zibal.v_toplevel_system_onChipRam_ram_symbol2.bin
	@ln -sf ../../../Zibal.v_toplevel_system_onChipRam_ram_symbol3.bin Zibal.v_toplevel_system_onChipRam_ram_symbol3.bin

clean:
	echo "clean"

fresh:
	rm -rf build
	rm Zibal.v_toplevel_system_onChipRam_ram_symbol*.bin
	echo "fresh"
