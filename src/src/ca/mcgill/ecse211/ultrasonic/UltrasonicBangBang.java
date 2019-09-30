package src.ca.mcgill.ecse211.ultrasonic;

import src.ca.mcgill.ecse211.odometer.Odometer;
import src.ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.motor.*;

public class UltrasonicBangBang {


	private final int bandCenter = 14;
	private final int bandwidth = 3;
	private final int motorLow = 150;
	private final int motorHigh = 230;

	private int distance;
	private int filterControl;
	private int turnCounter;
	private static final int FILTER_OUT = 40;
	private static final double ERROR_THRESHOLD = 0.08;

	private double startCoords[];

	private double startEndVector[];

	private final double WHEEL_RAD;
	private final double WHEEL_BASE;

	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;

	private EV3LargeRegulatedMotor usMotor;

	public float dotMagnitudeRatio = 0;
	
	public UltrasonicBangBang(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, EV3LargeRegulatedMotor usMotor,
			final double WHEEL_RAD, final double WHEEL_BASE) {
		// Default Constructor

		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.usMotor = usMotor;

		this.WHEEL_BASE = WHEEL_BASE;
		this.WHEEL_RAD = WHEEL_RAD;

		this.filterControl = 0;
		this.turnCounter = 0;

	}


	public void processUSData(int distance) {

		if (turnCounter > 0 ) {
			Odometer odo;
			try {
				odo = Odometer.getOdometer();
				double xyt[] = odo.getXYT();

				double[] currVector = new double [] {xyt[0] - this.startCoords[0], xyt[1] - this.startCoords[1]};

				// Dot product: determines when to stop performing the bang-bang corrections
				//using dot product calculations between the vector of the robot's position and 
				// the robot's destination
				float dotProduct = (float) (currVector[0]*this.startEndVector[0] + currVector[1]*this.startEndVector[1]);
				float currMagnitude = (float) Math.pow(Math.pow(currVector[0], 2) + Math.pow(currVector[1], 2), 1/2.0);
				float startEndMagnitude = (float) Math.pow(Math.pow(this.startEndVector[0], 2) + Math.pow(this.startEndVector[1], 2), 1/2.0);
				float magnitudeProduct = currMagnitude * startEndMagnitude;
				dotMagnitudeRatio = dotProduct/magnitudeProduct;
				if (Math.abs(dotProduct/magnitudeProduct) >= 1 - ERROR_THRESHOLD) {
					
					UltrasonicPoller usPoller = UltrasonicPoller.getInstance();

					usMotor.rotate(90);
					usPoller.isAvoiding = false;
					
					synchronized(usPoller.doneAvoiding) {
						usPoller.doneAvoiding.notifyAll();
					}

				}
			} catch (OdometerExceptions e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		// rudimentary filter - toss out invalid samples corresponding to null signal.
		if (distance >= 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} else if (distance >= 255) {
			// We have repeated large values, so there must actually be nothing
			// there: leave the distance alone
			this.distance = distance;

		} else {
			// distance went below 255: reset filter and leave
			// distance alone.
			filterControl = 0;
			this.distance = distance;
		}

		int error = this.distance - bandCenter;

		//If the error is within the limits, continue forward
		if (Math.abs(error) <= bandwidth) {
			leftMotor.setSpeed(motorHigh);
			rightMotor.setSpeed(motorHigh);
			leftMotor.forward();
			rightMotor.forward();
		}
		//If the error is negative, move farther from the wall (right turn)
		else if (error < 0) {
			//An even more negative error means that there is a convex corner, requiring a bigger adjustment
			if (error < -this.bandwidth) {
				leftMotor.setSpeed(motorLow);
				rightMotor.setSpeed(motorLow);

				leftMotor.forward();
				rightMotor.backward();
			}
			else {
				leftMotor.setSpeed(motorHigh);
				rightMotor.setSpeed(motorLow);

				leftMotor.forward();
				rightMotor.forward();
			}
		}

		//A positive error means we need to move closer to the wal
		else if (error > 0) {
			if (error >= bandwidth) {
				turnCounter++;
			}
			leftMotor.setSpeed(motorLow);
			rightMotor.setSpeed(motorHigh);

			leftMotor.forward();
			rightMotor.forward();

		}
	}

	public void initBangBang(double endX, double endY) {

		Odometer odo;
		try {
			odo = Odometer.getOdometer();
			double xyt[] = odo.getXYT();

			this.startCoords = new double[] {xyt[0], xyt[1]};
			this.startEndVector = new double [] {xyt[0] - endX, xyt[1] - endY};

		} catch (OdometerExceptions e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		turnCounter = 0;
		dotMagnitudeRatio = 0;

		usMotor.setSpeed(50);
		usMotor.rotate(-90, false);

		leftMotor.setSpeed(motorLow);
		rightMotor.setSpeed(motorLow);

		leftMotor.rotate(convertAngle(WHEEL_RAD, WHEEL_BASE, 90), true);
		rightMotor.rotate(-convertAngle(WHEEL_RAD, WHEEL_BASE, 90), false);

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

	public int readUSDistance() {
		return this.distance;
	}
}