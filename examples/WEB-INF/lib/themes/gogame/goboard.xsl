<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- Template for drawing GO board component -->
<xsl:template xmlns:go="GO Sample Namespace" match="go:goboard">

	<!-- Helper variable for easily changing the variable -->
	<xsl:variable name="move">document.getElementById('<xsl:value-of select="go:string/@id"/>').value</xsl:variable>

	<!-- GO board -->
    <DIV STYLE="text-align: center;">

	<!-- Players -->
	<nobr><font size="+1"><b><xsl:value-of select="@blackname"/> (black)</b></font></nobr> 
	<br/> vs. <br/>
	<nobr><font size="+1"><b><xsl:value-of select="@whitename"/> (white)</b></font></nobr>
	<br/><br/>

    <xsl:value-of select="@moves"/> moves done<BR/>
	<TABLE CELLPADDING="0" CELLSPACING="0">
 		<xsl:for-each select="go:row">
		<TR>
			<xsl:for-each select="go:col">
				<TD>
				<IMG>

					<!-- Moving -->
					<xsl:if test="(@move) and not(../../@readonly='true')">
						<xsl:attribute name="onclick"><xsl:value-of select="$move"/> = '<xsl:value-of select="@move"/>'; void(document.millstone.submit())</xsl:attribute>
					</xsl:if>
					
					<!-- Image -->
					<xsl:choose>
						<!-- Stones -->
						<xsl:when test="@stone">
							<xsl:attribute name="SRC"><xsl:value-of select="wa:resource('images/')"/><xsl:value-of select="@stone"/>.gif</xsl:attribute>
						</xsl:when>				
						
						<!-- Corners -->
						<xsl:when test="not(preceding-sibling::go:col) and not(../following-sibling::go:row)">
							<xsl:attribute name="SRC"><xsl:value-of select="wa:resource('images/board-bottom-left.gif')"/></xsl:attribute>
						</xsl:when>
						<xsl:when test="not(following-sibling::go:col) and not(../following-sibling::go:row)">
							<xsl:attribute name="SRC"><xsl:value-of select="wa:resource('images/board-bottom-right.gif')"/></xsl:attribute>
						</xsl:when>
						<xsl:when test="not(preceding-sibling::go:col) and not(../preceding-sibling::go:row)">
							<xsl:attribute name="SRC"><xsl:value-of select="wa:resource('images/board-top-left.gif')"/></xsl:attribute>
						</xsl:when>
						<xsl:when test="not(following-sibling::go:col) and not(../preceding-sibling::go:row)">
							<xsl:attribute name="SRC"><xsl:value-of select="wa:resource('images/board-top-right.gif')"/></xsl:attribute>
						</xsl:when>
						
						<!-- Long sides -->
						<xsl:when test="not(preceding-sibling::go:col)">
							<xsl:attribute name="SRC"><xsl:value-of select="wa:resource('images/board-left.gif')"/></xsl:attribute>
						</xsl:when>
						<xsl:when test="not(following-sibling::go:col)">
							<xsl:attribute name="SRC"><xsl:value-of select="wa:resource('images/board-right.gif')"/></xsl:attribute>
						</xsl:when>
						<xsl:when test="not(../following-sibling::go:row)">
							<xsl:attribute name="SRC"><xsl:value-of select="wa:resource('images/board-bottom.gif')"/></xsl:attribute>
						</xsl:when>
						<xsl:when test="not(../preceding-sibling::go:row)">
							<xsl:attribute name="SRC"><xsl:value-of select="wa:resource('images/board-top.gif')"/></xsl:attribute>
						</xsl:when>
						
						<!-- Empty space -->
						<xsl:otherwise>
							<xsl:attribute name="SRC"><xsl:value-of select="wa:resource('images/board-center.gif')"/></xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</IMG>
				</TD>
			</xsl:for-each>
		</TR>
		</xsl:for-each>
	</TABLE><BR/>
    <xsl:value-of select="@whitescaptured"/> whites and 
    <xsl:value-of select="@blackscaptured"/> blacks captured
    </DIV>
	
	<!-- Moving the pieces with move - variable -->
   	<INPUT TYPE="HIDDEN" NAME="{go:string/@id}" ID="{go:string/@id}" VALUE="" />
   	
</xsl:template>

</xsl:stylesheet>


