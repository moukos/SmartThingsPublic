
definition(
    name: "Motion controlled switch/inconsistent state",
    namespace: "smartthings",
    author: "themis",
    description: "Normal use: Turn your lights on when motion is detected and count number of motions. Problematic use: Same, but race condition on the shared value that stores number of motion is performed because of the delay",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet@2x.png"
)

preferences {
	section("Enable triggers by motion sensor") {
		input "motion1", "capability.motionSensor", title: "Where?", multiple: true
	}
	section("Enable control of the switch.") {
		input "switch1", "capability.switch", multiple: true
	}
}

def installed()
{
	initialize()
	subscribe(motion1, "motion.active", motionActiveHandler)
}

def updated()
{
	unsubscribe()
    initialize()
	subscribe(motion1, "motion.active", motionActiveHandler)
}


def initialize() {
	state.race = 0
}

def addDelay()
{
	def max = 150000
    def i = 0
    for( i=0; i<max; i++ ){
    	i = i + 1;
    }
    return i
}

def motionActiveHandler(evt) {
 	state.race = state.race + 1;
   	def value = addDelay()
    log.debug "${state.race}"
    if( value == 150000 ){
        switch1.on()
        switch1.off()
    }
}

