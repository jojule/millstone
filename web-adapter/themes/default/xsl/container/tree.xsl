<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
    xmlns="http://www.w3.org/TR/WD-html-in-xml/DTD/xhtml1-strict.dtd">        


<xsl:template match="tree[@style='menu']" mode="core">

  <xsl:variable name="class">tree<xsl:if test="(./@style) and (string-length(./@style) &gt; 0)">-<xsl:value-of select="./@style"/></xsl:if></xsl:variable>

  <DIV CLASS="{$class}-body">

    <!-- Create dummy images for expanded/collapsed state -->
    <!-- These are used in javascript to change the images -->
    <IMG ID="{array[@name='collapse']/@id}_IMG" STYLE="display:none;" SRC="{wa:resource('img/tree/menu/expanded.gif')}"/>  
    <IMG ID="{array[@name='expand']/@id}_IMG" STYLE="display:none;" SRC="{wa:resource('img/tree/menu/collapsed.gif')}"/>  

    <!-- Actions -->
    <xsl:variable name="actionlistid"><xsl:value-of select="generate-id(./actions)"/></xsl:variable>
  
    <xsl:for-each select="node|leaf">
      <xsl:apply-templates select="." mode="menu">
        <xsl:with-param name="level">1</xsl:with-param>
        <xsl:with-param name="selectable"><xsl:value-of select="(../@selectable) and not(../@readonly)"/></xsl:with-param>
        <xsl:with-param name="selectmode"><xsl:value-of select="../@selectmode"/></xsl:with-param>
        <xsl:with-param name="nodeselect"><xsl:value-of select="../@nodeselect"/></xsl:with-param>
        <xsl:with-param name="selectedid"><xsl:value-of select="../array[@name='selected']/@id"/></xsl:with-param>
        <xsl:with-param name="expandid"><xsl:value-of select="../array[@name='expand']/@id"/></xsl:with-param>
        <xsl:with-param name="collapseid"><xsl:value-of select="../array[@name='collapse']/@id"/></xsl:with-param>
        <xsl:with-param name="class"><xsl:value-of select="$class"/></xsl:with-param>
        <xsl:with-param name="immediate"><xsl:value-of select="../@immediate"/></xsl:with-param>
        <xsl:with-param name="actionlistid"><xsl:value-of select="$actionlistid"/></xsl:with-param>
      </xsl:apply-templates>
    </xsl:for-each>
  </DIV>

  <!-- Output variables -->  
  <xsl:call-template name="tree-variables" />  	
</xsl:template>

<xsl:template match="tree" mode="core">

  <xsl:variable name="class">tree<xsl:if test="(./@style) and (string-length(./@style) &gt; 0)">-<xsl:value-of select="./@style"/></xsl:if></xsl:variable>

  <!-- Create dummy images for expanded/collapsed state -->
  <!-- These are used in javascript to change the images -->
  <IMG ID="{array[@name='collapse']/@id}_IMG" STYLE="display:none;" SRC="{wa:resource('img/tree/expanded.gif')}"/>  
  <IMG ID="{array[@name='expand']/@id}_IMG" STYLE="display:none;" SRC="{wa:resource('img/tree/collapsed.gif')}"/>  

  <!-- Actions -->
  <xsl:variable name="actionlistid"><xsl:value-of select="generate-id(./actions)"/></xsl:variable>
  
  <xsl:for-each select="node|leaf">
    <xsl:apply-templates select="." mode="tree">
      <xsl:with-param name="selectable"><xsl:value-of select="(../@selectable) and not(../@readonly)"/></xsl:with-param>
      <xsl:with-param name="selectmode"><xsl:value-of select="../@selectmode"/></xsl:with-param>
      <xsl:with-param name="nodeselect"><xsl:value-of select="../@nodeselect"/></xsl:with-param>
      <xsl:with-param name="selectedid"><xsl:value-of select="../array[@name='selected']/@id"/></xsl:with-param>
      <xsl:with-param name="expandid"><xsl:value-of select="../array[@name='expand']/@id"/></xsl:with-param>
      <xsl:with-param name="collapseid"><xsl:value-of select="../array[@name='collapse']/@id"/></xsl:with-param>
      <xsl:with-param name="class"><xsl:value-of select="$class"/></xsl:with-param>
      <xsl:with-param name="immediate"><xsl:value-of select="../@immediate"/></xsl:with-param>
      <xsl:with-param name="actionlistid"><xsl:value-of select="$actionlistid"/></xsl:with-param>
    </xsl:apply-templates>
  </xsl:for-each>


  <!-- Output variables -->  
  <xsl:call-template name="tree-variables" />  
	
</xsl:template>

<!-- Tree node template -->

<xsl:template match="node|leaf" mode="tree">
<xsl:param name="selectable"/>
<xsl:param name="selectmode"/>
<xsl:param name="nodeselect"/>
<xsl:param name="selectedid"/>
<xsl:param name="expandid"/>
<xsl:param name="collapseid"/>
<xsl:param name="class"/>
<xsl:param name="immediate"/>
<xsl:param name="actionlistid"/>

<xsl:variable name="isLastNode" select="not(following-sibling::node | following-sibling::leaf)" />
<xsl:variable name="isLeafNode" select="local-name()='leaf'" />
<xsl:variable name="isSelectable" select="($selectable)  and (($selectmode='multi') or ($selectmode='single'))" />	
<xsl:variable name="childid"><xsl:value-of select="$expandid"/>_<xsl:value-of select="@key"/></xsl:variable>

<TABLE BORDER="0" CELLPADDING="0" CELLSPACING="0">
  <TR>
    <TD>     
      <!-- Check if node has following siblings -->
		<xsl:if test="not($isLastNode)">
			<xsl:attribute name="BACKGROUND"><xsl:value-of select="wa:resource('img/tree/dots.gif')"/> 
			</xsl:attribute>
		</xsl:if>

		<xsl:choose>
		
		  <!-- Leaf nodes -->
          <xsl:when test="$isLeafNode">
            <IMG BORDER="0" CLASS="{$class}">
              <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('img/tree/leaf.gif')"/></xsl:attribute>
            </IMG>
          </xsl:when>
          
          
          <!-- Internal nodes -->
          <xsl:otherwise>
            <xsl:attribute name="ONCLICK">treeExpClick('<xsl:value-of select="$expandid"/>','<xsl:value-of select="$collapseid"/>','<xsl:value-of select="@key"/>','<xsl:value-of select="$immediate"/>')</xsl:attribute>
            
            <IMG ID="img{$childid}" BORDER="0" CLASS="{$class}">
            <xsl:choose>
              <xsl:when test="@expanded">
                <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('img/tree/expanded.gif')"/></xsl:attribute>
              </xsl:when>
              <xsl:otherwise>
                <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('img/tree/collapsed.gif')"/></xsl:attribute>
              </xsl:otherwise>
            </xsl:choose>
            </IMG>
          </xsl:otherwise>
        </xsl:choose>

    </TD>

    <!-- Icon and caption cell -->
    <TD>          
      <xsl:apply-templates select="./al">
   	    <xsl:with-param name="actionlistid"><xsl:value-of select="$actionlistid" /></xsl:with-param>
      </xsl:apply-templates>   
      <A>      
        <!-- Current selection state -->
        <xsl:attribute name="CLASS"><xsl:value-of select="$class" />-node<xsl:if test="@selected">-selected</xsl:if></xsl:attribute>    

        <!-- Selection -->
        <xsl:if test="$isSelectable">		
          <xsl:variable name="selid"><xsl:value-of select="$selectedid"/>_<xsl:value-of select="@key"/></xsl:variable>  
          <xsl:attribute name="ID"><xsl:value-of select="$selid"/></xsl:attribute>
          <xsl:attribute name="HREF">javascript:treeSelClick('<xsl:value-of select="$selectedid"/>','<xsl:value-of select="@key"/>','<xsl:value-of select="$immediate"/>','<xsl:value-of select="$selectmode"/>')</xsl:attribute>            
        </xsl:if>

        <NOBR>
          <xsl:if test="@icon"><IMG class="icon" SRC="{@icon}" BORDER="0" /></xsl:if>
          <xsl:value-of select="@caption" />
        </NOBR>
      </A>              
	</TD>	
  </TR>
  <xsl:if test="node|leaf">
    <TR>
      <xsl:attribute name="ID"><xsl:value-of select="$childid"/></xsl:attribute>    
      <TD>
        <xsl:if test="not(@expanded='true')">
          <xsl:attribute name="STYLE">display:none;</xsl:attribute>
        </xsl:if>
        <!-- Following siblings -->
        <xsl:if test="not($isLastNode)">
          <xsl:attribute name="BACKGROUND"><xsl:value-of select="wa:resource('img/tree/dots.gif')"/></xsl:attribute>
        </xsl:if>
      </TD>
      <TD>
		<xsl:apply-templates select="leaf|node" mode="tree">
			<xsl:with-param name="selectable"><xsl:value-of select="$selectable"/></xsl:with-param>
			<xsl:with-param name="selectmode"><xsl:value-of select="$selectmode"/></xsl:with-param>
			<xsl:with-param name="nodeselect"><xsl:value-of select="$nodeselect"/></xsl:with-param>
			<xsl:with-param name="selectedid"><xsl:value-of select="$selectedid"/></xsl:with-param>
			<xsl:with-param name="expandid"><xsl:value-of select="$expandid"/></xsl:with-param>
			<xsl:with-param name="collapseid"><xsl:value-of select="$collapseid"/></xsl:with-param>
			<xsl:with-param name="class"><xsl:value-of select="$class"/></xsl:with-param>
			<xsl:with-param name="immediate"><xsl:value-of select="$immediate"/></xsl:with-param>
			<xsl:with-param name="actionlistid"><xsl:value-of select="$actionlistid"/></xsl:with-param>
		</xsl:apply-templates>
      </TD>
    </TR>
  </xsl:if>
</TABLE>
</xsl:template>



<!-- Menu node template -->

<xsl:template match="node|leaf" mode="menu">
<xsl:param name="level"/>
<xsl:param name="selectable"/>
<xsl:param name="selectmode"/>
<xsl:param name="nodeselect"/>
<xsl:param name="selectedid"/>
<xsl:param name="expandid"/>
<xsl:param name="collapseid"/>
<xsl:param name="class"/>
<xsl:param name="immediate"/>
<xsl:param name="actionlistid"/>

<xsl:variable name="isLastNode" select="not(following-sibling::node | following-sibling::leaf)" />
<xsl:variable name="isLeafNode" select="local-name()='leaf'" />
<xsl:variable name="isSelectable" select="($selectable)  and (($selectmode='multi') or ($selectmode='single'))" />	
<xsl:variable name="childid"><xsl:value-of select="$expandid"/>_<xsl:value-of select="@key"/></xsl:variable>
  <DIV>
    <!-- Current selection state -->
    <xsl:attribute name="CLASS"><xsl:value-of select="$class" />-<xsl:value-of select="$level" /><xsl:if test="@selected">-selected</xsl:if></xsl:attribute>    

    <!-- Selection -->
    <xsl:if test="$isSelectable and $dhtml">
      <xsl:variable name="selid"><xsl:value-of select="$selectedid"/>_<xsl:value-of select="@key"/></xsl:variable>  
      <xsl:attribute name="ID"><xsl:value-of select="$selid"/></xsl:attribute>
      <xsl:attribute name="ONCLICK">javascript:treeSelClick('<xsl:value-of select="$selectedid"/>','<xsl:value-of select="@key"/>','<xsl:value-of select="$immediate"/>','<xsl:value-of select="$selectmode"/>')</xsl:attribute>
    </xsl:if>

    <NOBR>

	<!-- Menu indent -->
    <xsl:choose>
      <!-- Leaf nodes -->
      <xsl:when test="$isLeafNode">
        <IMG BORDER="0" CLASS="{$class}-exp">
          <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('img/tree/menu/leaf.gif')"/></xsl:attribute>
        </IMG>
      </xsl:when>
          
      <!-- Internal nodes -->
      <xsl:otherwise>
            
        <IMG ID="img{$childid}" BORDER="0" CLASS="{$class}-exp">
          <xsl:if test="$dhtml">
            <xsl:attribute name="ONCLICK">treeExpClick('<xsl:value-of select="$expandid"/>','<xsl:value-of select="$collapseid"/>','<xsl:value-of select="@key"/>','<xsl:value-of select="$immediate"/>')</xsl:attribute>
          </xsl:if>
                  
          <xsl:choose>
            <xsl:when test="@expanded">
              <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('img/tree/menu/expanded.gif')"/></xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute name="SRC"><xsl:value-of select="wa:resource('img/tree/menu/collapsed.gif')"/></xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
        </IMG>
      </xsl:otherwise>
    </xsl:choose>

    <xsl:apply-templates select="./al">
      <xsl:with-param name="actionlistid"><xsl:value-of select="$actionlistid" /></xsl:with-param>
    </xsl:apply-templates>   

    <!-- Icon and caption -->
      <xsl:choose>      
        <xsl:when test="not($dhtml)">
          <INPUT TYPE="submit" name="set:{$selectedid}={@key}" value="{@caption}" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="@icon"><IMG class="icon" SRC="{@icon}" /></xsl:if>
         <xsl:value-of select="@caption" />
        </xsl:otherwise>
      </xsl:choose>
    </NOBR>
  </DIV>
  <xsl:if test="node|leaf">
    <DIV CLASS="{$class}-{$level}-child">
        <xsl:attribute name="ID"><xsl:value-of select="$childid"/></xsl:attribute>    
        <xsl:apply-templates select="leaf|node" mode="menu">
          <xsl:with-param name="level"><xsl:value-of select="$level + 1"/></xsl:with-param>
          <xsl:with-param name="selectable"><xsl:value-of select="$selectable"/></xsl:with-param>
          <xsl:with-param name="selectmode"><xsl:value-of select="$selectmode"/></xsl:with-param>
          <xsl:with-param name="nodeselect"><xsl:value-of select="$nodeselect"/></xsl:with-param>
          <xsl:with-param name="selectedid"><xsl:value-of select="$selectedid"/></xsl:with-param>
          <xsl:with-param name="expandid"><xsl:value-of select="$expandid"/></xsl:with-param>
          <xsl:with-param name="collapseid"><xsl:value-of select="$collapseid"/></xsl:with-param>
          <xsl:with-param name="class"><xsl:value-of select="$class"/></xsl:with-param>
          <xsl:with-param name="immediate"><xsl:value-of select="$immediate"/></xsl:with-param>
          <xsl:with-param name="actionlistid"><xsl:value-of select="$actionlistid"/></xsl:with-param>
        </xsl:apply-templates>
    </DIV>
  </xsl:if>
</xsl:template>


<!-- Tree variables -->

<xsl:template name="tree-variables">	
  <!-- Selection variable -->
  <xsl:if test="(@selectmode='single' or @selectmode='multi')">
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
  
  <!-- Expand variable -->
  <xsl:for-each select="./array[@name='expand']">    
    <INPUT TYPE="HIDDEN" ID="{@id}" NAME="array:{@id}" />
  </xsl:for-each>  

  <!-- Collapse variable -->
  <xsl:for-each select="./array[@name='collapse']">
    <INPUT TYPE="HIDDEN" ID="{@id}" NAME="array:{@id}" />
  </xsl:for-each>  

</xsl:template>

</xsl:stylesheet>

