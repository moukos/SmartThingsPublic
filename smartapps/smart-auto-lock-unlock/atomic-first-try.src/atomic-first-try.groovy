/**
 *  Lock / Unlock
 *
 *  Copyright 2014 Arnaud
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
    name: "Atomic first try",
    namespace: "smart-auto-lock-unlock",
    author: "Themis",
    description: "Locks door with delay",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences
{
    section("Select the door lock:") {
        input "lock1", "capability.lock", required: true
    }
 
	section("Delay ...") {
        input "secondsLater", "number", title: "Delay (in seconds):", required: true
    }
    section("Delay 2...") {
        input "secondsLater2", "number", title: "Delay (in seconds):", required: true
    }
	section( "Push notification?" ) {
		input "sendPushMessage", "enum", title: "Send push notification?", metadata:[values:["Yes", "No"]], required: false
   	}
    section( "Text message?" ) {
    	input "sendText", "enum", title: "Send text message notification?", metadata:[values:["Yes", "No"]], required: false
       	input "phoneNumber", "phone", title: "Enter phone number:", required: false
   	}
}

def installed()
{
    initialize()
}

def updated()
{
    unsubscribe()
    unschedule()
    initialize()
}

def initialize()
{
    log.debug "Settings: ${settings}"
    subscribe(lock1, "lock", doorHandler, [filterEvents: false])
    subscribe(lock1, "unlock", doorHandler, [filterEvents: false])  
    state.lcounter = 0
    state.ucounter = 0
}

def addDelay()
{
	def max = 1000000
    def i = 0
	Random random = new Random()
    //def num = random.nextInt(max + 1)
    def num = max
    log.debug("random ")
    for( i=0; i<num; i++ ){
    	
    }
    lock1.unlock()

}

def lockDoor()
{
	//addDelay();
    
	if (lock1.latestValue("lock") == "unlocked")
    	{
    	log.debug "Locking $lock1..."
    	//lock1.lock()
        log.debug ("Sending Push Notification...") 
    	if (sendPushMessage != "No") sendPush("$lock1 locked after $contact1 was closed for $minutesLater minute(s)!")
    	log.debug("Sending text message...")
		if ((sendText == "Yes") && (phoneNumber != "0")) sendSms(phoneNumber, "$lock1 locked after $contact1 was closed for $minutesLater minute(s)!")
        }
	else if (lock1.latestValue("lock") == "locked")
    	{
        log.debug "$lock1 was already locked..."
        }
}

def unlockDoor()
{
	//addDelay();
	if (lock1.latestValue("lock") == "locked")
    	{
    	log.debug "Unlocking $lock1..."
    	lock1.unlock()
        log.debug ("Sending Push Notification...") 
    	if (sendPushMessage != "No") sendPush("$lock1 unlocked after $contact1 was open for $secondsLater seconds(s)!")
    	log.debug("Sending text message...")
		if ((sendText == "Yes") && (phoneNumber != "0")) sendSms(phoneNumber, "$lock1 unlocked after $contact1 was open for $secondsLater seconds(s)!")        
        }
	else if (lock1.latestValue("lock") == "unlocked")
    	{
        log.debug "$lock1 was already unlocked..."
        }
}

def doorHandler(evt)
{
    if (evt.value == "locked")
    {
        if( state.ucounter == 0 && state.lcounter == 0){
            //addDelay()
            log.debug "Unlocking $lock1..."
            def delay2 = secondsLater2
            state.lcounter = state.lcounter+1
           // runIn (delay2, unlockDoor)
            
        }
    }
    else if (evt.value == "unlocked")
    {
   		if( state.ucounter == 0 && state.lcounter == 0){
            log.debug "Locking $lock1..."
            //lock1.lock()
            def delay = secondsLater
            state.ucounter = state.ucounter+1
            //runIn (delay, lockDoor)
         }
	}
    else
    	{
        log.debug "Problem with $lock1, the lock might be jammed!"
        unschedule (lockDoor)
        unschedule (unlockDoor)
    	}
}