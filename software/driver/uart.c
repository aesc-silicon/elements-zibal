#include "uart.h"

#define DEV_UART_IRQ_RX_EN		(1 << 1)

int uart_init(struct uart_driver *driver, unsigned int base_address,
		unsigned int frequency)
{
	driver->regs = (struct uart_regs *)base_address;
	volatile struct uart_regs *uart = driver->regs;

	uart->clock_div = frequency;
	uart->frame_cfg = 7;

	return 1;
}

void uart_puts(struct uart_driver *driver, unsigned char *str)
{
	while(*str != '\0') {
		uart_putc(driver, *str);
		str++;
	}
}

void uart_putc(struct uart_driver *driver, unsigned char c)
{
	volatile struct uart_regs *uart = driver->regs;

	while ((uart->status & 0x00FF0000) == 0);
	uart->read_write = c;
}

int uart_getc(struct uart_driver *driver, unsigned char *c)
{
	volatile struct uart_regs *uart = driver->regs;
	int val;

	val = uart->read_write;
	if (val & 0x10000) {
		*c = val & 0xFF;
		return 0;
	}

	return -1;
}

int uart_irq_rx_enable(struct uart_driver *driver)
{
	volatile struct uart_regs *uart = driver->regs;

	uart->ip |= DEV_UART_IRQ_RX_EN;
	uart->ie |= DEV_UART_IRQ_RX_EN;

	return 1;
}

int uart_irq_rx_disable(struct uart_driver *driver)
{
	volatile struct uart_regs *uart = driver->regs;

	uart->ie &= ~DEV_UART_IRQ_RX_EN;

	return 1;
}

int uart_irq_rx_ready(struct uart_driver *driver)
{
	volatile struct uart_regs *uart = driver->regs;

	return !!(uart->ip & DEV_UART_IRQ_RX_EN);
}
