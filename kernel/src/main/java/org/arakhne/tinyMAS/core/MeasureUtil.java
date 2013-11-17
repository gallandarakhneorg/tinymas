/* 
 * $Id$
 * 
 * Copyright (C) 2004-2007 St&eacute;phane GALLAND, Nicolas GAUD
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * This program is free software; you can redistribute it and/or modify
 */

package org.arakhne.tinyMAS.core;

/**
 * This class permits to manipulate measure units.
 * <p>
 * A foot is a unit of length, in a number of different systems, 
 * including English units, Imperial units, and United States
 * customary units. Its size can vary from system to system,
 * but in each is around a quarter to a third of a meter.
 * The most commonly used foot today is the international foot.
 * There are 3 feet in a yard and 12 inches in a foot.
 * <p>
 * An inch is the name of a unit of length in a number of
 * different systems, including English units, Imperial units,
 * and United States customary units. Its size can vary from system
 * to system. There are 36 inches in a yard and 12 inches in a foot.
 * <p>
 * The fathoms is the name of a unit of length,
 * in a number of different systems, including
 * English units, Imperial units, and United
 * States customary units. The name derives from
 * the Old English word f&aelig;thm (plural) meaning 
 * 'outstretched arms' which was the original
 * definition of the unit's measure.
 * 
 * @author St&eacute;phane GALLAND &lt;stephane.galland@utbm.fr&gt;
 * @version $Name:  $ $Revision: 1.1 $ $Date: 2007-02-20 08:52:35 $
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class MeasureUtil {

	/** Translate m/s to km/h.
	 */
	public static double ms2kmh(double ms) {
		//return ((ms/1000.0)*3600.0);
		return ms*3.6;
	}

	/** Translate km/h to m/s.
	 */
	public static double kmh2ms(double kmh) {
		//return ((kmh/3600.0)*1000.0);
		return kmh / 3.6;
	}

	/** Translate meters to kilometers.
	 */
	public static double m2km(double m) {
		return m / 1000.;
	}
	
	/** Translate kilometers to meters.
	 */
	public static double km2m(double km) {
		return km * 1000.;
	}

	/** Translate from "long" pixel coordinate to "system" pixel coordinate.
	 * 
	 * @param pixel_coord is the pixel coordinate to translate.
	 */
	public static int pix2pix(double pixel_coord) {
		return (int)Math.round(pixel_coord);
	}
	
	/** Translate from "long" pixel coordinate to "system" pixel coordinate.
	 * 
	 * @param pixel_coord is the pixel coordinate to translate.
	 */
	public static int pix2pix(long pixel_coord) {
		return (int)pixel_coord;
	}

	/** Translate from "long" pixel coordinate to "system" pixel coordinate.
	 * 
	 * @param pixel_coord is the pixel coordinate to translate.
	 */
	public static int pix2pix(float pixel_coord) {
		return Math.round(pixel_coord);
	}

	/** Translate from unit (10^0) to nano (10^-9).
	 */
	public static double unit2nano(double unit) {
		return unit / 1e-9;
	}
	
	/** Translate from nano (10^-9) to unit (10^0).
	 */
	public static double nano2unit(double nano) {
		return nano * 1e-9;
	}

	/** Translate from unit (10^0) to micro (10^-6).
	 */
	public static double unit2micro(double unit) {
		return unit / 1e-6;
	}
	
	/** Translate from micro (10^-6) to unit (10^0).
	 */
	public static double micro2unit(double micro) {
		return micro * 1e-6;
	}

	/** Translate from unit (10^0) to milli (10^-3).
	 */
	public static double unit2milli(double unit) {
		return unit / 1e-3;
	}
	
	/** Translate from milli (10^-3) to unit (10^0).
	 */
	public static double milli2unit(double milli) {
		return milli * 1e-3;
	}
	
	/** Translate from milli (10^-3) to micro (10^-6).
	 */
	public static double milli2micro(double milli) {
		return milli / 1e-3;
	}

	/** Translate from milli (10^-3) to nano (10^-9).
	 */
	public static double milli2nano(double milli) {
		return milli / 1e-6;
	}

	/** Translate from micro (10^-6) to nano (10^-9).
	 */
	public static double micro2nano(double milli) {
		return milli / 1e-3;
	}

	/** Translate from micro (10^-6) to milli (10^-3).
	 */
	public static double micro2milli(double micro) {
		return micro * 1e-3;
	}

	/** Translate from nano (10^-9) to micro (10^-6).
	 */
	public static double nano2micro(double nano) {
		return nano * 1e-3;
	}

	/** Translate from nano (10^-9) to milli (10^-3).
	 */
	public static double nano2milli(double nano) {
		return nano * 1e-6;
	}
	
	/** Translate meters to fathoms.
	 */
	public static double m2fh(double m) {
		return m * 0.5468;
	}

	/** Translate feets to fathoms.
	 */
	public static double ft2fh(double ft) {
		return ft * 0.1667;
	}

	/** Translate inches to fathoms.
	 */
	public static double in2fh(double in) {
		return in / 72;
	}

	/** Translate meters to feets.
	 */
	public static double m2ft(double m) {
		return m * 0.3048;
	}

	/** Translate inches to feets.
	 */
	public static double in2ft(double in) {
		return in / 12;
	}

	/** Translate fathoms to feets.
	 */
	public static double fh2ft(double fh) {
		return fh / 0.1667;
	}
	
	/** Translate meters to inches.
	 */
	public static double m2in(double m) {
		return m * 0.025;
	}
	
	/** Translate feets to inches.
	 */
	public static double ft2in(double ft) {
		return ft * 12;
	}

}