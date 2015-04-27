package com.github.odavid.maven.plugins.release;

import static org.junit.Assert.*;

import org.junit.Test;

class LocationKeyTest {

	@Test
	void testToStringWithoutTypeWithClassifier() {
		LocationKey locationKey = new LocationKey(groupId: '${project.groupId}', artifactId: 'aaa', classifier: 'sources')
		assert '${project.groupId}:aaa:jar:sources' == locationKey.toString()
	}
	@Test
	void testToStringWithTypeAndClassifier() {
		LocationKey locationKey = new LocationKey(groupId: '${project.groupId}', artifactId: 'aaa', classifier: 'sources', type: 'pom')
		assert '${project.groupId}:aaa:pom:sources' == locationKey.toString()
	}

	@Test
	void testToStringWithTypeWithoutClassifier() {
		LocationKey locationKey = new LocationKey(groupId: '${project.groupId}', artifactId: 'aaa', type: 'pom')
		assert '${project.groupId}:aaa:pom' == locationKey.toString()
	}

	@Test
	void testToStringWithoutTypeWithoutClassifier() {
		LocationKey locationKey = new LocationKey(groupId: '${project.groupId}', artifactId: 'aaa')
		assert '${project.groupId}:aaa:jar' == locationKey.toString()
	}
	
	@Test
	void testToStringWithParent() {
		LocationKey parent = new LocationKey(groupId: '${project.groupId}', artifactId: 'parent', type: 'maven-plugin')
		LocationKey locationKey = new LocationKey(groupId: '${project.groupId}', artifactId: 'aaa', parent: parent)
		assert '${project.groupId}:parent:maven-plugin/${project.groupId}:aaa:jar' == locationKey.toString()
	}
	@Test
	void testFromStringWithParent() {
		LocationKey parent = new LocationKey(groupId: '${project.groupId}', artifactId: 'parent', type: 'maven-plugin')
		LocationKey locationKey = new LocationKey(groupId: '${project.groupId}', artifactId: 'aaa', parent: parent)
		assert LocationKey.fromString('${project.groupId}:parent:maven-plugin/${project.groupId}:aaa:jar') == locationKey
	}
}
