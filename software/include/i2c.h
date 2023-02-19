#ifndef BOOTROM_I2C
#define BOOTROM_I2C

#define I2C_SPEED_STANDARD	100000U // 100 kHz
#define I2C_SPEED_FAST		400000U // 400 kHz

struct i2c_regs {
	unsigned int read_write;
	unsigned int status;
	unsigned int config;
	unsigned int clock_div;
	unsigned int ip;
	unsigned int ie;
};

struct i2c_driver {
	volatile struct i2c_regs *regs;
	unsigned int fifo_size;
};

#define I2C_CALC_CLOCK(freq, speed) (freq / speed / 4)

int i2c_init(struct i2c_driver *driver, unsigned int base_address,
	     unsigned int clock_divider);
int i2c_read(struct i2c_driver *driver, unsigned int addr, unsigned int reg,
	     unsigned int size, unsigned char *buf);

#endif
