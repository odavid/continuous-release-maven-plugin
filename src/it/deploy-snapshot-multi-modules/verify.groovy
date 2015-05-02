assert new File( basedir, "target/releases/deploy-test/deploy-snapshot-multi-modules/1.0.0.1/deploy-snapshot-multi-modules-1.0.0.1.pom" ).exists()

File snapshotLocation = new File(basedir, 'target/snapshots/deploy-test/deploy-snapshot-multi-modules/1.0.0-SNAPSHOT/')
assert snapshotLocation.listFiles({d, f-> f ==~ /deploy\-snapshot\-multi\-modules\-.*\.pom$/ } as FilenameFilter) 

assert new File( basedir, "module-a/target/releases/deploy-test/module-a/1.0.0.1/module-a-1.0.0.1.pom" ).exists()
assert new File( basedir, "module-a/target/releases/deploy-test/module-a/1.0.0.1/module-a-1.0.0.1.jar" ).exists()
assert new File( basedir, "module-a/target/releases/deploy-test/module-a/1.0.0.1/module-a-1.0.0.1-file1-classifier.file1-type" ).exists()

snapshotLocation = new File(basedir, 'module-a/target/snapshots/deploy-test/module-a/1.0.0-SNAPSHOT/')
assert snapshotLocation.listFiles({d, f-> f ==~ /module\-a.*\.pom$/ } as FilenameFilter) 
assert snapshotLocation.listFiles({d, f-> f ==~ /module\-a.*\.jar$/ } as FilenameFilter) 
assert snapshotLocation.listFiles({d, f-> f ==~ /module\-a.*\-file1\-classifier\.file1\-type$/ } as FilenameFilter) 
