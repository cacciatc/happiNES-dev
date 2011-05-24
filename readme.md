happiNES-dev
============

An attempt at bolting on some NES development tools to the Arduino PDE (much like Arduino built on top of Processing).

Building
--------
You'll need a java development environment setup to build.

1. Download the source.
2. Navigate to the build directory and run `ant <linux|windows|macosx>-build`.  For example, `ant windows-build`
3. Check the build folder for your output.

Getting Started
---------------
1. You will need python running on your machine.  Can be downloaded [here](http://www.python.org/getit/).
2. Build from source or download a distribution: [linux](http://www.mediocreradio.com/happiNES-dev/downloads/linux/happiNES-dev-001.zip) or [windows](http://www.mediocreradio.com/happiNES-dev/downloads/windows/happiNES-dev-001.zip).  Sorry, no mac at the moment.
3. Unzip and fire up the happiNES-dev executable.
4. For linux or macos users, you will have to navigate to the tools/assember/Ophis-1.0 folder and run `sudo python setup.py install` to install Ophis.
5. I recommend one of Michael Martin's projects: 
     * [NES 101](http://nesdev.parodius.com/NES101.zip)
     * [Galaxy Control](http://nesdev.parodius.com/Galaxy_Patrol.zip)
     * [Diffusion Chamber](http://nesdev.parodius.com/diffuse.zip)
     * Download, unzip, and drag and drop the files from one of the projects into the IDE; although you have to make sure that the file with the entry point is loaded first (this will be fixed soon).   

Dependencies
-----------
* python - Needed to run the assembler.

Notes
-----
* Building on macosx may take some tinkering.  I do not have access for testing 
* The assembler is [Ophis](https://hkn.eecs.berkeley.edu/~mcmartin/ophis/) which uses the p65 spec.  Copyright 2006-7 Michael Martin
* The emulator is [vNES](http://www.thatsanderskid.com/programming/vnes/index.html).  Copyright 2006-2010 Jamie Sanders