# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: Apache-2.0

# The intermediate directory for compiled object files.
BUILD ?= ${BUILD_ROOT}/${SOC}/${BOARD}/bootrom/

DRIVER = ${NAFARR_BASE}/software/driver/
MISC   = ${NAFARR_BASE}/software/misc/

SOURCE ?= ${ZIBAL_BASE}/hardware/scala/elements/soc/${SOC}/${BOARD}/bootrom/
SOC_HEADER ?= ${SOURCE}

# Optional directory with sources shared between applications, e.g. print helpers.
COMMON ?=

INCLUDE = -I ${NAFARR_BASE}/software/include/ -I ${BUILD} -I ${SOURCE} -I ${SOC_HEADER}
INCLUDE += $(if $(COMMON),-I $(COMMON))

TARGET = ${BUILD}/kernel.img
LIST = ${BUILD}/kernel.lst
MAP = ${BUILD}/kernel.map

LINKER = ${SOURCE}/kernel.ld

ARCH ?= rv32i_zicsr

# The base ISA sets the ABI default and the ELF formats; ABI can be overridden (e.g. ilp32d).
XLEN := $(if $(filter rv64%,$(ARCH)),64,$(if $(filter rv32%,$(ARCH)),32,$(error ARCH must start with rv32 or rv64: $(ARCH))))
ABI ?= $(if $(filter 64,$(XLEN)),lp64,ilp32)
LD_EMULATION = elf$(XLEN)lriscv
BFD_TARGET = elf$(XLEN)-littleriscv

AFLAGS = -march=${ARCH} -mabi=${ABI} -Wall
CFLAGS = -march=${ARCH} -mabi=${ABI} -Wall -g -ffunction-sections -fdata-sections
LFLAGS = -march=${ARCH} -mabi=${ABI} -m ${LD_EMULATION} --no-undefined -g --gc-sections

# rv32i resolves multiply and divide through libgcc, which ld does not add.
LIBGCC := $(shell ${GCC}-gcc -march=${ARCH} -mabi=${ABI} -print-libgcc-file-name)

OBJECTS := $(patsubst $(SOURCE)%.s,$(BUILD)%.o,$(wildcard $(SOURCE)*.s))
OBJECTS += $(patsubst $(SOURCE)%.c,$(BUILD)%.o,$(wildcard $(SOURCE)*.c))
OBJECTS += $(patsubst $(DRIVER)%.c,$(BUILD)%.o,$(wildcard $(DRIVER)*.c))
OBJECTS += $(patsubst $(MISC)%.c,$(BUILD)%.o,$(wildcard $(MISC)*.c))
ifneq ($(COMMON),)
OBJECTS += $(patsubst $(COMMON)%.c,$(BUILD)%.o,$(wildcard $(COMMON)*.c))
endif

# Rule to make everything.
all: $(TARGET) $(LIST)

# Rule to remake everything. Does not include clean.
rebuild: all

# Rule to make the listing file.
$(LIST): $(BUILD)output.elf
	$(GCC)-objdump -d $(BUILD)output.elf > $(LIST)

# Rule to make the image file.
$(TARGET): $(BUILD)output.elf
	$(GCC)-objcopy --input-target=${BFD_TARGET} $(BUILD)output.elf -O binary $(TARGET)

# Rule to make the elf file.
$(BUILD)output.elf: $(OBJECTS) $(LINKER)
	$(GCC)-ld $(LFLAGS) $(OBJECTS) $(LIBGCC) -Map $(MAP) -o $(BUILD)output.elf -T $(LINKER) --print-memory-usage

# Rule to make the object files.
$(BUILD)%.o: $(SOURCE)%.s $(BUILD)
	$(GCC)-as $(AFLAGS) -I $(SOURCE) $< -o $@

$(BUILD)%.o: $(SOURCE)%.c $(BUILD)
	$(GCC)-gcc $(CFLAGS) $(INCLUDE) -c $< -o $@

$(BUILD)%.o: $(DRIVER)%.c $(BUILD)
	$(GCC)-gcc $(CFLAGS) $(INCLUDE) -c $< -o $@

$(BUILD)%.o: $(MISC)%.c $(BUILD)
	$(GCC)-gcc $(CFLAGS) $(INCLUDE) -c $< -o $@

ifneq ($(COMMON),)
$(BUILD)%.o: $(COMMON)%.c $(BUILD)
	$(GCC)-gcc $(CFLAGS) $(INCLUDE) -c $< -o $@
endif

$(BUILD):
	mkdir -p $@

# Rule to clean files.
clean:
	-rm -rf $(BUILD)
