package com.github.odavid.maven.plugins.release

import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.deployer.ArtifactDeployer
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import org.apache.maven.project.MavenProject
import org.apache.maven.project.artifact.ProjectArtifactMetadata
import org.apache.maven.repository.RepositorySystem

import com.github.odavid.maven.plugins.groovy.GroovyMojoWithAntLogger

/**
 * @author odavid
 */
@Mojo(name="deploy-snapshots", requiresProject=true, threadSafe=true, defaultPhase=LifecyclePhase.DEPLOY)
class DeploySnapshotsMojo extends GroovyMojoWithAntLogger{

	@Parameter(defaultValue = '${project.artifact}', readonly = true, required = true)
	private Artifact artifact;
  
	/**
	 * Project POM file.
	 */
	@Parameter(defaultValue = '${project.file}', readonly = true, required = true)
	private File pomFile;
  
	/**
	 * Project's attached artifacts.
	 */
	@Parameter(defaultValue = '${project.attachedArtifacts}', readonly = true, required = true)
	private List<Artifact> attachedArtifacts;
  
	@Parameter(defaultValue = '${project}', readonly = true, required = true)
	private MavenProject project;
	
	@Parameter(defaultValue = '${localRepository}', readonly = true, required = true)
	private ArtifactRepository localRepository;

	@Parameter(defaultValue = '${session}', readonly = true, required = true)
	private MavenSession session;
	
	@Component
	private ArtifactDeployer deployer;
  
	@Component
	private RepositorySystem repositorySystem
	

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if(artifact.isSnapshot()){
			log.info ("Artifact ${project.id} is already a snapshot. skipping snapshot deployment")
			return
		}
		File snapshotPom = new File(pomFile.getParentFile(), 'pom.xml.snapshot')
		if(!snapshotPom.exists()){
			log.warn ("File ${snapshotPom} does not exist, skipping snapshot deployment")
			return
		}
		log.debug("Creating snapshot deployment request: ${project}, ${artifact}, ${snapshotPom}, ${attachedArtifacts}")
		DeploySnapshotRequest request = createSnapshotDeployRequest(project, artifact, snapshotPom, attachedArtifacts)
		deploy(request)
	}
	
	DeploySnapshotRequest createSnapshotDeployRequest(MavenProject project, Artifact releaseArtifact, File snapshotPomFile, List<Artifact> attachedArtifacts){
		boolean isPomArtifact = "pom".equals( project.packaging );
		Artifact snapshotProjectArtifact = repositorySystem.createArtifact(releaseArtifact.groupId, releaseArtifact.artifactId, snapshotVersion(releaseArtifact.version), releaseArtifact.type)
		if(!releaseArtifact.file){
			log.info ("No file for artifact: ${releaseArtifact}")
		}
		snapshotProjectArtifact.file = releaseArtifact.file
		if ( !isPomArtifact ){
			snapshotProjectArtifact.addMetadata( new ProjectArtifactMetadata( snapshotProjectArtifact, snapshotPomFile ));
		}else{
			snapshotProjectArtifact.file = snapshotPomFile 
		}
		List<Artifact> snapshotAttached = attachedArtifacts.collect {
			Artifact a = repositorySystem.createArtifactWithClassifier(it.groupId, it.artifactId, snapshotProjectArtifact.version, it.type, it.classifier)
			a.file = it.file
			a
		}
		
		new DeploySnapshotRequest(moduleArtifacts: [snapshotProjectArtifact] + snapshotAttached)
	}
	
	private void deploy(DeploySnapshotRequest request){
		request.moduleArtifacts.each {
			deployer.deploy(it.file, it, project.snapshotArtifactRepository, localRepository)
		}
	}
	
	private String snapshotVersion(String releaseVersion){
		releaseVersion.replaceAll(/(.*)\.\d+$/, '$1-SNAPSHOT')
	}
}
