package org.millstone.examples.stoneplayer;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.io.File;
import javazoom.jl.player.*;

/** Jukebox implementing a shared playlist and standard playing interface.
 *
 * @author  IT Mill Ltd
 */
public class Jukebox implements Runnable {

	private static LinkedList playList = new LinkedList();
	private static Song current = null;
	private static Player player;
	private static Thread playerThread;
	private static Random rand;
	private static boolean playing = false;
	private static LinkedList listeners = new LinkedList();

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
		if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3"))
			playList.add(new Song(file));
		else if (file.isDirectory()) {
			File[] f = file.listFiles();
			for (int i = 0; i < f.length; i++)
				addToPlayList(f[i]);
		}
		playlistChange();
		if (current == null && size() > 0) {
			current = (Song) playList.getFirst();
			stateChange();
		}
	}

	/** Remove a song from the playlist.
	 * @param song Song to be removed from the list.
	 */
	public void removeFromPlayList(Song song) {
		if (song == current)
			next();
		playList.remove(song);
		playlistChange();
	}

	/** Remove a song from the playlist.
	 * @param song Song to be removed from the list.
	 */
	public void clearPlayList() {
		if (isPlaying())
			stop();
		playList.clear();
		current = null;
		playlistChange();
	}

	/** Start playing.
	 */
	public synchronized void play() {
		if (size() > 0) {
			if (current == null)
				current = (Song) playList.getFirst();

			// Stop if currently playing
			playing = false;
			if (player != null)
				player.close();
			while (playerThread != null && playerThread.isAlive()) {
				playerThread.interrupt();
				try {
					wait(2);
				} catch (java.lang.InterruptedException e) {
					System.out.println("Interrupted exception at player.");
				}
			}

			// Start playing current
			playerThread = new Thread(this);
			playing = true;
			playerThread.start();
		}
		stateChange();
	}

	/** Stop playing.
	 */
	public synchronized void stop() {
		playing = false;
		if (playerThread != null) {
			if (player != null) {
				player.close();
				player = null;
			}
			while (playerThread.isAlive()) {
				playerThread.interrupt();
				try {
					wait(2);
				} catch (java.lang.InterruptedException e) {
					System.out.println("Interrupted exception at player.");
				}
			}
			playerThread = null;
		}
		stateChange();
	}

	/** Move to next song in playlist.
	 */
	public void next() {
		if (current != null) {
			int index = playList.indexOf(current);
			if (index < playList.size() - 1) {
				current = (Song) playList.get(index + 1);
				if (isPlaying()) play();
			} else {
				current = null;
				if (size() > 0)
					current = (Song) playList.getFirst();
				stop();
			}
		} else
			if (isPlaying()) play();
		playlistChange();
	}

	/** Move to previous song in playlist.
	 */
	public void prev() {
		if (current != null) {
			int index = playList.indexOf(current);
			if (index > 0)
				current = (Song) playList.get(index - 1);
		}
		if (isPlaying()) play();
		playlistChange();
	}

	/** Move to random song in playlist.
	 */
	public void random() {
		if (size() > 0) {
			if (rand == null)
				rand = new Random();
			int index = rand.nextInt(size());
			current = (Song) playList.get(index);
			if (isPlaying()) play();
			playlistChange();
		}
	}

	public void run() {
		while (playing) {
			try {
				AudioDevice audioDev =
					FactoryRegistry.systemRegistry().createAudioDevice();
				player = new Player(current.getStream(), audioDev);
				player.play();
				player.close();
			} catch (java.lang.Exception e) {
				System.out.println("Exception at player:"+e);
				playing = false;
			}

			if (playing) {
				int index = playList.indexOf(current);
				if (index < playList.size() - 1) {
					current = (Song) playList.get(index + 1);
					stateChange();
				} else
					playing = false;
			}
		}
	}

	/** Get the currently playing song */
	public Song getCurrentSong() {
		return current;
	}

	/** Get the currently playing song */
	public void setCurrentSong(Song song) {
		if (song != null) {
			if (!playList.contains(song))
				playList.addLast(song);
			if (current != song) {
				current = song;
				play();
			}
		} else
			stop();
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
}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */