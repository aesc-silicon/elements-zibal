#!/usr/bin/env bash
#
# SPDX-FileCopyrightText: 2026 aesc silicon
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
  echo "Netlist not found: $NETLIST (generate the SoC first: 'task SOC=<SOC> asic:prepare')" >&2
  exit 1
fi

# The peripheral list is NOT maintained here: it is generated from the elaborated SoC
# (PlatformComponent.dumpCosimManifest) so the module name, clock domain (= clk/resetn port prefix),
# base and PLIC/ESM presence can never drift from the real hardware. Columns (tab-separated):
#   name  module  clockDomain  base  size  irq  error   (irq/error = index or '-')
MANIFEST="$OUTROOT/peripherals.tsv"
if [ ! -f "$MANIFEST" ]; then
  echo "Cosim manifest not found: $MANIFEST (generate the SoC first: 'task SOC=<SOC> asic:prepare')" >&2
  exit 1
fi

# Module *types* that are not single-TL-bus co-sim targets: clock-domain-crossing bridges, and the
# interrupt controller / machine timer that Renode models natively. Everything else is co-simulated.
# HAS_INTERRUPT/HAS_ERROR and the clock domain are taken from the manifest, not hardcoded.
SKIP_MODULES=" FifoCc TileLinkPlic TileLinkMachineTimer "

# Each peripheral builds into its own dir from read-only inputs, so they are independent and run
# concurrently - this fills the cores that a single build leaves idle during Verilator's
# (single-threaded) elaboration. COSIM_JOBS caps how many build at once (Verilator + g++ are
# memory-hungry; lower it if RAM-bound); COSIM_BUILD_J is the per-build compile parallelism, kept
# at 1 so JOBS single-threaded builds map to ~one core each.
JOBS="${COSIM_JOBS:-$(nproc 2>/dev/null || echo 4)}"
BUILD_J="${COSIM_BUILD_J:-1}"
# `wait -n` (reap one finished job) needs bash >= 4.3; fall back to batched waits otherwise.
if ((BASH_VERSINFO[0] > 4 || (BASH_VERSINFO[0] == 4 && BASH_VERSINFO[1] >= 3))); then WAIT_N=1; else WAIT_N=0; fi

build_one() {
  local name="$1" module="$2" clockDomain="$3" hasIrq="$4" hasErr="$5"
  local d="$OUTROOT/$name"
  mkdir -p "$d"
  ln -sf "$NETLIST" "$d/dut.v"
  { cmake -S "$SRC" -B "$d" \
      -DUSER_RENODE_DIR="$RENODE" \
      -DRVI_DIR="$RVI" \
      -DVTOP="$d/dut.v" \
      -DTOP_MODULE="$module" \
      -DCOSIM_HAS_INTERRUPT="$hasIrq" \
      -DCOSIM_HAS_ERROR="$hasErr" \
      -DCOSIM_CLOCK_DOMAIN="$clockDomain" &&
    cmake --build "$d" -j"$BUILD_J"; } >"$d/build.log" 2>&1
}

echo "=== co-sim: building peripherals ($JOBS parallel) ==="
launched=()
running=0
while IFS=$'\t' read -r name module clockDomain base size irq error; do
  case "$name" in \#*|"") continue ;; esac
  case "$SKIP_MODULES" in *" $module "*) echo "--- skip:  $name ($module)"; continue ;; esac
  # Note: plain `[ ... ] && x=1` returns non-zero when false and would trip `set -e`.
  if [ "$irq" != "-" ]; then hasIrq=1; else hasIrq=0; fi
  if [ "$error" != "-" ]; then hasErr=1; else hasErr=0; fi
  echo "--- build: $name ($module, $clockDomain domain)"
  build_one "$name" "$module" "$clockDomain" "$hasIrq" "$hasErr" &
  launched+=("$name")
  running=$((running + 1))
  if [ "$running" -ge "$JOBS" ]; then
    if [ "$WAIT_N" -eq 1 ]; then wait -n || true; running=$((running - 1)); else wait; running=0; fi
  fi
done < "$MANIFEST"
wait

# Background failures are otherwise silent; verify each launched peripheral produced its library.
fail=0
if [ "${#launched[@]}" -gt 0 ]; then
  for name in "${launched[@]}"; do
    if [ -f "$OUTROOT/$name/libVtop.so" ]; then
      echo "    ok   $name"
    else
      echo "!!! FAILED: $name (see $OUTROOT/$name/build.log)" >&2
      tail -n 20 "$OUTROOT/$name/build.log" >&2 || true
      fail=1
    fi
  done
fi
[ "$fail" -eq 0 ] || { echo "One or more co-sim builds failed." >&2; exit 1; }

echo "=== co-sim libraries built under $OUTROOT/<name>/libVtop.so ==="
