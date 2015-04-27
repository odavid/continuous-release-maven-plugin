package com.github.odavid.maven.plugins.release

import groovy.transform.EqualsAndHashCode

import org.apache.maven.model.Model

@EqualsAndHashCode
class ReleasableModule {
	String file
	Coordinates coordinates
	SnapshotArtifact parent
	List<SnapshotArtifact> dependencies
	List<SnapshotArtifact> dependencyManagement
	List<SnapshotArtifact> boms

	void validate(List<ReleaseModuleProblem> problems){
		if(!coordinates.version){
			problems << new ReleaseModuleProblem(project: this, description: "Release version is not defined for project")
		}
		if(parent && !parent.coordinates.version){
			problems << new ReleaseModuleProblem(project: this, description: "Release version is not defined for parent", dependency: parent)
		}
		dependencies?.each { SnapshotArtifact dep ->
			if(!dep.coordinates.version){
				problems << new ReleaseModuleProblem(project: this, description: "Release version is not defined for dependency", dependency: dep)
			}
		}
		boms?.each { SnapshotArtifact dep ->
			if(!dep.coordinates.version){
				problems << new ReleaseModuleProblem(project: this, description: "Release version is not defined for bom", dependency: dep)
			}
		}
		dependencyManagement?.each { SnapshotArtifact dep ->
			if(!dep.coordinates.version){
				problems << new ReleaseModuleProblem(project: this, description: "Release version is not defined for dependencyManagement", dependency: dep)
			}
		}
	}

	static final ReleasableModule fromJsonMap(Map map){
		new ReleasableModule(
			coordinates: Coordinates.fromJsonMap(map), 
			file: map.file,
			dependencies: map.dependencies?.collect{ SnapshotArtifact.fromJsonMap(it) },
			boms: map.boms?.collect{ SnapshotArtifact.fromJsonMap(it) },
			dependencyManagement: map.dependencyManagement?.collect{ SnapshotArtifact.fromJsonMap(it) },
			parent: map.parent ? SnapshotArtifact.fromJsonMap(map.parent) : null)
	}

	final Map asJsonMap() {
		Map map = [
			id: coordinates.id,
			file: file,
			version: coordinates.version.toString()
		]
		if(parent){
			map << [parent: parent.asJsonMap()]
		}
		if(boms){
			map << [boms: boms.collect { dep -> dep.asJsonMap() } ]
		}
		if(dependencyManagement){
			map << [dependencyManagement: dependencyManagement.collect {dep -> dep.asJsonMap() }]
		}
		if(dependencies){
			map << [dependencies: dependencies.collect { dep -> dep.asJsonMap() }]
		}
		map
	}
}
