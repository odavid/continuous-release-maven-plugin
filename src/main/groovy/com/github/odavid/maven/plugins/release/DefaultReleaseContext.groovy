package com.github.odavid.maven.plugins.release

import org.apache.maven.execution.MavenSession
import org.apache.maven.model.Dependency
import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject

class DefaultReleaseContext implements ReleaseContext{
	final RepositoryMetadataService repositoryMetadataService
	final MavenSession session
	final MavenProject root
	final List<MavenProject> reactorProjects
	final long buildNumber
	final Map<String,MavenProject> idsToProjects;
	final Log log

	DefaultReleaseContext(RepositoryMetadataService repositoryMetadataService, MavenSession session, MavenProject root, List<MavenProject> reactorProjects, long buildNumber, Log log){
		this.repositoryMetadataService = repositoryMetadataService
		this.session = session
		this.root = root
		this.reactorProjects = reactorProjects
		this.buildNumber = buildNumber
		this.idsToProjects = reactorProjects.inject([:]) { map, project -> map << [("${project.groupId}:${project.artifactId}" as String): project] }
		this.log = log
	}
	
	List<ReleasableModule> analyzeReactor(){
		reactorProjects.collect{ MavenProject project ->
			fromProject(project)
		}
	}
	List<ReleaseModuleProblem> validate(List<ReleasableModule> modules){
		List<ReleaseModuleProblem> problems = []
		modules.each { it.validate(problems) }
		problems
	}
	
	
	ReleasableModule fromProject(MavenProject project){
		new ReleasableModule(file: relativePomPathToRoot(project), 
			coordinates: [
				groupId: project.groupId, 
				artifactId: project.artifactId, 
				type: project.packaging,
				version: releaseVersion(project)], 
			parent: parentOf(project), 
			dependencies: dependenciesOf(project),
			dependencyManagement: dependencyManagementOf(project),
			boms: bomsOf(project))
	}
	
	

	@Override
	public String relativePomPathToRoot(MavenProject project) {
		File pomFile = project.getFile()
		root.getFile().getParentFile().toURI().relativize(pomFile.toURI()).getPath()
	}

	@Override
	public Version releaseVersion(MavenProject project) {
		toReleaseVersion(project.version)
	}

	@Override
	public Version releaseVersionForExternalArtifact(String groupId, String artifactId, String type, String classifier, String version) {
		String lastReleaseVersion = repositoryMetadataService.lastReleaseVersion(groupId, artifactId, type, classifier, version)
		lastReleaseVersion ? new Version(snapshot: version, release: lastReleaseVersion) : null
	}


	@Override
	public boolean isPartOfReactor(String groupId, String artifactId) {
		idsToProjects["${groupId}:${artifactId}" as String]
	}

	@Override
	public boolean hasSnapshotParent(MavenProject project) {
		return project.parent && isSnapshot(project.parent.version);
	}

	@Override
	public SnapshotArtifact parentOf(MavenProject project) {
		MavenProject parent = project.parent
		SnapshotArtifact snapshotArtifact = null
		if(parent && isSnapshot(parent.version)){
			def internal = isPartOfReactor(parent.groupId, parent.artifactId)
			def releaseVersion = internal ? releaseVersion(parent) : releaseVersionForExternalArtifact(parent.groupId, parent.artifactId, 'pom', null, parent.version)
			snapshotArtifact = new SnapshotArtifact(
				coordinates: [
					groupId: parent.groupId, 
					artifactId: parent.artifactId, 
					type: 'pom', 
					version: releaseVersion], 
				external: !internal) 
		}
		snapshotArtifact
	}

	@Override
	public List<SnapshotArtifact> dependenciesOf(MavenProject project) {
		dependenciesToSnapshotArtifacts(project, project.originalModel.dependencies) { Dependency dependency ->
			new LocationKey(groupId: dependency.groupId, artifactId: dependency.artifactId, type: dependency.type, classifier: dependency.classifier)
		}
	}

	@Override
	public List<SnapshotArtifact> bomsOf(MavenProject project) {
		dependenciesToSnapshotArtifacts(project, project.originalModel.dependencyManagement?.dependencies.findAll { Dependency dependency -> dependency.scope == 'import' } ) { Dependency dependency ->
			new LocationKey(groupId: dependency.groupId, artifactId: dependency.artifactId, type: dependency.type, classifier: dependency.classifier)
		}
	}

	@Override
	public List<SnapshotArtifact> dependencyManagementOf(MavenProject project) {
		dependenciesToSnapshotArtifacts(project, project.originalModel.dependencyManagement?.dependencies.findAll { Dependency dependency -> dependency.scope != 'import' } ) { Dependency dependency ->
			new LocationKey(groupId: dependency.groupId, artifactId: dependency.artifactId, type: dependency.type, classifier: dependency.classifier)
		}
	}

	private List<SnapshotArtifact> dependenciesToSnapshotArtifacts(MavenProject project, List<Dependency> dependencies, Closure locationKeyProducer){
		dependencies?.collect { Dependency dependency ->
			Dependency interpolatedDependency = InterpolationUtil.interpolateDependency(dependency, project, session)
			SnapshotArtifact snapshotArtifact = null
			if(isSnapshot(interpolatedDependency.version)){
				def internal = isPartOfReactor(interpolatedDependency.groupId, interpolatedDependency.artifactId)
				def releaseVersion =  internal ? toReleaseVersion(interpolatedDependency.version) :
						releaseVersionForExternalArtifact(interpolatedDependency.groupId, interpolatedDependency.artifactId, interpolatedDependency.type, interpolatedDependency.classifier, interpolatedDependency.version)
				def locationKey = locationKeyProducer(dependency)
				snapshotArtifact = new SnapshotArtifact(
					coordinates: [
						groupId: interpolatedDependency.groupId, 
						artifactId: interpolatedDependency.artifactId, 
						type: interpolatedDependency.type, 
						classifier: interpolatedDependency.classifier, 
						version: releaseVersion
					], 
					locationKey: locationKey,
					external: !internal)
			}
			snapshotArtifact
		}.grep()
	}

	private boolean isSnapshot(String version){
		version?.endsWith('-SNAPSHOT')
	}

	private Version toReleaseVersion(String version){
		new Version(snapshot: version, release: version.minus('-SNAPSHOT') + '.' + buildNumber)
	}
}
