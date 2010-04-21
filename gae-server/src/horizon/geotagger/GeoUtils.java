package horizon.geotagger;

/**
 * Nicked from http://www.johndcook.com/blog/2009/04/27/converting-miles-to-degrees-longitude-or-latitude/
 */
public class GeoUtils
{
	private static final double EARTH_RADIUS_IN_MILES = 3960.0;
	
	private static final double EARTH_RADIUS_IN_KILOMETERS = 6371.0;
	
	private static final double METER_IN_MILES = 0.000621371192D;
	
	private static final double DEGREES_TO_RADIANS = Math.PI / 180.0;
	
	private static final double RADIANS_TO_DEGREES = 180.0 / Math.PI;
	
	/*

	# Distances are measured in miles.
	# Longitudes and latitudes are measured in degrees.
	# Earth is assumed to be perfectly spherical.

	earth_radius = 3960.0
	degrees_to_radians = math.pi/180.0
	radians_to_degrees = 180.0/math.pi
	
	*/

	/**
	 * "Given a distance north, return the change in latitude."
	 */
	public static double changeInLatitude(double meters)
	{
		double miles = meters * METER_IN_MILES;
		return (miles / EARTH_RADIUS_IN_MILES) * RADIANS_TO_DEGREES;
	}
	
	/**
	 * "Given a latitude and a distance west, return the change in longitude."
	 */
	public static double changeInLongitude(double latitude, double meters)
	{
		//  Find the radius of a circle around the earth at given latitude.
	    double radius  = EARTH_RADIUS_IN_MILES * Math.cos(latitude * DEGREES_TO_RADIANS);
	    double miles = meters * METER_IN_MILES;
	    return (miles / radius) * RADIANS_TO_DEGREES;
	}	  
	
	public static double distance(double latA, double lonA, double latB, double lonB)
	{
		double lat1 = Math.toRadians(latA);
		double lon1 = Math.toRadians(lonA);
		double lat2 = Math.toRadians(latB);
		double lon2 = Math.toRadians(lonB);
		double km = Math.acos(Math.sin(lat1) * Math.sin(lat2) 
				+ Math.cos(lat1) * Math.cos(lat2) 
				* Math.cos(lon2-lon1)) * EARTH_RADIUS_IN_KILOMETERS;
		return km * 1000;
	}
}
