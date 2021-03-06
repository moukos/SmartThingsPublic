/**
 *  DoorLockControlledBySwitch
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
    name: "DoorLockControlledBySwitch",
    namespace: "moukos",
    author: "Themis Melissaris",
    description: "Control Smart lock via switch",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Select Devices") {
		input "lock1","capability.lock", title: "Select a lock"
        input "sw1", "capability.switch", title: "Select a switch"
	}
}

def installed() {
	subscribe sw1, "switch.on", onHandler
    subscribe sw1, "switch.off", offHandler	
}

def updated() {
	unsubscribe()
    subscribe sw1, "switch.on", onHandler
    subscribe sw1, "switch.off", offHandler	
}

def onHandler(evt) {
	lock1.unlock()
}

def offHandler(evt) {
	lock1.lock()
}
// TODO: implement event handlers