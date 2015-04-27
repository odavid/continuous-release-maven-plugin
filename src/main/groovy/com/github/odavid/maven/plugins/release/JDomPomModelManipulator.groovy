package com.github.odavid.maven.plugins.release

import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.Namespace
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter

class JDomPomModelManipulator implements PomModelManipulator{

	@Override
	public void updatePom(Reader originalPomReader, ReleasableModule releasableModule, Writer manipulatedPomWriter) {
        String content = originalPomReader.text
		content = content.replaceAll(/(\r\n)|(\n)|(\r)/, System.lineSeparator)
		boolean hasXmlDecl = content.indexOf('<?xml') >= 0
		originalPomReader = new StringReader(content)
		
		SAXBuilder builder = new SAXBuilder();
        Document document = builder.build( originalPomReader );
		changeModel(document, releasableModule)

		Format format = Format.getRawFormat();
		format.omitDeclaration = !hasXmlDecl
		format.lineSeparator = System.lineSeparator
		XMLOutputter out = new XMLOutputter( format )
		
		out.output( document, manipulatedPomWriter );

	}
	
	private void changeModel(Document document, ReleasableModule releasableModule){
		Element rootElement = document.rootElement
		Namespace namespace = rootElement.namespace
		releasableModule.with{
			if(parent){
				rootElement.getChild('parent', namespace).getChild('version', namespace).text = parent.coordinates.version.release
			}
			Element version = rootElement.getChild('version', namespace)
			if(version){
				version.text = coordinates.version.release
			}
			boms?.each { SnapshotArtifact dep ->
				replaceDependencyManagementVersion(dep.locationKey, document, dep.coordinates.version.release)
			}
			dependencyManagement?.each { SnapshotArtifact dep ->
				replaceDependencyManagementVersion(dep.locationKey, document, dep.coordinates.version.release)
			}
			dependencies?.each { SnapshotArtifact dep ->
				replaceDependencyVersion(dep.locationKey, document, dep.coordinates.version.release)
			}
		}
	}

	
	private void replaceDependencyManagementVersion(LocationKey location, Document document, String version) {
		Element rootElement = document.rootElement
		Namespace namespace = rootElement.namespace
		Element container = rootElement.getChild('dependencyManagement', namespace).getChild('dependencies', namespace)
		replaceVersionInFoundDependency(container, namespace, location, version)
	}
	private void replaceDependencyVersion(LocationKey location, Document document, String version) {
		Element rootElement = document.rootElement
		Namespace namespace = rootElement.namespace
		Element container = rootElement.getChild('dependencies', namespace)
		replaceVersionInFoundDependency(container, namespace, location, version)
	}

	private void replaceVersionInFoundDependency(Element container, Namespace namespace, LocationKey location, String version){
		Element dependency = container.children.find{
			boolean met = it.getChild('groupId', namespace).text == location.groupId && it.getChild('artifactId', namespace).text == location.artifactId
			if(met){
				if(location.type != 'jar'){
					met = it.getChild('type', namespace)?.text == location.type
				}
				if(met){
					if(location.classifier){
						met = it.getChild('classifier', namespace)?.text == location.classifier
					}
				}
			}
			met
		}
		if(dependency){
			dependency.getChild('version', namespace).text = version
		}else{
			throw new IllegalStateException("Could not find dependency ${location} in project")
		}

	}
}
