#ifndef BOOTROM_PLIC
#define BOOTROM_PLIC

struct plic_driver {
	unsigned int *gateway_priority;
	unsigned int *gateway_pending;
	unsigned int *target_enable;
};


int plic_init(struct plic_driver *driver, unsigned int base_address);
int plic_irq_enable(struct plic_driver *driver, unsigned int number);
int plic_irq_disable(struct plic_driver *driver, unsigned int number);

#endif
