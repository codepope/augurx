/**
 * DimmerFilter.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util.swing;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

public class DimmerFilter extends RGBImageFilter
{
	public DimmerFilter()
	{
		// When this is set to true, the filter will work with images
		// whose pixels are indices into a color table (IndexColorModel).
		// In such a case, the color values in the color table are filtered.
		canFilterIndexColorModel = true;
	}
    
	// This method is called for every pixel in the image
	public int filterRGB(int x, int y, int rgb)
	{
		if (x == -1)
		{
			// The pixel value is from the image's color table rather than the image itself
		}
		
		Color c=new Color(rgb);
		
		return c.darker().getRGB();
	}
}


