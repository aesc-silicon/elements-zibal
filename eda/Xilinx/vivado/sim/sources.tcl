# SPDX-FileCopyrightText: 2025 aesc silicon
#
# SPDX-License-Identifier: CERN-OHL-W-2.0

exec xvlog $::env(VIVADO_PATH)/../data/verilog/src/glbl.v

exec xvlog ${path_rtl}/${SOC}.v
exec xvlog ${path_rtl}/${TOP}.v

puts "Loaded all RTL files"
