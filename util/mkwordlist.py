#!/usr/bin/python3
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
from generate import ALGORITHM, vashify
import os
import sys

# for unix based os', this is where cracklib stores its dictionaries
DICT_PATH = "/usr/share/dict"
def main():
	start = None
	if len(sys.argv) > 1:
		start = sys.argv[1]

	for fn in os.listdir(DICT_PATH):
		wordlist = os.path.join(DICT_PATH, fn)
		with open(wordlist, "r", encoding="UTF-8") as fp:
			for line in fp:
				if start is not None and line < start: continue
				line = line.strip()
				print("Generating: {}".format(line))
				vashify(algorithm=ALGORITHM, data=line, output="wordlist/" + line + ".png", width=64, height=64)

if __name__ == "__main__":
	main()

