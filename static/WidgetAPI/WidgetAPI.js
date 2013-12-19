var MashupPlatform = new Object();

/**
 * @description HTTP module.
 * @readonly
 * @type {Object}
 */
MashupPlatform.http = (function MashupPlatform$http(){})();

/**
 * Builds a URL suitable for working around the cross-domain problem.
 * This usually is handled using the wirecloud proxy but it also can be handled using
 * the access control request headers if the browser has support for them.
 * If all the needed requirements are meet, this function will return a URL without using the proxy.
 *
 * @param {String} 	url			The target URL.
 * @param {Object}	options		An object with a list of request options (see the request options section for more details). 
 * @return {String}
 */
MashupPlatform.http.buildProxyURL = function buildProxyURL(url, options) {return "";};

/**
 * Sends a HTTP request.
 * @param {String} 	url			The target URL.
 * @param {Object}	options		An object with a list of request options (see the request options section for more details).  
 */
MashupPlatform.http.makeRequest = function makeRequest(url, options) {return new Object();};



/**
 * Wiring property.
 * @type {Object}
 */
MashupPlatform.wiring = (function MashupPlatform$wiring(){})();

/**
 * Sends an event through the wiring.
 * @param {String} 	outputName	The name of the output endpoint as defined in the WDL (config.xml file).
 * @param {Object}	data		The content of the event.  
 */
MashupPlatform.wiring.pushEvent = function pushEvent(outputName, data) {};

/**
 * Registers a callback for a given input endpoint.
 * If the given endpoint already has registered a callback, it will be replaced by the new one.
 * @param {String} 	inputName	The name of the input endpoint as defined in the WDL (config.xml file).
 * @param {Object}	data		The callback function to use when an event reaches the given input endpoint.
 */
MashupPlatform.wiring.registerCallback = function registerCallback(intputName, callback) {};



/**
 * Preferences property.
 * @type {Object}
 */
MashupPlatform.prefs = {};

/**
 * Retrieves the value of a preference.
 * @param {String}	key			The preference to fetch
 * @return {Object} 
 */
MashupPlatform.prefs.get = function get(key) {};

/**
 * Sets the value of a preference.
 * @param {String}	key			The identifier of the preference.
 * @param {Object}	value			The new value to use for the preference.
 */
MashupPlatform.prefs.set = function set(key, value) {};

/**
 * Registers a callback for listening preference changes.
 * @param {Function}	callback		The callback function that will be called when the preferences of the widget changes.
 */
MashupPlatform.prefs.registerCallback = function registerCallback(callback) {};



/**
 * Widget property.
 * @type {Object}
 */
MashupPlatform.widget = {};

/**
 * Returns a widget variable by its name.
 * @param {String}	name 	The name of the variable to retrieve.
 */
MashupPlatform.widget.getVariable = function getVariable(name) {};

/**
 * Makes Wirecloud notify that the widget needs user's attention.
 */
MashupPlatform.widget.drawAttention = function drawAttention() {};

/**
 * Property id
 * Returns the widget id.
 * @type Object
 */
MashupPlatform.widget.id = new Object();

/**
 * Writes a message into the wirecloud's log console.
 * @param {String}	msg		 The text of the message to log.
 * @param {Number}	level	 An optional parameter with the level to uses for logging the message. (By default: info).
 */
MashupPlatform.widget.log = function log(msg, level) {};