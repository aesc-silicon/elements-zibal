#ifndef BOOTROM_GPIO
#define BOOTROM_GPIO

struct gpio_regs {
	int data_in;
	int data_out;
	int dir_en;
	int irq_high_mask;
	int irq_high_pending;
	int irq_low_mask;
	int irq_low_pending;
	int irq_falling_mask;
	int irq_falling_pending;
	int irq_rising_mask;
	int irq_rising_pending;
};

struct gpio_driver {
	volatile struct gpio_regs *regs;
};

int gpio_init(struct gpio_driver *driver, unsigned int base_address);
unsigned int gpio_value_get(struct gpio_driver *driver, unsigned int pin);
void gpio_value_set(struct gpio_driver *driver, unsigned int pin);
void gpio_value_clr(struct gpio_driver *driver, unsigned int pin);

void gpio_dir_set(struct gpio_driver *driver, unsigned int pin);
void gpio_dir_clr(struct gpio_driver *driver, unsigned int pin);

#endif
