<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="button" mode="core">
   <INPUT CLASS="button" TYPE="SUBMIT" ID="{./boolean/@id}" NAME="set:{./boolean/@id}=true" VALUE=" {@caption} ">
   	 <xsl:attribute name="CLASS">button<xsl:if test="string-length(./@style) &gt; 0">-<xsl:value-of select="./@style"/></xsl:if></xsl:attribute>    
	 <xsl:if test="@disabled='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
	 <xsl:if test="@readonly='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
   </INPUT>
   
  <!-- Set focus to field -->
  <xsl:if test="@focus='true' and $dhtml">
    <SCRIPT>document.getElementById('<xsl:value-of select="./boolean/@id"/>').focus()</SCRIPT>
  </xsl:if>
   
</xsl:template>

<xsl:template match="button">

  <!-- Core button -->
  <xsl:apply-templates select="." mode="core"/>
    
  <!-- descriptions and errors -->  
  <xsl:choose>
    <xsl:when test="$dhtml">
      <xsl:for-each select="./error"><xsl:apply-templates select="." mode="error"/></xsl:for-each>
      <xsl:for-each select="./description"><xsl:apply-templates select="." mode="description"/></xsl:for-each>
    </xsl:when>
    <xsl:otherwise>
      <xsl:if test="./error"><BR /><xsl:apply-templates select="./error" mode="inline"/></xsl:if>
      <xsl:if test="./description"><BR /><xsl:apply-templates select="./description" mode="inline"/></xsl:if>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- Link style -->

<xsl:template match="button[@icon and $dhtml]|button[@style='link' and $dhtml]">
  <DIV CLASS="button-link">
    <xsl:apply-templates select="." mode="core"/>
    <xsl:for-each select="./error"><xsl:apply-templates select="." mode="error"/></xsl:for-each>
    <xsl:for-each select="./description"><xsl:apply-templates select="." mode="description"/></xsl:for-each>
  </DIV>
</xsl:template>

<xsl:template match="button[@icon and $dhtml]|button[@style='link' and $dhtml]" mode="core">
  <A CLASS="button-link">
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
    <SPAN>
      <xsl:if test="not(@disabled) and $dhtml"><xsl:attribute name="ONCLICK">toggleCheckbox('<xsl:value-of select="./boolean/@id"/>',<xsl:value-of select="@immediate or false"/>)</xsl:attribute></xsl:if>
      <xsl:if test="@disabled"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
      <xsl:if test="@icon"><IMG class="icon" SRC="{@icon}" /></xsl:if>
      <xsl:value-of select="@caption"/>
	</SPAN>
  </NOBR>
  <xsl:choose>
    <xsl:when test="$dhtml">
      <xsl:for-each select="./error"><xsl:apply-templates select="." mode="error"/></xsl:for-each>
      <xsl:for-each select="./description"><xsl:apply-templates select="." mode="description"/></xsl:for-each>
    </xsl:when>
    <xsl:otherwise>
      <xsl:if test="./error"><BR /><xsl:apply-templates select="./error" mode="inline"/></xsl:if>
      <xsl:if test="./description"><BR /><xsl:apply-templates select="./description" mode="inline"/></xsl:if>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="button[@type='switch']" mode="core">
  <INPUT TYPE="HIDDEN" NAME="declare:{./boolean/@id}" VALUE="" />
  <INPUT TYPE="CHECKBOX" ID="{./boolean/@id}" NAME="{./boolean/@id}">
    <xsl:if test="@disabled='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
    <xsl:if test="@immediate='true' and $dhtml"><xsl:attribute name="onclick">millstone.submit()</xsl:attribute></xsl:if>
    <xsl:if test="./boolean/@value='true'"><xsl:attribute name="CHECKED">true</xsl:attribute></xsl:if>
    <xsl:if test="@readonly='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
   </INPUT>

  <!-- Set focus to field -->
  <xsl:if test="@focus='true' and $dhtml">
    <SCRIPT>document.getElementById('<xsl:value-of select="./boolean/@id"/>').focus()</SCRIPT>
  </xsl:if>
   
</xsl:template>

</xsl:stylesheet>

