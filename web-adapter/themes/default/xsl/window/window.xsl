<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
    xmlns="http://www.w3.org/TR/WD-html-in-xml/DTD/xhtml1-strict.dtd">

<xsl:template match="window">
  <HTML>
    <HEAD>
      <xsl:call-template name="window-head"/>
    </HEAD>
    <BODY>	         
      <FORM NAME="millstone" METHOD="POST" ACCEPT-CHARSET="UTF-8" ENCTYPE="multipart/form-data">
        <xsl:apply-templates/>
        <xsl:apply-templates mode="popup"/>
      </FORM>
    </BODY>
  </HTML>
</xsl:template>

<xsl:template name="window-head">

    <META http-equiv="Content-Type" content="text/html; charset=UTF-8" />    
    <TITLE><xsl:value-of select="@caption" /></TITLE>

    <LINK REL="STYLESHEET" TYPE="text/css" HREF="{wa:resource('css/default.css')}"/>

    <xsl:if test="./open[not(@name)] and not($dhtml)">
	    <META http-equiv="Refresh" content="0;{./open/@src}" />
    </xsl:if>

    <xsl:if test="$dhtml">
	    <SCRIPT LANGUAGE="Javascript" SRC="{wa:resource('script/default.js')}"/>
	    <SCRIPT LANGUAGE="Javascript">
		
			<!-- Open new windows -->
		    <xsl:for-each select="open">
				openWindow('<xsl:value-of select="@src"
		          />','<xsl:value-of select="@name"
		          />',<xsl:choose><xsl:when test="@width"><xsl:value-of select="@width"/></xsl:when><xsl:otherwise>-1</xsl:otherwise></xsl:choose
		          >,<xsl:choose><xsl:when test="@height"><xsl:value-of select="@height"/></xsl:when><xsl:otherwise>-1</xsl:otherwise></xsl:choose
		          >,'<xsl:value-of select="@border" />');
		    </xsl:for-each>
		    
		    <!-- Refresh other windows -->
	    	<xsl:value-of select="wa:windowScript()"/>

			<!-- Resize and scroll this window -->
			<!-- FIXME 
			<xsl:choose>
		 	    <xsl:when test="(./scrolldown) or (./scrollleft)">
						    
			    </xsl:when>
	    		<xsl:otherwise>
	    		
			    </xsl:otherwise>
		    </xsl:choose>
			-->
	    </SCRIPT>
    </xsl:if>

</xsl:template>

</xsl:stylesheet>

