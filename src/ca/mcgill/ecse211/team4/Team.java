package ca.mcgill.ecse211.team4;

import ca.mcgill.ecse211.team4.Resources.Point;
import ca.mcgill.ecse211.team4.Resources.Region;

public class Team {
  int corner;
  Region zoneRegion;
  Region tunnelRegion;
  Point binPoint;
  double[] zoneCoords;
  double[] tunnelCoords;
  double[] binCoords;
  
  Team(int corner, Region zoneRegion, Region tunnelRegion, Point binPoint) {
    this.corner = corner;
    this.zoneRegion = zoneRegion;
    this.tunnelRegion = tunnelRegion;
    this.binPoint = binPoint;
    this.zoneCoords = getRegionCoords(zoneRegion);
    this.tunnelCoords  = getRegionCoords(tunnelRegion);
    this.binCoords = getPointCoords(binPoint);
  }
   

  
  
  public double[] getRegionCoords(Region region) {
    double coords[] = new double[4]; //has format (lower left x coord, lower left y coord, upper right x coord, upper right y coord)
    coords[0] = region.ll.x;
    coords[1] = region.ll.y;
    coords[2] = region.ur.x;
    coords[3] = region.ur.y;
    return coords;
    
  }
  
  public double[] getPointCoords(Point point) {
    double coords[] = new double[2];
    
    coords[0] = point.x;
    coords[1] = point.y;
    return coords;
  }
  
}
