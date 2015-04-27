package com.github.odavid.maven.plugins.release;

import static org.junit.Assert.*

import java.util.Properties;

import org.apache.maven.model.Model
import org.apache.maven.model.io.DefaultModelReader
import org.apache.maven.model.io.ModelReader
import org.junit.Before
import org.junit.Test

class InterpolationUtilTest {
	Model model;
	@Before
	public void setUp(){
		ModelReader reader = new DefaultModelReader()
		String pom =
		"""
		<project>
			<modelVersion>4.0.0</modelVersion>
			<groupId>gid</groupId>
			<artifactId>aid</artifactId>
			<version>1.2.3-SNAPSHOT</version>
		</project>
        """
		model = reader.read(new StringReader(pom), null)

	}
	@Test
	public void interpolateProjectVersion() {
		assertEquals('1.2.3-SNAPSHOT', InterpolationUtil.interpolate('${project.version}' as String, new Properties(), new Properties(), new Properties(), model))
	}

}
