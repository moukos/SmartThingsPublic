definition(
    name: "WebAPI controlled energy measurement update",
    namespace: "smartthings",
    author: "SmartThings",
    description: "WebAPI event monitoring",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: [displayName: "WebAPI controlled energy measurement update", displayLink: "http://localhost:4577"])


preferences {
  section ("App aggregates events for switch-type devices in a Smart home using Web API.") {
    input "switches", "capability.switch", multiple: true, required: true
  }
}

mappings {
  path("/switches") {
    action: [
      GET: "listSwitches"
    ]
  }
  path("/switches/:command") {
    action: [
      PUT: "updateSwitches"
    ]
  }
}


def listSwitches() {
	Date date = new Date(97, 1, 23);
   	long diff = date.getTime();
    log.debug "$diff"
    def resp = []
    switches.each {
        resp << [name: it.displayName, value: it.currentValue("switch")]
    }
    return resp
}

void updateSwitches() {
  def data = request.JSON
  def command = params.command
	switch(command) {
        case "one":
            State.aggregate = State.aggregate + 1
            break
        case "zero":
            break
        default:
            httpError(400, "$command is not a valid command for all switches specified")
    }
	log.debug "${State.aggregate}"
}
def installed() {
	//State.aggregate = 0
}

def updated() {}