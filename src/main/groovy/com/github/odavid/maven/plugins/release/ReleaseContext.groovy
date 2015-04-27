package com.github.odavid.maven.plugins.release

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject

interface ReleaseContext {
	String relativePomPathToRoot(MavenProject project)
	
	Version releaseVersion(MavenProject project)
	
	Version releaseVersionForExternalArtifact(String groupId, String artifactId, String type, String classifier, String version)
	
	boolean isPartOfReactor(String groupId, String artifactId)
	
	boolean hasSnapshotParent(MavenProject project)
	
	SnapshotArtifact parentOf(MavenProject project) 
	
	List<SnapshotArtifact> dependenciesOf(MavenProject project)	
	
	List<SnapshotArtifact> bomsOf(MavenProject project)	

	List<SnapshotArtifact> dependencyManagementOf(MavenProject project)
	
}
