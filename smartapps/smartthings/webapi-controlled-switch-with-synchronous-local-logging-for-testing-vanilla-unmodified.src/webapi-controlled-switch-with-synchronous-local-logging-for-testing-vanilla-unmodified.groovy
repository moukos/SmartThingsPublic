include 'asynchttp_v1'

definition(
    name: "WebAPI controlled switch with synchronous local logging (For Testing Vanilla/Unmodified)",
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
//USE trigger-s.py
def logData(data) {
	def str = initialize_async(data)
    return str
}

def initialize_async(data) {
	
   // log.debug "${data.experimentid}"

   // if(data.iteration.equals("0")){
   // 	atomicState.cloudSeqNum = 0
   // }
   /* 
    if((state.measurementRun).equals(data.experimentid)){
    }
    else{
    	atomicState.cloudSeqNum = 0
        state.measurementRun = data.experimentid
    }
    */
 //   httpError(200,"${atomicState.cloudSeqNum}")
    atomicState.cloudSeqNum = atomicState.cloudSeqNum + 1
    return "${atomicState.cloudSeqNum}"
}

def responseHandler(response, data) {
    def status = response.status
    switch (status) {
        case 200:
            //log.debug "200 returned"
            break
        case 304:
            log.debug "304 returned"
            break
        default:
            log.warn "no handling for response with status $status"
            break
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

/*def listSwitches() {
	Date date = new Date(97, 1, 23);
   	long diff = date.getTime();
    log.debug "$diff"
    def resp = []
    switches.each {
        resp << [name: it.displayName, value: it.currentValue("switch")]
    }
    return resp
}
*/
def listSwitches() { 
	def data = request.JSON 
    //def id = request.experimentid 
   // def measurement = request?.iteration 
   // log.debug "${request.JSON.experimentid}"   
   //  log.debug "${request.JSON.iteration}"
   //  log.debug "$request.JSON"
  //
  //  log.debug "${data.iteration}"
  //  log.debug "${data.experimentid}" 
	def str = logData(data)
    def resp = []   
    resp << "${str}" 
    return resp
}

def updateSwitches() {
  def data = request.JSON
  def command = params.command 
  logData(data)
  def resp = []
	switch(command) {
        case "on":
            //switches.on()
            break
        case "off":
        //switches.off()
            break
        default:
            httpError(400, "$command is not a valid command for all switches specified")
    }
    
}

def installed() {
	atomicState.cloudSeqNum = 0
    state.measurementRun = 0
}

def updated() {
	atomicState.cloudSeqNum = 0
}