package org.millstone.examples.stoneplayer;

import org.millstone.base.ui.*;
import org.millstone.base.event.*;
import org.millstone.base.terminal.FileResource;
import org.millstone.base.terminal.ThemeResource;
import org.millstone.base.data.util.FilesystemContainer;
import org.millstone.base.data.util.MethodProperty;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

/** Shared MP3 jukebox.
 *
 * @author  IT Mill Ltd
 */
public class StonePlayer extends org.millstone.base.Application {

	/** Shared jukebox used for playing. */
	private static Jukebox sharedJukebox;

	/** Creates a new instance  StonePlayer UI */
	public StonePlayer() {
		if (sharedJukebox == null)
			sharedJukebox = new Jukebox();
	}

	/**
	* @see org.millstone.base.Application#init()
	*/
	public void init() {

		// Set the application-wide theme
		setTheme("stoneplayer");

		// Create framed main window 
		FrameWindow mainWindow = new FrameWindow("StonePlayer");
		setMainWindow(mainWindow);

		// Player window
		Window playerWin = new Window("Player");
		playerWin.addComponent(new Player());

		// Playlist window
		Window playlistWin = new Window("Playlist");
		playlistWin.addComponent(new Playlist());

		// Browser window
		Window browserWin = new Window("File browser");
		browserWin.addComponent(new FileBrowser());

		// Banner window
		Window bannerWin = new Window("Banner window");
		Embedded banner = new Embedded();
		banner.setType(Embedded.TYPE_IMAGE);
		banner.setSource(new ThemeResource("images/stoneplayer-1.3.jpg"));
		bannerWin.addComponent(banner);

		// Assign frames
		mainWindow.getFrameset().setVertical(true);
		mainWindow.getFrameset().newFrame(bannerWin, 0).setAbsoluteSize(48);
		FrameWindow.Frameset bodyFrame =
			mainWindow.getFrameset().newFrameset(false, 1);

		FrameWindow.Frameset leftFrame = bodyFrame.newFrameset(true, 0);
		leftFrame.setAbsoluteSize(300);
		leftFrame.newFrame(playerWin, 0).setAbsoluteSize(200);
		leftFrame.newFrame(playlistWin, 1);
		bodyFrame.newFrame(browserWin, 1);

	}

	/** Playlist UI implementation. */
	public class Playlist extends CustomComponent implements JukeboxListener {

		private Table playlist;
		private boolean updating = false;

		public Playlist() {

			setCompositionRoot(playlist = new Table());

			// Playlist
			playlist.setCaption("Playlist");
			playlist.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
			playlist.setSelectable(true);
			playlist.setImmediate(true);
			playlist.setStyle("list");

			// Listen julkebox changes
			playlist.setPropertyDataSource(
				new MethodProperty(sharedJukebox, "currentSong"));
			sharedJukebox.addListener(this);

			// Refresh the view to reflect the current jukebox state 
			jukeboxPlaylistChanged(sharedJukebox);
		}

		/** Invoked when state of the jukebox has changed.*/
		public void jukeboxStateChanged(Jukebox jukebox) {
			this.requestRepaint();
		}

		/** Refresh the playlist.
		 * @see org.millstone.examples.stoneplayer.JukeboxListener#jukeboxPlaylistChanged(Jukebox)
		 */
		public void jukeboxPlaylistChanged(Jukebox jukebox) {

			// Update the visible playlist
			Collection pl = jukebox.getPlayList();
			Song c = jukebox.getCurrentSong();
			playlist.setWriteThrough(false);
			playlist.removeAllItems();
			if (pl != null) {
				for (Iterator i = pl.iterator(); i.hasNext();) {
					playlist.addItem((Song) i.next());
				}
			}
			if (c != null)
				playlist.select(c);
			playlist.setWriteThrough(true);
		}
	}

	/** Player controls buttons and current song information.
	 *  This  class implements a component which can be used to  control the
	 * underlying jukebox.
	 */
	public class Player extends GridLayout implements JukeboxListener {

		/** Button for play and stop. */
		private Button playButton;

		public Player() {

			// Create grid for controls
			super(4, 2);

			// Create buttons by connecting them directly to
			// the jukebox functions. Play/stop button handled separately
			addComponent(new Button("Prev", sharedJukebox, "prev"));
			addComponent(playButton = new Button("Play", this, "togglePlay"));
			addComponent(new Button("Next", sharedJukebox, "next"));
			addComponent(new Button("Rand", sharedJukebox, "random"));

			// Current song title
			Panel titlePanel = new Panel("Currently playing");
			titlePanel.addComponent(
				new Label(new MethodProperty(sharedJukebox, "currentSong")));
			addComponent(titlePanel, 0, 1, 3, 1);

			// Listen julkebox changes
			sharedJukebox.addListener(this);

			// Refresh the view to reflect the current jukebox state 
			jukeboxStateChanged(sharedJukebox);
		}

		public void togglePlay() {
			if (sharedJukebox.isPlaying()) {
				sharedJukebox.stop();
			} else {
				sharedJukebox.play();
			}

		}

		/** Invoked when state of the jukebox has changed. */
		public void jukeboxStateChanged(Jukebox jukebox) {

			// update play/stop button state
			if (jukebox.isPlaying()) {
				playButton.setCaption("Stop");
			} else {
				playButton.setCaption("Play");
			}
		}

		/** Ignore playlist changes.
		 * @see org.millstone.examples.stoneplayer.JukeboxListener#jukeboxPlaylistChanged(Jukebox)
		 */
		public void jukeboxPlaylistChanged(Jukebox jukebox) {
			// Nothing to do here
		}
	}

	/** Music file browser component.
	 *  A component for browsing and selecting through music files.
	 */
	public class FileBrowser extends OrderedLayout implements Action.Handler {

		private Table results;

		public FileBrowser() {

			// Create file browser tree
			Tree tree = null;
			String mp3Path = StonePlayer.this.getProperty("mp3path");
			if (mp3Path != null && mp3Path.length() > 0) {
				File path = new File(mp3Path);
				if (path.exists()) {
					FilesystemContainer f =
						new FilesystemContainer(path, "mp3", true);
					tree = new Tree("MP3 browser", f);
				} else {
					addComponent(
						new Label(
							"Specified path does not exist: '"
								+ mp3Path
								+ "'. Please check the 'mp3path' parameter in server.xml or in web.xml."));
				}
			} else {
				addComponent(
					new Label(
						"<h1>Property 'mp3path' not specified in applicationproperties</h1>"
							+ "MP3-browsing disabled beacause application property named 'mp3path'"
							+ " was not found. Please declare 'mp3path' parameter in server.xml or "
							+ " in web.xml to enable file browsing. <br /><br />",
						Label.CONTENT_XHTML));
			}

			if (tree != null) {
				tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_ITEM);
				tree.setSelectable(false);
				tree.setItemIconPropertyId(FilesystemContainer.PROPERTY_ICON);
				tree.addActionHandler(this);
				addComponent(tree);
			}
		}

		/* Define some actions for files/folders */
		private Action ACT_PLAY = new Action("Play", null);
		private Action ACT_ENQ = new Action("Enqueue", null);
		private Action ACT_DL = new Action("Download", null);

		/** Return the list of available actions per (file or directory) item.
		 * @see org.millstone.base.event.Action.Handler#getActions(Object)
		 */
		public Action[] getActions(Object target, Object source) {

			if (((File) target).isDirectory()) {
				return new Action[] { ACT_PLAY, ACT_ENQ };
			} else {
				return new Action[] { ACT_PLAY, ACT_ENQ, ACT_DL };
			}
		}

		/** Handle file/directory actions.
		 * @see org.millstone.base.event.Action.Handler#handleAction(Action, Object, Object)
		 */
		public void handleAction(Action action, Object sender, Object target) {
			if (action.equals(ACT_PLAY)) {
				if (sharedJukebox.isPlaying())
					sharedJukebox.stop();
				sharedJukebox.clearPlayList();
				sharedJukebox.addToPlayList((File) target);
				sharedJukebox.play();
			} else if (action.equals(ACT_ENQ)) {
				sharedJukebox.addToPlayList((File) target);
			} else if (action.equals(ACT_DL)) {
				Window w = new Window();
				w.setBorder(Window.BORDER_MINIMAL);
				w.open(new FileResource((File) target, StonePlayer.this));
				addWindow(w);
			}
		}

	}
}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */