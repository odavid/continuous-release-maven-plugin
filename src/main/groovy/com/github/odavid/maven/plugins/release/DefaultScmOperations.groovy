package com.github.odavid.maven.plugins.release

import org.apache.maven.scm.ScmFileSet
import org.apache.maven.scm.provider.ScmProvider
import org.apache.maven.scm.repository.ScmRepository


class DefaultScmOperations implements ScmOperations{
	ScmRepository scmRepository
	ScmProvider scmProvider

	@Override
	public void commitReleaseInfo(File releaseInfoFile, String commitMessage) {
		ScmFileSet scmFileSet = new ScmFileSet(releaseInfoFile.parentFile, releaseInfoFile)
		scmProvider.checkIn(scmRepository, scmFileSet, commitMessage)
	}

	@Override
	public void tagWorkspace(File basedir, String tagName) {
	}

}
