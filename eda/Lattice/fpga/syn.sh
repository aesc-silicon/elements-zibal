#!/bin/bash

# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0

set -euo pipefail

# FPGA verification builds (BOARD differs from the ASIC TARGET) nest under their target as
# <SOC>/<TARGET>/fpga/<BOARD>; otherwise fall back to the flat <SOC>/<BOARD> layout.
if [ -n "${TARGET:-}" ] && [ "${TARGET}" != "${BOARD}" ]; then
	BASE="${BUILD_ROOT}/${SOC}/${TARGET}/fpga/${BOARD}"
	ZIBAL_BUILD="${BASE}/zibal/"
	FPGA_BUILD="${BASE}/"
else
	ZIBAL_BUILD="${BUILD_ROOT}/${SOC}/${BOARD}/zibal/"
	FPGA_BUILD="${BUILD_ROOT}/${SOC}/${BOARD}/fpga/"
fi
TOP="${BOARD}Top"

cd "${ZIBAL_BUILD}"

mkdir -p "${FPGA_BUILD}"

QUIET=0
for arg in "$@"; do
	case "$arg" in
		--quiet) QUIET=1 ;;
	esac
done

ADDITIONAL_VERILOG=""
if [ -f "${ZIBAL_BUILD}/${TOP}Blackboxes.v" ]; then
	ADDITIONAL_VERILOG="read_verilog ${ZIBAL_BUILD}/${TOP}Blackboxes.v;"
fi

YOSYS_LOG="${FPGA_BUILD}/yosys.log"
NEXTPNR_LOG="${FPGA_BUILD}/nextpnr.log"
ECPPACK_BIT_LOG="${FPGA_BUILD}/ecppack-bit.log"
ECPPACK_BIN_LOG="${FPGA_BUILD}/ecppack-bin.log"

run_logged() {
	local log_file="$1"
	shift

	if [ "$QUIET" -eq 1 ]; then
		"$@" 2>&1 | tee "${log_file}" >/dev/null
	else
		"$@" 2>&1 | tee "${log_file}"
	fi
}

run_logged "${YOSYS_LOG}" \
	yosys \
	-p "${ADDITIONAL_VERILOG} read_verilog ${ZIBAL_BUILD}/${TOP}.v; synth_${FPGA_FAMILY} -top ${TOP} -json ${FPGA_BUILD}/${TOP}.json"

run_logged "${NEXTPNR_LOG}" \
	"nextpnr-${FPGA_FAMILY}" \
	--timing-allow-fail \
	"--${FPGA_DEVICE}" \
	--package "${FPGA_PACKAGE}" \
	--json "${FPGA_BUILD}/${TOP}.json" \
	--lpf "${ZIBAL_BUILD}/${TOP}.lpf" \
	--textcfg "${FPGA_BUILD}/${TOP}.config" \
	--freq "${FPGA_FREQUENCY}"

TIMING_SUMMARY="$(
	awk '
		/Max frequency for clock/ {
			line = $0
			sub(/^[A-Za-z]+:[[:space:]]*/, "", line)

			key = line
			sub(/:[[:space:]]*[0-9.]+ MHz.*/, "", key)

			if (!(key in seen)) {
				order[++count] = key
				seen[key] = 1
			}
			timing[key] = line
		}
		END {
			for (i = 1; i <= count; i++)
				print timing[order[i]]
		}
	' "${NEXTPNR_LOG}"
)"

if printf '%s\n' "${TIMING_SUMMARY}" | grep -q 'FAIL at'; then
	if [ "${ALLOW_TIMING_FAIL:-0}" = "1" ]; then
		printf 'WARNING: FPGA timing constraints were not met; continuing because ALLOW_TIMING_FAIL=1.\n' >&2
	else
		printf 'ERROR: FPGA timing constraints were not met.\n' >&2
		printf '%s\n' "${TIMING_SUMMARY}" | grep 'FAIL at' >&2 || true
		printf 'Set ALLOW_TIMING_FAIL=1 to generate the bitstream anyway.\n' >&2
		exit 1
	fi
fi

run_logged "${ECPPACK_BIT_LOG}" \
	ecppack "${FPGA_BUILD}/${TOP}.config" "${FPGA_BUILD}/${TOP}.bit"

run_logged "${ECPPACK_BIN_LOG}" \
	ecppack "${FPGA_BUILD}/${TOP}.config" "${FPGA_BUILD}/${TOP}.bin"

printf '\nFPGA synthesis summary\n'
printf 'Timing:\n'
if [ -n "${TIMING_SUMMARY}" ]; then
	printf '%s\n' "${TIMING_SUMMARY}" | sed 's/^/  /'
fi

printf 'Device utilisation:\n'
awk '
	/Device utilisation:/ {
		in_util = 1
		next
	}
	in_util && /^Info:[[:space:]]*$/ {
		exit
	}
	in_util && /^Info:/ {
		line = $0
		sub(/^Info:[[:space:]]*/, "", line)

		if (line ~ /%/) {
			split(line, fields, /[[:space:]]+/)
			used = fields[2]
			sub(/\/$/, "", used)

			if (used + 0 > 0)
				print "  " line
		}
	}
' "${NEXTPNR_LOG}"
