import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class Debugger {
	private CPU mycpu;
	private Socket forPDETalking;
	private byte[] debugCmds;
	
	// debug commands
	public static int BREAK      = 0x00;
	public static int STEP_OVER  = 0x01;
	public static int STEP_INTO  = 0x02;
	public static int STEP_OUT   = 0x03;
	public static int CONTINUE   = 0x04;
	public static int NO_DEBUG   = 0x05;
	
	private boolean weAreOff;
	// k = filename, v = existence (true)
	private HashMap<Integer,String> breakpoints;
	// a collection of methods that return a boolean
	private ArrayList<Method> watches;
	
	public Debugger(CPU cpu){
		mycpu = cpu;
		weAreOff = false;
		breakpoints = new HashMap<Integer, String>();
		watches = new ArrayList<Method>();
		debugCmds = new byte[256];
		
		try {
			forPDETalking = new Socket("localhost",1045);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("PDE aien't listening or isn't running.");
			weAreOff = true;
		}
	}
	public boolean saysToWait(int opaddr){
		if(weAreOff){
			return false;
		}
		try {
			if(forPDETalking.getInputStream().available() > 0){
				forPDETalking.getInputStream().read(debugCmds, 0, 256);
				if(debugCmds[0] == NO_DEBUG){
					weAreOff = true;
				}
				System.out.println(debugCmds);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// will be more complicated for memory mappers other than 0
		if(breakpoints.get(opaddr) != null){
			System.out.println("break");
		}
		for(Method watch : watches){
			try {
				if((Boolean) watch.invoke(opaddr, mycpu)){
					System.out.println(watch.getName() + " fired!");
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	public void doesItsThing(){
		// System.out.println(">");
	}
}
