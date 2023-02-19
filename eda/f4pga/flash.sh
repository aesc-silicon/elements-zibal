#!/bin/bash

echo "Bitstream: ${BUILD_ROOT}/${SOC}/${BOARD}/${BOARD}Top.bit$"

OPENFPGALOADER_SOJ_DIR=$PWD/openFPGALoader/spiOverJtag $PWD/openFPGALoader/build/openFPGALoader -b ${OPENFPGALOADER_BOARD} ${FLASH} ${BUILD_ROOT}/${SOC}/${BOARD}/${BOARD}Top.bit
