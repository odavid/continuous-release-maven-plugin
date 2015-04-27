package com.github.odavid.maven.plugins.release;

import static org.junit.Assert.*

import java.util.regex.Matcher

import org.junit.Test

class SnapshotArtifactTest {

	@Test
	public void testToJsonMap() {
		SnapshotArtifact artifact = new SnapshotArtifact(coordinates: 
			[groupId: 'g', 
			artifactId: 'a', 
			type: 'pom', 
			classifier: 'sources', 
			version: Version.fromString('1.0-SNAPSHOT -> 1.0') ], 
			locationKey: LocationKey.fromString('${project.groupId}:aaa:pom'))
		def jsonMap = [id: 'g:a:pom:sources', version: '1.0-SNAPSHOT -> 1.0', loc: '${project.groupId}:aaa:pom']
		assert jsonMap == artifact.asJsonMap()
	}

	@Test
	public void testFromJsonMap() {
		SnapshotArtifact artifact = new SnapshotArtifact(coordinates: 
			[
				groupId: 'g.r.o.u.p', 
				artifactId: 'a.r.t.i.f.a.c.t', 
				type: 'pom', 
				classifier: 'sources', 
				version: Version.fromString('1.0-SNAPSHOT -> 1.0')], 
			locationKey: LocationKey.fromString('${project.groupId}:aaa:pom'))
		def jsonMap = [id: 'g.r.o.u.p:a.r.t.i.f.a.c.t:pom:sources', version: '1.0-SNAPSHOT -> 1.0', loc: '${project.groupId}:aaa:pom']
		assert artifact == SnapshotArtifact.fromJsonMap(jsonMap) 
	}
}
