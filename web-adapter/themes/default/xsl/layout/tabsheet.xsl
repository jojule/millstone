<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="tabsheet" mode="core">
  <xsl:variable name="selectid" select="string[@name='selected']/@id" />
  <INPUT TYPE="hidden" NAME="{$selectid}" VALUE="{string[@id=$selectid]/value}"/>
  <TABLE CELLPADDING="0" CELLSPACING="0" BORDER="0">
    <TR>
      <TD>
	    <TABLE CELLPADDING="0" CELLSPACING="0" BORDER="0">
	      <TR>
	        <xsl:for-each select="tabs/tab">
	          <TD>
	            <xsl:attribute name="CLASS"><xsl:choose><xsl:when test="@selected='true'">tab-l-s</xsl:when><xsl:otherwise>tab-l</xsl:otherwise></xsl:choose></xsl:attribute>
	          </TD>
	          <TD onClick="document.millstone.{$selectid}.value='{@key}';document.millstone.submit();">
	            <xsl:attribute name="CLASS"><xsl:choose><xsl:when test="@selected='true'">tab-c-s</xsl:when><xsl:otherwise>tab-c</xsl:otherwise></xsl:choose></xsl:attribute>
			    <NOBR>
			      <xsl:if test="@icon"><IMG SRC="{@icon}"/></xsl:if>
				  <xsl:value-of select="@caption"/>
				</NOBR>
	          </TD>
	          <TD>
	            <xsl:attribute name="CLASS"><xsl:choose><xsl:when test="@selected='true'">tab-r-s</xsl:when><xsl:otherwise>tab-r</xsl:otherwise></xsl:choose></xsl:attribute>
	          </TD>
	        </xsl:for-each>
	      </TR>
	    </TABLE>
	  </TD>
	</TR>
    <xsl:for-each select="./tabs/tab[@selected='true']">
 	  <TR>
	    <TD CLASS="tabsheet">
	       <xsl:apply-templates select="."/>
	    </TD>
	  </TR>
	</xsl:for-each>
  </TABLE>

</xsl:template>

</xsl:stylesheet>

