package org.millstone.examples.stoneplayer;

import org.millstone.base.ui.*;
import org.millstone.base.event.*;
import org.millstone.base.terminal.ClassResource;
import org.millstone.base.terminal.FileResource;
import org.millstone.base.terminal.ThemeResource;
import org.millstone.base.Application;
import org.millstone.base.data.Property;
import org.millstone.base.data.util.FilesystemContainer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/** Shared MP3 jukebox.
 *
 * @author  IT Mill Ltd
 */
public class StonePlayer extends org.millstone.base.Application {

	/** Shared jukebox used for playing. */
	private static Jukebox sharedJukebox;

	/** Creates a new instance of Calculator and initialized UI */
	public StonePlayer() {
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

	private class Playlist
		extends OrderedLayout
		implements JukeboxListener, Property.ValueChangeListener {

		private Table playlist;

		public Playlist() {

			playlist = new Table();

			// Playlist
			playlist.setCaption("Playlist");
			playlist.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
			playlist.setSelectable(true);
			playlist.setImmediate(true);
			playlist.addListener(this);
			playlist.setStyle("list");
			addComponent(playlist);

			// Listen julkebox changes
			sharedJukebox.addListener(this);

			// Refresh the view to reflect the current jukebox state 
			jukeboxPlaylistChanged(sharedJukebox);
		}

		/** Invoked when state of the jukebox has changed. */
		public void jukeboxStateChanged(Jukebox jukebox) {

			// select the current song
			Song song = jukebox.getCurrentSong();
			if (song != null) {
				playlist.setValue(song);
			}
		}

		/** Refresh the playlist.
		 * @see org.millstone.examples.stoneplayer.JukeboxListener#jukeboxPlaylistChanged(Jukebox)
		 */
		public void jukeboxPlaylistChanged(Jukebox jukebox) {

			// Update the visible playlist
			Collection pl = jukebox.getPlayList();
			playlist.removeAllItems();
			if (pl != null) {
				for (Iterator i = pl.iterator(); i.hasNext();) {
					playlist.addItem((Song) i.next());
				}
			}

			// (re)select the current song
			Song song = jukebox.getCurrentSong();
			if (song != null) {
				playlist.setValue(song);
			}
		}

		/** Invoked when the selection in playlist has changed.
		 * @see org.millstone.base.data.Property.ValueChangeListener#valueChange(Property.ValueChangeEvent)
		 */
		public void valueChange(Property.ValueChangeEvent event) {
			Song s = (Song) event.getProperty().getValue();
			if (s != null) {
				sharedJukebox.setCurrentSong(s);
			}
		}

	}

	/** Player controls buttons and current song information. */
	private class Player extends OrderedLayout implements JukeboxListener {

		private Label currentTitle;
		private Button playstop;

		public Player() {

			setOrientation(OrderedLayout.ORIENTATION_VERTICAL);

			// Create buttons by connecting them directly to
			// the jukebox functions. Play/stop button handled separately
			OrderedLayout hl =
				new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);

			Button b;
			hl.addComponent(b = new Button("Prev", sharedJukebox, "prev"));
			b.setStyle("stoneplayer");

			hl.addComponent(
				playstop = new Button("Play", sharedJukebox, "play"));
			playstop.setStyle("stoneplayer");

			hl.addComponent(b = new Button("Next", sharedJukebox, "next"));
			b.setStyle("stoneplayer");

			hl.addComponent(b = new Button("Rand", sharedJukebox, "random"));
			b.setStyle("stoneplayer");

			addComponent(hl);

			// Current song title
			Panel titlePanel = new Panel("Currently playing");

			titlePanel.addComponent(currentTitle = new Label(""));
			addComponent(titlePanel);

			// Listen julkebox changes
			sharedJukebox.addListener(this);

			// Refresh the view to reflect the current jukebox state 
			jukeboxStateChanged(sharedJukebox);
		}

		/** Invoked when state of the jukebox has changed. */
		public void jukeboxStateChanged(Jukebox jukebox) {

			// update play/stop button state
			if (jukebox.isPlaying()) {
				playstop.setCaption("Stop");
				playstop.removeListener(Button.ClickEvent.class, sharedJukebox);
				playstop.addListener(
					Button.ClickEvent.class,
					sharedJukebox,
					"stop");
			} else {
				playstop.setCaption("Play");
				playstop.removeListener(Button.ClickEvent.class, sharedJukebox);
				playstop.addListener(
					Button.ClickEvent.class,
					sharedJukebox,
					"play");
			}
			// select the current song
			Song song = jukebox.getCurrentSong();
			if (song != null) {
				currentTitle.setValue(song.getName());
			}

		}

		/** Ignore playlist changed.
		 * @see org.millstone.examples.stoneplayer.JukeboxListener#jukeboxPlaylistChanged(Jukebox)
		 */
		public void jukeboxPlaylistChanged(Jukebox jukebox) {
			// Nothing to do here
		}
	}

	/** Music file browser component.
	 * 
	 */
	private class FileBrowser extends OrderedLayout implements Action.Handler {

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
				tree.setMultiSelect(true);
				tree.setItemCaptionMode(tree.ITEM_CAPTION_MODE_ITEM);
				tree.setItemIconPropertyId(FilesystemContainer.PROPERTY_ICON);
				tree.addActionHandler(this);
				addComponent(tree);
			}
		}

		// Define some actions for files/folders
		private Action ACT_PLAY = new Action("Play", null);
		private Action ACT_ENQ = new Action("Enqueue", null);
		private Action ACT_DL = new Action("Download", null);

		/** Return the list of available actions per (file or directory) item.
		 * @see org.millstone.base.event.Action.Handler#getActions(Object)
		 */
		public Action[] getActions(Object target, Object source) {

			if (((File) target).isDirectory()) {

				// If directory contains mp3 files enable
				// play/enqueue actions for this directory
				boolean containsMp3 = false;
				String[] list = ((File) target).list();
				if (list == null)
					return new Action[] {
				};
				for (int i = 0; i < list.length && (!containsMp3); i++) {
					if (list[i].endsWith(".mp3"))
						containsMp3 = true;
				}
				if (containsMp3)
					return new Action[] { ACT_PLAY, ACT_ENQ };
				else
					return new Action[] {
				};
			} else {
				// MP3 files are always playable/downloadable
				return new Action[] { ACT_PLAY, ACT_ENQ, ACT_DL };
			}
		}

		/** Handle file actions.
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