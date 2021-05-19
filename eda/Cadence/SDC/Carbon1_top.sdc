set sdc_version 2.0

# Set the current design
current_design ${top_module}

create_clock -period 20.0 -name "top_clock"  -waveform {0.0 10.0} [get_ports io_clock]
create_clock -period 100.0 -name "top_jtag_tck" -waveform {0.0 50.0} [get_ports io_jtag_tck]
