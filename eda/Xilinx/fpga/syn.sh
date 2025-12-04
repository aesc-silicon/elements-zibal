#!/bin/bash

# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0

source "$F4PGA_INSTALL_DIR/$FPGA_ARCH/conda/etc/profile.d/conda.sh"
conda activate $FPGA_ARCH

#f4pga -vv build --flow ${BUILD_ROOT}/${SOC}/${BOARD}/zibal/${BOARD}Top.flow --nocache
f4pga -vv build --flow ${BUILD_ROOT}/${SOC}/${BOARD}/zibal/${BOARD}Top.flow
