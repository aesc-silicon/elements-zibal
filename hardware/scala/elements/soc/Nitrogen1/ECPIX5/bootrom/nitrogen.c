#include "soc.h"
#include "memc.h"
#include "relocate.h"

extern void hang(void);
extern void setup_sys(void (*)(void));

void _kernel(void)
{
	struct memc_driver memc;
	unsigned int *src = (unsigned int *)0xa0004000;
	unsigned int *dst = (unsigned int *)0x90000000;
	void (*app)(void) = (void (*)())dst;

	memc_init(&memc, MEMC0_BASE);

	relocate_code(src, dst, 0x40000);

	app();

	hang();
}
