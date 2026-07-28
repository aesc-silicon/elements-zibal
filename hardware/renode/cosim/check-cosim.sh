#!/usr/bin/env bash
#
# SPDX-FileCopyrightText: 2026 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0
#
# Co-simulation preflight: statically checks that the hand-maintained Renode platform files agree
# with the generated manifest and the libraries build-cosim.sh actually produced, BEFORE Renode is
# launched. Otherwise a mismatch only surfaces at runtime - either "libVtop.so: cannot open" at
# load, or a null-pointer abort a couple of seconds into boot when an unbound co-sim connection is
# first accessed.
#
# Usage: check-cosim.sh <cosim.repl> <cosim.resc> <manifest.tsv> <outroot>
#
set -euo pipefail

REPL="$1"      # hardware/scala/<soc>/<soc>_cosim.repl
RESC="$2"      # hardware/scala/<soc>/<soc>_cosim.resc
MANIFEST="$3"  # build/<SOC>/<TARGET>/cosim/peripherals.tsv
OUTROOT="$4"   # build/<SOC>/<TARGET>/cosim

for f in "$REPL" "$RESC" "$MANIFEST"; do
  [ -f "$f" ] || { echo "cosim preflight: missing $f" >&2; exit 1; }
done

errors=0
err() { echo "cosim preflight: $*" >&2; errors=$((errors + 1)); }

# --- Parse the .repl: Renode name -> base address, for every CoSimulatedPeripheral ---------------
#   e.g.  gpio0: CoSimulated.CoSimulatedPeripheral @ sysbus <0xF0000000, +0x1000>
declare -A repl_base
while read -r line; do
  name="${line%%:*}"
  base="$(printf '%s' "$line" | grep -oiE '0x[0-9a-f]+' | head -1 | tr 'A-F' 'a-f')"
  repl_base["$name"]="$base"
done < <(grep -E '^[A-Za-z0-9_]+:[[:space:]]*CoSimulated\.CoSimulatedPeripheral' "$REPL")

# --- Parse the .resc: Renode name -> library directory ------------------------------------------
#   e.g.  gpio0  SimulationFilePathLinux  $cosimdir/gpio0Ctrl/libVtop.so
declare -A resc_dir
while read -r name _kw path _rest; do
  dir="$(printf '%s' "$path" | sed -nE 's#.*/([^/]+)/libVtop\.so.*#\1#p')"
  resc_dir["$name"]="$dir"
done < <(grep -E 'SimulationFilePathLinux' "$RESC")

# --- Parse the manifest: base address -> instance name(s) at that address ------------------------
declare -A man_names
while IFS=$'\t' read -r name _module _domain base _size _irq _error; do
  case "$name" in \#*|"") continue ;; esac
  b="$(printf '%s' "$base" | tr 'A-F' 'a-f')"
  man_names["$b"]="${man_names[$b]:-} $name"
done < "$MANIFEST"

# --- Check 1: every co-simulated peripheral in the .repl is bound in the .resc -------------------
# (an unbound CoSimulatedPeripheral aborts Renode when the firmware first touches it)
for n in "${!repl_base[@]}"; do
  [ -n "${resc_dir[$n]:-}" ] ||
    err "'$n' is CoSimulated in $(basename "$REPL") but has no binding in $(basename "$RESC")"
done

# --- Check 2: every .resc binding refers to a peripheral the .repl actually co-simulates ---------
for n in "${!resc_dir[@]}"; do
  [ -n "${repl_base[$n]:-}" ] ||
    err "'$n' is bound in $(basename "$RESC") but is not CoSimulated in $(basename "$REPL")"
done

# --- Check 3: every bound library directory exists (was built by build-cosim.sh) -----------------
# (catches a name mismatch, or a peripheral whose module type build-cosim.sh skips)
for n in "${!resc_dir[@]}"; do
  dir="${resc_dir[$n]}"
  if [ -z "$dir" ]; then
    err "'$n' binding in $(basename "$RESC") has an unparseable library path"
  elif [ ! -f "$OUTROOT/$dir/libVtop.so" ]; then
    err "'$n' -> '$dir/libVtop.so' does not exist (not built; check the manifest and build log)"
  fi
done

# --- Check 4 (address join): the bound directory is a real instance at the .repl's address -------
# (catches a binding that exists but points at the wrong peripheral's library)
for n in "${!repl_base[@]}"; do
  base="${repl_base[$n]}"
  if [ -z "$base" ]; then
    err "'$n' has no parseable '@ sysbus <0x...>' address in $(basename "$REPL")"
    continue
  fi
  names="${man_names[$base]:-}"
  if [ -z "$names" ]; then
    err "'$n' @ $base is co-simulated but the manifest has no peripheral at that address"
    continue
  fi
  rdir="${resc_dir[$n]:-}"
  if [ -n "$rdir" ] && [[ " $names " != *" $rdir "* ]]; then
    err "'$n' @ $base binds '$rdir', but the manifest instance(s) there are:$names"
  fi
done

if [ "$errors" -gt 0 ]; then
  echo "cosim preflight: $errors problem(s); fix the .repl/.resc (or regenerate) before running." >&2
  exit 1
fi
echo "cosim preflight: OK (${#repl_base[@]} co-simulated peripherals consistent)"
