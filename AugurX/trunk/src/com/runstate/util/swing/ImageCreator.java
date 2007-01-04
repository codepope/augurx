package com.runstate.util.swing;

import java.awt.*;

import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;


/**
 * Provides utility functions for creating <code>ImageIcon</code> s and
 * <code>BufferedImage</code> s for the GUI.
 *
 * @author Kirill Grouchnikov
 */
public class ImageCreator {
	/**
	 * The main light color for the backgrounds.
	 */
	public static final Color mainUltraLightColor = new Color(128, 192, 255);

	/**
	 * The main light color for the backgrounds.
	 */
	public static final Color mainLightColor = new Color(0, 128, 255);

	/**
	 * The main medium color for the backgrounds.
	 */
	public static final Color mainMidColor = new Color(0, 64, 196);

	/**
	 * The main dark color for the backgrounds.
	 */
	public static final Color mainDarkColor = new Color(0, 0, 128);

	/**
	 * The main ultra-dark color for the backgrounds.
	 */
	public static final Color mainUltraDarkColor = new Color(0, 0, 64);

	/**
	 * The color for icons that represent <code>Class</code> entities.
	 */
	public static final Color iconClassColor = new Color(128, 255, 128);

	/**
	 * The color for icons that represent <code>Annotation</code> entities.
	 */
	public static final Color iconAnnotationColor = new Color(141, 112, 255);

	/**
	 * The color for icons that represent <code>Field</code> or
	 * <code>Method</code> entities.
	 */
	public static final Color iconFieldMethodColor = new Color(32, 128, 255);

	/**
	 * The color for arrows on icons.
	 */
	public static final Color iconArrowColor = new Color(128, 32, 0);

	/**
	 * The default dimension for icons (both width and height).
	 */
	public static final int ICON_DIMENSION = 15;


	/**
	 * Returns an image of specified dimensions that contains the specified
	 * string in big letters. The resulting image will have gradient background
	 * and semi-transparent background for the title. The title will be centered
	 * in the center of the image.
	 *
	 * @param width
	 *            The width of the output image.
	 * @param height
	 *            The height of the output image.
	 * @param title
	 *            Title string to write on the output image.
	 * @return The resulting image.
	 */
	public static ImageIcon getTitleImage(int width, int height, String title) {
		// get gradient background image
		BufferedImage image = ImageCreator.getBackgroundImage(width, height,
				false);

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		// Find the bounds of the entire string.
		Font font = new Font("Arial", Font.PLAIN, height - 8);
		graphics.setFont(font);
		FontMetrics fm = graphics.getFontMetrics();
		FontRenderContext frc = graphics.getFontRenderContext();
		TextLayout mLayout = new TextLayout(title, font, frc);
		// Place the first full string, horizontally centered,
		// at the bottom of the component.
		Rectangle2D bounds = mLayout.getBounds();
		double x = (width - bounds.getWidth()) / 2;
		double y = (height - bounds.getHeight() + fm.getHeight()) / 2 + 3;

		Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				(float) 0.8);
		graphics.setComposite(c);
		graphics.setColor(Color.black);
		graphics.drawString(title, (int) x + 1, (int) y + 1);
		c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1.0);
		graphics.setComposite(c);
		graphics.setColor(Color.white);
		graphics.drawString(title, (int) x, (int) y);

		return new ImageIcon(image);
	}

	/**
	 * Returns an image of specified dimensions that has gradient background.
	 *
	 * @param width
	 *            The width of the output image.
	 * @param height
	 *            The height of the output image.
	 * @param leftColor
	 *            The color of the left border.
	 * @param rightColor
	 *            The color of the right border.
	 * @return The resulting image.
	 */
	public static BufferedImage getBackgroundImage(int width, int height,
			Color leftColor, Color rightColor) {
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// fill the gradient
		for (int col = 0; col < width; col++) {
			double coef = (double) col / (double) width;
			int r = (int) (leftColor.getRed() + coef
					* (rightColor.getRed() - leftColor.getRed()));
			int g = (int) (leftColor.getGreen() + coef
					* (rightColor.getGreen() - leftColor.getGreen()));
			int b = (int) (leftColor.getBlue() + coef
					* (rightColor.getBlue() - leftColor.getBlue()));
			Color c = new Color(r, g, b);
			graphics.setColor(c);
			graphics.drawLine(col, 0, col, height - 1);
		}

		return image;
	}

	/**
	 * Returns an image of specified dimensions that has gradient background and
	 * an optional gradient separator stripe on its upper border.
	 *
	 * @param width
	 *            The width of the output image.
	 * @param height
	 *            The height of the output image.
	 * @param hasStripeTop
	 *            if <code>true</code>, a gradient stripe few pixels high is
	 *            added on the upper border of this image.
	 * @return The resulting image.
	 */
	public static BufferedImage getBackgroundImage(int width, int height,
			boolean hasStripeTop) {
		BufferedImage image = getBackgroundImage(width, height,
				ImageCreator.mainLightColor, ImageCreator.mainDarkColor);

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		if (hasStripeTop) {
			// create stripe on the bottom side
			for (int col = 0; col < width; col++) {
				// set color transparency - 1.0 on 0 and width, 0.0 in the
				// middle
				float transp = Math.abs((float) (col - width / 2))
						/ (float) (width / 2);
				transp = Math.min((float) 1.0, (float) (transp * 1.25));
				Composite c = AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, (float) 1.0 - transp);
				graphics.setComposite(c);
				graphics.setColor(Color.white);
				graphics.drawLine(col, 0, col, 2);
			}
		}

		return image;
	}

//	/**
//	 * Returns an image of specified width that contains (possibly) multilined
//	 * representation of the input string. The resulting image will have a
//	 * gradient background and semi-transparent background for the text. The
//	 * input string is broken into a sequence of lines. Each line does not
//	 * exceed the specified width of the resulting image. The height of the
//	 * resulting image is computed according to the number of lines.
//	 *
//	 * @param str
//	 *            The input string to be shown,.
//	 * @param width
//	 *            The width of the output image.
//	 * @return The resulting image.
//	 */
//	public static ImageIcon getMultiline(String str, int width) {
//		// first break up the string into lines, so that each line is no
//		// longer than specified width
//		BufferedImage tempImage = new BufferedImage(width, 20,
//				BufferedImage.TYPE_INT_ARGB);
//		Graphics2D tempGraphics = (Graphics2D) tempImage.getGraphics();
//		tempGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//
//		Font font = new Font("Arial", Font.PLAIN, 12);
//
//		// here we could use LineBreakMeasurer, but it's not good
//		// because we don't know the number of lines in advance.
//		// So, in any case we need to break up the string, compute
//		// the number of lines, allocate new image and then put
//		// the lines on it. Might as well take the simple approach
//		// and do it ourselves.
//		tempGraphics.setFont(font);
//		FontRenderContext frc = tempGraphics.getFontRenderContext();
//
//		LinkedList<String> lines = new LinkedList<String>();
//		StringTokenizer tokenizer = new StringTokenizer(str, " ", false);
//		String currLine = "";
//		while (tokenizer.hasMoreTokens()) {
//			String currToken = tokenizer.nextToken() + " ";
//			String newLine = currLine + currToken;
//			TextLayout mLayout = new TextLayout(newLine, font, frc);
//			// Place the first full string, horizontally centered,
//			// at the bottom of the component.
//			Rectangle2D bounds = mLayout.getBounds();
//			if (bounds.getWidth() > (width - 20)) {
//				// start new line
//				lines.addLast(currLine);
//				currLine = currToken;
//			} else {
//				currLine = newLine;
//			}
//		}
//		// add the last one
//		lines.addLast(currLine);
//
//		// count the number of lines
//		int lineCount = lines.size();
//		FontMetrics fm = tempGraphics.getFontMetrics();
//		int height = lineCount * (fm.getHeight() + 5);
//
//		// create new image
//		BufferedImage image = ImageCreator.getBackgroundImage(width, height,
//				false);
//		// write all strings
//		Graphics2D graphics = (Graphics2D) image.getGraphics();
//		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//		graphics.setFont(font);
//		int ypos = fm.getHeight();
//		for (String line : lines) {
//			Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
//					(float) 0.8);
//			graphics.setComposite(c);
//			graphics.setColor(Color.black);
//			graphics.drawString(line, (int) 6, (int) ypos + 1);
//			c = AlphaComposite
//					.getInstance(AlphaComposite.SRC_OVER, (float) 1.0);
//			graphics.setComposite(c);
//			graphics.setColor(Color.white);
//			graphics.drawString(line, 5, ypos);
//			ypos += (fm.getHeight() + 1);
//		}
//
//		return new ImageIcon(image);
//	}

	/**
	 * Returns an icon image with specified background that contains a single
	 * letter in its center with optional marker sign that is shown when
	 * <code>isCollection</code> parameter is <code>true</code>.
	 *
	 * @param letter
	 *            The letter to show in the center of the icon. This letter is
	 *            capitalized if it was not capitalized.
	 * @param isCollection
	 *            If <code>true</code>, a special marker sign is shown in the
	 *            upper-right portion of the resulting image icon.
	 * @param backgroundColor
	 *            The background color for the resulting image icon.
	 * @return An icon image with specified background that contains a single
	 *         letter in its center with optional marker sign that is shown when
	 *         <code>isCollection</code> parameter is <code>true</code>.
	 */
	public static BufferedImage getSingleLetterImage(char letter,
			boolean isCollection, Color backgroundColor) {

		BufferedImage image = new BufferedImage(ICON_DIMENSION, ICON_DIMENSION,
				BufferedImage.TYPE_INT_ARGB);
		// set completely transparent
		for (int col = 0; col < ICON_DIMENSION; col++) {
			for (int row = 0; row < ICON_DIMENSION; row++) {
				image.setRGB(col, row, 0x0);
			}
		}

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		letter = Character.toUpperCase(letter);
		graphics.setFont(new Font("Arial", Font.BOLD, 10));
		FontRenderContext frc = graphics.getFontRenderContext();
		TextLayout mLayout = new TextLayout("" + letter, graphics.getFont(),
				frc);

		float x = (float) (-.5 + (ICON_DIMENSION - mLayout.getBounds()
				.getWidth()) / 2);
		float y = ICON_DIMENSION
				- (float) ((ICON_DIMENSION - mLayout.getBounds().getHeight()) / 2);

		graphics.setColor(backgroundColor);
		graphics.fillOval(0, 0, ICON_DIMENSION - 1, ICON_DIMENSION - 1);

		// create a whitish spot in the left-top corner of the icon
		double id4 = ICON_DIMENSION / 4.0;
		double spotX = id4;
		double spotY = id4;
		for (int col = 0; col < ICON_DIMENSION; col++) {
			for (int row = 0; row < ICON_DIMENSION; row++) {
				// distance to spot
				double dx = col - spotX;
				double dy = row - spotY;
				double dist = Math.sqrt(dx * dx + dy * dy);

				// distance of 0.0 - comes 90% to Color.white
				// distance of ICON_DIMENSION - stays the same

				if (dist > ICON_DIMENSION) {
					dist = ICON_DIMENSION;
				}

				int currColor = image.getRGB(col, row);
				int transp = (currColor >>> 24) & 0xFF;
				int oldR = (currColor >>> 16) & 0xFF;
				int oldG = (currColor >>> 8) & 0xFF;
				int oldB = (currColor >>> 0) & 0xFF;

				double coef = 0.9 - 0.9 * dist / ICON_DIMENSION;
				int dr = 255 - oldR;
				int dg = 255 - oldG;
				int db = 255 - oldB;

				int newR = (int) (oldR + coef * dr);
				int newG = (int) (oldG + coef * dg);
				int newB = (int) (oldB + coef * db);

				int newColor = (transp << 24) | (newR << 16) | (newG << 8)
						| newB;
				image.setRGB(col, row, newColor);

			}
		}

		// draw outline of the icon
		graphics.setColor(Color.black);
		graphics.drawOval(0, 0, ICON_DIMENSION - 1, ICON_DIMENSION - 1);

		// draw the letter
		graphics.drawString("" + letter, x, y);

		// if collection - draw '+' sign
		if (isCollection) {
			int xp = ICON_DIMENSION - 6;
			int yp = 3;
			graphics.setColor(new Color(255, 255, 255, 128));
			graphics.drawLine(xp - 1, yp - 2, xp + 6, yp - 2);
			graphics.drawLine(xp - 1, yp - 1, xp + 6, yp - 1);
			graphics.drawLine(xp - 1, yp, xp + 6, yp);
			graphics.drawLine(xp - 1, yp + 1, xp + 6, yp + 1);
			graphics.drawLine(xp + 1, yp - 4, xp + 1, yp + 3);
			graphics.drawLine(xp + 2, yp - 4, xp + 2, yp + 3);
			graphics.drawLine(xp + 3, yp - 4, xp + 3, yp + 3);
			graphics.drawLine(xp + 4, yp - 4, xp + 4, yp + 3);
			graphics.setColor(new Color(255, 64, 64));
			graphics.drawLine(xp, yp - 1, xp + 5, yp - 1);
			graphics.drawLine(xp, yp, xp + 5, yp);
			graphics.drawLine(xp + 2, yp - 3, xp + 2, yp + 2);
			graphics.drawLine(xp + 3, yp - 3, xp + 3, yp + 2);
		}

		return image;
	}

	/**
	 * Returns an icon image with specified background that contains a single
	 * letter in its center with optional marker sign that is shown when
	 * <code>isCollection</code> parameter is <code>true</code>.
	 *
	 * @param letter
	 *            The letter to show in the center of the icon. This letter is
	 *            capitalized if it was not capitalized.
	 * @param isCollection
	 *            If <code>true</code>, a special marker sign is shown in the
	 *            upper-right portion of the resulting image icon.
	 * @param backgroundColor
	 *            The background color for the resulting image icon.
	 * @return An icon image with specified background that contains a single
	 *         letter in its center with optional marker sign that is shown when
	 *         <code>isCollection</code> parameter is <code>true</code>.
	 */
	public static ImageIcon getSingleLetterIcon(char letter,
			boolean isCollection, Color backgroundColor) {
		return new ImageIcon(getSingleLetterImage(letter, isCollection,
				backgroundColor));
	}

	/**
	 * Returns an icon image with specified background that contains a single
	 * letter in its center, an arrow in the bottom portion of the icon and an
	 * optional marker sign that is shown when <code>isCollection</code>
	 * parameter is <code>true</code>.
	 *
	 * @param letter
	 *            The letter to show in the center of the icon. This letter is
	 *            capitalized if it was not capitalized.
	 * @param isCollection
	 *            If <code>true</code>, a special marker sign is shown in the
	 *            upper-right portion of the resulting image icon.
	 * @param backgroundColor
	 *            The background color for the resulting image icon.
	 * @param isArrowToRight
	 *            If <code>true</code>, the arrow will point to the right,
	 *            otherwise the arrow will point to the left.
	 * @param arrowColor
	 *            The color of the arrow.
	 * @return An icon image with specified background that contains a single
	 *         letter in its center, an arrow in the bottom portion of the icon
	 *         and an optional marker sign that is shown when
	 *         <code>isCollection</code> parameter is <code>true</code>.
	 */
	public static ImageIcon getLetterIconWithArrow(char letter,
			boolean isCollection, Color backgroundColor,
			boolean isArrowToRight, Color arrowColor) {

		BufferedImage image = getSingleLetterImage(letter, isCollection,
				backgroundColor);

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		BufferedImage arrowImage = getArrowImage(arrowColor, ICON_DIMENSION,
				isArrowToRight);
		graphics.drawImage(arrowImage, 0, ICON_DIMENSION
				- arrowImage.getHeight(), null);

		return new ImageIcon(image);
	}

	/**
	 * Returns an image that contains a right-pointing arrow of specified width
	 * and color.
	 *
	 * @param color
	 *            Arrow color.
	 * @param width
	 *            Arrow width.
	 * @return An image that contains a right-pointing arrow of specified width
	 *         and color.
	 */
	public static BufferedImage getRightArrow(Color color, int width) {
		int height = 6;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		// set completely transparent
		for (int col = 0; col < width; col++) {
			for (int row = 0; row < height; row++) {
				image.setRGB(col, row, 0x0);
			}
		}

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// draw arrow
		Polygon pol = new Polygon();
		int ya = 3;
		pol.addPoint(1, ya);
		pol.addPoint(width / 2 + 3, ya);
		pol.addPoint(width / 2 + 3, ya + 2);
		pol.addPoint(width - 1, ya);
		pol.addPoint(width / 2 + 3, ya - 2);
		pol.addPoint(width / 2 + 3, ya);
		graphics.setColor(color);
		graphics.drawPolygon(pol);

		// create semi-transparent halo around arrow (to make it stand
		// out)
		BufferedImage fimage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		// set completely transparent
		for (int col = 0; col < width; col++) {
			for (int row = 0; row < height; row++) {
				fimage.setRGB(col, row, 0x0);
			}
		}
		Graphics2D fgraphics = (Graphics2D) fimage.getGraphics();
		for (int col = 0; col < width; col++) {
			int xs = Math.max(0, col - 1);
			int xe = Math.min(width - 1, col + 1);
			for (int row = 0; row < height; row++) {
				int ys = Math.max(0, row - 1);
				int ye = Math.min(height - 1, row + 1);
				int currColor = image.getRGB(col, row);
				int opacity = (currColor >>> 24) & 0xFF;
				if (opacity > 0) {
					// mark all pixels in 3*3 area
					for (int x = xs; x <= xe; x++) {
						for (int y = ys; y <= ye; y++) {
							int oldOpacity = (fimage.getRGB(x, y) >>> 24) & 0xFF;
							int newOpacity = Math.max(oldOpacity, opacity);
							// set semi-transparent white
							int newColor = (newOpacity << 24) | (255 << 16)
									| (255 << 8) | 255;
							fimage.setRGB(x, y, newColor);
						}
					}
				}
			}
		}

		// reduce opacity of all pixels by 30%
		for (int col = 0; col < width; col++) {
			for (int row = 0; row < height; row++) {
				int oldOpacity = (fimage.getRGB(col, row) >>> 24) & 0xFF;
				int newOpacity = (int) (0.7 * oldOpacity);
				int newColor = (newOpacity << 24) | (255 << 16) | (255 << 8)
						| 255;
				fimage.setRGB(col, row, newColor);
			}
		}

		// draw the original arrow image on top of the halo
		fgraphics.drawImage(image, 0, 0, null);

		return fimage;
	}

	/**
	 * Returns an image that contains a left-pointing arrow of specified width
	 * and color.
	 *
	 * @param arrowColor
	 *            Arrow color.
	 * @param width
	 *            Arrow width.
	 * @return An image that contains a left-pointing arrow of specified width
	 *         and color.
	 */
	public static BufferedImage getLeftArrow(Color arrowColor, int width) {
		BufferedImage rimage = getRightArrow(arrowColor, width);

		int height = rimage.getHeight();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		for (int col = 0; col < width; col++) {
			for (int row = 0; row < height; row++) {
				image.setRGB(col, row, rimage.getRGB(width - 1 - col, row));
			}
		}
		return image;
	}

	/**
	 * Returns an image that contains an arrow of specified width, direction and
	 * color.
	 *
	 * @param arrowColor
	 *            Arrow color.
	 * @param width
	 *            Arrow width.
	 * @param isArrowToRight
	 *            If <code>true</code>, the arrow is pointing to the right,
	 *            otherwise the arrow is pointing to the left.
	 * @return An image that contains a left-pointing arrow of specified width
	 *         and color.
	 */
	public static BufferedImage getArrowImage(Color arrowColor, int width,
			boolean isArrowToRight) {
		if (isArrowToRight) {
			return getRightArrow(arrowColor, width);
		} else {
			return getLeftArrow(arrowColor, width);
		}
	}

	/**
	 * Creates a one-pixel high gradient image of specified width, opacity and
	 * colors of the starting pixel and the ending pixel.
	 *
	 * @param width
	 *            The width of the resulting image.
	 * @param leftColor
	 *            The color of the first pixel of the resulting image.
	 * @param rightColor
	 *            The color of the last pixel of the resulting image.
	 * @param opacity
	 *            The opacity of the resulting image (in 0..1 range). The
	 *            smaller the value, the more transparent the resulting image
	 *            is.
	 * @return A one-pixel high gradient image of specified width, opacity and
	 *         colors of the starting pixel and the ending pixel.
	 */
	public static BufferedImage createGradientLine(int width, Color leftColor,
			Color rightColor, double opacity) {
		BufferedImage image = new BufferedImage(width, 1,
				BufferedImage.TYPE_INT_ARGB);
		int iOpacity = (int) (255 * opacity);

		for (int col = 0; col < width; col++) {
			double coef = (double) col / (double) width;
			int r = (int) (leftColor.getRed() + coef
					* (rightColor.getRed() - leftColor.getRed()));
			int g = (int) (leftColor.getGreen() + coef
					* (rightColor.getGreen() - leftColor.getGreen()));
			int b = (int) (leftColor.getBlue() + coef
					* (rightColor.getBlue() - leftColor.getBlue()));

			int color = (iOpacity << 24) | (r << 16) | (g << 8) | b;
			image.setRGB(col, 0, color);
		}
		return image;
	}

	/**
	 * Returns a gutter marker of specified dimensions and theme color. The
	 * resulting image is a rectangle with dark border and light filling. The
	 * colors of the border and the filling are created based on the input
	 * color.
	 *
	 * @param themeColor
	 *            Base color for border and filling.
	 * @param width
	 *            Gutter marker width.
	 * @param height
	 *            Gutter marker height.
	 * @return Gutter marker of specified dimensions and theme color.
	 */
	public static BufferedImage getGutterMarker(Color themeColor, int width,
			int height) {
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		// darker color for border
		int rd = themeColor.getRed() / 2;
		int gd = themeColor.getGreen() / 2;
		int bd = themeColor.getBlue() / 2;
		Color darkColor = new Color(rd, gd, bd);
		// lighter color for inside
		int rl = 255 - (255 - themeColor.getRed()) / 4;
		int gl = 255 - (255 - themeColor.getGreen()) / 4;
		int bl = 255 - (255 - themeColor.getBlue()) / 4;
		Color lightColor = new Color(rl, gl, bl);
		graphics.setColor(lightColor);
		graphics.fillRect(0, 0, width - 1, height - 1);
		graphics.setColor(darkColor);
		graphics.drawRect(0, 0, width - 1, height - 1);
		return image;
	}

	/**
	 * Returns a square gutter status image of specified dimensions and theme
	 * color. The resulting image is a square with gradient filling and dark and
	 * light borders.
	 *
	 * @param themeColor
	 *            Base color for borders and filling.
	 * @param dimension
	 *            Width and height of the resulting image.
	 * @return Square gutter status image of specified dimensions and theme
	 *         color
	 */
	public static BufferedImage getGutterStatusImage(Color themeColor,
			int dimension) {
		BufferedImage image = new BufferedImage(dimension, dimension,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();

		// ultra dark color
		int rud = themeColor.getRed() / 4;
		int gud = themeColor.getGreen() / 4;
		int bud = themeColor.getBlue() / 4;
		Color ultraDarkColor = new Color(rud, gud, bud);
		// darker color
		int rd = themeColor.getRed() / 2;
		int gd = themeColor.getGreen() / 2;
		int bd = themeColor.getBlue() / 2;
		Color darkColor = new Color(rd, gd, bd);
		// lighter color
		int rl = 255 - (255 - themeColor.getRed()) / 4;
		int gl = 255 - (255 - themeColor.getGreen()) / 4;
		int bl = 255 - (255 - themeColor.getBlue()) / 4;
		Color lightColor = new Color(rl, gl, bl);

		// create gradient
		GradientPaint gradient = new GradientPaint(0, 0, lightColor, dimension,
				dimension, darkColor);
		graphics.setPaint(gradient);
		Rectangle rect = new Rectangle(dimension, dimension);
		graphics.fill(rect);
		graphics.setColor(ultraDarkColor);
		graphics.drawLine(0, 0, dimension - 1, 0);
		graphics.drawLine(0, 0, 0, dimension - 1);
		graphics.setColor(lightColor);
		graphics.drawLine(0, dimension - 1, dimension - 1, dimension - 1);
		graphics.drawLine(dimension - 1, 1, dimension - 1, dimension - 1);

		return image;
	}

	/**
	 * Returns an error marker of specified dimension with an <code>X</code>
	 * inside. The resulting image is a red circular icon with white diagonal
	 * cross with black border.
	 *
	 * @param dimension
	 *            The diameter of the resulting marker.
	 * @return Error marker of specified dimension with an <code>X</code>
	 *         inside
	 */
	public static BufferedImage getErrorMarker(int dimension) {
		BufferedImage image = new BufferedImage(dimension, dimension,
				BufferedImage.TYPE_INT_ARGB);
		// set completely transparent
		for (int col = 0; col < dimension; col++) {
			for (int row = 0; row < dimension; row++) {
				image.setRGB(col, row, 0x0);
			}
		}

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		graphics.setColor(Color.red);
		graphics.fillOval(0, 0, dimension - 1, dimension - 1);

		// create a whitish spot in the left-top corner of the icon
		double id4 = dimension / 4.0;
		double spotX = id4;
		double spotY = id4;
		for (int col = 0; col < dimension; col++) {
			for (int row = 0; row < dimension; row++) {
				// distance to spot
				double dx = col - spotX;
				double dy = row - spotY;
				double dist = Math.sqrt(dx * dx + dy * dy);

				// distance of 0.0 - comes 90% to Color.white
				// distance of dimension - stays the same

				if (dist > dimension) {
					dist = dimension;
				}

				int currColor = image.getRGB(col, row);
				int transp = (currColor >>> 24) & 0xFF;
				int oldR = (currColor >>> 16) & 0xFF;
				int oldG = (currColor >>> 8) & 0xFF;
				int oldB = (currColor >>> 0) & 0xFF;

				double coef = 0.9 - 0.9 * dist / dimension;
				int dr = 255 - oldR;
				int dg = 255 - oldG;
				int db = 255 - oldB;

				int newR = (int) (oldR + coef * dr);
				int newG = (int) (oldG + coef * dg);
				int newB = (int) (oldB + coef * db);

				int newColor = (transp << 24) | (newR << 16) | (newG << 8)
						| newB;
				image.setRGB(col, row, newColor);

			}
		}

		// draw outline of the icon
		graphics.setColor(new Color(0, 0, 0, 128));
		graphics.drawOval(0, 0, dimension - 1, dimension - 1);

		// draw the X sign
		Polygon pol = new Polygon();
		int dm = dimension / 2;
		int[] x = new int[] { dm - 3, dm + 3, dm, dm - 3, dm + 3 };
		int[] y = new int[] { dm - 3, dm + 3, dm, dm + 3, dm - 3 };
		graphics.setStroke(new BasicStroke(2.4f));
		graphics.setColor(Color.black);
		graphics.drawPolyline(x, y, 5);
		graphics.setStroke(new BasicStroke(1.6f));
		graphics.setColor(Color.white);
		graphics.drawPolyline(x, y, 5);
		return image;
	}


	/**
	 * Returns a success marker of specified dimension with an <code>V</code>
	 * inside. The resulting image is a green circular icon with white V-shaped
	 * mark with black border.
	 *
	 * @param dimension
	 *            The diameter of the resulting marker.
	 * @return Success marker of specified dimension with a <code>V</code>
	 *         inside
	 */
	public static ImageIcon getErrorMarkerIcon(int dimension) {
		return new ImageIcon(getErrorMarker(dimension));
	}

	/**
	 * Returns a success marker of specified dimension with an <code>V</code>
	 * inside. The resulting image is a green circular icon with white V-shaped
	 * mark with black border.
	 *
	 * @param dimension
	 *            The diameter of the resulting marker.
	 * @return Success marker of specified dimension with a <code>V</code>
	 *         inside
	 */
	public static BufferedImage getSuccessMarker(int dimension) {
		BufferedImage image = new BufferedImage(dimension, dimension,
				BufferedImage.TYPE_INT_ARGB);
		// set completely transparent
		for (int col = 0; col < dimension; col++) {
			for (int row = 0; row < dimension; row++) {
				image.setRGB(col, row, 0x0);
			}
		}

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		graphics.setColor(new Color(0, 196, 0));
		graphics.fillOval(0, 0, dimension - 1, dimension - 1);

		// create a whitish spot in the left-top corner of the icon
		double id4 = dimension / 4.0;
		double spotX = id4;
		double spotY = id4;
		for (int col = 0; col < dimension; col++) {
			for (int row = 0; row < dimension; row++) {
				// distance to spot
				double dx = col - spotX;
				double dy = row - spotY;
				double dist = Math.sqrt(dx * dx + dy * dy);

				// distance of 0.0 - comes 60 to Color.white
				// distance of dimension - stays the same

				if (dist > dimension) {
					dist = dimension;
				}

				int currColor = image.getRGB(col, row);
				int transp = (currColor >>> 24) & 0xFF;
				int oldR = (currColor >>> 16) & 0xFF;
				int oldG = (currColor >>> 8) & 0xFF;
				int oldB = (currColor >>> 0) & 0xFF;

				double coef = 0.6 - 0.6 * dist / dimension;
				int dr = 255 - oldR;
				int dg = 255 - oldG;
				int db = 255 - oldB;

				int newR = (int) (oldR + coef * dr);
				int newG = (int) (oldG + coef * dg);
				int newB = (int) (oldB + coef * db);

				int newColor = (transp << 24) | (newR << 16) | (newG << 8)
						| newB;
				image.setRGB(col, row, newColor);

			}
		}

		// draw outline of the icon
		graphics.setColor(new Color(0, 0, 0, 128));
		graphics.drawOval(0, 0, dimension - 1, dimension - 1);

		// draw the V sign
		Polygon pol = new Polygon();
		int dm = dimension / 2;
		int[] x = new int[] { dm - 3, dm - 1, dm + 2 };
		int[] y = new int[] { dm - 1, dm + 2, dm - 3 };
		graphics.setStroke(new BasicStroke(2.5f));
		graphics.setColor(Color.black);
		graphics.drawPolyline(x, y, 3);
		graphics.setStroke(new BasicStroke(1.8f));
		graphics.setColor(Color.white);
		graphics.drawPolyline(x, y, 3);
		return image;
	}

	/**
	 * Returns an error marker of specified dimension with an <code>X</code>
	 * inside. The resulting image is a red circular icon with white diagonal
	 * cross with black border.
	 *
	 * @param dimension
	 *            The diameter of the resulting marker.
	 * @return Error marker of specified dimension with an <code>X</code>
	 *         inside
	 */
	public static ImageIcon getSuccessMarkerIcon(int dimension) {
		return new ImageIcon(getSuccessMarker(dimension));
	}
}
