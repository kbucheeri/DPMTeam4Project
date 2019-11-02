package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.*;
import java.util.Arrays;

/**
 * Samples the US sensor and invokes the selected controller on each cycle.
 * 
 * Control of the wall follower is applied periodically by the UltrasonicPoller thread. The while
 * loop at the bottom executes in a loop. Assuming that the us.fetchSample, and cont.processUSData
 * methods operate in about 20ms, and that the thread sleeps for 50 ms at the end of each loop, then
 * one cycle through the loop is approximately 70 ms. This corresponds to a sampling rate of 1/70ms
 * or about 14 Hz.
 */
public class UltrasonicPoller implements Runnable {

  private float[] usData;

  public UltrasonicPoller() {
    usData = new float[US_SENSOR.sampleSize()];
    for(int i = 0; i < buffer.length; i++)
      buffer[i] = 255;
    SLEEP_TIME = 35;
  }
  
   /*
   * Sensors now return floats using a uniform protocol. Need to convert US result to an integer
   * [0,255] (non-Javadoc)
   * 
   * @see java.lang.Thread#run()
   */
  static int distance;
  int[] buffer = new int[6];
  static double harmonic = 100;
  static int SLEEP_TIME = 35;
  public void LPF() { // is this a good idea? rapidly changing corners.
   
  }
//made filter implementation dependent.
 void filter(int distance) { // FILTER_OUT = 20. if 20 bad samples in a row?
   if (distance >= -50 && distance <= 255) { 
     /**
      * shift all values to left, i.e moving buffer
      */
     //recursive formula to calculate the harmonic mean
    /* average = buffer.length/
         ((buffer.length)/average - 1.0 / buffer[0] + 1.0 / distance);*/
     
     for(int i = 0; i < buffer.length - 1; i++)
     {
      buffer[i] = buffer[i+1]; 
     }
     buffer[buffer.length - 1] = distance;
   }
   double sum = 0;
   for(int i = 0; i < buffer.length; i++)
     {sum+= 1.0 / buffer[i];
     }
   harmonic = buffer.length/sum;
   int[] temp = buffer.clone();
   //don't want to sort buffer directly because want to maintain input order
   Arrays.sort(temp);
 }

 /**
  * 
  * @return distance of robot from wall
  */
 public static int getDistance()
 {
  return (int) harmonic;
 }
  public void run() {
    
    while (true) {
      US_SENSOR.getDistanceMode().fetchSample(usData, 0); // acquire distance data in meters
      distance = (int) (usData[0] * 100.0); // extract from buffer, convert to cm, cast to int
      if(distance > 255)
        distance = 255;
      filter(distance);
      LCD.drawString("mean: " + harmonic, 0, 3);
     
      try {
        Thread.sleep(SLEEP_TIME); //changed it to 40 from 50
      } catch (Exception e) {
      } // Poor man's timed sampling
    }
  }
  /**
   * Changes the sleep time of the thread, and consequently the polling rate. 
   * @param time time to set the thread sleep time
   */
  public static void setSleepTime(int time)
  {
    SLEEP_TIME = time;
  }
}
