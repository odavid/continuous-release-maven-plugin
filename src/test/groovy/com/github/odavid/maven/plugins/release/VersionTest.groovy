package com.github.odavid.maven.plugins.release;

import static org.junit.Assert.*

import org.junit.Test

class VersionTest {
	

	@Test
	void testToString() {
		Version v = new Version(snapshot: '1.0-SNAPSHOT', release: '1.0')
		assert v.toString() == '1.0-SNAPSHOT -> 1.0'
	}
	@Test
	void testFromString(){
		assert Version.fromString('1.0-SNAPSHOT -> 1.0') == new Version(snapshot: '1.0-SNAPSHOT', release: '1.0') 
		assert Version.fromString('1.0-SNAPSHOT ->    1.0') == new Version(snapshot: '1.0-SNAPSHOT', release: '1.0') 
		assert Version.fromString('1.0-SNAPSHOT->1.0') == new Version(snapshot: '1.0-SNAPSHOT', release: '1.0') 
	}
	@Test(expected=IllegalArgumentException)
	void testException(){
		assert Version.fromString('1.0-SNAPSHOT> 1.0') == new Version(snapshot: '1.0-SNAPSHOT', release: '1.0')
	}

}
