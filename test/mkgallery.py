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

from optparse import OptionParser
from subprocess import STDOUT, PIPE
import os
import os.path
import subprocess

parser = OptionParser()
parser.add_option('--exec', dest='target', default='java -jar Vash.jar')
parser.add_option('--count', dest='count', default='100')
parser.add_option('--mode', dest='mode', default='auto')
parser.add_option('--verbose', dest='verbose', action="store_true")
options, args = parser.parse_args()

if not os.path.isdir('gallery'):
	os.mkdir('gallery')

COUNT = int(options.count)
PROGRAM = options.target.split()

if len(args):
	keys = [int(arg) for arg in args]
else:
	keys = range(1, COUNT + 1)

for i in keys:
	seed = "%04d" % i
	print("AT: {}".format(seed))
	genargs = PROGRAM + [
				'--width', '256', '--height', '128',
				'--data', seed, '--algorithm', '1-fast',
				'--output', './gallery/%s.png' % seed]
	proc = subprocess.Popen(genargs, stdin=None, stdout=PIPE, stderr=STDOUT)
	data = proc.communicate()
	if proc.returncode != 0 or options.verbose:
		print("Command:")
		print(' '.join(genargs))
		print(data[0])
	else:
		with open('./gallery/%s-fmt.txt' % seed, 'wb') as fp:
			fp.write(data[0])

