package com.github.odavid.maven.plugins.release

class ReleaseModuleProblem {
	ReleasableModule project
	String description
	SnapshotArtifact dependency
	
	@Override
	public String toString() {
		"Problem in ${project.coordinates.id}: ${description} ${dependency ? dependency.coordinates.id : ''}"
	}
}
