package org.millstone.examples.stoneplayer;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.io.File;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.*;

/** Jukebox implementing a shared playlist and standard playing interface.
 *
 * @author  IT Mill Ltd
 */
public class Jukebox {

	// Fields used by controller thread only
	private LinkedList listeners = new LinkedList();
	private Thread playerThread;
	private boolean disablePlaylistChange = false;
	private Random rand;

	// Fields used by both threads
	private AudioDevice audioDevice;
	private Player player;
	private LinkedList playList = new LinkedList();
	private Song current = null;
	private boolean playing = false;

	/** Construct new jukebox instance */
	public Jukebox() {

	}

	/** Get iterator for iterating trough songs in playlist. */
	public Iterator getPlayListIterator() {
		return playList.iterator();
	}

	/** Add set of files toplaylist.
	 * @param files Set of files
	 */
	public void addToPlayList(Set files) {
		Object[] f = files.toArray();
		for (int i = 0; i < f.length; i++)
			addToPlayList((File) f[i]);
	}

	/** Add new MP3-file to playlist.
	 * @param file MP3-file or directory to be added to playlist
	 */
	public void addToPlayList(File file) {
		if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
			synchronized (playList) {
				playList.add(new Song(file));
			}
		} else if (file.isDirectory()) {
			File[] f = file.listFiles();
			this.disablePlaylistChange = true;
			for (int i = 0; i < f.length; i++)
				if (f[i].getName().toLowerCase().endsWith(".mp3"))
					addToPlayList(f[i]);
			this.disablePlaylistChange = false;
		}
		if (!disablePlaylistChange)
			playlistChange();
		if (current == null && size() > 0) {
			current = (Song) playList.getFirst();
		}
	}

	/** Remove a song from the playlist.
	 * @param song Song to be removed from the list.
	 */
	public void removeFromPlayList(Song song) {
		if (song == current)
			next();
		synchronized (playList) {
			playList.remove(song);
		}
		playlistChange();
	}

	/** Remove a song from the playlist.
	 * @param song Song to be removed from the list.
	 */
	public void clearPlayList() {
		if (isPlaying())
			stop();

		synchronized (playList) {
			playList.clear();
		}
		current = null;
		playlistChange();
	}

	/** Pley the current song if any,
	 *  This function does not trigger events.
	 */
	private synchronized void startPlayerThread() {

		// Stop first
		stopPlayerThread();

		// Start playing mode
		playing = true;

		// Start playing current
		playerThread = new Thread(new PlayerThread(), "Audio Player Thread");
		playerThread.start();
	}

	/** Stop playing the current song. 
	 *  This function does not trigger events.
	 * 
	 */
	private synchronized void stopPlayerThread() {

		playing = false;
		if (player != null) {
			player.close();
		}
		player = null;
		if (audioDevice != null)
			audioDevice.close();
		audioDevice = null;
		if (playerThread != null)
			playerThread.interrupt();
		playerThread = null;
	}

	/** Start playing.
	 */
	public void play() {
		startPlayerThread();
		stateChange();
	}

	/** Stop playing.
	 */
	public void stop() {
		stopPlayerThread();
		stateChange();
	}

	/** Move to next song in playlist.
	 */
	public void next() {

		Song next = null;

		synchronized (playList) {

			if (current != null) {
				int index = playList.indexOf(current);
				next = null;
				if (index < playList.size() - 1) {
					next = (Song) playList.get(index + 1);
				}
			}
		}
		setCurrentSong(next);
	}

	/** Move to previous song in playlist.
	 */
	public void prev() {

		Song prev = null;

		synchronized (playList) {

			if (current != null) {
				int index = playList.indexOf(current);
				if (index > 0) {
					prev = (Song) playList.get(index - 1);
				}
			}
		}

		setCurrentSong(prev);
	}

	/** Move to random song in playlist.
	 */
	public void random() {

		Song random = null;
		synchronized (playList) {
			if (size() > 0) {
				if (rand == null)
					rand = new Random();
				int index = rand.nextInt(size());
				random = (Song) playList.get(index);
			}
		}

		setCurrentSong(random);
	}

	/** Get the currently playing song */
	public Song getCurrentSong() {
		return current;
	}

	/** Get the currently playing song */
	public void setCurrentSong(Song song) {

		boolean wasPlaying = isPlaying();
		Song oldSong = current;

		stopPlayerThread();
		synchronized (playList) {
			if (song != null) {
				if (!playList.contains(song))
					playList.addLast(song);
				if (oldSong != song) {
					current = song;
				}

			} else {
				current = null;
			}
		}

		// Resume playing state
		if (wasPlaying)
			startPlayerThread();

		// Trigger event
		if (current != oldSong)
			stateChange();

	}

	/** Is the player active */
	public boolean isPlaying() {
		return playing;
	}

	/** Get the number of songs in playlist */
	public int size() {
		return playList.size();
	}

	/** Get the current song index */
	public int getCurrentSongIndex() {
		if (current != null)
			return playList.indexOf(current);
		else
			return -1;
	}

	/** Add jukebox listener for this jukebox */
	public void addListener(JukeboxListener listener) {
		listeners.add(listener);
	}

	/** Remove jukebox listener from this jukebox */
	public void removeListener(JukeboxListener listener) {
		listeners.remove(listener);
	}

	/** Emit jukebox state change (if the state have really changed) */
	private void stateChange() {
		for (Iterator i = listeners.iterator(); i.hasNext();)
			 ((JukeboxListener) i.next()).jukeboxStateChanged(this);
	}

	/** Emit jukebox playlist change */
	private void playlistChange() {
		for (Iterator i = listeners.iterator(); i.hasNext();)
			 ((JukeboxListener) i.next()).jukeboxPlaylistChanged(this);
	}

	/** Get the playlist */
	public LinkedList getPlayList() {
		return playList;
	}

	private class PlayerThread implements Runnable {

		private boolean stopNow = false;

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			stopNow = false;
			try {
				audioDevice =
					FactoryRegistry.systemRegistry().createAudioDevice();
			} catch (JavaLayerException e) {
				System.out.println("Failed to open audio device:" + e);
			}

			try {

				// Play until somebody stops
				Song song = getCurrentSong();
				while (!stopNow && isPlaying() && song != null) {

					player = new Player(song.getStream(), audioDevice);
					player.play();

					// Check if the playing was stopped
					if (!isPlaying()) {
						song = null;
					} else {
						// Get next song, if available
						synchronized (playList) {
							if (current != null) {
								int index = playList.indexOf(current);
								current = null;
								if (index < playList.size() - 1) {
									current = (Song) playList.get(index + 1);
									stateChange();
								}
								song = current;
							}
						}
					}
				}

				// Catch exeptions
			} catch (java.lang.Exception e) {
				System.out.println("Exception in player:" + e);
			}

			// Stop from playing
			playing=false;
			stateChange();

		}

	}
}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */