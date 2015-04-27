package com.github.odavid.maven.plugins.release;

import static org.junit.Assert.*

import org.apache.maven.model.Model
import org.apache.maven.model.io.DefaultModelReader
import org.junit.Before
import org.junit.Test


class PomModelManipulatorTest {
	ReleasableModule module
	String pomContents
	
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
		pomContents = """<?xml version="1.0"?>
		<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<modelVersion>4.0.0</modelVersion>
			<parent>
				<groupId>com.github.odavid.maven.plugins</groupId>
				<artifactId>parent</artifactId>
				<version>0.1-alpha-37-SNAPSHOT</version>
			</parent>
			<groupId>groupId</groupId>
			<artifactId>artifactId</artifactId>
			<version>1.0-SNAPSHOT</version>
			<dependencyManagement>
				<dependencies>
					<dependency>
						<groupId>com.github.odavid.maven.plugins</groupId>
						<artifactId>groovy</artifactId>
						<version>0.1-alpha-37-SNAPSHOT</version>
						<type>pom</type>
						<scope>import</scope>
					</dependency>
					<dependency>
						<groupId>com.github.odavid.maven.plugins</groupId>
						<artifactId>depm</artifactId>
						<version>0.1-alpha-37-SNAPSHOT</version>
						<type>pom</type>
					</dependency>
				</dependencies>
			</dependencyManagement>
			<dependencies>
				<dependency>
					<groupId>com.github.odavid.maven.plugins</groupId>
					<artifactId>dep1</artifactId>
					<version>0.1-alpha-37-SNAPSHOT</version>
				</dependency>
				<dependency>
					<groupId>com.github.odavid.maven.plugins</groupId>
					<artifactId>dep2</artifactId>
					<version>0.1-alpha-37-SNAPSHOT</version>
				</dependency>
			</dependencies>
			<!-- COMMENT HERE -->
		</project>
		"""
	}

	@Test
	public void testJDomPomModelManipulatorUpdatePom() {
		JDomPomModelManipulator manipulator = new JDomPomModelManipulator()
		StringWriter out = new StringWriter()
		manipulator.updatePom(new StringReader(pomContents), module, out)
		Model model = new DefaultModelReader().read(new StringReader(out.toString()), null)
		assert out.toString().indexOf('<?xml') >= 0
		assert out.toString().indexOf('<!-- COMMENT HERE -->') > 0
		assert model.version == '1.0'
		assert model.parent.version == '0.1-alpha-37.10'
		assert model.dependencies[0].version == '0.1-alpha-37.10'
		assert model.dependencies[1].version == '0.1-alpha-37.10'
		assert model.dependencyManagement.dependencies[0].version == '0.1-alpha-37.10'
		assert model.dependencyManagement.dependencies[1].version == '0.1-alpha-37.10'
	}

	@Test
	public void testXpp3PomModelManipulatorUpdatePom() {
		Xpp3PomModelManipulator manipulator = new Xpp3PomModelManipulator()
		StringWriter out = new StringWriter()
		manipulator.updatePom(new StringReader(pomContents), module, out)
		Model model = new DefaultModelReader().read(new StringReader(out.toString()), null)
		assert model.version == '1.0'
		assert model.parent.version == '0.1-alpha-37.10'
		assert model.dependencies[0].version == '0.1-alpha-37.10'
		assert model.dependencies[1].version == '0.1-alpha-37.10'
		assert model.dependencyManagement.dependencies[0].version == '0.1-alpha-37.10'
		assert model.dependencyManagement.dependencies[1].version == '0.1-alpha-37.10'
	}
}
