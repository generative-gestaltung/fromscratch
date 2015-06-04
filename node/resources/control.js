var CC_WEB_GUI = (function() {


	var that = {};
	var app_config = {};
	var appObject = {};

	var mouse = {x: 0, y: 0};
	var start = {x: 0, y: 0};
	var selectedElement = null;
	var editors = {};
	
	/*
	$(".header").click(function () {
		alert("HEAD");
	}
	
    $header = $(this);
    //getting the next element
    $content = $header.next();
    //open up the content needed - toggle the slide- if visible, slide up, if not slidedown.
    $content.slideToggle(500, function () {
        //execute this after slideToggle is done
        //change text of header based on visibility of content div
        $header.text(function () {
            //change text based on condition
            return $content.is(":visible") ? "Collapse" : "Expand";
        });
    });

	});
	*/


	that.start = function (url) {
		document.addEventListener('mousemove', function(e) { 
   			mouse.x = e.clientX || e.pageX; 
    		mouse.y = e.clientY || e.pageY 

    		var dx = mouse.x -start.x;
    		var dy = mouse.y -start.y; 	

		}, false);
  	};

	that.save = function () {
		var presetName = document.getElementById("presetSelector").value;
		sendData (presetName, "save");
		window.location.replace('./?'+presetName);
	}
	
	that.saveNew = function () {
		var presetName = document.getElementById("newPresetName").value
		sendData (presetName, "save");
		window.location.replace('./?'+presetName);
	}
	
	that.load = function () {
		var presetName = document.getElementById("presetSelector").value;
		sendData (presetName, "load");
		window.location.replace('./?'+presetName);
	}
	
	that.preset = function (value) {
		console.log(value);
	}
	
  	var sendData = function (theVal, theId) {
		var xmlhttp; 
        if (window.XMLHttpRequest) {
        	xmlhttp = new XMLHttpRequest();
        } else {
         	xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        xmlhttp.onreadystatechange=function() {
        	if (this.readyState == 4 && this.status == 200) {
            	console.log(this.responseText);
            }
        }
         
         //var sendString = JSON.stringify(theJSON);
        xmlhttp.open("POST", "?"+theId+"="+encodeURIComponent(theVal), true);
		xmlhttp.send();
         
        console.log(theVal, theId);
         
        //window.location.reload();
	}


	that.changeValue = function(id) {
		var slider = document.getElementById(id);
		var value = slider.value;
		var text = document.getElementById("@"+id);
		text.value = value;
		sendData (value, id);
	}

	that.updateCode = function (id) {
		var editor = editors[id];
		var val = editor.getValue();
		sendData(val, id);
	}

	that.registerEditor = function (id, theEditor) {
		editors[id] = theEditor;
 	}

  	that.slide = function (id) {
  		console.log("SLIDER");
  		var slider = document.getElementById(id);
  		var rect   = slider.getBoundingClientRect();
  		
  		slider = document.getElementById(id+"A");
  		slider.setAttribute("width", (mouse.x-rect.left));

    	sendData ((mouse.x-rect.left)/(rect.right-rect.left), id);
  	}

	return that;
})();