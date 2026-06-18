// SPDX-FileCopyrightText: 2026 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0
//
// TileLink-UL target-bus driver implementation. See tilelink_ul.h.
//
//
#include "tilelink_ul.h"
#include <cstdio>

void TileLinkUL::tick(bool countEnable, uint64_t steps = 1)
{
    for(uint64_t i = 0; i < steps; i++) {
        *clk = 1;
        evaluateModel();
        *clk = 0;
        evaluateModel();
    }
    if(countEnable) {
        tickCounter += steps;
    }
}

void TileLinkUL::timeoutTick(uint8_t* signal, uint8_t expectedValue, int timeout = DEFAULT_TIMEOUT)
{
    do {
        tick(true);
        timeout--;
    } while((*signal != expectedValue) && timeout > 0);

    if(timeout == 0) {
        throw "TileLink-UL operation timeout";
    }
}

int TileLinkUL::sizeForWidth(int width)
{
    // TileLink size field is log2(bytes).
    switch(width) {
        case 1:  return 0;
        case 2:  return 1;
        case 4:  return 2;
        case 8:  return 3;
        default: return 2;
    }
}

void TileLinkUL::driveIdle()
{
    *a_valid = 0;
    *a_param = 0;
    *a_source = 0;
    *a_corrupt = 0;
    *d_ready = 0;
    evaluateModel();
}

void TileLinkUL::writeAddress(uint32_t addr)
{
    // Only the pointer matching the Verilated port's width is non-null.
    if(a_addr8 != nullptr)       *a_addr8 = (uint8_t)addr;
    else if(a_addr16 != nullptr) *a_addr16 = (uint16_t)addr;
    else if(a_addr32 != nullptr) *a_addr32 = addr;
}

void TileLinkUL::waitHigh(uint8_t* signal)
{
    int timeout = DEFAULT_TIMEOUT;
    while(*signal == 0) {
        if(--timeout <= 0) {
            throw "TileLink-UL operation timeout";
        }
        tick(true);
    }
}

void TileLinkUL::write(int width, uint64_t addr, uint64_t value)
{
    // Drive a PutFullData beat on the A channel.
    *a_opcode  = TL_A_PUT_FULL_DATA;
    *a_param   = 0;
    *a_source  = 0;
    writeAddress((uint32_t)addr);
    *a_size    = sizeForWidth(width);
    *a_mask    = (uint8_t)((1u << width) - 1); // 0xF for a 4-byte access
    *a_data    = (uint32_t)value;
    *a_corrupt = 0;
    *a_valid   = 1;
    *d_ready   = 1;
    evaluateModel();

    // a_ready is combinational on d_ready; once high, one clock fires the A beat and arms d_valid.
    waitHigh(a_ready);
    tick(true);
    *a_valid = 0;
    evaluateModel();

    // d_valid is now a single-cycle pulse; accept it without over-clocking, then complete.
    waitHigh(d_valid);
    tick(true);
    *d_ready = 0;
    evaluateModel();
}

uint64_t TileLinkUL::read(int width, uint64_t addr)
{
    // Drive a Get beat on the A channel.
    *a_opcode  = TL_A_GET;
    *a_param   = 0;
    *a_source  = 0;
    writeAddress((uint32_t)addr);
    *a_size    = sizeForWidth(width);
    *a_mask    = (uint8_t)((1u << width) - 1);
    *a_data    = 0;
    *a_corrupt = 0;
    *a_valid   = 1;
    *d_ready   = 1;
    evaluateModel();

    waitHigh(a_ready);
    tick(true);
    *a_valid = 0;
    evaluateModel();

    // Capture the AccessAckData payload while d_valid is asserted, then complete the beat.
    waitHigh(d_valid);
    uint64_t result = *d_data;
    tick(true);
    *d_ready = 0;
    evaluateModel();

    return result;
}

void TileLinkUL::reset()
{
    driveIdle();
    *resetn = 0;
    tick(true);
    tick(true);
    *resetn = 1;
    tick(true);
    tick(true);
}

bool TileLinkUL::areSignalsConnected()
{
    return isSignalConnected(clk, "clk")
        && isSignalConnected(resetn, "resetn")
        && isSignalConnected(a_valid, "a_valid")
        && isSignalConnected(a_ready, "a_ready")
        && isSignalConnected(a_opcode, "a_opcode")
        && (a_addr8 != nullptr || a_addr16 != nullptr || a_addr32 != nullptr)
        && isSignalConnected(a_size, "a_size")
        && isSignalConnected(a_mask, "a_mask")
        && isSignalConnected(a_data, "a_data")
        && isSignalConnected(d_valid, "d_valid")
        && isSignalConnected(d_ready, "d_ready")
        && isSignalConnected(d_opcode, "d_opcode")
        && isSignalConnected(d_data, "d_data");
}
