<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="window">
  <HTML>
    <HEAD>
      <xsl:call-template name="window-head"/>
    </HEAD>
    <BODY>	 
    
	  <!-- Window scrolling events and initialization -->
	  <xsl:variable name="scrolldownid"><xsl:value-of select="./integer[@name='scrolldown']/@id"/></xsl:variable>
	  <xsl:variable name="scrollleftid"><xsl:value-of select="./integer[@name='scrollleft']/@id"/></xsl:variable>
      <xsl:if test="$dhtml and $scrolldownid and $scrollleftid">
        <xsl:attribute name="onscroll">setVarById('<xsl:value-of select="$scrolldownid"/>',document.body.scrollTop,false);setVarById('<xsl:value-of select="$scrollleftid"/>',document.body.scrollLeft,false)</xsl:attribute>
        <xsl:attribute name="onload">document.body.scrollTop = <xsl:value-of select="./integer[@name='scrolldown']/@value"/>; document.body.scrollLeft = <xsl:value-of select="./integer[@name='scrollleft']/@value"/></xsl:attribute>
      </xsl:if>     
      
      <!-- Main form -->
      <FORM NAME="millstone" METHOD="POST" ACCEPT-CHARSET="UTF-8" ENCTYPE="multipart/form-data">

  	    <!-- Window scrolling variables -->
        <xsl:if test="$dhtml and $scrolldownid and $scrollleftid">
          <INPUT TYPE="HIDDEN" ID="{$scrolldownid}" NAME="{$scrolldownid}" VALUE="{./integer[@name='scrolldown']/@value}"/>
          <INPUT TYPE="HIDDEN" ID="{$scrollleftid}" NAME="{$scrollleftid}" VALUE="{./integer[@name='scrollleft']/@value}"/>
        </xsl:if>     

        <!-- Sub component -->
        <xsl:apply-templates/>
        
        <!-- Popup layers -->
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
		          />','<xsl:value-of select="wa:getWindowTargetName(@name)"
		          />',<xsl:choose><xsl:when test="@width"><xsl:value-of select="@width"/></xsl:when><xsl:otherwise>-1</xsl:otherwise></xsl:choose
		          >,<xsl:choose><xsl:when test="@height"><xsl:value-of select="@height"/></xsl:when><xsl:otherwise>-1</xsl:otherwise></xsl:choose
		          >,'<xsl:value-of select="@border" />');
		    </xsl:for-each>
		    
		    <!-- Refresh other windows -->
	    	<xsl:value-of select="wa:windowScript()"/>

	    </SCRIPT>
    </xsl:if>

</xsl:template>

</xsl:stylesheet>

