package src.ca.mcgill.ecse211.lab3;
import src.ca.mcgill.ecse211.odometer.Odometer;
import src.ca.mcgill.ecse211.odometer.OdometerExceptions;
import src.ca.mcgill.ecse211.ultrasonic.UltrasonicBangBang;
import src.ca.mcgill.ecse211.ultrasonic.UltrasonicPoller;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;


public class Lab3 {

	// Motor Objects, and Robot related parameters
	private static final EV3LargeRegulatedMotor leftMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor usMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	
	private static final Port usPort = LocalEV3.get().getPort("S1"); //ultrasonic sensor port
	public static final SensorModes usSensor = new EV3UltrasonicSensor(usPort); 
	public static final SampleProvider usDistance = usSensor.getMode("Distance"); 
	public static final float[] usData = new float[usDistance.sampleSize()]; 
	
	private static final TextLCD lcd = LocalEV3.get().getTextLCD();
	public static final double WHEEL_RAD = 2.10;
	public static final double WHEEL_BASE = 11.53;
	public static final double TILE_SIZE = 30.48; 
	public static final double MIN_DISTANCE = 14.0;

	public static void main(String[] args) throws OdometerExceptions {

		int buttonChoice;

		// Odometer related objects
		Odometer odometer = Odometer.getOdometer(leftMotor, rightMotor, WHEEL_BASE, WHEEL_RAD);
		Display odometryDisplay = new Display(lcd); // No need to change
		Navigation navigator = new Navigation(odometer,leftMotor, rightMotor, WHEEL_RAD, WHEEL_BASE, TILE_SIZE);

		do {
			// clear the display
			lcd.clear();

			// ask the user whether the motors should drive in a square or float
			lcd.drawString("< Left | Right >", 0, 0);
			lcd.drawString("       |        ", 0, 1);
			lcd.drawString("Simple | with  ", 0, 2);
			lcd.drawString("navig. | avoid-   ", 0, 3);
			lcd.drawString("       | ance ", 0, 4);

			buttonChoice = Button.waitForAnyPress(); // Record choice (left or right press)
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) { //Simple navigation has been selected

			//Start odometer thread
			Thread odoThread = new Thread(odometer);
			odoThread.start();

			//Start display thread
			Thread odoDisplayThread = new Thread(odometryDisplay);
			odoDisplayThread.start();

			//Set up navigation points
			
			  // navigator.travelTo(1, 1);
	            navigator.travelTo(3, 2);
	            navigator.travelTo(2, 2);
	            navigator.travelTo(2, 3);
	            navigator.travelTo(3, 1);

			//Start navigation thread
			Thread navigatorThread = new Thread(navigator);
			navigatorThread.start();


		} else { //Navigation w/ avoidane has been selected, so we include an ultrasonic thread as well
			LCD.clear();
			
			//Declaring ultrasonic and controller variables
			UltrasonicBangBang usCont = new UltrasonicBangBang(leftMotor, rightMotor, usMotor, WHEEL_RAD, WHEEL_BASE);
			UltrasonicPoller usPoller = UltrasonicPoller.getInstance(usSensor, usData, usCont, MIN_DISTANCE);
			
			//Start odometer thread
			Thread odoThread = new Thread(odometer);
			odoThread.start();
			
			//Start display thread
			Thread odoDisplayThread = new Thread(odometryDisplay);
			odoDisplayThread.start();
//
			//Start ultrasonic thread
			Thread ultrasonicThread = new Thread(usPoller);
			ultrasonicThread.start();
			
			//Set up navigation points
			navigator.travelTo(1, 1);
			navigator.travelTo(3, 2);
			navigator.travelTo(2, 2);
			navigator.travelTo(2, 3);
			navigator.travelTo(3, 1);

			//Start navigation thread
			Thread navigatorThread = new Thread(navigator);
			navigatorThread.start();

		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}


}
