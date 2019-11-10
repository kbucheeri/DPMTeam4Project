package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.*;
import java.util.Arrays;

/**
 * Samples the US sensor and applies filtering.
 * 
 * Control of the wall follower is applied periodically by the UltrasonicPoller thread. The while
 * loop at the bottom executes in a loop. Assuming that the us.fetchSample, and cont.processUSData
 * methods operate in about 20ms, and that the thread sleeps for 50 ms at the end of each loop, then
 * one cycle through the loop is approximately 70 ms. This corresponds to a sampling rate of 1/70ms
 * or about 14 Hz.
 */
import ca.mcgill.ecse211.team4.BangBangController;
import lejos.robotics.SampleProvider;

/**
 * Samples the US sensor and invokes the selected controller on each cycle.
 * 
 * Control of the wall follower is applied periodically by the UltrasonicPoller thread. The while loop at the bottom
 * executes in a loop. Assuming that the us.fetchSample, and cont.processUSData methods operate in about 20ms, and that
 * the thread sleeps for 50 ms at the end of each loop, then one cycle through the loop is approximately 70 ms. This
 * corresponds to a sampling rate of 1/70ms or about 14 Hz.
 */
public class UltrasonicPoller extends Thread {

  // Thread control tools
  // concurrent writing
  public volatile boolean isAvoiding = false; // Indicates if a thread is trying to avoid an obstacle.

  public volatile boolean isInitializing = false; // Indicates if a thread is trying to avoid an obstacle.

  public Object doneAvoiding = new Object(); // Let other threads know that avoiding obstacle operation is over.

  public Object doneInit = new Object();
  private static int sleepTime = 10;

  private float[] usData;


  public UltrasonicPoller() {
    usData = new float[US_SENSOR.sampleSize()];
    new BangBangController(leftMotor, rightMotor, WHEEL_RAD, TRACK);
  }

  private SampleProvider us = US_SENSOR;
  private BangBangController cont;

  public static int distance = 0;

  private double MIN_DISTANCE;

  private static UltrasonicPoller ultraSPollInst;

  public static UltrasonicPoller getInstance() {
    return ultraSPollInst;
  }
  public static int getDistance() {
	  return distance;
  }

  public static UltrasonicPoller getInstance(SampleProvider us, float[] usData, BangBangController cont,
      final double MIN_DISTANCE) {
    if (ultraSPollInst == null) {
      ultraSPollInst = new UltrasonicPoller(us, usData, cont, MIN_DISTANCE);
    }
    return ultraSPollInst;
  }

  public void init(double endX, double endY) {
    try {
      cont.initBangBang(endX, endY);
      isInitializing = false;
      isAvoiding = true; // Once the bang-bang control is initialized, it has begun avoiding
    } finally {
      synchronized (doneInit) {
        doneInit.notifyAll();
      }

    }
  }

  public BangBangController getController() {
    return this.cont;
  }

  private UltrasonicPoller(SampleProvider us, float[] usData, BangBangController cont, final double MIN_DISTANCE) {
    this.us = us;
    this.cont = cont;
    this.usData = usData;
    this.MIN_DISTANCE = MIN_DISTANCE;
  }

  private void processUSData(int distance) {
    if (this.isAvoiding) {
      cont.processUSData(distance);
    } else if (this.isInitializing) {
      try {
        while (this.isInitializing) {
          synchronized (doneInit) {
            doneInit.wait();
          }
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } else {
      if (distance < MIN_DISTANCE) {
        isInitializing = true;
      }
    }
  }

  /*
   * Sensors now return floats using a uniform protocol. Need to convert US result to an integer [0,255] (non-Javadoc)
   * 
   * @see java.lang.Thread#run()
   */
  /*
   * Sensors now return floats using a uniform protocol. Need to convert US result to an integer [0,255] (non-Javadoc)
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
        Thread.sleep(10);
      } catch (Exception e) {
      } // Poor man's timed sampling
    }
  }
  
  public static void setSleepTime(int time)
  {
	  sleepTime = time;
  }

}