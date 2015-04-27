package com.github.odavid.maven.plugins.release

import groovy.transform.EqualsAndHashCode;

@EqualsAndHashCode
class Version {
	String snapshot
	String release
	
	@Override
	public String toString() {
		"${snapshot} -> ${release}"
	}
	final static Version fromString(String s){
		def versions = s.split(/->/)
		if(versions.size() != 2){
			throw new IllegalArgumentException("${s} is not a valid version string")
		}
		new Version(snapshot: versions[0].trim(), release: versions[1].trim())
	}
}
