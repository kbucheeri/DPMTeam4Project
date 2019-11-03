package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.RegulatedMotor;

public class Launcher {
  /**
   * Launches then waits for a button press to launch again
   */
  public static void launchThenWait() {
    launchMotor1.setAcceleration(999999);
    launchMotor2.setAcceleration(999999);
    launchMotor1.setSpeed(800);
    launchMotor2.setSpeed(800);
    leftMotor.stop();
    rightMotor.stop();
    resetLauncher();
    /**
     * Launches then waits for button press.
     * Press back to exit.
     */
    while (true) {
      Sound.beepSequence();
      moveLaunchers(100);
      launchMotor1.stop();
      launchMotor2.stop();
      Team4.sleepFor(500);
      resetLauncher();
      Sound.buzz();
      launchMotor1.flt();
      launchMotor2.flt();
      if (Button.waitForAnyPress(0) == Button.ID_ESCAPE)
        break;
      Sound.beepSequence();
      Team4.sleepFor(3000);
    }
    System.exit(0);
  }

  /**
   * Rotates both launchers by the angle
   * 
   * @param angle angle relative to neutral/loading position
   */
  public static void moveLaunchers(int angle) {
    Resources.launchMotor1.rotateTo(-angle, true);
    Resources.launchMotor2.rotateTo(-angle, false);
  }

  /**
   * resets the launch to its initial position
   */
  public static void resetLauncher() {
    int initialSpeed = launchMotor1.getSpeed();
    launchMotor1.setSpeed(RESET_SPEED);
    launchMotor2.setSpeed(RESET_SPEED);
    Team4.sleepFor(200);
    moveLaunchers(-150);
    /**
     * set it back to the launch speed.
     */
    launchMotor1.setSpeed(initialSpeed);
    launchMotor2.setSpeed(initialSpeed);
  }
  /**
   * Press up and down to change launcher speed
   */
  public static void launchThenWaitTest() {
    int speed = 800;
    launchMotor1.setAcceleration(999999);
    launchMotor2.setAcceleration(999999);
    launchMotor1.setSpeed(speed);
    launchMotor2.setSpeed(speed);
    leftMotor.stop();
    rightMotor.stop();
    resetLauncher();
    /**
     * Launches then waits for button press.
     * Press back to exit.
     */
    while (true) {
      launchMotor1.setSpeed(speed);
      launchMotor2.setSpeed(speed);
      Sound.beepSequence();
      moveLaunchers(100);
      launchMotor1.stop();
      launchMotor2.stop();
      Team4.sleepFor(500);
      resetLauncher();
      Sound.buzz();
      launchMotor1.flt();
      launchMotor2.flt();
      int choice = Button.waitForAnyPress(0);
      if (choice == Button.ID_ESCAPE)
        {
          break;
        }
/*      else if(choice == Button.ID_DOWN)
        speed -= 100;
      else if(choice == Button.ID_LEFT)
        speed -= 25;
      else if(choice == Button.ID_RIGHT)
        speed += 25;
      else if(choice == Button.ID_UP)
        speed += 100;
        */
      System.out.println("speed: " + speed);
      Sound.beepSequence();
      Team4.sleepFor(1000);
    }
    System.exit(0);
  }

}
