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

import os
import subprocess

GOAL = os.listdir('test/goal')
GOAL.sort(key=lambda s: int(s[4:-4]))
for img in GOAL:
	src = os.path.join(os.getcwd(), 'test', 'goal', img)
	tgt = os.path.join(os.getcwd(), 'test', 'results', img[4:-4] + '.png')
	res = os.path.join(os.getcwd(), 'test', 'diff', img[4:])
	
	cmpargs = ['compare', tgt, src,
					'-metric', 'ae', '-fuzz', '1%', res]
	result = subprocess.Popen(cmpargs, stdin=None, stdout=subprocess.PIPE, stderr=subprocess.STDOUT).communicate()
	result = result[0].strip()
	msg = 'OK' if result == b'0' else 'FAIL'

	print(src[len(os.getcwd()) + 1:], '?=', tgt[len(os.getcwd()) + 1:], ':', msg)#, result)

