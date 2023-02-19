#include "gpio.h"

int gpio_init(struct gpio_driver *driver, unsigned int base_address)
{
	driver->regs = (struct gpio_regs *)base_address;

	return 1;
}

unsigned int gpio_value_get(struct gpio_driver *driver, unsigned int pin)
{
	volatile struct gpio_regs *gpio = driver->regs;
	unsigned int val;

	val = gpio->data_in >> pin;

	return val & 0x1;
}

void gpio_value_set(struct gpio_driver *driver, unsigned int pin)
{
	volatile struct gpio_regs *gpio = driver->regs;
	unsigned int val;

	val = 1 << pin;
	val = gpio->data_out | val;

	gpio->data_out = val;
}

void gpio_value_clr(struct gpio_driver *driver, unsigned int pin)
{
	volatile struct gpio_regs *gpio = driver->regs;
	unsigned int val;

	val = 1 << pin;
	val = gpio->data_out & ~val;

	gpio->data_out = val;
}

void gpio_dir_set(struct gpio_driver *driver, unsigned int pin)
{
	volatile struct gpio_regs *gpio = driver->regs;
	unsigned int val;

	val = 1 << pin;
	val = gpio->dir_en | val;

	gpio->dir_en = val;
}

void gpio_dir_clr(struct gpio_driver *driver, unsigned int pin)
{
	volatile struct gpio_regs *gpio = driver->regs;
	unsigned int val;

	val = 1 << pin;
	val = gpio->dir_en & ~val;

	gpio->dir_en = val;
}

