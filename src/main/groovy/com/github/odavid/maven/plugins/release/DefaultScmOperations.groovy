package com.github.odavid.maven.plugins.release

import org.apache.maven.scm.ScmFileSet
import org.apache.maven.scm.ScmTagParameters
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.repository.ScmRepository


class DefaultScmOperations implements ScmOperations{
	ScmRepository scmRepository
	ScmManager scmManager

	@Override
	public void commitReleaseInfo(File releaseInfoFile, String commitMessage) {
		ScmFileSet scmFileSet = new ScmFileSet(releaseInfoFile.parentFile, releaseInfoFile)
		scmManager.checkIn(scmRepository, scmFileSet, commitMessage)
	}

	@Override
	public void tagWorkspace(File basedir, String tagName) {
		ScmFileSet scmFileSet = new ScmFileSet(basedir)
		ScmTagParameters params = new ScmTagParameters("Tagging release: ${tagName}")
		scmManager.tag(scmRepository, scmFileSet, tagName, "TAG: ${tagName}")
	}

}
