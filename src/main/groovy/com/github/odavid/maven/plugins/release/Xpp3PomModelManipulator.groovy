package com.github.odavid.maven.plugins.release

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model
import org.apache.maven.model.io.DefaultModelReader
import org.apache.maven.model.io.DefaultModelWriter
import org.apache.maven.model.io.ModelReader
import org.apache.maven.model.io.ModelWriter

class Xpp3PomModelManipulator implements PomModelManipulator{

	@Override
	public void updatePom(Reader originalPomReader, ReleasableModule releasableModule, Writer manipulatedPomWriter) {
		ModelReader modelReader = new DefaultModelReader()
		Model model = modelReader.read(originalPomReader, [:])
		Model newModel = changeModel(model, releasableModule)
		
		ModelWriter modelWriter = new DefaultModelWriter()
		modelWriter.write(manipulatedPomWriter, [:], newModel)
	}
	
	private Model changeModel(Model model, ReleasableModule releasableModule){
		model = model.clone()
		releasableModule.with{
			if(parent){
				model.parent.version = parent.coordinates.version.release
			}
			if(model.version){
				model.version = coordinates.version.release
			}
			boms?.each { SnapshotArtifact dep ->
				findDependencyManagement(dep.locationKey, model).version = dep.coordinates.version.release
			}
			dependencyManagement?.each { SnapshotArtifact dep ->
				findDependencyManagement(dep.locationKey, model).version = dep.coordinates.version.release
			}
			dependencies?.each { SnapshotArtifact dep ->
				findDependency(dep.locationKey, model).version = dep.coordinates.version.release
			}
		}
		model
	}
	
	private Dependency findDependency(LocationKey location, Model model) {
		location.with{
			model.dependencies.find{ Dependency dep ->
				dep.groupId == groupId && dep.artifactId == artifactId && dep.type == type && dep.classifier == classifier
			}
		}
	}
	
	private Dependency findDependencyManagement(LocationKey location, Model model) {
		location.with{
			model.dependencyManagement.dependencies.find{ Dependency dep ->
				dep.groupId == groupId && dep.artifactId == artifactId && dep.type == type && dep.classifier == classifier
			}
		}
	}
}
