
// Globals
var xsltProcessor = new XSLTProcessor();
var variableChanges = "";

function debug(message) {
	
	// Debug disabled
	return;
	
	var ta = document.getElementById("debug");
    if (ta == null) {
		// Debug box
		document.write("<TEXTAREA STYLE='border: 1px solid gray; background-color: #ffffbb' id='debug' COLS='80' ROWS='20'></TEXTAREA>");
	    ta = document.getElementById("debug");    
    }
    ta.value = ta.value + "\n==========\n" + message;
    ta.scrollTop = ta.scrollHeight;
}

// Load updates and return them as xml
function sendGetChanges(repaintAll) {
	var x = new XMLHttpRequest();
	x.open("GET", windowUrl + "?xmlHttpRequest=1" +  (repaintAll ? "&repaintAll=1" : "") + variableChanges, false);
	x.send(null);
	var updates = x.responseXML;
	debug(x.responseText);
	delete x;
	
	variableChanges = "";

	var changes = updates.getElementsByTagName("change");
	for (i=0; i<changes.length; i++) {
	  debug("changes nro: " + i);
	  var change = changes.item(i);
	  var paintableId = change.getAttribute("pid");
	  var transformed = xsltProcessor.transformToFragment(change,document);
	  var node = document.getElementById(paintableId);
	  node.parentNode.replaceChild(transformed,node);
	  
//	  node.innerHTML = "";
	//  node.appendChild(transformed);
	}
}	

function init() {

	// Stylesheet
	var myXMLHTTPRequest = new XMLHttpRequest();
	myXMLHTTPRequest.open("GET", "theme.xsl", false);
	myXMLHTTPRequest.send(null);
	var xslStylesheet = myXMLHTTPRequest.responseXML;
	xsltProcessor.importStylesheet(xslStylesheet);
}

function variableChange(name, value, immediate) {
	
	debug("variableChange('" + name + "', '" + value + "', " + immediate + ");");

	variableChanges = variableChanges + "&" + name + "=" + value;

	if (immediate) 
		sendGetChanges(false);
 }

init();
sendGetChanges(true);
