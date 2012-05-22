package kz.crystalspring.android_client.augmented_reality.data;

import kz.crystalspring.android_client.augmented_reality.common.Vector;

import android.location.Location;


/**
 * This class is used to represent a physical locations which have a latitude, longitude, and alitude.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PhysicalLocation {
	private double latitude = 0.0;
	private double longitude = 0.0;
	private double altitude = 0.0;

	private static float[] x = new float[1];
	private static double y = 0.0d;
	private static float[] z = new float[1];
	
	public PhysicalLocation() { }

	public PhysicalLocation(PhysicalLocation pl) {
		if (pl==null) throw new NullPointerException();
		
		set(pl.latitude, pl.longitude, pl.altitude);
	}

	/**
	 * Set this objects parameters. This should be used instead of creating new objects.
	 * @param latitude Latitude of the Location.
	 * @param longitude Longitude of the Location.
	 * @param altitude Altitude of the Location.
	 */
	public void set(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
	}

	/**
	 * Set the Latitude of the PhysicalLocation.
	 * @param latitude double representing the latitude of the PhysicalLocation.
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Get the Latitude of the PhysicalLocation.
	 * @return double representation the latitude of the PhysicalLocation.
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Set the Longitude of the PhysicalLocation.
	 * @param longitude double representing the longitude of the PhysicalLocation.
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Get the Longitude of the PhysicalLocation.
	 * @return double representation the longitude of the PhysicalLocation.
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Set the Altitude of the PhysicalLocation.
	 * @param altitude double representing the altitude of the PhysicalLocation.
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	/**
	 * Get the Altitude of the PhysicalLocation.
	 * @return double representation the altitude of the PhysicalLocation.
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * Converts a Location to a Vector given a PhysicalLocation.
	 * @param org Origin Location.
	 * @param gp Current PhysicalLocation.
	 * @param v Vector to populate.
	 * @throws NullPointerException if Location, PhysicalLocation, or Vector is NULL.
	 */
	public static synchronized void convLocationToVector(Location org, PhysicalLocation gp, Vector v) {
		if (org==null || gp==null || v==null) 
		    throw new NullPointerException("Location, PhysicalLocation, and Vector cannot be NULL.");

		Location.distanceBetween(	org.getLatitude(), org.getLongitude(), 
									gp.getLatitude(), org.getLongitude(), 
									z);

		Location.distanceBetween(	org.getLatitude(), org.getLongitude(), 
									org.getLatitude(), gp.getLongitude(), 
									x);
		y = gp.getAltitude() - org.getAltitude();
		if (org.getLatitude() < gp.getLatitude())
			z[0] *= -1;
		if (org.getLongitude() > gp.getLongitude())
			x[0] *= -1;
		
		v.set(x[0], (float) y, z[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "(lat=" + latitude + ", lng=" + longitude + ", alt=" + altitude + ")";
	}
}
