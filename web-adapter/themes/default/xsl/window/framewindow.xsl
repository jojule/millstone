<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">


<xsl:template match="framewindow">
  <HTML>
    <HEAD>
      <xsl:call-template name="window-head"/>
    </HEAD>
    <xsl:apply-templates select="frameset"/>
  </HTML>
</xsl:template>

<xsl:template match="frameset">
    <FRAMESET>
    	<xsl:if test="@rows">
    		<xsl:attribute name="ROWS">
    			<xsl:value-of select="@rows"/>
    		</xsl:attribute>
    	</xsl:if>
    	<xsl:if test="@cols">
    		<xsl:attribute name="COLS">
    			<xsl:value-of select="@cols"/>
    		</xsl:attribute>
    	</xsl:if>
        <xsl:apply-templates/>
    </FRAMESET>
</xsl:template>

<xsl:template match="frame">
    <FRAME SRC="{@src}" NAME="{@name}" />
</xsl:template>

</xsl:stylesheet>

