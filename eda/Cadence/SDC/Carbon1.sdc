set sdc_version 2.0

set_units -capacitance 1000fF
set_units -time 1000ps

# Set the current design
current_design ${SOC}

create_clock -period 20.0 -name "top_clock"  -waveform {0.0 10.0} [get_ports io_sys_clock]
create_clock -period 100.0 -name "top_jtag_tck" -waveform {0.0 50.0} [get_ports io_sys_jtag_tck]
