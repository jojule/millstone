<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="module"/>

<xsl:output method="text" indent="no" />

<xsl:template match="changelog/entry">
  <xsl:text>&#x0A;</xsl:text>
  - <xsl:value-of select="./msg"/>
</xsl:template>

<xsl:template match="changelog">
  <xsl:value-of select="$module" />:
  <xsl:apply-templates select="child::entry" />
</xsl:template>

</xsl:stylesheet>
