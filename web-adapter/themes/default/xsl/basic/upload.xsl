<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    
<xsl:template match="upload" mode="core">
  <INPUT TYPE="file" NAME="{./uploadstream/@id}" VALUE="{./uploadstream}"> 
    <xsl:if test="@modified='true'"><xsl:attribute name="CLASS">modified</xsl:attribute></xsl:if>
    <xsl:if test="@readonly='true'"><xsl:attribute name="READONLY">true</xsl:attribute></xsl:if>
    <xsl:if test="not(@immediate='true')"><xsl:attribute name="onchange">this.CLASS='modified'</xsl:attribute></xsl:if>
  </INPUT>
  <xsl:if test="@immediate='true'">
    <INPUT TYPE="submit" />
  </xsl:if>
</xsl:template>

</xsl:stylesheet>
