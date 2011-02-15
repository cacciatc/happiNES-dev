#!/usr/bin/python

from distutils.core import setup

setup (name='Ophis',
       version='1.0',
       author='Michael Martin',
       author_email='mcmartin@gmail.com',
       url='http://hkn.eecs.berkeley.edu/~mcmartin/ophis/',
       package_dir = {'': 'lib'},
       packages = ['Ophis'],
       scripts = ['ophis'])
       
       
       
