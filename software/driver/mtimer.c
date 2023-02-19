#include "mtimer.h"

int mtimer_init(struct mtimer_driver *driver, unsigned int base_address)
{
	driver->regs = (struct mtimer_regs *)base_address;

	// Write compare registers to unlock the counter
	driver->regs->cmp_low = 0;
	driver->regs->cmp_high = 0;

	return 1;
}


unsigned int mtimer_sleep32(struct mtimer_driver *driver, unsigned int cycles)
{
	volatile struct mtimer_regs *mtimer = driver->regs;

	unsigned int compare = mtimer->cnt_low + cycles;

	while (compare > mtimer->cnt_low);

	return 1;
}
