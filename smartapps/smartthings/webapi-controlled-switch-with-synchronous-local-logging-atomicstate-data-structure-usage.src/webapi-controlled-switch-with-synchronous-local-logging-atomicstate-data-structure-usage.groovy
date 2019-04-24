include 'asynchttp_v1'

definition(
    name: "WebAPI controlled switch with synchronous local logging, AtomicState data structure usage",
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

final SIZE = 10

def logData(clientId, experimentId, size) {
	def str = initialize_async(clientId, experimentId,size)
    return str
}

def initialize_async(clientId, experimentId, size) {

    if((state.measurementRun).equals(experimentId)){
    }
    else{
    	atomicState.cloudSeqNum = 0
        state.measurementRun = experimentId
    }
    
    log.debug "locks before ${atomicState.locks} clientId ${clientId}"
    log.debug "bool before ${atomicState.bool} clientId ${clientId}"
    def u = str2Int(clientId) 
    def dummy = lock(u,size)
    log.debug "locks critical ${atomicState.locks} clientId ${clientId}"
    log.debug "bool critical ${atomicState.bool} clientId ${clientId}"
//	int t = u % size //clientNum % size
 //	log.debug " ${t}  ${atomicState.cloudSeqNum} ${u} Critical section"
   	atomicState.cloudSeqNum = atomicState.cloudSeqNum + 1
    unlock(u,size)
    log.debug "locks after ${atomicState.locks} clientId ${clientId} dummy ${dummy}"
    log.debug "bool after ${atomicState.bool} clientId ${clientId}"
    return "${atomicState.cloudSeqNum}"
}  

def addDelay()
{
	/*def max = 10
    def i = 0
    Random random = new Random()
    def num = random.nextInt(max + 1)
    for( i=0; i<num; i++ ){
    	i = i + 1;
    }
    return i
    */
}

def lock(clientNum, size){
	def dummy = 0 
	def j = (clientNum-1) % size
    def temp = atomicState.bool
    temp[j] = 1
    atomicState.bool = temp
    def max = findMaximumElement(atomicState.locks,size)
    temp = atomicState.locks
    temp[j] = max + 1
    atomicState.locks = temp
    temp = atomicState.bool
    temp[j] = 0
    atomicState.bool = temp
    def k = 0
    for ( k = 0; k < size; k++) {
    		while (atomicState.bool[k]){
            	//spin wait
                //log.debug "in here" 
                addDelay()
            }
            while ( atomicState.locks[k] != 0 && (( atomicState.locks[k] < atomicState.locks[j]) || ((atomicState.locks[k] == atomicState.locks[j]) && k < j))) {
                //spin wait
                //log.debug "in here"
                addDelay()//add dummy counter to disallow optimization
            }
    }
    return dummy
}

def unlock(clientNum, size){
	def j = (clientNum-1) % size
    def temp = atomicState.locks
    temp[j] = 0
    atomicState.locks = temp
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

def findMaximumElement(lockArray,size) {
	def maxValue = Integer.MIN_VALUE;
	def i = 0
	for ( i = 0; i < size; i++ ) {
		if ( lockArray[i] > maxValue ) {
			maxValue = lockArray[i]
		}
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
  	def str = logData(clientId, command,10)
    def resp = []   
    resp << "${str}" 
    //log.debug "${str}"
    return resp
}

def updateSwitches() {
  def data = request.JSON
  def command = params.command 
  def clientNum = data.iteration
  logData(data,10)
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
	atomicState.map = []
    def temp = initializeShared(size)
    def temp2 = initializeShared2(size)
}

def updated() {
	atomicState.cloudSeqNum = 0
    atomicState.map = []
    def temp = initializeShared(10)
    def temp2 = initializeShared2(10)
}