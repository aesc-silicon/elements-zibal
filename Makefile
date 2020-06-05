# == Vars ==
#APP="satellites/serial"
#APP="subsys/shell/shell_module/"
#APP="startup/spi/"
APP="elements/startup/mtimer/"
# APP="elements/startup/gpio"
BOARD="TH-250"
BOARDSIM="TH-250" #currently only sim files for TH-253 --> should not be any difference, because its the same FPGA
FLASHBOARD=$(subst -,,$(BOARD))#BOARD var for flash has no hyphen - 
MCUPATH=$(shell pwd)# get current dir
CALLPATH="$(MCUPATH)/.."# Top dir of the project folder
VIVADO="/opt/xilinx/Vivado/2019.2/"
ZEPHYR_SDK_PATH="$(CALLPATH)/../zephyr-sdk-0.11.2"
OPENOCD_PATH=$(CALLPATH)/../openocd_spinal

.PHONY: clean cpfw mcu synth flash sim simSynth

allSim: zephyr cpfw mcu sim

allSynth: zephyr cpfw mcu synth

# == Build Zephyr ==
zephyr:
	export ZEPHYR_TOOLCHAIN_VARIANT=zephyr && \
	export ZEPHYR_SDK_INSTALL_DIR=$(ZEPHYR_SDK_PATH) && \
	cd $(CALLPATH)/zephyr-dev  && \
	west build -p -b hydrogen_th --build-dir ./build zephyr/samples/$(APP)

# == Flash Zephyr and Run debug session == 
debug:
	$(OPENOCD_PATH)/src/openocd -f $(OPENOCD_PATH)/tcl/interface/jlink.cfg -c 'set HYDROGEN_CPU0_YAML $(MCUPATH)/build/VexRiscv.yaml' -f ./hydrogen.cfg 
#	$(OPENOCD_PATH)/src/openocd -f $(OPENOCD_PATH)/tcl/interface/jlink.cfg -c 'set MURAX_CPU0_YAML $(MCUPATH)/build/VexRiscv.yaml' -f /home/kuenstler/work/openocd_spinal/tcl/target/murax.cfg

# == Run GDB ==
gdb:	
	$(ZEPHYR_SDK_PATH)/riscv64-zephyr-elf/bin/riscv64-zephyr-elf-gdb -x $(MCUPATH)/gdbCommands $(CALLPATH)/zephyr-dev/build/zephyr/zephyr.elf

# == Build Zephyr, Run GDB, Flash Zephyr ==
gdbFresh: zephyr	
	$(ZEPHYR_SDK_PATH)/riscv64-zephyr-elf/bin/riscv64-zephyr-elf-gdb -x $(MCUPATH)/gdbCommands $(CALLPATH)/zephyr-dev/build/zephyr/zephyr.elf

# == Build Zephyr and Flash ==
gdbFlash: zephyr	
	$(ZEPHYR_SDK_PATH)/riscv64-zephyr-elf/bin/riscv64-zephyr-elf-gdb -x $(MCUPATH)/gdbCommandsFlash $(CALLPATH)/zephyr-dev/build/zephyr/zephyr.elf

# == Copy FW files ==
cpfw: zephyr 
	cp -f $(CALLPATH)/zephyr-dev/build/zephyr/zephyr.bin $(MCUPATH)/software/zephyr/firmware.bin
	cp -f $(CALLPATH)/zephyr-dev/build/zephyr/zephyr.elf $(MCUPATH)/software/zephyr/firmware.elf

# == Generate the MCU ==
mcu:	
	cd $(MCUPATH)/ && \
	sbt "runMain zibal.soc.Hydrogen"

# == Sythesize the bitstream ==
synth:	
	cd $(MCUPATH)/eda/Xilinx/syn/ && \
	make syn BOARD=$(BOARD)

# == Flash the bitstream ==
flash:
	cd $(MCUPATH)/eda/Xilinx/ && \
	openocd -c "set BOARD $(FLASHBOARD)" -f flash_xc7.cfg

# == Flash bitstream to SPI ==
flashSpi:
	cd $(MCUPATH)/eda/Xilinx/ && \
	openocd -c "set BOARD $(FLASHBOARD)" -f flash_spi.cfg

# == Run simultion ==
sim:
	cd $(MCUPATH)/eda/Xilinx/sim && \
	make init SOC=Hydrogen && \
	make sim BOARD=${BOARDSIM}


# == Run simultion of Sythesized design ==
simSynth:
	cd $(MCUPATH)/eda/Xilinx/syn && \
	make init SOC=Hydrogen && \
	make sim BOARD=${BOARD}

# == clean ==
clean:
	rm -rf $(CALLPATH)/zephyr-dev/build
	cd $(MCUPATH)/eda/Xilinx/sim && $(MAKE) clean
	cd $(MCUPATH)/eda/Xilinx/syn && $(MAKE) clean


# == fresh  ==
fresh: clean
	rm -rf $(MCUPATH)/target
	rm -rf $(CALLPATH)/VexRiscv/target
	rm -rf $(CALLPATH)/target
	rm -rf $(MCUPATH)/project/target
	rm -rf $(MCUPATH)/scripts/target
	rm -rf $(MCUPATH)/build
	rm -rf $(CALLPATH)/zephyr-dev/target
	rm -rf $(CALLPATH)/VexRiscv/project/project/target
	rm -rf $(CALLPATH)/VexRiscv/project/target
	rm -rf $(CALLPATH)/SpinalHDL-dev/lib/target
	rm -rf $(CALLPATH)/SpinalHDL-dev/debugger/target
	rm -rf $(CALLPATH)/SpinalHDL-dev/sim/target
	rm -rf $(CALLPATH)/SpinalHDL-dev/core/target/
	rm -rf $(CALLPATH)/SpinalHDL-dev/project/project/target/
	rm -rf $(CALLPATH)/SpinalHDL-dev/project/target/
	rm -rf $(CALLPATH)/SpinalHDL-dev/tester/target/
	rm -rf $(CALLPATH)/SpinalHDL-dev/demo/target/
	rm -rf $(CALLPATH)/SpinalHDL-dev/target/
	cd $(MCUPATH)/eda/Xilinx/sim && $(MAKE) fresh
	cd $(MCUPATH)/eda/Xilinx/syn && $(MAKE) fresh
	
