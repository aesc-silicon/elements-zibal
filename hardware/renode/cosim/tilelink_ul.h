// SPDX-FileCopyrightText: 2026 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0
//
// Minimal TileLink-UL (Uncached Lightweight) target-bus driver for Renode co-simulation.
//
// Renode acts as the bus master: every CPU access to the co-simulated peripheral is turned
// into a TL-UL A-channel beat (Get for reads, PutFullData for writes); the D-channel response
// is collected and returned to Renode. The port pointers are bound in sim_main.cpp to the
// Verilated model and match the SpinalHDL-generated naming of nafarr's TileLink peripherals
// (io_bus_a_*, io_bus_d_*, system_clk, system_resetn).
//
// This is intentionally small and bus-only: it is the seed for adding first-class TileLink
// support to Renode's IntegrationLibrary (the long-term goal), alongside apb3/axi/wishbone.
//
//
#ifndef TILELINK_UL_H
#define TILELINK_UL_H

#include "src/buses/bus.h"
#include <cstdint>

struct TileLinkUL : public BaseTargetBus
{
    virtual void tick(bool countEnable, uint64_t steps);
    virtual void write(int width, uint64_t addr, uint64_t value);
    virtual uint64_t read(int width, uint64_t addr);
    virtual void reset();
    virtual bool areSignalsConnected();
    void timeoutTick(uint8_t* signal, uint8_t expectedValue, int timeout);
    // Check-first wait: returns without ticking if the signal is already high, otherwise
    // advances the clock until it is. Needed because TL-UL d_valid is a single-cycle pulse.
    void waitHigh(uint8_t* signal);

    // Clock and active-low reset.
    uint8_t  *clk = nullptr;
    uint8_t  *resetn = nullptr;

    // A channel: master -> slave request.
    uint8_t  *a_valid = nullptr;
    uint8_t  *a_ready = nullptr;
    uint8_t  *a_opcode = nullptr;
    uint8_t  *a_param = nullptr;
    uint8_t  *a_source = nullptr;
    uint8_t  *a_size = nullptr;
    uint8_t  *a_mask = nullptr;
    uint32_t *a_data = nullptr;
    uint8_t  *a_corrupt = nullptr;

    // Address port width varies per peripheral (e.g. 8-bit on syscon, 12-bit elsewhere), so
    // Verilator types it as CData/SData/IData accordingly. Bind it type-safely via these overloads
    // from sim_main (`bus->SetAddressPort(&top->io_bus_a_payload_address)`); only one is non-null.
    void SetAddressPort(uint8_t  *p) { a_addr8 = p; }
    void SetAddressPort(uint16_t *p) { a_addr16 = p; }
    void SetAddressPort(uint32_t *p) { a_addr32 = p; }

    // D channel: slave -> master response.
    uint8_t  *d_valid = nullptr;
    uint8_t  *d_ready = nullptr;
    uint8_t  *d_opcode = nullptr;
    uint8_t  *d_denied = nullptr;
    uint32_t *d_data = nullptr;
    uint8_t  *d_corrupt = nullptr;

private:
    void driveIdle();
    void writeAddress(uint32_t addr);
    static int sizeForWidth(int width);

    uint8_t  *a_addr8 = nullptr;
    uint16_t *a_addr16 = nullptr;
    uint32_t *a_addr32 = nullptr;

    // TileLink opcodes (A channel).
    static const uint8_t TL_A_PUT_FULL_DATA = 0;
    static const uint8_t TL_A_GET = 4;
};
#endif
