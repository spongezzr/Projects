READ ME

Requirement: 

1. Chainer(https://github.com/pfnet/chainer)

```
$ pip install chainer
```

2. VGG16 model 

Use the following command to download and install VGG16 model. 

```
sh setup_model.sh
```

Train: 

1. Download training data from https://people.eecs.berkeley.edu/~nzhang/datasets/pipa_train.tar

2. Use following code to train a model from a style image. The model will be saved in the same file path with the name of that style image. 

```
python train.py -s <style_image_path> -d <training_dataset_path> -g <use_gpu ? gpu_id : -1>
```