<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="label|datefield|embedded|textfield|upload|select|table|tree|customlayout|gridlayout|orderedlayout|tabsheet">
  <DIV>
  	<xsl:attribute name="CLASS"><xsl:value-of select="local-name()"/><xsl:if test="(./@style) and (string-length(./@style) &gt; 0)">-<xsl:value-of select="./@style"/></xsl:if></xsl:attribute>
    <xsl:if test="(@caption)|(@icon)|(./description)|(./error)">
      <xsl:if test="@disabled='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
      <NOBR CLASS="caption">
        <xsl:if test="@icon"><IMG SRC="{@icon}"/></xsl:if>
        <xsl:value-of select="@caption"/>
      </NOBR>
      <xsl:for-each select="./error"><xsl:apply-templates select="." mode="error"/></xsl:for-each>
      <xsl:for-each select="./description"><xsl:apply-templates select="." mode="description"/></xsl:for-each>
      <BR />
    </xsl:if>
    <xsl:apply-templates select="." mode="core"/>
  </DIV>
</xsl:template>

<!-- Description popup -->

<xsl:template match="description"/>

<xsl:template match="description" mode="description">
  <xsl:variable name="descid" select="generate-id(.)"/>
  <A onclick="showPopupById('{$descid}',event.clientX,event.clientY);">	
    <IMG> 
      <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('icon/error/info.gif')"/></xsl:attribute>
    </IMG>
  </A>
</xsl:template>

<xsl:template match="description" mode="popup">
  <xsl:variable name="descid" select="generate-id(.)"/>
  <DIV ID="{$descid}" CLASS="popup" STYLE="display:none" onclick="hidePopupById('{$descid}');">
    <xsl:apply-templates/>		 
  </DIV>
</xsl:template>



<!-- Error popup -->

<xsl:template match="error"/>

<xsl:template match="error" mode="error">
  <xsl:variable name="errid" select="generate-id(.)"/>
  <A onclick="showPopupById('{$errid}',event.clientX-4,event.clientY-4);">	
    <xsl:call-template name="error-icon">
	  <xsl:with-param name="level" select="@level"/>
	</xsl:call-template>
  </A>
</xsl:template>

<xsl:template match="error" mode="popup">
  <xsl:variable name="errid" select="generate-id(.)"/>
  <DIV ID="{$errid}" CLASS="popup" STYLE="display:none" onclick="hidePopupById('{$errid}');">
    <xsl:apply-templates select="." mode="errordesc"/>		 
  </DIV>
</xsl:template>

<xsl:template match="error" mode="errordesc">
  <TABLE BORDER="0"><TR><TD VALIGN="TOP">
    <xsl:call-template name="error-icon">
      <xsl:with-param name="level" select="@level"/>
    </xsl:call-template></TD></TR>
  <TR><TD><xsl:apply-templates/><xsl:apply-templates select="error" mode="errordesc"/></TD></TR></TABLE>
</xsl:template>

<xsl:template name="error-icon">
  <xsl:param name="level"/>
  <xsl:choose>
	<xsl:when test="$level='info'">
	  <IMG> 
        <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('icon/error/info.gif')"/></xsl:attribute>
	  </IMG>
	</xsl:when>
	<xsl:when test="$level='error'">
	  <IMG>
        <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('icon/error/error.gif')"/></xsl:attribute>
	  </IMG>
	</xsl:when>
	<xsl:when test="$level='warning'">
	  <IMG>
        <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('icon/error/warning.gif')"/></xsl:attribute>
	  </IMG>
	</xsl:when>
	<xsl:when test="$level='critical'">
	  <IMG>
        <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('icon/error/critical.gif')"/></xsl:attribute>
	  </IMG>
	</xsl:when>
	<xsl:otherwise>
	  <IMG>
        <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('icon/error/system.gif')"/></xsl:attribute>
	  </IMG>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>

