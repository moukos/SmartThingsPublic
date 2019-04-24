include 'asynchttp_v1'

definition(
    name: "WebAPI testing of concurrency control using synchronou HTTP variables",
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

final SIZE = 3

def logData(clientId, experimentId, size) {
	def str = initialize_async(clientId, experimentId,size)
    return str
}

//def logData(data) {
//	initialize_async(data)
//}

def initialize_async(data) {

    if((state.measurementRun).equals(data.experimentid)){
    }
    else{
    	atomicState.cloudSeqNum = 0
        state.measurementRun = data.experimentid
    }

    def params = [
        uri: 'http://45.79.174.224:2864',
        path: '/log',
        //uri: 'http://requestb.in/y41lljy4',
        //contentType: 'application/json',
        body: [experimentid: "${data.experimentid}", timestamp: "${data.timestamp}" , command: "${data.command}", clientSeqNum: "${data.iteration}", cloudSeqNum: "${atomicState.cloudSeqNum}"]
    ]
    //log.debug "${data.experimentid}"
    //log.debug "${state.measurementRun}"
 	if((data.command).equals("on")) {
    	asynchttp_v1.post(responseHandler, params,data)
    }
   	atomicState.cloudSeqNum = atomicState.cloudSeqNum + 1
    //log.debug "${atomicState.cloudSeqNum}"
}


/*def initialize_async(clientId, experimentId, size) {

    if((state.measurementRun).equals(experimentId)){
    }
    else{
    	atomicState.cloudSeqNum = 0
        state.measurementRun = experimentId
    }
    
    log.debug "locks before ${atomicState.zerol} ${atomicState.onel} ${atomicState.twol} ${atomicState.threel} clientId ${clientId}"
    log.debug "bool before ${atomicState.zerob} ${atomicState.oneb} ${atomicState.twob} ${atomicState.threeb} clientId ${clientId}"
    def u = str2Int(clientId) 
    def dummy = lock(u,size)
    log.debug "locks critical ${atomicState.zerol} ${atomicState.onel} ${atomicState.twol} ${atomicState.threel} clientId ${clientId}"
    log.debug "bool critical ${atomicState.zerob} ${atomicState.oneb} ${atomicState.twob} ${atomicState.threeb} clientId ${clientId}"
//	int t = u % size //clientNum % size
 //	log.debug " ${t}  ${atomicState.cloudSeqNum} ${u} Critical section"
   	atomicState.cloudSeqNum = atomicState.cloudSeqNum + 1
    unlock(u,size)
    log.debug "locks after ${atomicState.zerol} ${atomicState.onel} ${atomicState.twol} ${atomicState.threel} clientId ${clientId}"
    log.debug "bool after ${atomicState.zerob} ${atomicState.oneb} ${atomicState.twob} ${atomicState.threeb} clientId ${clientId}"
    return "${atomicState.cloudSeqNum}"
}  
*/


def lock(clientNum, size){
	def dummy = 0 
    def max = 0
	def j = (clientNum-1) % size
    switch (j) {
        case 0:
            atomicState.zerob = 1
            atomicState.zerol = findMaximumElement(size)
            max = atomicState.zerol
            atomicState.zerob = 0
            break
        case 1:
            atomicState.oneb = 1
            atomicState.onel = findMaximumElement(size)
            max = atomicState.onel
            atomicState.oneb = 0
            break
        case 2:
            atomicState.twob = 1
            atomicState.twol = findMaximumElement(size)
            max = atomicState.twol
            atomicState.twob = 0
            break 
        case 3:
            atomicState.threeb = 1
            atomicState.threel = findMaximumElement(size)
            max = atomicState.threel
            atomicState.threeb = 0
            break
        default:
            log.debug "Wrong index"
    }
    
    def k = 0
    for ( k = 0; k < size; k++) {
    	switch (k) {
        case 0:
          	while (atomicState.zerob){
            	//spin wait
                //log.debug "in here" 
                addDelay()
            }
            while ( atomicState.zerol != 0 && (( atomicState.zerol < max) || ((atomicState.zerol == max && k < 0)))) {
                //spin wait
                //log.debug "in here"
                addDelay()//add dummy counter to disallow optimization
            }
            break
        case 1:
           while (atomicState.oneb){
            	//spin wait
                //log.debug "in here" 
                addDelay()
            }
            while ( atomicState.onel != 0 && (( atomicState.onel < max) || ((atomicState.onel == max && k < 0)))) {
                //spin wait
                //log.debug "in here"
                addDelay()//add dummy counter to disallow optimization
            }
            break
        case 2:
            while (atomicState.twob){
            	//spin wait
                //log.debug "in here" 
                addDelay()
            }
            while ( atomicState.twol != 0 && (( atomicState.twol < max) || ((atomicState.twol == max && k < 0)))) {
                //spin wait
                //log.debug "in here"
                addDelay()//add dummy counter to disallow optimization
            }
            break 
        case 3:
        	while (atomicState.threeb){
            	//spin wait
                //log.debug "in here" 
                addDelay()
            }
            while ( atomicState.threel != 0 && (( atomicState.threel < max) || ((atomicState.threel == max && k < 0)))) {
                //spin wait
                //log.debug "in here"
                addDelay()//add dummy counter to disallow optimization
            }
            
            break
        default:
            log.debug "Wrong index at loop"
    }
    
    }
    return dummy
}

def unlock(clientNum, size){
	def j = (clientNum-1) % size
    switch (j) {
        case 0:
            atomicState.zerol = 0
            break
        case 1:
            atomicState.onel = 0
            break
        case 2:
            atomicState.twol = 0
            break 
        case 3:
            atomicState.threel = 0
            break
        default:
            log.debug "Wrong index"
    }
}

def lock2(clientNum, size){
	def dummy = 0
	def j = (clientNum-1) % size
    def temp = atomicState.bool
    temp[j] = 1
    atomicState.bool = temp
    def max = findMaximumElement(atomicState.locks,size)
    temp = atomicState.locks
    temp[j] = max + 1
    atomicState.locks = temp
    def k = 0
    for ( k = 0; k < size; k++) {
            while ((k != j) && atomicState.bool[k] && (( atomicState.locks[k] < atomicState.locks[j]) || ((atomicState.locks[k] == atomicState.locks[j]) && k < j))) {
                //spin wait
                //log.debug "in here"
                dummy = dummy + addDelay()//add dummy counter to disallow optimization
            }
    }
    return dummy
}

def unlock2(clientNum, size){
	def j = (clientNum-1) % size
    def temp = atomicState.bool
    temp[j] = 0
    atomicState.bool = temp
}

def findMaximumElement(size) {
	def maxValue = Integer.MIN_VALUE;
	def i = 0
	
	if ( atomicState.zerol > maxValue ) {
			maxValue = zerol
	}
    if ( atomicState.onel > maxValue ) {
			maxValue = atomicState.onel
	}
    if ( atomicState.twol > maxValue ) {
			maxValue = atomicState.twol
	}
    if ( atomicState.threel > maxValue ) {
			maxValue = atomicState.threel
	}
	return maxValue
}

def initializeShared(size){
	atomicState.bool = []
    def temp = atomicState.bool
    def i = 0 
    for( i = 0; i < size; i++){
		temp[i] = 0
	} 
    atomicState.bool = temp
     //log.debug "log ${atomicState.bool}"
    return atomicState.bool
}

def initializeShared2(size){
	atomicState.locks = []
    def temp = atomicState.locks
    def i = 0 
    for( i = 0; i < size; i++){
		temp[i] = 0
	} 
    atomicState.locks = temp
    // log.debug "log ${atomicState.locks}"
    return atomicState.locks
}

def str2Int(clientNum){
	if (clientNum.isInteger()) {
  		int value = clientNum as Integer
	}
	//log.debug "${clientNum}"
	int u = Integer.parseInt(clientNum)
    return u
}

def accessState(clientNum,size) {

	// parametrize number of threads, pass as parameter. perform evaluation across size of threads n 
   //create a size n = 10 thread array, where n=10 supposedly the upper limit
	
    log.debug "log ${atomicState.locks}"
    log.debug "log ${atomicState.bool}"
  
   	if (clientNum.isInteger()) {
  		int value = clientNum as Integer
	}
	int u = Integer.parseInt(clientNum)
	lock(u,size)
	int t = u % size //clientNum % size
 	log.debug " ${t} ${size} ${clientNum} Critical section"
	unlock(u,size)
  
    log.debug "log ${atomicState.bool}"
   	log.debug "log ${atomicState.locks}"
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
  //  log.debug "${data.iteration}"
   /// log.debug "${data.experimentid}" 
	def experimentId = params.command 
    def clientId = params.id
  	//log.debug "command ${params.command}"
    //log.debug "id ${params.id}"
    //def clientNum = data.iteration
  	def str = logData(clientId, command,3)
    def resp = []   
    resp << "${str}" 
    //log.debug "${str}"
    return resp
}

def updateSwitches() {
  def data = request.JSON
  def command = params.command 
  def clientNum = data.iteration
  logData(data,3)
  def resp = []
	switch(command) {
        case "on":
            //switches.on()
            //accessState(clientNum,10)
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
    initialize()
}

def initialize() {
	atomicState.zerob = 0
    atomicState.zerol = 0
	atomicState.oneb = 0
    atomicState.onel = 0
    atomicState.twob = 0
    atomicState.twol = 0
    atomicState.threeb = 0
    atomicState.threel = 0
	atomicState.map = []
    def temp = initializeShared(size)
    def temp2 = initializeShared2(size)
}

def updated() {
	atomicState.zerob = 0
    atomicState.zerol = 0
	atomicState.oneb = 0
    atomicState.onel = 0
    atomicState.twob = 0
    atomicState.twol = 0
    atomicState.threeb = 0
    atomicState.threel = 0
	atomicState.cloudSeqNum = 0
    atomicState.map = []
    def temp = initializeShared(3)
    def temp2 = initializeShared2(3)
}