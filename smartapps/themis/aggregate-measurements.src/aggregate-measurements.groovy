/**
 *  Aggregate measurements
 *
 *  Copyright 2017 Themis Melissaris
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Aggregate measurements",
    namespace: "themis",
    author: "Themis Melissaris",
    description: "Aggregation of event count on shared variable",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


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
 
def initialize(){
	log.debug "never entered"
	state.aggregate = 0
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
    //        state.aggregate = state.aggregate + 1
            break
        case "zero":
            break
        default:
            httpError(400, "$command is not a valid command for all switches specified")
    }
  //   log.debug "Count: $state.aggregate"
	//log.debug "${state.aggregate}"
}
def installed() {
	initialize()
}

def updated() {}