VERSION=1.1.0

doc:
	javadoc -public -sourcepath src -subpackages vash -overview src/overview.html -breakiterator -d doc -use -version -windowtitle "Vash Documentation" -linksource

jar:
	jar cfm Vash.jar src/manifest.mf -C bin vash/ -C bin ec/

dist: jar doc
	mkdir -p dist
	mkdir -p vash-${VERSION}
	cp -ra doc src Vash.jar README.md LICENSE vash-${VERSION}/
	tar -czvf dist/vash-${VERSION}.tar.gz vash-${VERSION}
	tar -cjvf dist/vash-${VERSION}.tar.bz2 vash-${VERSION}
	tar -cJvf dist/vash-${VERSION}.tar.xz vash-${VERSION}
	zip -r9 dist/vash-${VERSION}.zip vash-${VERSION}
	rm -rf vash-${VERSION}

clean:
	rm -rf test/diff/*
	rm -rf test/result/*
	rm -rf gallery/*.*
	rm -rf gallery/trees/*
	rm -f *.jar
	rm -rf dist
	rm -rf doc

