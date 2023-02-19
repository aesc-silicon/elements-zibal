#ifndef BOOTROM_MTIMER
#define BOOTROM_MTIMER

#define TIMER_MS(freq, cycles) ((freq / 1000) * cycles)

struct mtimer_regs {
	unsigned int cnt_low;
	unsigned int cnt_high;
	unsigned int cmp_low;
	unsigned int cmp_high;
};

struct mtimer_driver {
	volatile struct mtimer_regs *regs;
};

int mtimer_init(struct mtimer_driver *driver, unsigned int base_address);
unsigned int mtimer_sleep32(struct mtimer_driver *driver, unsigned int cycles);

#endif
