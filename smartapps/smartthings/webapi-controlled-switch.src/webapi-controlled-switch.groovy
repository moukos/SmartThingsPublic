
definition(
    name: "WebAPI controlled switch",
    namespace: "smartthings",
    author: "SmartThings",
    description: "WebAPI controlled switch",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: [displayName: "WebAPI controlled switch", displayLink: "http://localhost:4567"])


preferences {
  section ("Allow external service to control a switch via Web API.") {
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
    log.debug "${data}"
	switch(command) {
        case "on":
            switches.on()
            break
        case "off":
            switches.off()
            break
        default:
            httpError(400, "$command is not a valid command for all switches specified")
    }

}
def installed() {}

def updated() {}