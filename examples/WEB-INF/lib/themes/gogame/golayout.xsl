<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- No javascript warning -->
<xsl:template match="customlayout[@style='goroom' and not($dhtml)]">
	
	<h1>Browser support missing</h1>
	
	<p>GO Game requires support for ECMA-262 complian JavaScript. The game
	has been tested with the following browsers:
	<ul>
		<li>Mozilla 1.0, 1.1 and 1.2</li>
		<li>Internet Explorer 6</li>
	</ul>
	</p>

</xsl:template>

<!-- Layouting GO with GO layout -->
<xsl:template match="customlayout[@style='goroom' and $dhtml]">

	<!-- Login/out box -->
	<DIV>
		<xsl:choose>
			<xsl:when test="location[@name='logoutbutton']/*">
		 		<xsl:attribute name="STYLE">position: absolute; left: 5px; bottom: 5px;  z-index: 100;</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
		 		<xsl:attribute name="STYLE">position: background-color: #ddddff; width: 100%; height: 99%; text-align: center;</xsl:attribute>
				<BR/><BR/><H1>Welcome to Millstone GO</H1>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="location[@name='loginname']/*"/>
		<xsl:apply-templates select="location[@name='loginbutton']/*"/>
		<xsl:apply-templates select="location[@name='logoutbutton']/*"/>
	</DIV>

	<!-- Go board -->
	<xsl:if test="location[@name='board']/*">
		<DIV STYLE="position: absolute; top: 0px; height: 99%; width: 70%; left: 30%;">
			<TABLE BORDER="0" WIDTH="100%" HEIGHT="100%"><TR><TD WIDTH="50%"></TD><TD ALIGN="CENTER" VALIGN="CENTER">
				<DIV STYLE="border: 2px solid #eeee99; background-color: #ffffdd; z-index: 100; padding: 20px; z-index: 200;">
					<xsl:apply-templates select="location[@name='board']/*"/>
				</DIV>
			</TD><TD WIDTH="50%"></TD></TR></TABLE>
		</DIV>
	</xsl:if>

	<!-- Show players and rules when logged in. Also listen server. -->
	<xsl:if test="location[@name='players']/*">
		<xsl:if test="not(location[@name='board']/*)">
			<DIV STYLE="width: 70%; position: absolute; left: 30%; top: 0px; height: 100%; overflow: auto; background-color: #eeeeff;">
				<DIV STYLE="padding: 20px"><xsl:call-template name="go-rules"/></DIV>
			</DIV>
		</xsl:if>
		<DIV STYLE="width: 30%; position: absolute; top: 0px; height: 100%; left: 0px; overflow: auto; background-color: #ddddff; border-right: 2px solid #aaaaff; z-index: 5;">
			<xsl:apply-templates select="location[@name='players']/*"/>
		</DIV>

	   	<!-- Stream for listening updates -->
   		<IFRAME SRC="?SERVER_COMMANDS=1" WIDTH="0" HEIGHT="0"/>
	</xsl:if>


</xsl:template>


<!-- Rules of GO -->
<xsl:template name="go-rules">

<h1>Millstone GO sample</h1>

<h3>Try it out first</h3>

<p>To play this game of go, invite your friend to login to game at the 
same time with you. The players list below should show your friend (and
if the game is installed on public server, maybe some other players also).
Challenge your friend by clicking the blue arrow button in the end of
table line and select <i>Challenge</i> from the popup. The moves on the 
board are made by clicking on emty space on board. Setting new stones
automatically refreshes both your and your friends browser</p>

<h3>Features</h3>

<p>This simple GO-game implementation with Millstone demonstrates the 
following advanced capabilities of the Millstone base-library and Web adapter:
<ul>
  <li>Easy creation of new functional components (Go board)</li>
  <li>Server managed events</li>
  <li>Flexible layouting with CSS and XSL-stylesheet</li>
</ul>
</p>

<h3>Creation of custon UI components</h3>

<p>The GO-board in this example is created by extending existing UI component. 
Any existing component can be extended, but the <code>AbstractComponent</code>
has been selected to be the basis for the GO-board, because of its simplicity.</p>

<p>The component has been implemented simply by defining a custom UIDL tag for it (in its own
namespace), overriding <code>paint()</code> and <code>changeVariables()</code> 
methods and creating theme for the component.</p>

<p>You can also create composite components using existing components without 
need to create theme, define variable changing or painting. The most strightforward
component for this purpose is <code>CustomComponent</code>.</p>

<h3>Server events</h3>

<p>All the changes in the components on server side generate events. All the visible
components notify the terminal when they has been changed. Normally web is one way
medium - the web-browser requests pages as it wishes. In this example, the web-browser
listens server components changes trough open http-connection. This way the moves
made are immediately reflected on both players GO-boards.</p>

<h3>Advanced layouting</h3>

<p>Millstone includes basic layouting capabilities for user interfaces. For more advanced
layouting needs the full power of XSL stylesheets and CSS can be utilized. This is simply
done by using layout component called <code>CustomLayout</code> and defining the layout
with XSL stylesheet.</p>

<h1>The Japanese Rules of Go</h1>
<BR/>
April 10, 1989  (Effective May 15, 1989)
<BR/>
Translated by James Davies
<BR/>
Reformatted, adapted, and edited by Fred Hansen
<BR/>
<a href="http://www-2.cs.cmu.edu/~wjh/go/rules/Japanese.html">Original text with commentary</a>
<p>
The Nihon Kiin and Kansai Kiin hereby revise the Nihon Kiin's Rules
of Go formulated in October 1949 and establish the Japanese Rules of
Go.  These rules must be applied in a spirit of good sense and mutual
trust between the players.
</p>

<h3>Article 1.  The game of go</h3>
<p>Go is a game in which two players compete in skill on a board, from
the beginning of the game until the game stops according to Article
9, to see which can take more territory.  A &quot;<i>game&quot;</i> refers to the
moves played until the &quot;<i>end of the game.&quot;</i>
</p>

<h3>Article 2.  Play</h3>
<p>The players can alternately play one move at a time, one player playing
the black stones, his opponent the white stones.
</p>

<h3>Article 3.  Point of play</h3>
<p>The board is a grid of 19 horizontal and 19 vertical lines forming
361 intersections.  A stone can be played on any unoccupied intersection
(called an &quot;<i>empty point&quot;</i>) on which Article 4 permits it to exist.
 The point on which a stone is played is called its &quot;<i>point of play</i>.&quot;
</p>

<h3>Article 4.  Stones that may exist on the board</h3>
<p>After a move is completed, a group of one or more stones belonging
to one player exists on its points of play on the board as long as
it has a horizontally or vertically adjacent empty point, called a
&quot;<i>liberty</i>.&quot; No group of stones without a liberty can exist on the
board.
</p>

<h3>Article 5.  Capture</h3>
<p>If, due to a player's move, one or more of his opponent's stones cannot
exist on the board according to the preceding article, the player must
remove all these opposing stones, which are called &quot;<i>prisoners</i>.&quot;
In this case, the move is completed when the stones have been removed.
</p>

<h3>Article 6.  Ko</h3>
<p>A shape in which the players can alternately capture and recapture
one opposing stone is called a &quot;<i>ko</i>.&quot; A player whose stone has
been captured in a ko cannot recapture in that ko on the next move.
</p>

<h3>Article 7.  Life and death</h3>
<p><b>1. </b> Stones are said to be &quot;<i>alive</i>&quot; if they cannot be captured
by the opponent, or if capturing them would enable a new stone to be
played that the opponent could not capture.  Stones which are not alive
are said to be &quot;<i>dead</i>.&quot;
<BR/>
<b>2. </b> In the confirmation of life and death after the game stops
in Article 9, recapturing in the same ko is prohibited.  A player whose
stone has been captured in a ko may, however, capture in that ko again
after passing once for that particular ko capture.
</p>

<h3>Article 8.  Territory</h3>
<p>Empty points surrounded by the live stones of just one player are called
&quot;<i>eye points.</i>&quot; Other empty points are called &quot;<i>dame</i>.&quot; Stones
which are alive but possess dame are said to be in &quot;<i>seki</i>.&quot; Eye
points surrounded by stones that are alive but not in seki are called
&quot;<i>territory</i>,&quot; each eye point counting as one point of territory.
</p>

<h3>Article 9.  End of the game</h3>
<p><b>1.</b>  When a player passes his move and his opponent passes in
succession, the game stops.
<BR/>
<b>2.</b>  After stopping, the game ends through confirmation and agreement
by the two players about the life and death of stones and territory.
 This is called &quot;<i>the end of the game.</i>&quot;
<BR/>
<b>3.</b>  If a player requests resumption of a stopped game, his opponent
must oblige and has the right to play first.
</p>

<h3>Article 10.  Determining the result</h3>
<p><b>1.</b>  After agreement that the game has ended, each player removes
any opposing dead stones from his territory as is, and adds them to
his prisoners.
<BR/>
<b>2. </b> Prisoners are then filled into the opponent's territory,
and the points of territory are counted and compared.  The player with
more territory wins.  If both players have the same amount the game
is a draw, which is called a &quot;<i>jigo</i>.&quot;
<BR/>
<b>3.</b>  If one player lodges an objection to the result, both players
must reconfirm the result by, for example, replaying the game.
<BR/>
<b>4.</b>  After both players have confirmed the result, the result
cannot be changed under any circumstances.
</p>

<h3>Article 11.  Resignation</h3>
<p>During a game, a player may end the game by admitting defeat.  This
is called &quot;<i>resigning</i>.&quot; The opponent is said to &quot;<i>win by resignation</i>.&quot;
</p>

<h3>Article 12.  No result</h3>
<p>When the same whole-board position is repeated during a game, if the
players agree, the game ends without result.
</p>

<h3>Article 13.  Both players lose</h3>
<p><b>1.</b>  After the game stops according to Article 9, if the players
find an effective move, which would affect the result of the game,
and therefore cannot agree to end the game, both players lose.
<BR/>
<b>2. </b> If a stone on the board has been moved during the game and
the game has proceeded, the game continues with the stone returned
to its original point of play.  If the players cannot agree, both players
lose. 
</p>

<h3>Article 14.  Forfeit</h3>
<p>Violation of the above rules causes immediate loss of the game, provided
the result has not yet been confirmed by both players. 
<BR/>
</p>
</xsl:template>

</xsl:stylesheet>


