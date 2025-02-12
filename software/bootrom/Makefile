GCC ?= ${ELEMENTS_BASE}/zephyr-sdk-0.16.0/riscv64-zephyr-elf/bin/riscv64-zephyr-elf

# The intermediate directory for compiled object files.
BUILD = ${BUILD_ROOT}/${SOC}/${BOARD}/bootrom/

DRIVER = ${NAFARR_BASE}/software/driver/
MISC   = ${NAFARR_BASE}/software/misc/

SOURCE ?= ${ZIBAL_BASE}/hardware/scala/elements/soc/${SOC}/${BOARD}/bootrom/

INCLUDE = -I ${NAFARR_BASE}/software/include/ -I ${BUILD} -I ${SOURCE}

TARGET = ${BUILD}/kernel.img
LIST = ${BUILD}/kernel.lst
MAP = ${BUILD}/kernel.map

LINKER = ${SOURCE}/kernel.ld

AFLAGS = -march=rv32im_zicsr -mabi=ilp32 -Wall
CFLAGS = -march=rv32im_zicsr -mabi=ilp32 -Wall
LFLAGS = -march=rv32im_zicsr -mabi=ilp32 -m elf32lriscv --no-undefined

OBJECTS := $(patsubst $(SOURCE)%.s,$(BUILD)%.o,$(wildcard $(SOURCE)*.s))
OBJECTS += $(patsubst $(SOURCE)%.c,$(BUILD)%.o,$(wildcard $(SOURCE)*.c))
OBJECTS += $(patsubst $(DRIVER)%.c,$(BUILD)%.o,$(wildcard $(DRIVER)*.c))
OBJECTS += $(patsubst $(MISC)%.c,$(BUILD)%.o,$(wildcard $(MISC)*.c))

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

$(BUILD)%.o: $(MISC)%.c $(BUILD)
	$(GCC)-gcc $(CFLAGS) $(INCLUDE) -c $< -o $@

$(BUILD):
	mkdir $@

# Rule to clean files.
clean : 
	-rm -rf $(BUILD)
