
document.write("<DIV STYLE='border: 1px solid black; background: #ffffaa;'><b>Debug</b><PRE STYLE='border-top: 1px solid gray;' id='debug'></PRE></DIV>");
function debug(message) {
    document.getElementById("debug").innerHTML = message;
}

// Load updates and return them as xml
function getChangesUIDL() {
	var x = new XMLHttpRequest();
	x.open("GET", windowUrl + "?xmlHttpRequest=1&repaintAll=1", false);
	x.send(null);
	var updates = x.responseXML;
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
