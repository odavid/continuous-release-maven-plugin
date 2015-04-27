package com.github.odavid.maven.plugins.release;

import groovy.transform.EqualsAndHashCode;

import org.apache.maven.model.Dependency
import org.apache.maven.model.InputLocationTracker
import org.apache.maven.model.Model

@EqualsAndHashCode
class LocationKey {
	LocationKey parent
	String groupId
	String artifactId
	String type = 'jar'
	String classifier

	public Dependency findDependency(Model model) {
		model.dependencies.find{ Dependency dep -> 
			dep.groupId == groupId && dep.artifactId == artifactId && dep.type == type && dep.classifier == classifier 
		}
	}
	
	public Dependency findDependencyManagement(Model model) {
		model.dependencyManagement.dependencies.find{ Dependency dep -> 
			dep.groupId == groupId && dep.artifactId == artifactId && dep.type == type && dep.classifier == classifier 
		}
	}

	@Override
	String toString() {
		StringBuilder stringBuilder = new StringBuilder()
		if(parent){
			stringBuilder.append(parent).append('/')
		}
		stringBuilder.append(groupId).append(':').append(artifactId)
		stringBuilder.append(':').append(type?:'jar')
		if(classifier){
			stringBuilder.append(':').append(classifier)
		}
		stringBuilder.toString()
	}
	
	static final LocationKey fromString(String s){
		LocationKey parent = null
		int parentSeparatorIndex = s.indexOf('/')
		if( parentSeparatorIndex >= 0){
			String parentString = s.substring(0, parentSeparatorIndex)
			parent = fromString(parentString)
			s = s.substring(parentSeparatorIndex+1)  
		}
		def coords = s.split(/\:/)
		if(coords.length < 3){
			throw new IllegalArgumentException("Invalid location key: ${s}")
		}
		new LocationKey(parent: parent, groupId: coords[0], artifactId: coords[1], type: coords[2], classifier: coords.length > 3? coords[3] : null)
	}
}
