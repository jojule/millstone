
document.write("<hr><b>Debug</b><br/><TEXTAREA STYLE='border: 1px solid gray; background-color: #ffffbb' id='debug' COLS='80' ROWS='10'></TEXTAREA>");
function debug(message) {
    document.getElementById("debug").value = message;
}

// Load updates and return them as xml
function getChangesUIDL() {
	var x = new XMLHttpRequest();
	x.open("GET", windowUrl + "?xmlHttpRequest=1&repaintAll=1", false);
	x.send(null);
	var updates = x.responseXML;
	debug(x.responseText);
	delete x;
	return updates;
}	
var myXMLHTTPRequest = new XMLHttpRequest();
myXMLHTTPRequest.open("GET", "theme.xsl", false);
myXMLHTTPRequest.send(null);
var xslStylesheet = myXMLHTTPRequest.responseXML;
var xsltProcessor = new XSLTProcessor();
xsltProcessor.importStylesheet(xslStylesheet);

var changes = getChangesUIDL().getElementsByTagName("changes");
for (i=0; i<changes.length; i++) {
  var change = changes.item(i);
  var transformed = xsltProcessor.transformToFragment(change,document);
  var node = document.getElementById(windowId)
  node.innerHTML = "";
  node.appendChild(transformed);
}
