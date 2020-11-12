set sdc_version 1.7

set_units -capacitance 1000.0fF
set_units -time 1000.0ps

# Set the current design
current_design ${SOC}

create_clock -period 10.0 -name top_systemClk [get_ports io_systemClock]
create_clock -period 100.0 -name top_jtag_tck [get_ports io_jtag_tck]
