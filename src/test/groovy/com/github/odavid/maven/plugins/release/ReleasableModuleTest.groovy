package com.github.odavid.maven.plugins.release;

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class ReleasableModuleTest {
	ReleasableModule module
	Map map
	
	@Before
	void setup(){
		module = new ReleasableModule(
			file: 'aaa/pom.xml',
			coordinates: [groupId: 'groupId',
				artifactId: 'artifactId', type: 'pom',
				version: [release:'1.0', snapshot: '1.0-SNAPSHOT'] ],
			boms: [
				SnapshotArtifact.fromJsonMap(
					[id: "com.github.odavid.maven.plugins:groovy:pom",
					loc: "com.github.odavid.maven.plugins:groovy:pom",
					version: "0.1-alpha-37-SNAPSHOT -> 0.1-alpha-37.10", external: true])
			],
			dependencies: [
				SnapshotArtifact.fromJsonMap(
					[id: "com.github.odavid.maven.plugins:dep1:jar",
					loc: "com.github.odavid.maven.plugins:dep1:jar",
					version: "0.1-alpha-37-SNAPSHOT -> 0.1-alpha-37.10"]),
				SnapshotArtifact.fromJsonMap(
					[id: "com.github.odavid.maven.plugins:dep2:jar",
					loc: "com.github.odavid.maven.plugins:dep2:jar",
					version: "0.1-alpha-37-SNAPSHOT -> 0.1-alpha-37.10"])

			],
			dependencyManagement: [
				SnapshotArtifact.fromJsonMap(
					[id: "com.github.odavid.maven.plugins:depm:pom",
					version: "0.1-alpha-37-SNAPSHOT -> 0.1-alpha-37.10",
					loc: 'com.github.odavid.maven.plugins:depm:pom',
					])
			],
			parent: SnapshotArtifact.fromJsonMap(
					[id: "com.github.odavid.maven.plugins:parent:pom",
					version: "0.1-alpha-37-SNAPSHOT -> 0.1-alpha-37.10",
					])
		)

		map = [
			id: 'groupId:artifactId:pom', version: '1.0-SNAPSHOT -> 1.0', file: 'aaa/pom.xml',
			boms:[
				[
					id: "com.github.odavid.maven.plugins:groovy:pom", 
					version: "0.1-alpha-37-SNAPSHOT -> 0.1-alpha-37.10", 
					loc: 'com.github.odavid.maven.plugins:groovy:pom',
					external: true
				]
			],
			dependencies:[
				[
					id: "com.github.odavid.maven.plugins:dep1:jar", 
					version: "0.1-alpha-37-SNAPSHOT -> 0.1-alpha-37.10",
					loc: 'com.github.odavid.maven.plugins:dep1:jar',
				],
				[
					id: "com.github.odavid.maven.plugins:dep2:jar", 
					version: "0.1-alpha-37-SNAPSHOT -> 0.1-alpha-37.10",
					loc: 'com.github.odavid.maven.plugins:dep2:jar',
				],
			],
			dependencyManagement:[
				[
					id: "com.github.odavid.maven.plugins:depm:pom", 
					version: "0.1-alpha-37-SNAPSHOT -> 0.1-alpha-37.10",
					loc: 'com.github.odavid.maven.plugins:depm:pom',
				],
			],
			parent: [id: "com.github.odavid.maven.plugins:parent:pom", version: "0.1-alpha-37-SNAPSHOT -> 0.1-alpha-37.10"],
		]
		
	}
	
	@Test
	public void testToJsonMap() {
		assert map == module.asJsonMap()
	}
	
	@Test
	void testFromJsonMap(){
		ReleasableModule readModule = ReleasableModule.fromJsonMap(map)
		assert readModule.coordinates == module.coordinates
		assert readModule.boms == module.boms
		assert readModule.dependencies == module.dependencies
		assert readModule.parent == module.parent
		assert readModule.dependencyManagement == module.dependencyManagement
		assert readModule.file == module.file
		assert readModule == module
	}
	
	@Test
	void testValidationMissingBomVersion(){
		module.boms[0].coordinates.version = null
		def problems = []
		module.validate(problems)
		assert problems.size() == 1
		assert problems[0].dependency.coordinates.artifactId == 'groovy'
	}
	@Test
	void testValidationMissingDependencyVersion(){
		module.dependencies[0].coordinates.version = null
		def problems = []
		module.validate(problems)
		assert problems.size() == 1
		assert problems[0].dependency.coordinates.artifactId == 'dep1'
	}
	@Test
	void testValidationMissingDependencyManagementVersion(){
		module.dependencyManagement[0].coordinates.version = null
		def problems = []
		module.validate(problems)
		assert problems.size() == 1
		assert problems[0].dependency.coordinates.artifactId == 'depm'
	}
	@Test
	void testValidationMissingParentVersion(){
		module.parent.coordinates.version = null
		def problems = []
		module.validate(problems)
		assert problems.size() == 1
		assert problems[0].dependency.coordinates.artifactId == 'parent'
	}
}
