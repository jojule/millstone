package org.millstone.examples.stoneplayer;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

/** StonePlayer song
 *
 * @author  IT Mill Ltd
 */
public class Song {

	File file;

	/** Name of the song. */
	private String name;

	/** Artist of the song. */
	private String artist;

	/** Creates a new instance of Song */
	public Song(File file) {
		this.file = file;

		if (file != null) {

			// We should use ID3 here
			name = file.getName();
			artist = file.getParent();
		}
	}

	/** Get the name of the song.
	 * @return Name of the song.
	 */
	public String getName() {
		return this.name;
	}

	/** Get the name of the artist.
	 * @return Name of the artist.
	 */
	public String getArtist() {
		return this.artist;
	}

	/** Get the name of the song.
	 * @return Name of the song.
	 */
	public String toString() {
		return getName();
	}

	/** Get the input stream from which this song can be read. */
	public InputStream getStream() {
		try {
			return new BufferedInputStream(new FileInputStream(file));
		} catch (java.io.FileNotFoundException e) {
		}
		return null;
	}

	/** Get the file for this song. */
	public File getFile() {
		return this.file;
	}
}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */