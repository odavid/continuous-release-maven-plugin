package com.github.odavid.maven.plugins.release

interface ScmOperations {
	void commitReleaseInfo(File releaseInfoFile, String commitMessage)
	void tagWorkspace(File basedir, String tagName)
}
