#!/usr/bin/env bash
#
# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0
#
# Builds one Verilated Renode co-simulation library per peripheral from the generated SoC netlist.
# Each peripheral is a TileLink* submodule elaborated with Verilator --top-module; the output is
#   <OUTROOT>/<name>/libVtop.so
# which the .resc binds to the matching CoSimulatedPeripheral.
#
# Usage: build-cosim.sh <netlist.v> <outroot> <rvi-dir> <renode-dir>
#
set -euo pipefail

NETLIST="$1"   # absolute path to <board>Top.v
OUTROOT="$2"   # build/<SOC>/<board>/cosim
RVI="$3"       # tools/renode-verilator-integration
RENODE="$4"    # /opt/renode
SRC="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ ! -f "$NETLIST" ]; then
  echo "Netlist not found: $NETLIST (run 'task generate' first)" >&2
  exit 1
fi

# Peripherals to co-simulate (everything except uart0/plic/mtimer, which stay functional, and the
# multi-bus SpiXipController). Columns: name | module | HAS_INTERRUPT | HAS_ERROR
PERIPHERALS="
gpio0|TileLinkGpio|1|0
i2c0|TileLinkI2cController|1|0
pio0|TileLinkPio|1|1
pwm0|TileLinkPwm|1|1
prng|TileLinkPrng|0|1
pinmux|TileLinkPinmux|0|0
resetCtrl|TileLinkResetController|0|0
clockCtrl|TileLinkClockController|0|0
syscon|TileLinkSyscon|0|0
watchdog|TileLinkWatchdog|1|1
esm|TileLinkEsm|0|0
"
# NOTE: TileLinkEsm uses non-standard output names (io_infoInterrupt/io_warnInterrupt/io_errorSignal)
# instead of io_interrupt/io_error, so its signals are not surfaced here (register RTL only).

for entry in $PERIPHERALS; do
  IFS='|' read -r name module hasIrq hasErr <<< "$entry"
  echo "=== co-sim build: $name ($module) ==="
  d="$OUTROOT/$name"
  mkdir -p "$d"
  ln -sf "$NETLIST" "$d/dut.v"
  cmake -S "$SRC" -B "$d" \
    -DUSER_RENODE_DIR="$RENODE" \
    -DRVI_DIR="$RVI" \
    -DVTOP="$d/dut.v" \
    -DTOP_MODULE="$module" \
    -DCOSIM_HAS_INTERRUPT="$hasIrq" \
    -DCOSIM_HAS_ERROR="$hasErr"
  cmake --build "$d" -j
done

echo "=== co-sim libraries built under $OUTROOT/<name>/libVtop.so ==="
