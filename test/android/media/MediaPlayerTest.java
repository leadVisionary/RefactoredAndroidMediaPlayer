package android.media;

import static org.junit.Assert.*;

import org.junit.Test;

public class MediaPlayerTest {
	MediaPlayer toTest;
	
	@Test
	public void testCreateAMediaPlayer() {
		toTest = new MediaPlayer();
		assertNotNull(toTest);
	}

}
