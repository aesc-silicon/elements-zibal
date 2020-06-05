set path ../../../
set path_rtl ${path}/build/

set_msg_config -id "Synth 8-3331" -limit 1000

#set part XC7A100TCSG324-1
set part XC7A35TFTG256-1
set_param general.maxThreads 8

puts "Loaded the global config file"
