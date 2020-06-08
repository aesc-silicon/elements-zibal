# Makefile for elements MCU
#
#

#====== BEGIN CONFIG ==================================================

# == APP ==
APP=elements/startup/mtimer/# default app
# APP=basic/minimal
# APP="satellites/serial"
# APP="subsys/shell/shell_module/"
# APP="startup/spi/"
# APP="elements/startup/gpio"

# == BOARD CONFIG ==
BOARD="TH-250"
BOARDSIM="TH-250" 
FLASHBOARD=$(subst -,,$(BOARD))# BOARD var for flash has no hyphen - 

# == PATH CONFIG ==
MCU_PATH=$(shell pwd)# get current dir
BASE_PATH=$(MCU_PATH)/..# Top dir of the project folder
VIVADO=/opt/xilinx/Vivado/2019.2/
ZEPHYR_PATH=$(BASE_PATH)/elements-zephyr-dev
ZEPHYR_SDK_PATH=$(BASE_PATH)/zephyr-sdk-0.11.3
OPENOCD_PATH=$(BASE_PATH)/elements-openocd-dev

# ====== END CONFIG ==================================================

# == PHONY targets ==
.PHONY: clean cpfw mcu synth flash sim simSynth

# == Run simulation ==
allSim: zephyr cpfw mcu sim

# == Run synthesis ==
allSynth: zephyr cpfw mcu synth

# == Build Zephyr ==
zephyr:
	export ZEPHYR_TOOLCHAIN_VARIANT=zephyr && \
	export ZEPHYR_SDK_INSTALL_DIR=$(ZEPHYR_SDK_PATH)/ && \
	cd $(ZEPHYR_PATH)  && \
	west build -p -b hydrogen_th --build-dir ./build zephyr/samples/$(APP)

# == Flash Zephyr and run debug session == 
debug:
	$(OPENOCD_PATH)/src/openocd -f $(OPENOCD_PATH)/tcl/interface/jlink.cfg -c 'set HYDROGEN_CPU0_YAML $(MCU_PATH)/build/VexRiscv.yaml' -f ./hydrogen.cfg 

# == Run GDB ==
gdb:	
	$(ZEPHYR_SDK_PATH)/riscv64-zephyr-elf/bin/riscv64-zephyr-elf-gdb -x $(MCU_PATH)/gdbCommands $(ZEPHYR_PATH)/build/zephyr/zephyr.elf

# == Build Zephyr, run GDB, flash Zephyr ==
gdbFresh: zephyr	
	$(ZEPHYR_SDK_PATH)/riscv64-zephyr-elf/bin/riscv64-zephyr-elf-gdb -x $(MCU_PATH)/gdbCommands $(ZEPHYR_PATH)/build/zephyr/zephyr.elf

# == Build Zephyr and flash ==
gdbFlash: zephyr	
	$(ZEPHYR_SDK_PATH)/riscv64-zephyr-elf/bin/riscv64-zephyr-elf-gdb -x $(MCU_PATH)/gdbCommandsFlash &(ZEPHYR_PATH)/build/zephyr/zephyr.elf

# == Copy FW files ==
cpfw: zephyr 
	cp -f $(ZEPHYR_PATH)/build/zephyr/zephyr.bin $(MCU_PATH)/software/zephyr/firmware.bin
	cp -f $(ZEPHYR_PATH)/build/zephyr/zephyr.elf $(MCU_PATH)/software/zephyr/firmware.elf

# == Generate the MCU ==
mcu:	
	cd $(MCU_PATH)/ && \
	sbt "runMain zibal.soc.Hydrogen"

# == Sythesize the bitstream ==
synth:	
	cd $(MCU_PATH)/eda/Xilinx/syn/ && \
	make syn BOARD=$(BOARD)

# == Flash the bitstream ==
flash:
	cd $(MCU_PATH)/eda/Xilinx/ && \
	openocd -c "set BOARD $(FLASHBOARD)" -f flash_xc7.cfg

# == Flash bitstream to SPI ==
flashSpi:
	cd $(MCU_PATH)/eda/Xilinx/ && \
	openocd -c "set BOARD $(FLASHBOARD)" -f flash_spi.cfg

# == Run simultion ==
sim:
	cd $(MCU_PATH)/eda/Xilinx/sim && \
	make init SOC=Hydrogen && \
	make sim BOARD=${BOARDSIM}


# == Run simultion of sythesized design ==
simSynth:
	cd $(MCU_PATH)/eda/Xilinx/syn && \
	make init SOC=Hydrogen && \
	make sim BOARD=${BOARD}

# == clean ==
clean:
	rm -rf $(ZEPHYR_PATH)/build
	cd $(MCU_PATH)/eda/Xilinx/sim && $(MAKE) clean
	cd $(MCU_PATH)/eda/Xilinx/syn && $(MAKE) clean


# == fresh  ==
fresh: clean
	rm -rf $(MCU_PATH)/target
	rm -rf $(BASE_PATH)/VexRiscv/target
	rm -rf $(BASE_PATH)/target
	rm -rf $(MCU_PATH)/project/target
	rm -rf $(MCU_PATH)/scripts/target
	rm -rf $(MCU_PATH)/build
	rm -rf $(ZEPHYR_PATH)/target
	rm -rf $(BASE_PATH)/VexRiscv/project/project/target
	rm -rf $(BASE_PATH)/VexRiscv/project/target
	rm -rf $(BASE_PATH)/SpinalHDL-dev/lib/target
	rm -rf $(BASE_PATH)/SpinalHDL-dev/debugger/target
	rm -rf $(BASE_PATH)/SpinalHDL-dev/sim/target
	rm -rf $(BASE_PATH)/SpinalHDL-dev/core/target/
	rm -rf $(BASE_PATH)/SpinalHDL-dev/project/project/target/
	rm -rf $(BASE_PATH)/SpinalHDL-dev/project/target/
	rm -rf $(BASE_PATH)/SpinalHDL-dev/tester/target/
	rm -rf $(BASE_PATH)/SpinalHDL-dev/demo/target/
	rm -rf $(BASE_PATH)/SpinalHDL-dev/target/
	cd $(MCU_PATH)/eda/Xilinx/sim && $(MAKE) fresh
	cd $(MCU_PATH)/eda/Xilinx/syn && $(MAKE) fresh
	
