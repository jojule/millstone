<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
    xmlns="http://www.w3.org/TR/WD-html-in-xml/DTD/xhtml1-strict.dtd">

<xsl:template match="panel">
  <DIV>
    <xsl:attribute name="CLASS">
      <xsl:choose>
        <xsl:when test="@style='strong'">panel-strong</xsl:when>
        <xsl:when test="@style='light'">panel-light</xsl:when>
        <xsl:otherwise>panel</xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>    
    <xsl:if test="(@caption)|(@icon)|(./description)|(./error)">
      <xsl:if test="@disabled='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
      <NOBR>
        <xsl:attribute name="CLASS">
          <xsl:choose>
            <xsl:when test="@style='strong'">panel-strong</xsl:when>
            <xsl:when test="@style='light'">panel-light</xsl:when>
            <xsl:otherwise>panel</xsl:otherwise>
          </xsl:choose>
      </xsl:attribute>
        <xsl:if test="@icon"><IMG SRC="{@icon}"/></xsl:if>
        <xsl:value-of select="@caption"/>
      </NOBR>
      <xsl:for-each select="./error"><xsl:apply-templates select="." mode="popup"/></xsl:for-each>
      <xsl:for-each select="./description"><xsl:apply-templates select="." mode="description"/></xsl:for-each>
	  <BR />
    </xsl:if>    
    <DIV>
      <xsl:attribute name="CLASS">
        <xsl:choose>
          <xsl:when test="@style='strong'">panel-body-strong</xsl:when>
          <xsl:when test="@style='light'">panel-body-light</xsl:when>
          <xsl:otherwise>panel-body</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="STYLE">
        <xsl:if test="./integer[@name='height']/@value &gt; 0">height: <xsl:value-of select="./integer[@name='height']/@value"/>;</xsl:if>
        <xsl:if test="./integer[@name='width']/@value &gt; 0">width: <xsl:value-of select="./integer[@name='width']/@value"/>;</xsl:if>
      </xsl:attribute>
      <xsl:apply-templates/>
    </DIV>
  </DIV>
</xsl:template>

<xsl:template match="panel" mode="core">
  <DIV CLASS="panel">
    <xsl:if test="@disabled='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
    <xsl:apply-templates/>
  </DIV>
</xsl:template>

</xsl:stylesheet>

