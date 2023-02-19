#include "plic.h"

int plic_init(struct plic_driver *driver, unsigned int base_address)
{
	driver->gateway_priority = (unsigned int *)base_address;
	driver->gateway_pending = (unsigned int *)base_address +
		(0x1000 / sizeof(unsigned int));
	driver->target_enable = (unsigned int *)base_address +
		(0x2000 / sizeof(unsigned int));

	return 1;
}

int plic_irq_enable(struct plic_driver *driver, unsigned int number)
{
	*driver->target_enable |= 0x1 << number;

	return 1;
}

int plic_irq_disable(struct plic_driver *driver, unsigned int number)
{
	*driver->target_enable &= ~(0x1 << number);

	return 1;
}
