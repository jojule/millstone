<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="button" mode="core">
	 <INPUT CLASS="button" TYPE="SUBMIT" NAME="set:{./boolean/@id}=true" VALUE=" {@caption} ">
	 <xsl:if test="@disabled='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
	 <xsl:if test="@readonly='true'"><xsl:attribute name="READONLY">true</xsl:attribute></xsl:if>
	 </INPUT>
</xsl:template>

<xsl:template match="button">
  <INPUT  TYPE="SUBMIT" NAME="set:{./boolean/@id}=true" VALUE=" {@caption} ">
  	<xsl:attribute name="CLASS">button<xsl:if test="./@style">-<xsl:value-of select="./@style"/></xsl:if></xsl:attribute>    
    <xsl:if test="@disabled='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
    <xsl:if test="@readonly='true'"><xsl:attribute name="READONLY">true</xsl:attribute></xsl:if>
  </INPUT>
  <xsl:for-each select="./error"><xsl:apply-templates select="." mode="popup"/></xsl:for-each>
  <xsl:for-each select="./description"><xsl:apply-templates select="." mode="description"/></xsl:for-each>
</xsl:template>

<!-- Link style -->

<xsl:template match="button[@icon and $dhtml]|button[@style='link' and $dhtml]">
  <DIV CLASS="button">
    <xsl:apply-templates select="." mode="core"/>
    <xsl:for-each select="./error"><xsl:apply-templates select="." mode="popup"/></xsl:for-each>
    <xsl:for-each select="./description"><xsl:apply-templates select="." mode="description"/></xsl:for-each>
  </DIV>
</xsl:template>

<xsl:template match="button[@icon and $dhtml]|button[@style='link' and $dhtml]" mode="core">
  <A CLASS="button">
    <xsl:if test="not(@disabled)"><xsl:attribute name="HREF">javascript:setVarById('<xsl:value-of select="./boolean/@id"/>','true',true)</xsl:attribute></xsl:if>
    <xsl:if test="@disabled"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
   	<xsl:if test="@icon"><IMG class="icon" SRC="{@icon}" /></xsl:if>
	<INPUT TYPE="HIDDEN" ID="{./boolean/@id}" NAME="{./boolean/@id}" VALUE="{./boolean/@value}" />
    <xsl:value-of select="@caption" />
  </A>
</xsl:template>

<!-- Switch -->

<xsl:template match="button[@type='switch']">
  <NOBR class="button">		
    <xsl:apply-templates select="." mode="core"/>
    <SPAN onclick="">
      <xsl:if test="not(@disabled)"><xsl:attribute name="onclick">toggleCheckbox('<xsl:value-of select="./boolean/@id"/>',<xsl:value-of select="@immediate or false"/>)</xsl:attribute></xsl:if>
      <xsl:if test="@disabled"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
      <xsl:if test="@icon"><IMG class="icon" SRC="{@icon}" /></xsl:if>
      <xsl:value-of select="@caption"/>
	</SPAN>
  </NOBR>
  <xsl:for-each select="./error"><xsl:apply-templates select="." mode="popup"/></xsl:for-each>
  <xsl:for-each select="./description"><xsl:apply-templates select="." mode="description"/></xsl:for-each>
</xsl:template>

<xsl:template match="button[@type='switch']" mode="core">
  <INPUT TYPE="HIDDEN" NAME="declare:{./boolean/@id}" VALUE="" />
  <INPUT TYPE="CHECKBOX" ID="{./boolean/@id}" NAME="{./boolean/@id}">
    <xsl:if test="@disabled='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
    <xsl:if test="@immediate='true'"><xsl:attribute name="onclick">millstone.submit()</xsl:attribute></xsl:if>
    <xsl:if test="./boolean/@value='true'"><xsl:attribute name="CHECKED">true</xsl:attribute></xsl:if>
    <xsl:if test="@readonly='true'"><xsl:attribute name="READONLY">true</xsl:attribute></xsl:if>
   </INPUT>
</xsl:template>

</xsl:stylesheet>

