<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
    xmlns="http://www.w3.org/TR/WD-html-in-xml/DTD/xhtml1-strict.dtd">


<!-- STYLE: default -->

<xsl:template match="orderedlayout[@orientation='flow']" mode="core">
  <xsl:for-each select="*">
    <xsl:apply-templates select="."/>
  </xsl:for-each>
</xsl:template>

<xsl:template match="orderedlayout[@orientation='horizontal']" mode="core">
  <xsl:if test="child::*">
    <TABLE BORDER="0" CELLPADDING="0" CELLSPACING="0">
      <TR>
        <xsl:for-each select="*">
          <TD><xsl:apply-templates select="."/></TD>
        </xsl:for-each>
      </TR>
    </TABLE>
  </xsl:if>
</xsl:template>

<xsl:template match="orderedlayout" mode="core">
  <xsl:if test="child::*">
    <TABLE BORDER="0" CELLPADDING="0" CELLSPACING="0">
      <xsl:for-each select="*">
        <TR><TD><xsl:apply-templates select="."/></TD></TR>
      </xsl:for-each>
    </TABLE>
  </xsl:if>
</xsl:template>


<!-- STYLE: form -->

<xsl:template match="orderedlayout[@style='form']" mode="core">
  <xsl:if test="./child::*">
    <TABLE BORDER="0" WIDTH="{$maxtablewidth}">
      <xsl:for-each select="*">
        <TR>
          <TD>
		    <xsl:if test="not(local-name()!='button' and @type='switch') and local-name()!='link' and @caption">
		      <NOBR CLASS="caption">
  		        <xsl:if test="@icon"><IMG SRC="{@icon}" /></xsl:if>
  	            <xsl:value-of select="@caption"/>
		      </NOBR>
		    </xsl:if>
		    <xsl:if test="not(@caption) and @icon"><IMG SRC="{@icon}" /></xsl:if>
          </TD>
          <TD><xsl:apply-templates select="." mode="core"/></TD>
        </TR>
      </xsl:for-each>
    </TABLE>
  </xsl:if>
</xsl:template>


<xsl:template match="orderedlayout[(@orientation='flow') and (@style='form')]" mode="core">
  <xsl:for-each select="*">
    <TABLE BORDER="0" WIDTH="{$maxtablewidth}">
      <TR CLASS="flowform-caption">
        <TD>
	      <xsl:if test="not(local-name()!='button' and @type='switch') and local-name()!='link' and @caption">
		    <NOBR CLASS="caption">
  		      <xsl:if test="@icon"><IMG SRC="{@icon}" /></xsl:if>
	          <xsl:value-of select="@caption"/>
		    </NOBR>
		  </xsl:if>
		  <xsl:if test="not(@caption) and @icon"><IMG SRC="{@icon}" /></xsl:if>
		  <xsl:if test="not(@caption) and not(@icon)"><IMG SRC="" WIDTH="0" HEIGHT="0" /></xsl:if>
        </TD>
      </TR>
      <TR>
        <TD><xsl:apply-templates select="." mode="core"/></TD>
      </TR>
    </TABLE>
  </xsl:for-each>
</xsl:template>

<xsl:template match="orderedlayout[(@orientation='horizontal') and (@style='form')]" mode="core">
  <xsl:if test="./child::*">
    <TABLE BORDER="0">
      <TR>
        <xsl:for-each select="*">
          <TD>
		    <xsl:if test="not(local-name()!='button' and @type='switch') and local-name()!='link' and @caption">
		      <NOBR CLASS="caption">
  		        <xsl:if test="@icon"><IMG SRC="{@icon}" /></xsl:if>
		        <xsl:value-of select="@caption"/>
		      </NOBR>
		    </xsl:if>
		    <xsl:if test="not(@caption) and @icon"><IMG SRC="{@icon}" /></xsl:if>
          </TD>
        </xsl:for-each>
      </TR>
      <TR>
        <xsl:for-each select="*">
	        <TD><xsl:apply-templates select="." mode="core"/></TD>
        </xsl:for-each>
      </TR>
    </TABLE>
  </xsl:if>
</xsl:template>


</xsl:stylesheet>