from PIL import Image
import numpy

def resize_img(img, max_width, max_height):
	img.thumbnail((max_width, max_height), Image.ANTIALIAS)
	return numpy.array(img)

def blend(img, background, x, y):
	background.paste(img, (x, y), img.convert('RGBA'))
	return numpy.array(background)