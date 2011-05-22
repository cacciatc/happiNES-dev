happiNES-dev
============

An attempt at bolting on some NES development tools to the Arduino PDE (much like Arduino built on top of Processing).

Building
--------
* Download the source.
* Run ant run <linux|windows|macos>

Getting Started
---------------
* Download a distribution.
* Unzip and fire it up.  
* Navigate to the tools/assember/Ophis-1.0 folder and run sudo python setup.py install to install Ophis.

Dependencies
-----------
* python - Needed to run the assembler.
* jdk and ant.


Notes
-----
* Not truly cross-platform at the moment.  I am only building and testing on windows, but it is possible to get going on other platforms with a little work.
* The assembler is [Ophis](https://hkn.eecs.berkeley.edu/~mcmartin/ophis/) which uses the p65 spec.  Copyright 2006-7 Michael Martin
* The emulator is [vNES](http://www.thatsanderskid.com/programming/vnes/index.html).  Copyright 2006-2010 Jamie Sanders