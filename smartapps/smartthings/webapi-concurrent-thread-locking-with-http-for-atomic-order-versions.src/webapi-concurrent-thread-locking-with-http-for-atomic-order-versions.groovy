include 'asynchttp_v1'

definition(
    name: "WebAPI concurrent thread locking with HTTP (for ATOMIC/ORDER versions)",
    namespace: "smartthings",
    author: "SmartThings",
    description: "WebAPI controlled switch",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: [displayName: "WebAPI controlled switch", displayLink: "http://localhost:4567"])
//use trigger-s-2.py

preferences {
  section ("Allow external service to control a switch via Web API.") { 
    input "switches", "capability.switch", multiple: true, required: true
  }
}

def logData(clientId, experimentId, data) {
	def str = initialize_async(clientId, experimentId, data)
    return str
}

def lock(clientNum, data){
 	def params = [
         uri: "http://45.79.174.224:9997?clientSeqNum=${clientNum}&cloudSeqNum=${atomicState.cloudSeqNum}",
        path: '/lock',
        //uri: 'http://requestb.in/y41lljy4',
       // query: [experimentid: "${data.experimentid}", timestamp: "${data.timestamp}" , command: "${data.command}", clientSeqNum: "${data.iteration}", cloudSeqNum: "${atomicState.cloudSeqNum}"]
    ]
 
    try {
        httpGet(params) {resp ->
        return 1
        }
    } catch (e) {
        log.error "error: $e"
        return 0
    }
    
}

def unlock(clientNum, data){
 	def params = [
         uri: "http://45.79.174.224:9997?clientSeqNum=${clientNum}&cloudSeqNum=${atomicState.cloudSeqNum}",
        path: '/unlock',
        //uri: 'http://requestb.in/y41lljy4',
       // query: [experimentid: "${data.experimentid}", timestamp: "${data.timestamp}" , command: "${data.command}", clientSeqNum: "${data.iteration}", cloudSeqNum: "${atomicState.cloudSeqNum}"]
    ]
 
    try {
        httpGet(params) {resp ->
        return 1
            //log.debug "resp data: ${resp}"
            //log.debug "Done"
        }
    } catch (e) {
        log.error "error: $e"
        return 0
    }
    
}
/*def unlock(clientNum, data){
 	def params = [
        uri: 'http://45.79.174.224:9999',
        path: '/unlock',
        //uri: 'http://requestb.in/y41lljy4',
        //contentType: 'application/json',
        body: [experimentid: "${data.experimentid}", timestamp: "${data.timestamp}" , command: "${data.command}", clientSeqNum: "${data.iteration}", cloudSeqNum: "${atomicState.cloudSeqNum}"]
    ]
    try {
    httpPostJson(params) { resp ->
        log.debug "Done"
    }
	} catch (e) {
    log.debug "something went wrong: $e"
}
}*/


def initialize_async(clientId, experimentId,data) {
/*
    if((state.measurementRun).equals(experimentid)){
    }
    else{
    	lock(0,data)
    	atomicState.cloudSeqNum = 0
        state.measurementRun = experimentid
    	unlock(0,data)
    } 
 */
    def u = str2Int(clientId) 
    def dummy = lock(u,data)
    if (dummy == 1){
    //log.debug "Critical section ${atomicState.cloudSeqNum}"
 //	log.debug " ${t}  ${atomicState.cloudSeqNum} ${u} Critical section"
   	atomicState.cloudSeqNum = atomicState.cloudSeqNum + 1
 	def temp = atomicState.cloudSeqNum
    unlock(u,data)
    return "${temp}"
    }
    else{
    	return "error"
    }
    //log.debug "${atomicState.cloudSeqNum}"
}


def str2Int(clientNum){
	if (clientNum.isInteger()) {
  		int value = clientNum as Integer
	}
	//log.debug "${clientNum}"
	int u = Integer.parseInt(clientNum)
    return u
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
  path("/switches/:id/:command") {
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
	def data = request.JSON 
    //def id = request.experimentid 
  //  log.debug "${data.iteration}"
   /// log.debug "${data.experimentid}" 
	def experimentId = params.command 
    def clientId = params.id
  	//log.debug "command ${params.command}"
    //log.debug "id ${params.id}"
    //def clientNum = data.iteration
  	def str = logData(clientId, experimentId, data)
    def resp = []   
    resp << "${str}" 
    //log.debug "${str}"
    return resp
}

void updateSwitches() {
  def data = request.JSON
  def command = params.command 
  def experimentId = params.command 
  def clientId = params.id
  logData(clientId, command)
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
    state.measurementRun = 0
}