package org.millstone.examples.stoneplayer;

/** Jukebox listener interface.
 * 
 * This interface is used to listen changes in a jukebox.
 *
 * @author  IT Mill Ltd
 */
public interface JukeboxListener {
    
    /** Called when the song has changed */
    public void jukeboxStateChanged(Jukebox jukebox);

    /** Called when the playlist content has changed */
    public void jukeboxPlaylistChanged(Jukebox jukebox);

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */