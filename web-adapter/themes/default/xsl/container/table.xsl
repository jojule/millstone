<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- Table core. Abstract component template prints caption, icon, ... -->
<xsl:template match="table" mode="core">

  <!-- Construc CSS class name -->
  <xsl:variable name="class">table<xsl:if test="(./@style)  and (string-length(./@style) &gt; 0)">-<xsl:value-of select="./@style"/></xsl:if></xsl:variable>

  <xsl:variable name="actionlistid"><xsl:value-of select="generate-id(./actions)"/></xsl:variable>

  <TABLE CELLSPACING="0" CELLPADDING="0" BORDER="0" WIDTH="{$maxtablewidth}" CLASS="{$class}">
    <xsl:if test="@colheaders='true'">
      <xsl:call-template name="table-column-header">
        <xsl:with-param name="class"><xsl:value-of select="$class"/></xsl:with-param>      
      </xsl:call-template>
    </xsl:if>
    <xsl:for-each select="rows/tr">
      <xsl:call-template name="table-row">
        <xsl:with-param name="class"><xsl:value-of select="$class"/></xsl:with-param>
        <xsl:with-param name="actionlistid"><xsl:value-of select="$actionlistid"/></xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>
  </TABLE>
  <xsl:call-template name="table-cursor">
    <xsl:with-param name="class"><xsl:value-of select="$class"/></xsl:with-param>
  </xsl:call-template>

  <!-- Selection variable -->
  <xsl:if test="$dhtml and (@selectmode='single' or @selectmode='multi')">
    <xsl:for-each select="./array[@name='selected']">
      <INPUT TYPE="HIDDEN" NAME="declare:{@id}"/>
      <INPUT TYPE="HIDDEN" ID="{@id}" NAME="array:{@id}">
        <xsl:attribute name="VALUE">
          <xsl:for-each select="ai">
            <xsl:value-of select="text()"/>
            <xsl:if test="following-sibling::*">,</xsl:if>  
          </xsl:for-each>
        </xsl:attribute>
      </INPUT>
    </xsl:for-each>
  </xsl:if>
</xsl:template>


<!-- Table column headers -->

<xsl:template name="table-column-header">
  <xsl:param name="class" />
  <TR>

	<!-- Selection column -->
    <xsl:if test="not($dhtml) and (@selectmode='multi' or @selectmode='single')">
      <TD CLASS="{$class}-column-header"></TD>
    </xsl:if>
    
    <!-- Row headings column -->
    <xsl:if test="@rowheaders='true'"><TD CLASS="{$class}-row-header"></TD></xsl:if>
    
    <!-- Column headers -->
	<xsl:for-each select="cols/ch">
      <TD CLASS="{$class}-column-header">
        <xsl:if test="@icon"><xsl:value-of select="@icon"/></xsl:if>
        <xsl:value-of select="@caption"/>
      </TD>
    </xsl:for-each>
    
  </TR>
</xsl:template>


<!-- Table row -->

<xsl:template name="table-row">
  <xsl:param name="class" />
  <xsl:param name="actionlistid" />

  <TR>
    <xsl:attribute name="CLASS">
      <xsl:choose>
        <xsl:when test="@selected='true' and $dhtml"><xsl:value-of select="$class"/>-selected</xsl:when>
        <xsl:otherwise><xsl:value-of select="$class"/></xsl:otherwise>
	  </xsl:choose>
	</xsl:attribute>

    <xsl:call-template name="table-row-header">
      <xsl:with-param name="class"><xsl:value-of select="$class"/></xsl:with-param>
    </xsl:call-template>

    <!-- Cells -->
    <xsl:for-each select="td/*">
      <TD CLASS="{$class}">

        <!-- Cell alignment -->
        <xsl:variable name="thispos" select="position()" />
        <xsl:for-each select="../../../cols/ch">
          <xsl:if test="position() = $thispos">
            <xsl:choose>
              <xsl:when test="@align = 'c'"><xsl:attribute name="ALIGN">CENTER</xsl:attribute></xsl:when>
              <xsl:when test="@align = 'e'"><xsl:attribute name="ALIGN">RIGHT</xsl:attribute></xsl:when>
            </xsl:choose>
          </xsl:if>
        </xsl:for-each>
        
        <!-- Component caption, icon, errors, descriptions -->
        <xsl:if test="@disabled='true'"><xsl:attribute name="DISABLED">true</xsl:attribute></xsl:if>
        <xsl:if test="@icon"><IMG SRC="{@icon}"/></xsl:if>
        <xsl:value-of select="@caption"/>
        <xsl:for-each select="./error"><xsl:apply-templates select="." mode="popup"/></xsl:for-each>
        <xsl:for-each select="./description"><xsl:apply-templates select="." mode="description"/></xsl:for-each>

        <xsl:apply-templates select="." mode="core"/>
      </TD>
    </xsl:for-each>

    <!-- Actions  -->
    <xsl:for-each select="al">
	  <TD>
	    <xsl:choose>
	      <xsl:when test="$dhtml">
            <xsl:apply-templates select=".">
              <xsl:with-param name="actionlistid" select="$actionlistid" />
            </xsl:apply-templates>   
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="." mode="inline">
              <xsl:with-param name="actionsvar" select="../../../actions" />
            </xsl:apply-templates>   
          </xsl:otherwise>
        </xsl:choose>
      </TD>
	</xsl:for-each>      
    
  </TR>
</xsl:template>



<!-- Table row header -->

<xsl:template name="table-row-header">
  <xsl:param name="class" />

  <!-- Selection -->
  <xsl:if test="(../../@selectmode='multi') or (../../@selectmode='single')">
    <xsl:variable name="selectedid" select="../../array[@name='selected']/@id"/>
 
    <xsl:choose>

      <!-- DHTML mode selection -->
      <xsl:when test="$dhtml">
          <xsl:variable name="trid"><xsl:value-of select="$selectedid"/>_<xsl:value-of select="@key"/></xsl:variable>  
          <xsl:attribute name="ID"><xsl:value-of select="$trid"/></xsl:attribute>
          <xsl:attribute name="onclick">tableSelClick('<xsl:value-of select="$selectedid"/>','<xsl:value-of select="@key"/>','<xsl:value-of select="../../@immediate"/>','<xsl:value-of select="../../@selectmode"/>')</xsl:attribute>
      </xsl:when>

      <!-- Normal HTML mode selection -->
      <xsl:otherwise>

        <TD CLASS="{$class}-select">

          <!-- Radiobutton or checkbox for selection -->
          <INPUT NAME="{$selectedid}" VALUE="{@key}" CLASS="{$class}-select">
            <xsl:attribute name="TYPE">
              <xsl:choose>
                <xsl:when test="../../@selectmode='multi'">CHECKBOX</xsl:when>
                <xsl:otherwise>RADIO</xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>
            <xsl:if test="@selected='true'">
              <xsl:attribute name="CHECKED">CHECKED</xsl:attribute>
            </xsl:if>
            <xsl:if test="../../@readonly='true'">
              <xsl:attribute name="DISABLED">true</xsl:attribute>
            </xsl:if>
          </INPUT>
        </TD>
      </xsl:otherwise>

    </xsl:choose>
  </xsl:if>

  <!-- Header -->
  <xsl:if test="../../@rowheaders='true'">
    <TD CLASS="{$class}-row-header">
      <xsl:if test="@icon"><xsl:value-of select="@icon"/></xsl:if>
      <xsl:value-of select="@caption"/>
    </TD>
  </xsl:if>
  
</xsl:template>


<!-- Table cursors -->

<xsl:template name="table-cursor">
  <xsl:param name="class" />
  <xsl:variable name="firstvisible" select="./integer[@name='firstvisible']/@value"/>
  <xsl:variable name="fid" select="./integer[@name='firstvisible']/@id"/>

  <!-- Only tables with pagelength can have cursors -->
  <xsl:if test="@pagelength">
    <TABLE CELLPADDING="0" BORDER="0" CELLSPACING="0" CLASS="{$class}-cursor" WIDTH="{$maxtablewidth}">
      <TR>

        <TD ALIGN="LEFT">
          <INPUT TYPE="IMAGE" BORDER="0" NAME="set:{$fid}=1">
            <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('icon/arrows/left-double.gif')"/></xsl:attribute>
          </INPUT>
                 
          <INPUT TYPE="IMAGE" BORDER="0">
            <xsl:attribute name="NAME">set:<xsl:value-of select="$fid"/>=<xsl:value-of select="$firstvisible - @pagelength"/></xsl:attribute>
            <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('icon/arrows/left.gif')"/></xsl:attribute>
          </INPUT>
        </TD>

        <TD ALIGN="CENTER">
          <xsl:value-of select="$firstvisible"/> - 
          <xsl:value-of select="$firstvisible + @pagelength - 1"/> / 
          <xsl:value-of select="@totalrows"/>
        </TD>

        <TD  ALIGN="RIGHT">
          <INPUT TYPE="IMAGE" BORDER="0">
            <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('icon/arrows/right.gif')"/></xsl:attribute>          
            <xsl:if test="(@pagelength + $firstvisible) &lt; @totalrows "> 
              <xsl:attribute name="NAME">set:<xsl:value-of select="$fid"/>=<xsl:value-of select="$firstvisible + @pagelength"/></xsl:attribute>
            </xsl:if>
          </INPUT>
          <INPUT TYPE="IMAGE" BORDER="0" NAME="set:{$fid}={@totalrows - @pagelength + 1}">
            <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('icon/arrows/right-double.gif')"/></xsl:attribute>          
          </INPUT>
        </TD>
      </TR>
    </TABLE>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>

