package com.github.odavid.maven.plugins.release

import groovy.transform.EqualsAndHashCode;
import groovy.transform.TupleConstructor;

import org.apache.maven.model.Dependency
import org.apache.maven.model.Parent

@EqualsAndHashCode
class SnapshotArtifact {
	Coordinates coordinates
	LocationKey locationKey
	boolean external
	
	final Map asJsonMap(){
		def map = [
			id: coordinates.id,
			version: coordinates.version.toString(),
		]
		if(locationKey) map << [loc: locationKey.toString()]
		if(external) map << [external: true]
		map
	}
	
	static final SnapshotArtifact fromJsonMap(Map map){
		Coordinates coordinates = Coordinates.fromJsonMap(map)
		return new SnapshotArtifact(coordinates: coordinates, 
			external: map.external?: false, 
			locationKey: map.loc? LocationKey.fromString(map.loc) : null)
	}
}
