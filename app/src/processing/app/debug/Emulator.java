/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the happiNES-dev project - http://github.com/cacciatc/happiNES-dev

  Copyright (c) 2011 Chris Cacciatore

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package processing.app.debug;

import processing.app.Base;
import processing.app.Preferences;
import processing.app.Sketch;
import processing.app.SketchCode;
import processing.core.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import java.nio.*;

public class Emulator implements MessageConsumer {
  static final String BUGS_URL =
    "http://github.com/cacciatc/happiNES-dev";
  static final String SUPER_BADNESS =
    "Compiler error, please submit this code to " + BUGS_URL;

  String romPath;
  String buildPath;
  Sketch sketch;
  
  boolean verbose;
  boolean firstErrorFound;
  boolean secondErrorFound;

  RunnerException exception;

  public Emulator() { }

  /**
   * Emulate with vNES applet.
   *
   * @param romPath Where the rom lives!
   * @param primaryClassName the name of the combined sketch file w/ extension
   * @return true if successful.
   * @throws RunnerException Only if there's a problem. Only then.
   */
  public boolean emulate(Sketch sketch,String buildPath,String romPath, boolean verbose) 
  throws RunnerException {
    this.romPath = romPath;
    this.buildPath = buildPath;
    this.verbose = verbose;
    this.sketch = sketch;

    // create a custom html harness
    String harnessPath = pullAndUpdateHarness(romPath);
    
    // run vNES
    execAsynchronously(getCommandEmulatorString(harnessPath));
   
    return true;
  }
  
  public String pullAndUpdateHarness(String romPath)
  throws RunnerException{
	  // copy the base-harness into build directory
	  String baseHarnessPath = Base.getEmulatorPath() + File.separator + "base-harness.html";
	  String harnessPath = "test." + "happiNES-dev" + ".html";
	  File harnessFile = new File(buildPath, harnessPath);
	  try{
		  // hack due to classpath tom-foolery in the applet
		  Base.copyDir(new File(Base.getEmulatorPath(),""),new File(buildPath,""));
	  }
	  catch (IOException e) {
	    	e.printStackTrace();
	    	throw new RunnerException("Problem moving " + harnessPath + " to the build folder");
	  }
	  // should just modify the base harness, but for now will rewrite it....string manip is a pain in java.
	  try{ 
		  PrintWriter pwr = new PrintWriter(new FileWriter(harnessFile.getAbsolutePath()));
		  pwr.println("<html>");
		  pwr.println("<applet code=\"vNES.class\" archive=\"vNES-213.jar\" width=\"512\" height=\"480\">");
		  pwr.println("<param name=\"rom\" value=\"" + romPath + ".nes\" />");
		  pwr.println("<param name=\"romsize\" value=\"\" />");
		  pwr.println("<param name=\"sound\" value=\""+getEmulatorAttr("emulator.sound").toLowerCase()+"\" />");
		  pwr.println("<param name=\"stereo\" value=\""+getEmulatorAttr("emulator.stereo").toLowerCase()+"\" />");
		  pwr.println("<param name=\"scanlines\" value=\""+getEmulatorAttr("emulator.scanlines").toLowerCase()+"\" />");
		  pwr.println("<param name=\"scale\" value=\""+getEmulatorAttr("emulator.scale").toLowerCase()+"\" />");
		  pwr.println("<param name=\"fps\" value=\""+getEmulatorAttr("emulator.fps").toLowerCase()+"\" />");
		  pwr.println("<param name=\"p1_up\" value=\""+getEmulatorAttr("emulator.p1_up")+"\" />");
		  pwr.println("<param name=\"p1_down\" value=\""+getEmulatorAttr("emulator.p1_down")+"\" />");
		  pwr.println("<param name=\"p1_left\" value=\""+getEmulatorAttr("emulator.p1_left")+"\" />");
		  pwr.println("<param name=\"p1_right\" value=\""+getEmulatorAttr("emulator.p1_right")+"\" />");
		  pwr.println("<param name=\"p1_a\" value=\""+getEmulatorAttr("emulator.p1_a")+"\" />");
		  pwr.println("<param name=\"p1_b\" value=\""+getEmulatorAttr("emulator.p1_b")+"\" />");
		  pwr.println("<param name=\"p1_start\" value=\""+getEmulatorAttr("emulator.p1_start")+"\" />");
		  pwr.println("<param name=\"p1_select\" value=\""+getEmulatorAttr("emulator.p1_select")+"\" />");
		  
		  pwr.println("<param name=\"p2_up\" value=\""+getEmulatorAttr("emulator.p2_up")+"\" />");
		  pwr.println("<param name=\"p2_down\" value=\""+getEmulatorAttr("emulator.p2_down")+"\" />");
		  pwr.println("<param name=\"p2_left\" value=\""+getEmulatorAttr("emulator.p2_left")+"\" />");
		  pwr.println("<param name=\"p2_right\" value=\""+getEmulatorAttr("emulator.p2_right")+"\" />");
		  pwr.println("<param name=\"p2_a\" value=\""+getEmulatorAttr("emulator.p2_a")+"\" />");
		  pwr.println("<param name=\"p2_b\" value=\""+getEmulatorAttr("emulator.p2_b")+"\" />");
		  pwr.println("<param name=\"p2_start\" value=\""+getEmulatorAttr("emulator.p2_start")+"\" />");
		  pwr.println("<param name=\"p2_select\" value=\""+getEmulatorAttr("emulator.p2_select")+"\" />");
		  pwr.println("</applet>");
		  pwr.println("</html>");
		  pwr.flush();
		  pwr.close();
	  }
	  catch (IOException e) {
	    	e.printStackTrace();
	    	throw new RunnerException("Problem updating " + harnessPath + " within the build folder");
	  }
	  return harnessFile.getAbsolutePath();
  }
  
  private String getEmulatorAttr(String key){
	  return Preferences.get(key);
  }

  /**
   * Either succeeds or throws a RunnerException fit for public consumption.
   */
  private void execAsynchronously(List<String> commandList) throws RunnerException {
	  
    String[] command = new String[commandList.size()];
    commandList.toArray(command);
    int result = 0;
    
    if (verbose || Preferences.getBoolean("build.verbose")) {
      for(int j = 0; j < command.length; j++) {
        System.out.print(command[j] + " ");
      }
      System.out.println();
    }

    firstErrorFound = false;  // haven't found any errors yet
    secondErrorFound = false;

    Process process;
    
    try {
      process = Runtime.getRuntime().exec(command);
    } catch (IOException e) {
      RunnerException re = new RunnerException(e.getMessage());
      re.hideStackTrace();
      throw re;
    }

    MessageSiphon in = new MessageSiphon(process.getInputStream(), this);
    MessageSiphon err = new MessageSiphon(process.getErrorStream(), this);

    // wait for the process to finish.  if interrupted
    // before waitFor returns, continue waiting
    boolean emulating = true;
    while (emulating) {
      try {
        if (in.thread != null)
          in.thread.join();
        if (err.thread != null)
          err.thread.join();
        result = process.waitFor();
        //System.out.println("result is " + result);
        emulating = false;
      } catch (InterruptedException ignored) { }
    }

    // an error was queued up by message(), barf this back to compile(),
    // which will barf it back to Editor. if you're having trouble
    // discerning the imagery, consider how cows regurgitate their food
    // to digest it, and the fact that they have five stomaches.
    //
    //System.out.println("throwing up " + exception);
    if (exception != null) { throw exception; }

    if (result > 1) {
      // a failure in the tool (e.g. unable to locate a sub-executable)
      System.err.println(command[0] + " returned " + result);
    }

    if (result != 0) {
      RunnerException re = new RunnerException("Error emulating.");
      re.hideStackTrace();
      throw re;
    }
  }


  /**
   * Part of the MessageConsumer interface, this is called
   * whenever a piece (usually a line) of error message is spewed
   * out from the assembler. The errors are parsed for their contents
   * and line number, which is then reported back to Editor.
   */
  public void message(String s) {
    int i;

    // remove the build path so people only see the filename
    // can't use replaceAll() because the path may have characters in it which
    // have meaning in a regular expression.
    if (!verbose) {
      while ((i = s.indexOf(buildPath + File.separator)) != -1) {
        s = s.substring(0, i) + s.substring(i + (buildPath + File.separator).length());
      }
    }
  
    // look for error line, which contains file name, line number,
    // and at least the first line of the error message
    String errorFormat = "([\\w\\d_]+.\\w+):(\\d+):\\s*error:\\s*(.*)\\s*";
    String[] pieces = PApplet.match(s, errorFormat);

//    if (pieces != null && exception == null) {
//      exception = sketch.placeException(pieces[3], pieces[1], PApplet.parseInt(pieces[2]) - 1);
//      if (exception != null) exception.hideStackTrace();
//    }
    
    if (pieces != null) {
      RunnerException e = sketch.placeException(pieces[3], pieces[1], PApplet.parseInt(pieces[2]) - 1);

      // replace full file path with the name of the sketch tab (unless we're
      // in verbose mode, in which case don't modify the compiler output)
      if (e != null && !verbose) {
        SketchCode code = sketch.getCode(e.getCodeIndex());
        String fileName = code.isExtension(sketch.getDefaultExtension()) ? code.getPrettyName() : code.getFileName();
        s = fileName + ":" + e.getCodeLine() + ": error: " + e.getMessage();        
      }       
      
      if (exception == null && e != null) {
        exception = e;
        exception.hideStackTrace();
      }      
    }
    
    System.err.print(s);
  }

  /////////////////////////////////////////////////////////////////////////////

  /**
   * Generates the string we feeding to the emulator.  Note: the java bin must be in the path!
   */
  static private List<String> getCommandEmulatorString(String harnessPath) {
    List<String> baseCommandEmulator = new ArrayList<String>(Arrays.asList(new String[] {
      "appletviewer",
      harnessPath
    }));
    return baseCommandEmulator;
  }

}
