package com.github.odavid.maven.plugins.release

import groovy.json.JsonBuilder

import javax.inject.Inject

import org.apache.maven.RepositoryUtils
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import org.apache.maven.project.MavenProject
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.repository.ScmRepository
import org.eclipse.aether.RepositorySystem

import com.github.odavid.maven.plugins.groovy.GroovyMojoWithAntLogger

/**
 * @author odavid
 */
@Mojo(name="prepare", requiresDependencyResolution=ResolutionScope.NONE, requiresProject=true, threadSafe=true, aggregator=true)
class ReleasePrepareMojo extends GroovyMojoWithAntLogger{
	@Parameter(defaultValue='false')
	boolean dryRun

	@Parameter(required=true, property='buildNumber')
	Long buildNumber;


	@Parameter(required=true, readonly=true, defaultValue='${basedir}')
	File basedir
	
	@Parameter(required = true, readonly = true, defaultValue = '${project}')
	MavenProject project
	
	@Parameter(readonly=true, defaultValue='${reactorProjects}')
	List<MavenProject> reactorProjects
	
	@Parameter(readonly=true, defaultValue='${session}')
	MavenSession session
	
	@Parameter(required=true, property='releaseInfoFile', defaultValue='${basedir}/release-info.json')
	File releaseInfoFile;

	@Inject
	RepositorySystem repositorySystem
	
	@Inject
	ScmManager scmManager
	
	
	@Override
	void execute() throws MojoExecutionException, MojoFailureException {
		DefaultRepositoryMetadataService repositoryMetadataService = new DefaultRepositoryMetadataService(repositorySystem, session.repositorySession, RepositoryUtils.toRepos( project.remoteArtifactRepositories), log)
		DefaultReleaseContext context = new DefaultReleaseContext(repositoryMetadataService, session, project, reactorProjects, buildNumber, log)
		
		log.info("Preparing release, buildNumber: ${buildNumber}, reactor: ${project.id}, dryRun = ${dryRun}")
		log.info("Analyzing ${reactorProjects.size()} reactor modules")
		List<ReleasableModule> projects = context.analyzeReactor()
		List<ReleaseModuleProblem> problems = context.validate(projects)
		log.info("Analyzed reactor modules")
		
		if(problems){
			log.error("${problems.size()} were found")
			log.error("------------------------------")
			StringWriter err = new StringWriter() 
			problems.each { ReleaseModuleProblem problem ->
				log.error(problem.toString())
				err.println problem.toString()
			}
			log.error("------------------------------")
			throw new MojoExecutionException(err.toString())
		}
		
		log.info("Writing release-info.json")
		JsonBuilder json = new JsonBuilder()
		json {
			buildNumber buildNumber
			root project.id
			modules projects.collect{ it.asJsonMap() }
		}
		if(releaseInfoFile.exists()){ releaseInfoFile.delete() }
		releaseInfoFile.withWriter{w -> w << json.toPrettyString() } 
		log.info("release-info.json was written")

		if(!dryRun){
			log.info("Getting scm url")
			String scmUrl = project?.scm.developerConnection
			log.info("Got scm url: ${scmUrl}. Initializing scm repository")
			ScmOperations scmOperations = createScmOperations(scmUrl)
			log.info("Committing release info file")
			scmOperations.commitReleaseInfo(releaseInfoFile, "releas-info")
		}
// 		SHOULD BE USED In perform stage				
//		log.info("Changing pom files")
//		PomManipulator pomManipulator = new PomManipulator()
//		projects.each {
//			log.info("Changing ${it.file}")
//			pomManipulator.updatePom(it, basedir, log)
//		}
//		log.info("pom files were changed")
	}
	String getScmUrl(MavenProject project){
		String scmUrl = project?.scm.developerConnection
		if(!scmUrl){
			throw new IllegalStateException("No SCM URL is defined")
		}
	}
	ScmOperations createScmOperations(String scmUrl){
		ScmRepository repository = scmManager.makeScmRepository(scmUrl)
		ScmOperations operations = new DefaultScmOperations(scmRepository: repository)
	}
}
