// SPDX-FileCopyrightText: 2026 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0
//
// Generic Renode co-simulation entry point for any Nafarr TileLink peripheral.
//
// The Verilated DUT is one `TileLink*` submodule elaborated out of the full SoC netlist
// (build/<SOC>/<board>/zibal/<board>Top.v) via Verilator's --top-module; the netlist is symlinked
// to a fixed name so the generated class/header are always `Vdut`. All Nafarr TileLink peripherals
// share the same TL-UL bus port (io_bus_a_*, io_bus_d_*), so this one file drives every peripheral.
// Clock and reset are ports of the peripheral's SpinalHDL clock domain, so their names carry that
// domain's prefix (system_clk/system_resetn, peripheral_clk/peripheral_resetn, ...). Per-peripheral
// selections are made at build time by build-cosim.sh:
//   -DCOSIM_CLOCK_DOMAIN=<name> -> clock/reset port prefix (default: system)
//   -DCOSIM_HAS_INTERRUPT       -> register io_interrupt as the next cosim->Renode signal
//   -DCOSIM_HAS_ERROR           -> register io_error     as the next cosim->Renode signal
// Peripheral-specific I/O (gpio pins, uart txd/rxd, i2c scl/sda, ...) is left at Verilator defaults.
//
#include <verilated.h>
#include "Vdut.h"
#include "tilelink_ul.h"
#include "src/renode_bus.h"
#include <cstdio>
#include <cstdlib>

// Resolve the clock-domain-prefixed port names, e.g. peripheral -> peripheral_clk/peripheral_resetn.
#ifndef COSIM_CLOCK_DOMAIN
#define COSIM_CLOCK_DOMAIN system
#endif
#define COSIM_CAT_(a, b) a##b
#define COSIM_CAT(a, b) COSIM_CAT_(a, b)
#define COSIM_CLK    COSIM_CAT(COSIM_CLOCK_DOMAIN, _clk)
#define COSIM_RESETN COSIM_CAT(COSIM_CLOCK_DOMAIN, _resetn)

RenodeAgent *agent = new RenodeAgent;
Vdut *top = new Vdut;

void eval()
{
    top->eval();
}

void initAgent(RenodeAgent *a)
{
    TileLinkUL *bus = new TileLinkUL();

    bus->clk       = &top->COSIM_CLK;
    bus->resetn    = &top->COSIM_RESETN;

    bus->a_valid   = &top->io_bus_a_valid;
    bus->a_ready   = &top->io_bus_a_ready;
    bus->a_opcode  = &top->io_bus_a_payload_opcode;
    bus->a_param   = &top->io_bus_a_payload_param;
    bus->a_source  = &top->io_bus_a_payload_source;
    bus->SetAddressPort(&top->io_bus_a_payload_address); // width-agnostic (8/16/32-bit)
    bus->a_size    = &top->io_bus_a_payload_size;
    bus->a_mask    = &top->io_bus_a_payload_mask;
    bus->a_data    = &top->io_bus_a_payload_data;
    bus->a_corrupt = &top->io_bus_a_payload_corrupt;

    bus->d_valid   = &top->io_bus_d_valid;
    bus->d_ready   = &top->io_bus_d_ready;
    bus->d_opcode  = &top->io_bus_d_payload_opcode;
    bus->d_denied  = &top->io_bus_d_payload_denied;
    bus->d_data    = &top->io_bus_d_payload_data;
    bus->d_corrupt = &top->io_bus_d_payload_corrupt;

    bus->evaluateModel = &eval;
    a->addBus(bus);

    // Optional outputs -> cosim->Renode signals (indices assigned sequentially, interrupt first).
    int signalIndex = 0;
    (void)signalIndex;
#ifdef COSIM_HAS_INTERRUPT
    a->registerInterrupt(&top->io_interrupt, signalIndex++);
#endif
#ifdef COSIM_HAS_ERROR
    a->registerInterrupt(&top->io_error, signalIndex++);
#endif
}

RenodeAgent *Init()
{
    agent->connectNative();
    initAgent(agent);
    return agent;
}

int main(int argc, char **argv, char **env)
{
    if(argc < 3) {
        printf("Usage: %s {receiverPort} {senderPort} [{address}]\n", argv[0]);
        exit(-1);
    }
    const char *address = argc < 4 ? "127.0.0.1" : argv[3];

    Verilated::commandArgs(argc, argv);
    agent->connect(atoi(argv[1]), atoi(argv[2]), address);
    initAgent(agent);
    agent->simulate();
    top->final();
    exit(0);
}
