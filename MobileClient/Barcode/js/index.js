/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

function getGeo() {
	var options = {
		timeout : 6000,
		enableHighAccuracy : true,
		maximumAge : 9000
	};
	navigator.geolocation.getCurrentPosition(onSuccess, onError, options);
}

var gesamtAusgabe = "";
var codeCounter = null;
var validUrl = null;

window.onload = checkCounter;

function checkCounter() {
	if (localStorage.getItem("codeCounter") == null) {
		codeCounter = 0;
	} else {
		codeCounter = localStorage.getItem("codeCounter");
	}
}

// onSuccess Geolocation
//
function onSuccess(position) {
	var d = new Date();
	var datum = (d.getYear() + 1900) + "-" + d.getMonth() + "-" + d.getDate();
	var zeit = d.toLocaleTimeString();
	gesamtAusgabe = '"lat":"' + (position.coords.latitude) + '", "long":"'
			+ (position.coords.longitude) + '", "time":"' + datum + ' ' + zeit
			+ '"}';
}

// onError Callback receives a PositionError object
//
function onError(error) {
	alert('code: ' + error.code + '\n' + 'message: ' + error.message + '\n');
}

var app = {
	// Application Constructor
	initialize : function() {
		this.bindEvents();
	},
	// Bind Event Listeners
	//
	// Bind any events that are required on startup. Common events are:
	// `load`, `deviceready`, `offline`, and `online`.
	bindEvents : function() {
		document.addEventListener('deviceready', this.onDeviceReady, false);
		document.getElementById('scan').addEventListener('click', this.scan,
				false);
		document.getElementById('encode').addEventListener('click',
				this.encode, false);
	},

	// deviceready Event Handler
	//
	// The scope of `this` is the event. In order to call the `receivedEvent`
	// function, we must explicity call `app.receivedEvent(...);`
	onDeviceReady : function() {
		app.receivedEvent('deviceready');
	},

	// Update DOM on a Received Event
	receivedEvent : function(id) {
		var parentElement = document.getElementById(id);
		var listeningElement = parentElement.querySelector('.listening');
		var receivedElement = parentElement.querySelector('.received');

		listeningElement.setAttribute('style', 'display:none;');
		receivedElement.setAttribute('style', 'display:block;');

		console.log('Received Event: ' + id);
	},

	scan : function() {
		console.log('scanning');

		getGeo();
		var scanner = cordova.require("cordova/plugin/BarcodeScanner");

		scanner.scan(function(result) {

			alert("We got a barcode\n" + "Result: " + result.text + "\n"
					+ "Format: " + result.format + "\n" + "Cancelled: "
					+ result.cancelled);
			testURL(result.text);
			if (validUrl) {
				sendCodes(result.text)
			} else {
				savelocal(result.text);

			}
			console.log(result);
			/*
			 * if (args.format == "QR_CODE") {
			 * window.plugins.childBrowser.showWebPage(args.text, {
			 * showLocationBar: false }); }
			 */

		}, function(error) {
			console.log("Scanning failed: ", error);
		});
	},

	encode : function() {
		var scanner = cordova.require("cordova/plugin/BarcodeScanner");
		var txt_encode_field = document.getElementById("txt_encode_field").value;
		scanner.encode(scanner.Encode.TEXT_TYPE, txt_encode_field, function(
				success) {
			alert("encode success: " + success);
		}, function(fail) {
			alert("encoding failed: " + fail);
		});
	}
};

function testURL(str) {
	if (str.substring(0, 4) == "http") {
		alert("http");
		validUrl = true;
	} else {
		alert("not http");
		validUrl = false;
	}

}

function savelocal(ergebnis) {
	ergebnis = '{"code":"' + ergebnis + '", ' + gesamtAusgabe;
	localStorage.setItem(codeCounter, ergebnis);
	alert("localStorage Item: " + localStorage.getItem(codeCounter))
	codeCounter++;
}

document.addEventListener("deviceready", onDeviceReady, false);
function onDeviceReady() {
	document.addEventListener("backbutton", onBackKeyDown, false);
}

function onBackKeyDown() {
	localStorage.setItem("codeCounter", codeCounter)
	navigator.app.exitApp();
}

function getOffList() {
	if (codeCounter != 0 && codeCounter > 0 && codeCounter != null) {
		for (var i = 0; i < codeCounter; i++) {
			alert(localStorage.getItem(i));
		}
	} else
		alert("CodeCounter ist leer");
}

function clearLocalData() {
	localStorage.removeItem("codeCounter");
	for (var i = 0; i < codeCounter; i++) {
		localStorage.removeItem("" + i.toString() + "")
	}
	codeCounter = 0;
}

function sendCodes(result) {
	var allCodes = '{"codes":[';
	if (codeCounter != 0 && codeCounter > 0 && codeCounter != null) {
		for (var i = 0; i < codeCounter; i++) {
			allCodes += localStorage.getItem(i) + ', ';
		}
	}
	allCodes = allCodes.slice(0, (allCodes.length - 3))
	allCodes += '}]}';
	var test = '{\u0022codes\u0022:';
	var test2 = '}';
	allCodes = test + allCodes;
	allCodes = allCodes + test2;
	alert(allCodes);
	$.ajax({
		type : "POST",
		url : result,
		data : allCodes,
		contentType : "application/json; charset=utf-8",
		dataType : "text",
		success : OnSuccessMainMenu,
		error : OnErrorMainMenu
	});
}

function OnSuccessMainMenu(data, status) {
	alert("Done")
}

function OnErrorMainMenu(request, status, error) {
	alert(status);
	alert(error);
//	alert("error")
}
