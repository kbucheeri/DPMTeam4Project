package src.ca.mcgill.ecse211.odometer;
/**
 * This class is meant as a skeleton for the odometer class to be used.
 *
 */

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends OdometerData implements Runnable {

  private OdometerData odoData;
  private static Odometer odo = null; // Returned as singleton

  // Motors and related variables
  private int leftMotorTachoCount;
  private int rightMotorTachoCount;
  
  private int oldLeftMotorTachoCount;
  private int oldRightMotorTachoCount;
  
  private double Theta; // overall angle
  
  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;

  private final double TRACK;
  private final double WHEEL_RAD;

  private double[] position;


  private static final long ODOMETER_PERIOD = 25; // odometer update period in ms

  /**
   * This is the default constructor of this class. It initiates all motors and variables once.It
   * cannot be accessed externally.
   * 
   * @param leftMotor
   * @param rightMotor
   * @throws OdometerExceptions
   */
  private Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
      final double TRACK, final double WHEEL_RAD) throws OdometerExceptions {
    odoData = getOdometerData(); // Allows access to x,y,z
                                              // manipulation methods
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;

    // Reset the values of x, y and z to 0
    odoData.setXYT(0, 0, 0);

    this.leftMotorTachoCount = 0;
    this.rightMotorTachoCount = 0;

    this.oldLeftMotorTachoCount = 0;
    this.oldRightMotorTachoCount = 0;
    this.TRACK = TRACK;
    this.WHEEL_RAD = WHEEL_RAD;

  }

  /**
   * This method is meant to ensure only one instance of the odometer is used throughout the code.
   * 
   * @param leftMotor
   * @param rightMotor
   * @return new or existing Odometer Object
   * @throws OdometerExceptions
   */
  public synchronized static Odometer getOdometer(EV3LargeRegulatedMotor leftMotor,
      EV3LargeRegulatedMotor rightMotor, final double TRACK, final double WHEEL_RAD)
      throws OdometerExceptions {
    if (odo != null) { // Return existing object
      return odo;
    } else { // create object and return it
      odo = new Odometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
      return odo;
    }
  }

  /**
   * This class is meant to return the existing Odometer Object. It is meant to be used only if an
   * odometer object has been created
   * 
   * @return error if no previous odometer exists
   */
  public synchronized static Odometer getOdometer() throws OdometerExceptions {

    if (odo == null) {
      throw new OdometerExceptions("No previous Odometer exits.");

    }
    return odo;
  }

  public int[] getTachoCount() {
	  int[] tachos = new int[2];
	  tachos[0] = leftMotorTachoCount;
	  tachos[1] = rightMotorTachoCount;
	  return tachos;
  }
  
  public double[] calcXYT() {
	  
	  double XYT[] = new double[3];
      oldLeftMotorTachoCount = leftMotorTachoCount;
      oldRightMotorTachoCount = rightMotorTachoCount;
      leftMotorTachoCount = leftMotor.getTachoCount();
      rightMotorTachoCount = rightMotor.getTachoCount();
      
      double distL = Math.PI*WHEEL_RAD*(leftMotorTachoCount-oldLeftMotorTachoCount)/180;  

      double distR = Math.PI*WHEEL_RAD*(rightMotorTachoCount-oldRightMotorTachoCount)/180;   // displacements 

      double deltaD = 0.5*(distL+distR);       // compute vehicle displacement 
      double deltaT = (distL-distR)/TRACK;        // compute change in heading 
      Theta += deltaT;  // update heading 
      double dX = deltaD * Math.sin(Theta);    // compute X component of displacement 
      double dY = deltaD * Math.cos(Theta);  // compute Y component of displacement
      XYT[0] = dX;
      XYT[1] = dY;
      XYT[2] = deltaT/Math.PI*180;
      return XYT;
      
  }
  
  
  /**
   * This method is where the logic for the odometer will run. Use the methods provided from the
   * OdometerData class to implement the odometer.
   */
  // run method (required for Thread)
  public void run() {
    long updateStart, updateEnd;

    while (true) {
      updateStart = System.currentTimeMillis();

      double[] xyt = calcXYT();
      
      // TODO Update odometer values with new calculated values
      odo.update(xyt[0], xyt[1], xyt[2]);

      // this ensures that the odometer only runs once every period
      updateEnd = System.currentTimeMillis();
      if (updateEnd - updateStart < ODOMETER_PERIOD) {
        try {
          Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
        } catch (InterruptedException e) {
          // there is nothing to be done
        }
      }
    }
  }

}