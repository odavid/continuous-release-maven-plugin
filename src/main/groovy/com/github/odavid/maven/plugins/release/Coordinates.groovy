package com.github.odavid.maven.plugins.release

import groovy.transform.EqualsAndHashCode;

import java.util.regex.Matcher


@EqualsAndHashCode
class Coordinates {
	String groupId
	String artifactId
	String type = 'jar'
	String classifier
	Version version

	String getId(){
		StringBuilder builder = new StringBuilder()
		builder
			.append(groupId)
			.append(':')
			.append(artifactId)
			.append(':')
			.append(type ?: 'jar')
			if(classifier){
				builder.append(':').append(classifier)
			}
		builder.toString()
	}
	
	String getReleaseVersion(){ 
		version ? version.release : null
	}
	
	String getSnapshotVersion(){
		version ? version.snapshot : null
	}
	
	final static Coordinates fromJsonMap(Map json){
		if(!json.id){
			throw new IllegalArgumentException("Json map : ${json} does not contain id")
		}
		if(!json.version){
			throw new IllegalArgumentException("Json map : ${json} does not contain version")
		}
		Version v = Version.fromString(json.version)
		def map = [version: v]
		Matcher matcher = json.id =~ /([\w\._]+):([\w\._]+):([\w\._]+)(:([\w\._]+))?/
		if(matcher.matches()){
			map << [
				groupId: matcher.group(1),
				artifactId: matcher.group(2),
				type: matcher.group(3),
				classifier: matcher.groupCount() > 4 ? matcher.group(5) : null
			]
		}else{
			throw new IllegalArgumentException("id ${json.id} is not a valid artifact coordinates as id")
		}
		new Coordinates(map)
	}
}
