#!/usr/bin/python2
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
import os.path
import matplotlib.image as mpimg
import numpy as np


ALL = []
for fn in os.listdir("wordlist"):
	print(fn)
	path = os.path.join("wordlist", fn)
	img = mpimg.imread(path)
	ALL.append((fn, img))


def diversity(img):
	THRESHOLD = 8.0 / 256.0
	different = 0
	R, G, B = np.dsplit(img, 3)
	R, G, B = R.flatten(), G.flatten(), B.flatten()
	R -= np.average(R)
	G -= np.average(G)
	B -= np.average(B)
	up = np.empty([len(R)])
	down = np.empty([len(R)])
	up.fill(THRESHOLD)
	down.fill(-THRESHOLD)
	cnt = (
		np.sum(np.greater(R, up)) +
		np.sum(np.greater(G, up)) +
		np.sum(np.greater(B, up)) +
		np.sum(np.less(R, down)) +
		np.sum(np.less(G, down)) +
		np.sum(np.less(B, down))
	)
	return float(cnt) / (len(img) * len(img[0]) * 3)


# compute image diversity
print "Compute per-image diversity"
DIVERSITY = []
for fn, img in ALL:
	d = diversity(img)
	DIVERSITY.append((d, fn))
	print fn, d

DIVERSITY.sort()
with open("diversity.txt", "w") as fp:
	for d, fn in DIVERSITY:
		fp.write("{} {}\n".format(d, fn))
MIN_DIVERSITY = 0.05
with open("diversity_files.txt", "w") as fp:
	for d, fn in DIVERSITY:
		if d < MIN_DIVERSITY:
			fp.write("{}\n".format(fn))
		else:
			break

