package com.github.odavid.maven.plugins.release;

interface RepositoryMetadataService {
	String lastReleaseVersion(String groupId, String artifactId, String type, String classifier, String version)
}
