package src.ca.mcgill.ecse211.lab3;
import java.text.DecimalFormat;
import java.util.ArrayList;

import src.ca.mcgill.ecse211.odometer.Odometer;
import src.ca.mcgill.ecse211.ultrasonic.UltrasonicPoller;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation extends Thread{

	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	private final double WHEEL_RAD;
	private final double WHEEL_BASE;
	private final double TILE_SIZE;

	private Odometer odometer;
	private double x, y, theta;
	private boolean isNavigating;
	
	private ArrayList<double[]> _coordsList;

	public Navigation(Odometer odo, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, 
			final double WHEEL_RAD, final double WHEEL_BASE, double tileSize) {

		this.odometer = odo;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.WHEEL_RAD = WHEEL_RAD;
		this.WHEEL_BASE = WHEEL_BASE;
		this.TILE_SIZE = tileSize;
		this.isNavigating = false;
		this._coordsList = new ArrayList<double[]>();

	}

	/**
	 * Adds the navigation points to the coordList, setting up the thread
	 * 
	 * @param navX coordinate of position
	 * @param navY coordinate of position
	 */
	void travelTo(double navX, double navY) {
		this._coordsList.add(new double[] {navX*TILE_SIZE, navY*TILE_SIZE});
		
	}

	//The run method runs through the _coordsList and travels through the points
	public void run() {
		while (!this._coordsList.isEmpty()) {
			double[] coords = this._coordsList.remove(0);
			this._travelTo(coords[0], coords[1]);
		}
	}

	boolean _travelTo(double navX, double navY) {

		UltrasonicPoller usPoller = null;
		if (UltrasonicPoller.getInstance() != null) {
			usPoller = UltrasonicPoller.getInstance();
		}

		// get current coordinates
		theta = odometer.getXYT()[2];
		x = odometer.getXYT()[0];
		y = odometer.getXYT()[1];	


		double deltaX = navX - x;
		double deltaY = navY - y;

		// get absolute values of deltas
		double absDeltaX = Math.abs(deltaX);
		double absDeltaY = Math.abs(deltaY);

		//need to convert theta from degrees to radians
		double deltaTheta = Math.atan2(deltaX, deltaY) / Math.PI * 180;

		// turn to the correct direction
		this._turnTo(theta, deltaTheta);
		Sound.beep();
		
		// move until destination is reached
		// while loop is used in case of collision override
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.forward();
		rightMotor.forward();

		while(true) {
			double newTheta, newX, newY;
			
			double xyt[] = odometer.getXYT();
			//need to convert theta from degrees to radians
			newTheta = xyt[2];
			newX = xyt[0];
			newY = xyt[1];	

			//If the difference between the current x/y and the x/y we started from is similar to the deltaX/deltaY, 
			//Stop the motors because the point has been reached
			if (Math.pow(newX - x, 2) + Math.pow(newY - y, 2) > Math.pow(absDeltaX, 2) + Math.pow(absDeltaY, 2)) {
				break;
			}

			// This is only relevant if the ultrasonic poller thread is being used
			if (usPoller != null) {
				if (usPoller.isInitializing) { 	//isInitializing is true when the distance is too close
					leftMotor.stop(true);
					rightMotor.stop(false);
					usPoller.init(navX, navY); //hopefully blocking
					
					try {
						synchronized(usPoller.doneAvoiding) {
							while(usPoller.isAvoiding) {
								usPoller.doneAvoiding.wait();
							}
							Sound.beepSequenceUp();
						}
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
					
					this._coordsList.add(0, new double[] {navX, navY});
					
					return false;
				}
			}

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		leftMotor.stop(true);
		rightMotor.stop(false);
		this.isNavigating = false;
		Sound.twoBeeps();
		return true;
	}

	//	This method causes the robot to turn (on point) to the absolute heading theta. This method
	//	should turn a MINIMAL angle to its target.
	/**
	 * Turns the robot to the absolute (minima) heading theta
	 * 
	 * @param currTheta current theta
	 * @param destTheta to turn robot by
	 */
	void _turnTo(double currTheta, double destTheta) {
		// get theta difference
		double deltaTheta = destTheta - currTheta;
		// normalize theta (get minimum value)
		deltaTheta = normalizeAngle(deltaTheta);

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		leftMotor.rotate(convertAngle(WHEEL_RAD, WHEEL_BASE, deltaTheta), true);
		rightMotor.rotate(-convertAngle(WHEEL_RAD, WHEEL_BASE, deltaTheta), false);
	}

	//Getting the minimum angle to turn:
	//It is easier to turn +90 than -270
	//Also, it is easier to turn -90 than +270
	double normalizeAngle(double theta) {
		if (theta <= -180) {
			theta += 360;
		}
		else if (theta > 180) {
			theta -= 360;
		}
		return theta;
	}


	boolean isNavigating() {
		return isNavigating;
	}

	/**
	 * This method allows the conversion of a distance to the total rotation of each wheel need to
	 * cover that distance.
	 * 
	 * @param radius
	 * @param distance
	 * @return
	 */
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
