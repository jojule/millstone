<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
    xmlns:wa="xalan://org.millstone.webadapter.ThemeFunctionLibrary" 
    xmlns:browser="xalan://org.millstone.webadapter.WebBrowser">

<xsl:template name="client-probe">

    <SCRIPT LANGUAGE="javascript1.1">
    	var ver11 = "JavaScript 1.1";
    </SCRIPT>
    <SCRIPT LANGUAGE="javascript1.2">
    	var ver12 = "JavaScript 1.2";
    </SCRIPT>
    <SCRIPT LANGUAGE="javascript1.3">
   		var ver13 = "JavaScript 1.3";
	</SCRIPT>
    <SCRIPT LANGUAGE="javascript1.4">
    	var ver14 = "JavaScript 1.4";
    </SCRIPT>
    <SCRIPT LANGUAGE="javascript1.5">
    	var ver15 = "JavaScript 1.5";
    </SCRIPT>

    <SCRIPT LANGUAGE="JavaScript">
    	var ver10;
    	var ver11;
    	var ver12;
    	var ver13;
    	var ver14;
    	var ver15;
    	var jscript;
    	
		/*@cc_on @*/
		/*@if (@_jscript_version)
  			 jscript = "JScript " + @_jscript_version;  			 
   		  @else @*/
   		     jscript = null;
		/*@end @*/

    	
    	var ver = ver10 ? ver10 : 'JavaScript none';
    	ver = ver11 ? ver11 : ver;
    	ver = ver12 ? ver12 : ver;
    	ver = ver13 ? ver13 : ver;
    	ver = ver14 ? ver14 : ver;
    	ver = ver15 ? ver15 : ver;
    	ver = jscript ? jscript: ver;
    </SCRIPT>


    <!-- Form variables -->
	<xsl:variable name="type">hidden</xsl:variable>
   	<INPUT ID="wa_clientprobe" NAME="wa_clientprobe" TYPE="{$type}" VALUE="0" />
   	<INPUT ID="wa_jsversion" NAME="wa_jsversion" TYPE="{$type}" VALUE="" />
   	<INPUT ID="wa_screenwidth" NAME="wa_screenwidth" TYPE="{$type}" VALUE="" />
   	<INPUT ID="wa_screenheight" NAME="wa_screenheight" TYPE="{$type}" VALUE="" />
   	<INPUT ID="wa_javaenabled" NAME="wa_javaenabled" TYPE="{$type}" VALUE="" />

  <SCRIPT LANGUAGE="JavaScript">

    function setVariables() {
    	document.millstone.wa_clientprobe.value = "1";
    	document.millstone.wa_jsversion.value = ver;
    	document.millstone.wa_screenwidth.value = window.screen.width;
    	document.millstone.wa_screenheight.value =  window.screen.height;
    	document.millstone.wa_javaenabled.value = navigator.javaEnabled();
    }
    
    setVariables();
  </SCRIPT>

</xsl:template>

</xsl:stylesheet>

