from __future__ import print_function
import numpy as np
import argparse
import scipy.misc
from PIL import Image, ImageFilter
import time
from util import *

import tensorflow as tf
import tensorflow.contrib.slim as slim
import slim_net

import chainer
from chainer import cuda, Variable, serializers
from net import *

parser = argparse.ArgumentParser(description='Real-time style transfer image generator')
parser.add_argument('input')
parser.add_argument('style_image')
parser.add_argument('--x_pos', '-x', default=350, type=int)
parser.add_argument('--y_pos', '-y', default=200, type=int)
parser.add_argument('--max_width', default=200, type=int)
parser.add_argument('--max_height', default=200, type=int)
parser.add_argument('--gpu', '-g', default=-1, type=int,
                    help='GPU ID (negative value indicates CPU)')
parser.add_argument('--model', '-m', default='models/style.model', type=str)
parser.add_argument('--out', '-o', default='out.jpg', type=str)
parser.add_argument('--median_filter', default=3, type=int)
parser.add_argument('--keep_colors', action='store_true')
parser.set_defaults(keep_colors=False)
args = parser.parse_args()


NUM_CLASSES = 2

COLOR_SET = [
    [255, 255, 255], [125, 135, 185], [190, 193, 212], [214, 188, 192],
    [187, 119, 132], [142, 6, 59], [74, 111, 227], [133, 149, 225],
    [181, 187, 227], [230, 175, 185], [224, 123, 145], [211, 63, 106],
    [17, 198, 56], [141, 213, 147], [198, 222, 199], [234, 211, 198],
    [240, 185, 141], [239, 151, 8], [15, 207, 192], [156, 222, 214],
    [213, 234, 231], [243, 225, 235], [246, 196, 225], [247, 156, 212]
]


def build_image(filename):
    MEAN_VALUES = np.array([104.00698793, 116.66876762, 122.67891434])
    MEAN_VALUES = MEAN_VALUES.reshape((1, 1, 1, 3))
    img = scipy.misc.imread(filename, mode='RGB')[:, :, ::-1]
    height, width, _ = img.shape
    img = np.reshape(img, (1, height, width, 3)) - MEAN_VALUES
    return img


def save_image(result, filename, orig_image):
    _, h, w = result.shape
    result = result.reshape(h * w)
    orig_image = orig_image.reshape(h * w, 3)
    image = []
    for i in range(len(result)):
        image.append((*orig_image[i], result[i] * 255))
    image = np.array(image)
    image = np.reshape(image, (h, w, 4))
    scipy.misc.imsave(filename, image)

    return result

    # resized_img = resize_img(Image.fromarray(image.astype('uint8'), 'RGBA'), max_width, max_height)
    # print(resized_img.shape)
    # scipy.misc.imsave("resized.png", resized_img)

    # background = Image.open(background_img).convert('RGB')
    # foreground = Image.open("resized.png")
    # blended_img = blend(foreground, background, x, y)

    # print(blended_img.shape)
    # scipy.misc.imsave("blended.png", blended_img)


def test(image_name):
    inputs = tf.placeholder(tf.float32, [1, None, None, 3])
    with slim.arg_scope(slim_net.fcn8s_arg_scope()):
        logits, _ = slim_net.fcn8s(inputs, NUM_CLASSES)

    image = build_image(image_name)

    orig_image = scipy.misc.imread(image_name, mode='RGB')

    with tf.Session() as sess:
        saver = tf.train.Saver(tf.global_variables())
        model_file = tf.train.latest_checkpoint('./model/')

        if model_file:
            saver.restore(sess, model_file)
        else:
            raise Exception('Testing needs pre-trained model!')

        feed_dict = {
            inputs: image,
        }
        result = sess.run(tf.argmax(logits, axis=-1), feed_dict=feed_dict)
    return result, orig_image


# from 6o6o's fork. https://github.com/6o6o/chainer-fast-neuralstyle/blob/master/generate.py
def original_colors(original, stylized):
    h, s, v = original.convert('HSV').split()
    hs, ss, vs = stylized.convert('HSV').split()
    return Image.merge('HSV', (h, s, vs)).convert('RGB')

if __name__ == '__main__':
    result_image, orig_image = test(args.input)
    alpha = save_image(result_image, "image_seg.png", orig_image)
    
    model = FastStyleNet()
    serializers.load_npz(args.model, model)
    if args.gpu >= 0:
        cuda.get_device(args.gpu).use()
        model.to_gpu()
    xp = np if args.gpu < 0 else cuda.cupy

    start = time.time()
    original = Image.open("image_seg.png").convert('RGB')
    image = np.asarray(original, dtype=np.float32).transpose(2, 0, 1)
    image = image.reshape((1,) + image.shape)

    image = xp.asarray(image)
    x = Variable(image)

    y = model(x)
    result = cuda.to_cpu(y.data)

    result = np.uint8(result[0].transpose((1, 2, 0)))
    h, w, _ = result.shape

    med = Image.fromarray(result)
    if args.median_filter > 0:
    	med = med.filter(ImageFilter.MedianFilter(args.median_filter))
    if args.keep_colors:
        med = original_colors(original, med)
    med.save("style_transferred.png")
    print(time.time() - start, 'sec')

    result = result.reshape(h * w, 3)

    alp_img = []
    for i in range(len(alpha)):
        alp_img.append((*result[i], alpha[i] * 255))

    image = np.array(alp_img)
    image = np.reshape(image, (h, w, 4))

    scipy.misc.imsave("before_resized.png", image)

    resized_img = resize_img(Image.fromarray(image.astype('uint8'), 'RGBA'), args.max_width, args.max_height)
    print(resized_img.shape)
    scipy.misc.imsave("resized.png", resized_img)

    background = Image.open(args.style_image).convert('RGB')
    foreground = Image.open("resized.png")
    blended_img = blend(foreground, background, args.x_pos, args.y_pos)

    print(blended_img.shape)
    scipy.misc.imsave(args.out, blended_img)
