#include "i2c.h"

#define ELEMENTS_READ			1
#define ELEMENTS_WRITE			0

#define ELEMENTS_CMD_START		0x0100
#define ELEMENTS_CMD_STOP		0x0200
#define ELEMENTS_CMD_READ		0x0400
#define ELEMENTS_CMD_WRITE		0x0000
#define ELEMENTS_CMD_ACK		0x0800
#define ELEMENTS_CMD_NACK		0x0000

#define ELEMENTS_ADDR_READ(addr)	(addr | 0x0001)
#define ELEMENTS_ADDR_WRITE(addr)	(addr & ~0x1)

#define ELEMENTS_RSP_ERROR		0x0100

int i2c_init(struct i2c_driver *driver, unsigned int base_address,
	     unsigned int clock_divider)
{
	driver->regs = (struct i2c_regs *)base_address;
	volatile struct i2c_regs *i2c = driver->regs;

	i2c->clock_div = clock_divider;
	driver->fifo_size = i2c->status >> 16;

	return 1;
}

static inline void i2c_busy(struct i2c_driver *driver)
{
	volatile struct i2c_regs *i2c = driver->regs;

	while ((i2c->status >> 16) != driver->fifo_size) {}
}

static inline void i2c_wait(struct i2c_driver *driver)
{
	volatile struct i2c_regs *i2c = driver->regs;

	while (!(i2c->status & 0xFF)) {}
}

int i2c_read(struct i2c_driver *driver, unsigned int addr, unsigned int reg,
	     unsigned int size, unsigned char *buf)
{
	volatile struct i2c_regs *i2c = driver->regs;
	unsigned int result;

	// Send device address ...
	i2c_busy(driver);
	i2c->read_write = (ELEMENTS_CMD_START | ELEMENTS_CMD_WRITE |
			   ELEMENTS_ADDR_READ(addr));
	i2c_wait(driver);
	if (i2c->read_write & ELEMENTS_RSP_ERROR)
		return -1;

	// and register.
	i2c_busy(driver);
	i2c->read_write = (ELEMENTS_CMD_WRITE | ELEMENTS_CMD_STOP |
			   (reg & 0xFF));
	i2c_wait(driver);
	if (i2c->read_write & ELEMENTS_RSP_ERROR)
		return -2;

	// Send device address ...
	i2c_busy(driver);
	i2c->read_write = (ELEMENTS_CMD_START | ELEMENTS_CMD_WRITE |
			   ELEMENTS_ADDR_READ(addr));
	i2c_wait(driver);
	if (i2c->read_write & ELEMENTS_RSP_ERROR)
		return -3;

	// and read data.
	for (int i = 0; i < size; i++) {
		i2c_busy(driver);

		if (i == (size - 1)) {
			i2c->read_write = (ELEMENTS_CMD_READ |
					   ELEMENTS_CMD_NACK |
					   ELEMENTS_CMD_STOP);
		} else {
			i2c->read_write = ELEMENTS_CMD_READ | ELEMENTS_CMD_ACK;
		}

		i2c_wait(driver);

		result = i2c->read_write;
		if (result & ELEMENTS_RSP_ERROR)
			return i;
		buf[i] = result && 0xFF;
	}

	return size;
}
