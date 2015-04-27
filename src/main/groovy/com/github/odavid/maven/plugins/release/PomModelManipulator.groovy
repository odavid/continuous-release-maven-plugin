package com.github.odavid.maven.plugins.release

interface PomModelManipulator {
	void updatePom(Reader originalPomReader, ReleasableModule releasableModule, Writer manipulatedPomWriter)

}
