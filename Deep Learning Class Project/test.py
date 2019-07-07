import numpy as np
import scipy.misc
import tensorflow as tf
import tensorflow.contrib.slim as slim

import slim_net
from util import *
from PIL import Image

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


def save_image(result, filename, orig_image, background_img, x, y, max_width, max_height):
    _, h, w = result.shape
    result = result.reshape(h * w)
    orig_image = orig_image.reshape(h * w, 3)
    image = []
    for i in range(len(result)):
        image.append((*orig_image[i], result[i] * 255))
    image = np.array(image)
    image = np.reshape(image, (h, w, 4))
    scipy.misc.imsave(filename, image)

    resized_img = resize_img(Image.fromarray(image.astype('uint8'), 'RGBA'), max_width, max_height)
    print(resized_img.shape)
    scipy.misc.imsave("resized.png", resized_img)

    background = Image.open(background_img).convert('RGB')
    foreground = Image.open("resized.png")
    blended_img = blend(foreground, background, x, y)

    print(blended_img.shape)
    scipy.misc.imsave("blended.png", blended_img)


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


if __name__ == '__main__':
    result_image, orig_image = test("image.jpg")
    save_image(result_image, "result.png", orig_image, "hokusai.jpg", 350, 200, 250, 250)
