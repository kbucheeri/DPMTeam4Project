package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.colorSensor;
import static ca.mcgill.ecse211.team4.Resources.leftMotor;
import static ca.mcgill.ecse211.team4.Resources.odometer;
import static ca.mcgill.ecse211.team4.Resources.rightMotor;
import lejos.hardware.Sound;
/**
 * Samples the light sensor and applies filtering and difference calculations.
 *
 */
public class lightPoller implements Runnable {
  public static int[] buffer = new int[5];
  public static int getIntensity()
  {
    return diff;
  }
  public static int SLEEP_TIME = 70;
  public static void changeSleepTime(int time)
  {
    SLEEP_TIME = time;
  }
  /**
   * calculates the intensity from the light sensor and applies rudimentary filtering In the form of the arithmetic
   * mean.
   * 
   * @return The average of the previous light sensor intensity values.
   */
  public static int calculateIntensity() {
    float[] lightData = new float[3];
    colorSensor.getRGBMode().fetchSample(lightData, 0);
    /**
     * Resizing the actual intensity values to make it more readable 
     * and thus easier to test.
     *  Also easier to deal with ints than double precision
     */

    for (int i = 0; i < 3; i++) {
      lightData[i] *= 2000;
    }
    for (int i = 0; i < buffer.length - 1; i++) {
      buffer[i] = buffer[i + 1];
    }
    int intensity = (int) (lightData[0] + lightData[1] + lightData[2]);
    buffer[buffer.length - 1] = intensity;
    return average(buffer);
  }
 
  public static  int prevIntensity = 1500;
  public static int intensity = 2000;
  // difference
  public static int diff = 1000;
  /**
   * Performs light polling
   * Assumes it starts facing 0 degrees in the corner block
   */
  public void run() {
    /**
     * Float Array to store RGB Raw values
     */

    initalize(buffer);
    while (true) {
      intensity = calculateIntensity();
      diff = intensity - prevIntensity;

      sleepFor(SLEEP_TIME);
      prevIntensity = intensity;

    }
  }
  /*
   * returns RMS of an int array
   * 
   */

  public static int average(int[] arr) {
    double sum = 0;
    for (int i = 0; i < arr.length; i++)
      sum = sum + arr[i] * arr[i];

    return (int) Math.sqrt((sum / arr.length));
  }

  /**
   * sleeps thread for a set amount of time
   * 
   * @param duration amount to sleep for
   */
  public static void sleepFor(int duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * initalizes the buffer array
   * 
   * @param buffer array to initalize
   */
  public static void initalize(int[] buffer) {

    float[] lightData = new float[3];
    colorSensor.getRGBMode().fetchSample(lightData, 0);
    /**
     * Resizing the actual intensity values to make it more readable and thus easier to test. Also easier to deal with
     * ints than double precision
     */

    for (int i = 0; i < 3; i++) {
      lightData[i] *= 2000;
    }
    int init = (int) (lightData[0] + lightData[1] + lightData[2]);

    for (int i = 0; i < buffer.length; i++) {
      buffer[i] = init;
    }
  }
}
