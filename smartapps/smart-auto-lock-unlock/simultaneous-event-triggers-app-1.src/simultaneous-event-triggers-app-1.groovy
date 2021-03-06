definition(
    name: "Simultaneous event triggers App 1 ",
    namespace: "smart-auto-lock-unlock",
    author: "themis",
    description: "Normally lock/unlock operation",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences
{
    section("Select the door lock:") {
        input "lock1", "capability.lock", required: true
    }
    section("Select the door contact sensor:") {
    	input "contact1", "capability.contactSensor", required: true
    }
	section("Automatically lock the door when closed...") {
        input "minutesLater", "number", title: "Delay (in minutes):", required: true
    }
    section("Automatically unlock the door when open...") {
        input "secondsLater", "number", title: "Delay (in seconds):", required: true
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
    subscribe(contact1, "contact.open", doorHandler)
	subscribe(contact1, "contact.closed", doorHandler)
    
}


def lockDoor()
{
	if (lock1.latestValue("lock") == "unlocked")
    	{
    	log.debug "Locking $lock1..."
    	lock1.lock()
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
    if ((contact1.latestValue("contact") == "open") && (evt.value == "locked"))
    	{
        	runIn (0, unlockDoor)
    	}
    else if ((contact1.latestValue("contact") == "open") && (evt.value == "unlocked"))
    	{
        unschedule (unlockDoor)
		}
    else if ((contact1.latestValue("contact") == "closed") && (evt.value == "locked"))
    	{
        unschedule (lockDoor)
    	}   
    else if ((contact1.latestValue("contact") == "closed") && (evt.value == "unlocked"))
    	{
        	runIn (0, lockDoor)
        }
    else if ((lock1.latestValue("lock") == "unlocked") && (evt.value == "open"))
    	{
        unschedule (lockDoor)
        log.debug "Unlocking $lock1..."
    	lock1.unlock()
    	}
    else if ((lock1.latestValue("lock") == "unlocked") && (evt.value == "closed"))
    	{
        	runIn (delay, lockDoor)
    	}
	else if ((lock1.latestValue("lock") == "locked") && (evt.value == "open"))
    	{
        unschedule (lockDoor)
        log.debug "Unlocking $lock1..."
    	lock1.unlock()
    	}
    else if ((lock1.latestValue("lock") == "locked") && (evt.value == "closed"))
    	{
        unschedule (lockDoor)
        log.debug "Unlocking $lock1..."
    	lock1.unlock()
    	}
    else
    	{
        log.debug "Problem with $lock1, the lock might be jammed!"
        unschedule (lockDoor)
        unschedule (unlockDoor)
    	}
}