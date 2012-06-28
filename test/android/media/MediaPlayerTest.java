package android.media;

import static org.junit.Assert.*;

import org.junit.Test;

import android.media.player.RefactoredMediaPlayer;

public class MediaPlayerTest {
	MediaPlayer toTest;
	
	@Test
	public void testCreateAMediaPlayer() {
		toTest = new RefactoredMediaPlayer();
		assertNotNull(toTest);
	}

}
