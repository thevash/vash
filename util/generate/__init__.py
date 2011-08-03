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
import subprocess

# common seed location, so we can update it for all utilities at once
ALGORITHM = "1.1"
VASH_LOCATION = "Vash.jar"

def vashify(algorithm=None, data=None, output=None, width=128, height=128):
	if algorithm is None or data is None or output is None:
		raise ValueError("No value passed for one of (algorithm, data, output)")
		
	args = ['java', '-jar', VASH_LOCATION,
				'--width', str(width), '--height', str(height),
				'--data', data, '--algorithm', algorithm,
				'--output', output]
	proc = subprocess.Popen(
		args, 
		stdin=None, 
		stdout=subprocess.PIPE, 
		stderr=subprocess.STDOUT)
	data = proc.communicate()
	if proc.returncode != 0:
		print("Command:")
		print(' '.join(args))
		print(data[0])

