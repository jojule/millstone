<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN"/>
	
	<xsl:template match="change">change: <xsl:apply-templates /></xsl:template>
	
	<xsl:template match="label">label: <xsl:apply-templates/></xsl:template>

	<xsl:template match="window">window: <xsl:apply-templates/></xsl:template>

</xsl:stylesheet>
