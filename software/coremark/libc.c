/*
 * SPDX-FileCopyrightText: 2026 aesc silicon
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <stddef.h>

/* GCC emits calls to these even when they are never written out, and the link
 * has no libc. no-tree-loop-distribute-patterns stops it from recognising the
 * loops below as the very functions they implement.
 */
#define NO_LOOP_IDIOM __attribute__((optimize("no-tree-loop-distribute-patterns")))

NO_LOOP_IDIOM void *memset(void *dest, int c, size_t n)
{
	unsigned char *d = dest;

	while (n--)
		*d++ = (unsigned char)c;

	return dest;
}

NO_LOOP_IDIOM void *memcpy(void *dest, const void *src, size_t n)
{
	unsigned char *d = dest;
	const unsigned char *s = src;

	while (n--)
		*d++ = *s++;

	return dest;
}

void *memmove(void *dest, const void *src, size_t n)
{
	unsigned char *d = dest;
	const unsigned char *s = src;

	if (d < s || d >= s + n)
		return memcpy(dest, src, n);

	d += n;
	s += n;
	while (n--)
		*--d = *--s;

	return dest;
}

int memcmp(const void *a, const void *b, size_t n)
{
	const unsigned char *x = a, *y = b;

	while (n--) {
		if (*x != *y)
			return *x - *y;
		x++;
		y++;
	}

	return 0;
}
