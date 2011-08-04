# Copyright 2011, Zettabyte Storage LLC
# 
# This file is part of Vash.
#
# Vash is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# Vash is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with Vash.  If not, see <http://www.gnu.org/licenses/>.
VERSION=1.1.0

doc:
	javadoc -public -sourcepath src -subpackages vash -overview src/overview.html -breakiterator -d doc -use -version -windowtitle "Vash Documentation" -linksource

jar:
	jar cfm Vash.jar src/manifest.mf -C bin vash/ -C bin ec/ -C bin util/

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

