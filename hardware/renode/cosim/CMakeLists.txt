# SPDX-FileCopyrightText: 2026 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0

cmake_minimum_required(VERSION 3.8)

project(tilelink_cosim)

# Absolute path to the generated SoC netlist (a build artifact, never committed).
# e.g. build/<SOC>/<board>/zibal/<board>Top.v
if(NOT VTOP)
  message(FATAL_ERROR "Set -DVTOP=<absolute path to generated SoC .v>")
endif()
if(NOT EXISTS ${VTOP})
  message(FATAL_ERROR "VTOP netlist not found: ${VTOP}\nRun 'task generate' for the matching SOC/board first.")
endif()

# Elaborate only one TileLink* submodule out of the full-SoC netlist.
# configure-and-verilate.cmake forwards this to verilate(... TOP_MODULE ...).
set(TOP_MODULE TileLinkGpio CACHE STRING "Verilog module to co-simulate")

# Our integration sources (the Renode IntegrationLibrary *.cpp are added automatically).
set(CSOURCES
    ${CMAKE_CURRENT_SOURCE_DIR}/sim_main.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/tilelink_ul.cpp)

set(COMP_LIB_ARGS -fPIC)
# Select the peripheral's optional outputs (passed per-peripheral by build-cosim.sh).
if(COSIM_HAS_INTERRUPT)
  list(APPEND COMP_LIB_ARGS -DCOSIM_HAS_INTERRUPT)
  list(APPEND COMP_EXEC_ARGS -DCOSIM_HAS_INTERRUPT)
endif()
if(COSIM_HAS_ERROR)
  list(APPEND COMP_LIB_ARGS -DCOSIM_HAS_ERROR)
  list(APPEND COMP_EXEC_ARGS -DCOSIM_HAS_ERROR)
endif()
# SpinalHDL netlists emit benign width warnings; don't let Verilator treat them as fatal.
# Both the shared library (libVtop, what Renode loads) and the executable (Vtop) are built,
# and they use separate Verilator arg variables, so set both.
set(VERI_LIB_ARGS "-Wno-fatal")
set(VERI_EXEC_ARGS "-Wno-fatal")

# RVI_DIR = checkout of antmicro/renode-verilator-integration (provides cmake/).
# USER_RENODE_DIR (passed on the command line) must point at the Renode install that
# contains plugins/IntegrationLibrary, e.g. /opt/renode.
if(NOT RVI_DIR)
  message(FATAL_ERROR "Set -DRVI_DIR=<path to renode-verilator-integration checkout>")
endif()
include(${RVI_DIR}/cmake/configure-and-verilate.cmake)
