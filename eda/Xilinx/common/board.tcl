# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0

set SOC $::env(SOC)
set BOARD $::env(BOARD)
set TOP "$::env(BOARD)Top"

set PART $::env(PART)

puts "Loaded the board config file"
