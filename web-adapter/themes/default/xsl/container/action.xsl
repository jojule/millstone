<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- Do not output text by default in popups mode -->
<xsl:template match="text()" mode="popup"></xsl:template>

<xsl:template match="actions" mode="popup">
  <xsl:variable name="actionlistid"><xsl:value-of select="generate-id(.)" /></xsl:variable>
  <xsl:variable name="actionvariableid"><xsl:value-of select="./string[@name='action']/@id"/></xsl:variable>

  <DIV ID="{$actionlistid}_POPUP" 
       CLASS="action-popup" 
       STYLE="display:none">

    <INPUT ID="{$actionvariableid}" NAME="{$actionvariableid}" TYPE="HIDDEN" />
    <INPUT ID="{$actionlistid}_ACTIVE_ITEM" TYPE="HIDDEN" NAME="{$actionlistid}_ACTIVE_ITEM" />
       
    <xsl:for-each select="./action">     
      <DIV ID="{$actionlistid}_{@key}" 
           CLASS="action-item"
           ONMOUSEOVER="this.className = toHighlightClassName(this.className);"
           ONMOUSEOUT="this.className = toUnselectedClassName(this.className);"
           ONCLICK="fireAction('{$actionlistid}','{$actionvariableid}','{@key}');">
        <NOBR><xsl:value-of select="@caption" /></NOBR>
 	  </DIV>
 	</xsl:for-each> 	
  </DIV>
</xsl:template>


<xsl:template match="al">
  <xsl:param name="actionlistid" />

  <xsl:variable name="itemid"><xsl:value-of select="../@key"/></xsl:variable>
  <xsl:variable name="activeactions"><xsl:for-each select="./ak"><xsl:value-of select="."/>,</xsl:for-each></xsl:variable>
  
  <xsl:if test="./ak">
    <IMG SRC="{wa:resource('img/popup-button.gif')}" CLASS="action" BORDER="0">
      <xsl:attribute name="onclick">actionPopup(event,'<xsl:value-of select="$actionlistid"/>','<xsl:value-of select="$itemid"/>','<xsl:value-of select="$activeactions"/>')</xsl:attribute>
    </IMG>
  </xsl:if>
</xsl:template>


</xsl:stylesheet>

