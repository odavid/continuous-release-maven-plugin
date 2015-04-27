package com.github.odavid.maven.plugins.release

import org.apache.maven.model.Model
import org.apache.maven.model.io.DefaultModelReader
import org.apache.maven.model.io.DefaultModelWriter
import org.apache.maven.model.io.ModelReader
import org.apache.maven.model.io.ModelWriter
import org.apache.maven.plugin.logging.Log

class PomManipulator {
	void updatePom(ReleasableModule module, File basedir, Log log, boolean withBackup = true) throws IOException{
		File pom = new File(basedir, module.file)
		String origContents = pom.text
		
		if(withBackup){
			File backup = new File(basedir, module.file + '.snapshot')
			if(backup.exists()){
				log.warn("Backup file ${backup} already exists. Trying to delete")
				backup.delete()
			}
			if(!pom.renameTo(backup)){
				throw new IOException("Could not backup pom file: ${pom}")
			}
		}
		pom = new File(basedir, module.file)
		StringWriter out = new StringWriter()
		new JDomPomModelManipulator().updatePom(new StringReader(origContents), module, out)
		pom << out.toString()
	}
}
