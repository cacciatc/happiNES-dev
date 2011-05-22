happiNES-dev
============

An attempt at bolting on some NES development tools to the Arduino PDE (much like Arduino built on top of Processing).

Building
--------
You'll need a java development environment setup to build.
* Download the source.
* Navigate to the build directory and run 
  ant <linux|windows|macosx>-build 
For example, 
  ant windows-build
* Check the build folder for your output.

Getting Started
---------------
* You will need python running on your machine.  Can be downloaded [here](http://www.python.org/getit/).
* Build from source or download a distribution: [linux](http://www.mediocreradio.com/happiNES-dev/downloads/linux/happiNES-dev-001.zip) or [windows](http://www.mediocreradio.com/happiNES-dev/downloads/windows/happiNES-dev-001.zip).  Sorry, no mac at the moment.
* Unzip and fire up the happiNES-dev executable.
* For linux or macos users, you will have to navigate to the tools/assember/Ophis-1.0 folder and run 
  sudo python setup.py install
to install Ophis.

Dependencies
-----------
* python - Needed to run the assembler.

Notes
-----
* Building on macos may take some tinkering.  I do not have access to for testing 
* The assembler is [Ophis](https://hkn.eecs.berkeley.edu/~mcmartin/ophis/) which uses the p65 spec.  Copyright 2006-7 Michael Martin
* The emulator is [vNES](http://www.thatsanderskid.com/programming/vnes/index.html).  Copyright 2006-2010 Jamie Sanders