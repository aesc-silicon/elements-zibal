proc elements_connect_PG type {
	set globalNetsList {}

	switch $type {
	"tie" {
		set globalNetsList [get_global_tie_nets]
	}
	"core" {
		set globalNetsList [get_global_core_nets]
	}
	"per" {
		set globalNetsList [get_global_per_nets]
	}
	}

	foreach gnet $globalNetsList {
		set net [lindex $gnet 0]
		set pin_type [lindex $gnet 1]
		if {[llength $gnet] == 2} {
			globalNetConnect $net -type $pin_type
			print "Info: GlobalConnect net $net to $pin_type"
		} else {
			set pin [lindex $gnet 2]
			set inst [lindex $gnet 3]
			globalNetConnect $net -type $pin_type -pin $pin -inst $inst -module {}
			print "Info: GlobalConnect all $pin pins ($pin_type) of instance $inst to net $net"
		}
	}
}

proc elements_create_PG_pin {offset layer net name} {
	set pinWidth [expr $offset + 30]

	createPGPin -geom $layer $offset 0 $pinWidth 20.0 -net $net $name
}

proc elements_power_route {{pownetsList {}}} {
	set offset 3.0
	set spacing 2.0

	# foreach power net in the specified list
	# route a ring
	foreach pownet $pownetsList {
		set name [lindex $pownet 0]
		set width [lindex $pownet 1]
		print "----$name $width $offset----"
		setAddRingMode -stacked_via_bottom_layer M1
		setAddRingMode -stacked_via_top_layer M1
		addRing \
			-width $width \
			-spacing $spacing \
			-offset $offset \
			-layer {top M1 bottom M1 left M2 right M2} \
			-around default_power_domain \
			-type core_rings \
			-jog_distance 0.7 \
			-threshold 0.7 \
			-nets $name

		set offset [ expr $offset + $spacing + $width]
	}

	sroute \
		-connect { blockPin padPin corePin padRing floatingStripe } \
		-padPinPortConnect {allPort allGeom}
}

proc elements_power_strip {offset {pownetsList {}}} {
	set margin 3.0
	set spacing 2.0
	set start [ expr $offset - $margin ]

	foreach pownet $pownetsList {
		set name [lindex $pownet 0]
		 set width [lindex $pownet 1]

		addStripe \
			-width $width \
			-spacing $spacing \
			-layer M2 \
			-start $offset \
			-direction vertical \
			-nets $name \
			-number_of_sets 1

		set offset [ expr $offset + $spacing + $width]
	}

	set end [ expr $offset + $margin ]
	puts $start
	puts $end
	createPlaceBlockage -box $start 0.0 $end 3000.0 -type hard
}
