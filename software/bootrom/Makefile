GCC ?= ${ELEMENTS_BASE}/internal/riscv32-unknown-elf/bin/riscv32-unknown-elf

# The intermediate directory for compiled object files.
BUILD = ${ELEMENTS_BASE}/build/${SOC}/${BOARD}/software/${STORAGE}/

DRIVER = ${ELEMENTS_BASE}/internal/nafarr/software/driver/

INCLUDE = -I ${ELEMENTS_BASE}/internal/nafarr/software/include/ -I ${BUILD}

SOURCE = ${ELEMENTS_BASE}/hardware/scala/soc/${SOC}/${BOARD}/${STORAGE}/

TARGET = ${BUILD}/kernel.img
LIST = ${BUILD}/kernel.list
MAP = ${BUILD}/kernel.map

LINKER = ${SOURCE}/kernel.ld

AFLAGS = -march=rv32im -mabi=ilp32 -Wall
CFLAGS = -march=rv32im -mabi=ilp32 -Wall
LFLAGS = --no-undefined

OBJECTS := $(patsubst $(SOURCE)%.s,$(BUILD)%.o,$(wildcard $(SOURCE)*.s))
OBJECTS += $(patsubst $(SOURCE)%.c,$(BUILD)%.o,$(wildcard $(SOURCE)*.c))
OBJECTS += $(patsubst $(DRIVER)%.c,$(BUILD)%.o,$(wildcard $(DRIVER)*.c))

# Rule to make everything.
all: $(TARGET) $(LIST)

# Rule to remake everything. Does not include clean.
rebuild: all

# Rule to make the listing file.
$(LIST) : $(BUILD)output.elf
	$(GCC)-objdump -d $(BUILD)output.elf > $(LIST)

# Rule to make the image file.
$(TARGET) : $(BUILD)output.elf
	$(GCC)-objcopy --input-target=elf32-littleriscv $(BUILD)output.elf -O binary $(TARGET) 

# Rule to make the elf file.
$(BUILD)output.elf : $(OBJECTS) $(LINKER)
	$(GCC)-ld $(LFLAGS) $(OBJECTS) -Map $(MAP) -o $(BUILD)output.elf -T $(LINKER) --print-memory-usage

# Rule to make the object files.
$(BUILD)%.o: $(SOURCE)%.s $(BUILD)
	$(GCC)-as $(AFLAGS) -I $(SOURCE) $< -o $@

$(BUILD)%.o: $(SOURCE)%.c $(BUILD)
	$(GCC)-gcc $(CFLAGS) $(INCLUDE) -c $< -o $@

$(BUILD)%.o: $(DRIVER)%.c $(BUILD)
	$(GCC)-gcc $(CFLAGS) $(INCLUDE) -c $< -o $@

$(BUILD):
	mkdir $@

# Rule to clean files.
clean : 
	-rm -rf $(BUILD)
