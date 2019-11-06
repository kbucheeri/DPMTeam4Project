/**
 * Osman Warsi, Mairead Maloney, Davide Bartolucci, Yuxiang Ma and Khalid Bucheeri

 */
package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.*;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import lejos.hardware.Button;
import lejos.hardware.Sound;

/**
 * The main driver class for the entire program.
 * @version 1.00
 */

public class Main {

  /**
   * The main entry point.
   * 
   * @param args
   */
  public static void main(String[] args) {
    /*
     * Variables
     */
    int[] starting_coords = new int[2]; //array of x and y values for starting position
    double tunnel_entry_x;
    double tunnel_entry_y;
    double tunnel_exit_x;
    double tunnel_exit_y;
    Color color;
    Team team;
    
    
    /**
     * Start general threads
     */
    //Display
    new Thread(new Display()).start();
    
    //Odometer
    new Thread(odometer).start();
    
    
    //Localize to 0 degrees
    localizeAngle();
    
    //Localize to starting point
    localizePosition();
       
    //Get team color
    color = getColor();
    
    if(color.equals(Color.RED)) {
      team = new Team(redCorner, red, tnr, bin);
    }
    else {
      team = new Team(greenCorner, green, tng, bin);
    }
    
    //robot is currently at top right corner of given corner of the field (0,1,2 or 3) after localization
    int currPos[] = getStartingPoint(team.corner);
    odometer.setXYT(getRealCoord(currPos[0]), getRealCoord(currPos[1]), 0);

   
   //Get coordinates for entry point of the tunnel
     tunnel_entry_x = getRealCoord(team.tunnelCoords[0]-1);
     tunnel_entry_y = getRealCoord((team.tunnelCoords[1]+(team.tunnelCoords[4]))/2);
         
    //Navigate to tunnel entry point
    Navigation.travelTo(tunnel_entry_x, tunnel_entry_y);    
    
    //Navigate to tunnel exit point
    tunnel_exit_x = getRealCoord((team.tunnelCoords[3])+1);
    tunnel_exit_y = tunnel_entry_y;
    
    Navigation.travelTo(tunnel_exit_x, tunnel_exit_y);
    
    //Navigate to launch point
    
    //Launch
    
  
  }

  /**
   * Sleeps current thread for the specified duration.
   * 
   * @param duration sleep duration in milliseconds
   */
  public static void sleepFor(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      // There is nothing to be done here
    }
  }
  /**
   * Uses ultrasonic localization to determine angle and adjust robot to 0 degrees
   */
  public static void localizeAngle() {
    //start ultrasonic poller to get data for angle position
    new Thread(new UltrasonicPoller()).start();
    sleepFor(1000);
    UltrasonicLocalizer.RisingEdge(); //uses rising edge to determine angle position from sensor readings
    sleepFor(500);
    UltrasonicPoller.setSleepTime(2000);
  }
  
  /**
   * Localizes robot to the right-top corner of the square it is in
   * 
   * @return void
   */
  public static void localizePosition() {
    //start light sensor poller to get data to determine position
    new Thread(new lightPoller()).start();
     LightLocalizer.localizeDistance(); //localizes to the right-top corner of the square
     sleepFor(1000);
     lightPoller.changeSleepTime(1000);
  }
  /**
   * Gets color for the team
   * 
   * @return Team color from Team enumeration (red or green)
   */
  public static Color getColor() {
    //red team
    if (redTeam == TEAM_NUMBER) {
      return Color.RED;
    }
    //green team
    else if (greenTeam == TEAM_NUMBER) {
      return Color.GREEN;
    }
    else {
      return null;
    }
  } 
  
  /**
   * 
   * @param int initial - will be replaced by the value of the corner the robot is in (0,1,2 or 3)
   * @return 
   * @return array of position coordinates
   */
   public static int[] getStartingPoint(int initial) {
    int[] position = new int[2];
    
    //checks the value of initial, sets the position values accordingly
     switch(initial) {
       case 0:
         position[0] = 1;
         position[1] = 1;
        
       case 1:
         position[0] = 14;
         position[1] = 1;
         
       case 2:
         position[0] = 14;
         position[1] = 8;
         
       case 3:
         position[0] = 1;
         position[1] = 8;
     }
     
     //return an array of size 2, containing x and y
     return(position);
     
   }
   
   /**
    * @param double coordinate value
    * @return double real value in cm
    */
   public static double getRealCoord(double value) {
     value*=TILE_SIZE;
     return value;
   }
  
   
  
    
  

}
