<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
    xmlns="http://www.w3.org/TR/WD-html-in-xml/DTD/xhtml1-strict.dtd">

<xsl:template match="component">
	    <xsl:for-each select="*"><xsl:apply-templates select="."/></xsl:for-each>
</xsl:template>

</xsl:stylesheet>

