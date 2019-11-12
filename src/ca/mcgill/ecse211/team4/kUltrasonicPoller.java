package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.*;
import java.util.Arrays;

import lejos.utility.TimerListener;

/**
 * @version 1.1 Samples the US sensor and applies filtering.
 * 
 *          The function gets repeatedly called by the Timer class provided by
 *          LeJOS.
 *          https://lejos.sourceforge.io/ev3/docs/lejos/utility/Timer.html.
 * 
 *          Since the timer is based on the hardware clock, which generates an
 *          interrupt every X milliseconds, the polling rate is more consistent
 *          than the simple approach of putting the thread to sleep for X
 *          milliseconds.
 */
public class kUltrasonicPoller implements TimerListener {
    private float[] usData;

    /**
     * Creates an instance of a poller, initalizing the buffer array.
     */
    public kUltrasonicPoller() {
        usData = new float[US_SENSOR.sampleSize()];
      
        // cm, cast to int
        //initalize buffer array
        for (int i = 0; i < buffer.length; i++)
            buffer[i] = 100;
        median = 100; //initalize average
    }

    
    /**
     * This method is called by the Timer object in Main. It records the data
     * from the ultrasonic sensor and stores it in the buffer array. It then
     * calls the filter method.
     */
    public void timedOut() {
        US_SENSOR.getDistanceMode().fetchSample(usData, 0); // acquire distance
                                                            // data in meters
    //  if(usData[0] != 0)
            distance = (int) ( 100 *  usData[0]);
        filter(distance);
    }


    /*
     * Sensors now return floats using a uniform protocol. Need to convert US
     * result to an integer [0,255] (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    static int distance;
    /**
     * buffer stores inverse distance
     */
    int[] buffer = new int[5];
    final static int SCALE_FACTOR = 2048; // = 2^20
    //stores the mean closeness / harmonic distance
    static int median;

    /**
     * Stores data in the buffer array, then calculates the harmonic mean.
     */
    void filter(int distance) {
    if (distance >= 0) {
            for (int i = 0; i < buffer.length - 1; i++) {
                buffer[i] = buffer[i + 1];
            }
            buffer[buffer.length - 1] = distance;
        }       
        
        int[] temp = buffer.clone();
        // don't want to sort buffer directly because want to maintain input
        // order
        Arrays.sort(temp);
        median = temp[temp.length / 2];
        
    
    }

    /**
     * 
     * @return distance of robot from wall
     */
    public static int getDistance() {
        /*
         * convert back to distance in cm
         */
        return (int) (median);
    }

}
