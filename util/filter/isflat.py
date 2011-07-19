#!/usr/bin/python2
import sys
import png

if len(sys.argv) < 2:
	print("No Input!")
	sys.exit(0)

threshold=8


def diff(a, b):
        return abs(a-b) > threshold

def average(values):
         return sum(values, 0.0) / len(values)

png_pixels=png.Reader(filename=sys.argv[1]).read_flat()[2]
current = []
pixels = []
for i,pix in enumerate(png_pixels):
        current.append(pix)
        if not (i%3 == 2): continue
        pixels.append(current)
        current = []
r,g,b = zip(*pixels)

baseline = [average(r), average(g), average(b)]

same = 0
different = 0

for pix in pixels:
        if any((diff(pix[0],baseline[0]), diff(pix[1], baseline[1]), diff(pix[2], baseline[2]))):
                different += 1
        else:
                same += 1

diversity = float((len(pixels)-same))/len(pixels)
print("{} {} {} {}".format(sys.argv[1], same, different, int(diversity > 0.05)))

sys.exit(int(diversity <= 0.05))

