<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN"/>
	
	<xsl:template match="change"><xsl:apply-templates /></xsl:template>
	
	<xsl:template match="window"><xsl:apply-templates/></xsl:template>


<!-- Bold formatting -->
<xsl:template match="b"><b><xsl:apply-templates /></b></xsl:template>

<!-- Italic formatting -->
<xsl:template match="i"><i><xsl:apply-templates /></i></xsl:template>

<!-- Underline formatting -->
<xsl:template match="u"><u><xsl:apply-templates /></u></xsl:template>

<!-- Linebreak  -->
<xsl:template match="br"><br /></xsl:template>

<!-- Unordered list -->
<xsl:template match="ul"><ul><xsl:apply-templates /></ul></xsl:template>

<!-- List item -->
<xsl:template match="li"><li><xsl:apply-templates /></li></xsl:template>

<!-- Headers -->
<xsl:template match="h1"><h1><xsl:apply-templates /></h1></xsl:template>
<xsl:template match="h2"><h2><xsl:apply-templates /></h2></xsl:template>
<xsl:template match="h3"><h3><xsl:apply-templates /></h3></xsl:template>
<xsl:template match="h4"><h4><xsl:apply-templates /></h4></xsl:template>
<xsl:template match="h5"><h5><xsl:apply-templates /></h5></xsl:template>
<xsl:template match="h6"><h6><xsl:apply-templates /></h6></xsl:template>

<!-- Preformatted data -->
<xsl:template match="pre">
   <PRE><xsl:copy-of select="text()|*"/></PRE>
</xsl:template>

<!-- XML raw data (should be in some other namespace) -->
<xsl:template match="data">
	   <xsl:copy-of select="text()|*"/>
</xsl:template>

<!-- XML raw data with escape="false" -->
<xsl:template match="data[@escape='false']">
	   <xsl:value-of select="text()|*" disable-output-escaping="yes" />
</xsl:template>

<xsl:template match="label|datefield|embedded|textfield|upload|select|table|tree|customlayout|gridlayout|orderedlayout|tabsheet">
  <DIV ID='{@id}'>
    <xsl:if test="(@caption)|(@icon)|(./description)|(./error)">
      <xsl:if test="@disabled='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
      <NOBR CLASS="caption">
        <xsl:if test="@icon"><IMG SRC="{@icon}"/></xsl:if>
        <xsl:value-of select="@caption"/>
      </NOBR>
          <xsl:if test="./error"><BR /><xsl:apply-templates select="./error" mode="inline"/></xsl:if>
          <xsl:if test="./description"><BR /><xsl:apply-templates select="./description" mode="inline"/></xsl:if>
      <BR />
    </xsl:if>    
    <xsl:apply-templates select="." mode="core"/>    
  </DIV>
</xsl:template>


<!-- Grid layout component -->
<xsl:template match="gridlayout" mode="core">
  <table border="0">
    <!-- Table rows -->
    <xsl:for-each select="gr">
      <tr>
      <xsl:for-each select="gc">
        <xsl:variable name="colspan">
          <xsl:choose>
            <xsl:when test="@w">
              <xsl:value-of select="@w"/>
            </xsl:when>
            <xsl:otherwise>1</xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:variable name="rowspan">
          <xsl:choose>
            <xsl:when test="@h">
              <xsl:value-of select="@h"/>
            </xsl:when>
            <xsl:otherwise>1</xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <!-- Grid cells -->
            <td valign="top" align="left"><xsl:if test="$colspan &gt; 1">
                  <xsl:attribute name="colspan"><xsl:value-of select="$colspan"/></xsl:attribute>
                </xsl:if>
              <xsl:if test="$rowspan &gt; 1">
                <xsl:attribute name="rowspan"><xsl:value-of select="$rowspan"/></xsl:attribute>
              </xsl:if>
              <!-- apply component -->
              <xsl:apply-templates/>
             </td>
      </xsl:for-each>      
      </tr>
    </xsl:for-each>
  </table>
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




<xsl:template match="button">
  <xsl:choose>

    <!-- Link Style -->
    <xsl:when test="@style='link'">
      <A CLASS="button-link">
        <xsl:if test="@focusid"><xsl:attribute name="FOCUSID"><xsl:value-of select="@focusid"/></xsl:attribute></xsl:if>
        <xsl:if test="not(@disabled)"><xsl:attribute name="HREF">javascript:Millstone.setVarById('<xsl:value-of select="./boolean/@id"/>','true',true)</xsl:attribute></xsl:if>
        <xsl:if test="@disabled"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
        <xsl:if test="@icon"><IMG class="icon" SRC="{@icon}" /></xsl:if>
        <INPUT TYPE="HIDDEN" ID="{./boolean/@id}" NAME="{./boolean/@id}" VALUE="{./boolean/@value}" />
        <xsl:value-of select="@caption" />
      </A>
    </xsl:when>
    
    <!-- Normal Style -->
    <xsl:otherwise>
      <xsl:if test="@icon"><INPUT TYPE="image" src="{@icon}" NAME="set:{./boolean/@id}=true" VALUE=" {@caption} ">
   	    <xsl:if test="@disabled='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
        <xsl:if test="@readonly='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
        <xsl:if test="not(string-length(@caption) &gt; 0)"><xsl:attribute name="ID"><xsl:value-of select="./boolean/@id"/></xsl:attribute></xsl:if>
      </INPUT></xsl:if>
      <xsl:if test="string-length(@caption) &gt; 0">
      <INPUT CLASS="button" TYPE="BUTTON" ONCLICK="variableChange('{./boolean/@id}','true',true)" VALUE=" {@caption} ">
        <xsl:attribute name="CLASS">button<xsl:if test="string-length(./@style) &gt; 0">-<xsl:value-of select="./@style"/></xsl:if></xsl:attribute>    
   	    <xsl:if test="@disabled='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
        <xsl:if test="@readonly='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
     	<xsl:if test="@tabindex"><xsl:attribute name="tabindex"><xsl:value-of select="@tabindex"/></xsl:attribute></xsl:if>
        <xsl:if test="@focusid"><xsl:attribute name="FOCUSID"><xsl:value-of select="@focusid"/></xsl:attribute></xsl:if>
      </INPUT>
      </xsl:if>
   
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
