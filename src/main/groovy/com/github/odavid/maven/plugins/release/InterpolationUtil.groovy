package com.github.odavid.maven.plugins.release

import org.apache.maven.execution.MavenSession
import org.apache.maven.model.Dependency
import org.apache.maven.model.Model
import org.apache.maven.model.Parent
import org.apache.maven.project.MavenProject
import org.codehaus.plexus.interpolation.EnvarBasedValueSource
import org.codehaus.plexus.interpolation.ObjectBasedValueSource
import org.codehaus.plexus.interpolation.PrefixedValueSourceWrapper
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource
import org.codehaus.plexus.interpolation.RegexBasedInterpolator

class InterpolationUtil {
	
	static final String interpolate(String source, Properties systemProperties, Properties userProperties, Properties projectProperties, Model projectModel){
		if(!source) return source
		RegexBasedInterpolator interpolator = new RegexBasedInterpolator();
		interpolator.addValueSource( new EnvarBasedValueSource() );
		interpolator.addValueSource( new PropertiesBasedValueSource( systemProperties) );
		interpolator.addValueSource( new PropertiesBasedValueSource( userProperties ) );
		interpolator.addValueSource( new PropertiesBasedValueSource( projectProperties) );
		interpolator.addValueSource( new PrefixedValueSourceWrapper( new ObjectBasedValueSource( projectModel ), ['project.'], true ))
		interpolator.interpolate(source)
	}
	
	static final String interpolateWithProject(String source, MavenProject project, MavenSession session){
		interpolate(source, session.systemProperties, session.userProperties, project.properties, project.model)	
	}

	static final Parent interpolateParent(Parent source, MavenProject project, MavenSession session)	{
		if(!source) return source
		Parent interpolated = source.clone()
		interpolated.groupId = interpolateWithProject(source.groupId, project, session)
		interpolated.artifactId = interpolateWithProject(source.artifactId, project, session)
		interpolated.version = interpolateWithProject(source.version, project, session)
		interpolated
	}
	static final Dependency interpolateDependency(Dependency source, MavenProject project, MavenSession session)	{
		Dependency interpolated = source.clone()
		interpolated.groupId = interpolateWithProject(source.groupId, project, session)
		interpolated.artifactId = interpolateWithProject(source.artifactId, project, session)
		interpolated.version = interpolateWithProject(source.version, project, session)
		interpolated.type = interpolateWithProject(source.type, project, session)
		interpolated.classifier = interpolateWithProject(source.classifier, project, session)
		interpolated
	}

}
