#!/bin/bash

source "$F4PGA_INSTALL_DIR/$FPGA_FAM/conda/etc/profile.d/conda.sh"
conda activate $FPGA_FAM

f4pga -vv build --flow ${BUILD_ROOT}/${SOC}/${BOARD}/zibal/${BOARD}Top.flow --nocache
