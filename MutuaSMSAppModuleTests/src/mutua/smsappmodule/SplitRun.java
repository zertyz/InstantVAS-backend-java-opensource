package mutua.smsappmodule;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Test;

/** <pre>
 * SplitRun.java
 * =============
 * (created by luiz, Jul 25, 2015)
 *
 * Allows several threads to run, simultaneously, their tasks -- waiting for all of them
 * to finish, ideal for profiling/tuning and reentrancy tests purposes.
 * 
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public abstract class SplitRun extends Thread {

	public static ArrayList<SplitRun> instances = new ArrayList<SplitRun>();
	
	public static void add(SplitRun instance) {
		instances.add(instance);
	}
	
	private static void reset() {
		instances.clear();
	}
	
	public static void runAndWaitForAll() throws InterruptedException {

		// run
		for (SplitRun instance : instances) {
			instance.start();
		}
		
		// wait
		for (SplitRun instance : instances) {
			synchronized (instance) {
				if (instance.running) {
					instance.wait();
				}
			}
		}
		
		// prepare for the next use
		reset();
	}
	
	public abstract void splitRun(int arg) throws Throwable;

	private int arg;
	
	public SplitRun(int arg) {
		this.arg = arg;
	}

	public boolean running = true;
	@Override
	public void run() {
		try {
			splitRun(arg);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		running = false;
		synchronized (this) {
			notify();
		}
	}
	
}
