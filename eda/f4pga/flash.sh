#!/bin/bash

source "$F4PGA_INSTALL_DIR/$FPGA_FAM/conda/etc/profile.d/conda.sh"
conda activate $FPGA_FAM

echo "Bitstream: ${BUILD_ROOT}/${SOC}/${BOARD}/f4pga/${BOARD}Top.bit$"

openFPGALoader -b ${OPENFPGALOADER_BOARD} ${FLASH} ${BUILD_ROOT}/${SOC}/${BOARD}/f4pga/${BOARD}Top.bit
