package src.ca.mcgill.ecse211.ultrasonic;


import java.util.concurrent.locks.Condition;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import src.ca.mcgill.ecse211.lab3.Lab3;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

/**
 * Control of the wall follower is applied periodically by the UltrasonicPoller thread. The while
 * loop at the bottom executes in a loop. Assuming that the us.fetchSample, and cont.processUSData
 * methods operate in about 20mS, and that the thread sleeps for 50 mS at the end of each loop, then
 * one cycle through the loop is approximately 70 mS. This corresponds to a sampling rate of 1/70mS
 * or about 14 Hz.
 */
public class UltrasonicPoller extends Thread {

	// Thread control tools
	// concurrent writing
	public volatile boolean isAvoiding = false; // Indicates if a thread is
	// trying to avoid an obstacle

	public volatile boolean isInitializing = false; // Indicates if a thread is
	// trying to avoid an obstacle

	public Object doneAvoiding = new Object(); // Let other threads
	public Object doneInit = new Object();
	// know that avoiding obstacle
	// operation is over.

	private SampleProvider us;
	private UltrasonicBangBang cont;
	private float[] usData;
	private final double MIN_DISTANCE;

	public int distance = 0;
	
	private static UltrasonicPoller _instance;

	public static UltrasonicPoller getInstance() {
		return _instance;
	}

	public static UltrasonicPoller getInstance(SampleProvider us, float[] usData, UltrasonicBangBang cont,
			final double MIN_DISTANCE) {
		if (_instance == null) {
			_instance = new UltrasonicPoller(us, usData, cont,
					MIN_DISTANCE);
		}
		return _instance;
	}

	private UltrasonicPoller(SampleProvider us, float[] usData, UltrasonicBangBang cont,
			final double MIN_DISTANCE) {
		this.us = us;
		this.cont = cont;
		this.usData = usData;
		this.MIN_DISTANCE = MIN_DISTANCE;
	}

	/*
	 * Sensors now return floats using a uniform protocol. Need to convert US result to an integer
	 * [0,255] (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() { 
		int distance;
		while (true) {
			us.fetchSample(usData, 0); // acquire data
			distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
			this.distance = distance;
			this.processUSData(distance); // now take action depending on value
			try {
				Thread.sleep(20);
			} catch (Exception e) {
			} // Poor man's timed sampling
		}
	}
	
	public void init(double endX, double endY) {
		try {
			cont.initBangBang(endX, endY);
			isInitializing = false;
			isAvoiding = true;	//Once the bang-bang control is initialized, it has begun avoiding
		} finally {
			synchronized(doneInit) {
				doneInit.notifyAll();
			}
			
		}
	}

	public UltrasonicBangBang getController() {
		return this.cont;
	}
	
	private void processUSData(int distance) {
		if (this.isAvoiding) {
			cont.processUSData(distance);
		} else if (this.isInitializing) {
			try {
					while(this.isInitializing) {
						synchronized(doneInit) {
							doneInit.wait();
						}
					}
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		} else { 
			if (distance < MIN_DISTANCE) {
				Sound.buzz();
				isInitializing = true;
			}
		}
	}

}