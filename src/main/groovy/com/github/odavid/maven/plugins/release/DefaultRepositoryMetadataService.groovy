package com.github.odavid.maven.plugins.release

import org.apache.maven.plugin.logging.Log
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.VersionRangeRequest
import org.eclipse.aether.resolution.VersionRangeResult

class DefaultRepositoryMetadataService implements RepositoryMetadataService{
	final RepositorySystem repositorySystem
	final RepositorySystemSession repoSession
	final List<RemoteRepository> remoteRepos
	final Log log
	
	DefaultRepositoryMetadataService(RepositorySystem repositorySystem, RepositorySystemSession repoSession, List<RemoteRepository> remoteRepos, Log log){
		this.repositorySystem = repositorySystem
		this.repoSession = repoSession
		this.remoteRepos = remoteRepos
		this.log = log
	}
	
	@Override
	public String lastReleaseVersion(String groupId, String artifactId, String type, String classifier, String version) {
		VersionRangeRequest req = new VersionRangeRequest().setArtifact(new DefaultArtifact(groupId, artifactId, classifier, type, releaseVersionRange(version)))
		req.setRepositories(remoteRepos)
		VersionRangeResult res = repositorySystem.resolveVersionRange(repoSession, req)
		res.highestVersion
	}
	private String releaseVersionRange(String snapshotVersion){
		String noSnapshot = snapshotVersion.minus('-SNAPSHOT')
		def p = /(.*)\.(\d+)/
		int lastNumber = Integer.parseInt(noSnapshot.replaceAll(p, '$2'))
		String nextRelease = noSnapshot.replaceAll(p, '$1') + ".${lastNumber + 1}"
		"[${noSnapshot},${nextRelease}-SNAPSHOT)"
	}
}
