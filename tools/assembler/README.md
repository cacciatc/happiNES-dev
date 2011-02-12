P65 Assembler
-------------
-------------

This a modified version of Michael Martin's Perl 6502 assembler.  This version has an extra flag "-l" which when set will cause the assembler to emit a listing file to accompany the binary.  Emitted listing files can then be consumed by the happiNES debugger.

Example usage with the new flag (assuming a source file called "tutor.p65"):

    perl p65.pl -l tutor.p65 tutor

The above line would generate two files:

* tutor.nes
* tutor.lis