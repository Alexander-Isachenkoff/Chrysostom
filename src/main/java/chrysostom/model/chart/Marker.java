package chrysostom.model.chart;

import javax.swing.*;
import java.awt.*;

public abstract class Marker
{
	final static Marker CROSS;
	final static Marker CIRCLE;
	final static Marker LINE;
	final static Marker DIAMOND;
	final static Marker SQUARE;
	final static Marker TRIANGLE;

	static {
		CIRCLE = new Marker()
		{
			@Override
			public SmoothImage getImage(int imgSize, int canvasSize, Color color) {
				SmoothImage image = new SmoothImage(canvasSize);
				Graphics g = image.getGraphics();
				g.setColor(color);
				int origin = (canvasSize - imgSize) / 2;
				g.fillOval(origin, origin, imgSize, imgSize);
				return image;
			}
		};

		CROSS = new Marker()
		{
			@Override
			public SmoothImage getImage(int imgSize, int canvasSize, Color color) {
				SmoothImage image = new SmoothImage(canvasSize);
				Graphics g = image.getGraphics();
				g.setColor(color);
				int origin = (canvasSize - imgSize) / 2;
				g.drawLine(origin, origin, canvasSize - origin, canvasSize - origin);
				g.drawLine(origin, canvasSize - origin, canvasSize - origin, origin);
				return image;
			}
		};

		LINE = new Marker()
		{
			@Override
			public SmoothImage getImage(int imgSize, int canvasSize, Color color) {
				SmoothImage image = new SmoothImage(canvasSize);
				Graphics g = image.getGraphics();
				g.setColor(color);
				int origin = (canvasSize - imgSize) / 2;
				int x = canvasSize / 2;
				g.drawLine(x, origin, x, canvasSize - origin);
				return image;
			}
		};

		SQUARE = new Marker()
		{
			@Override
			public SmoothImage getImage(int imgSize, int canvasSize, Color color) {
				SmoothImage image = new SmoothImage(canvasSize);
				Graphics g = image.getGraphics();
				g.setColor(color);
				int origin = (canvasSize - imgSize) / 2;
				g.fillRect(origin, origin, imgSize, imgSize);
				return image;
			}
		};

		DIAMOND = new Marker()
		{
			@Override
			public SmoothImage getImage(int imgSize, int canvasSize, Color color) {
				SmoothImage image = new SmoothImage(canvasSize);
				Graphics g = image.getGraphics();
				g.setColor(color);
				int origin = (canvasSize - imgSize) / 2;
				int[] xPoints = new int[]{ canvasSize / 2, origin, canvasSize / 2, canvasSize - origin };
				int[] yPoints = new int[]{ origin, canvasSize / 2, canvasSize - origin, canvasSize / 2 };
				g.fillPolygon(xPoints, yPoints, 4);
				return image;
			}
		};

		TRIANGLE = new Marker()
		{
			@Override
			public SmoothImage getImage(int imgSize, int canvasSize, Color color) {
				SmoothImage image = new SmoothImage(canvasSize);
				Graphics g = image.getGraphics();
				g.setColor(color);
				int xOrigin = (canvasSize - imgSize) / 2;
				int yOrigin = (canvasSize - (int) Math.round(imgSize * Math.sin(Math.toRadians(60)))) / 2;
				int[] xPoints = new int[]{ xOrigin, canvasSize / 2, canvasSize - xOrigin };
				int[] yPoints = new int[]{ canvasSize - yOrigin, yOrigin, canvasSize - yOrigin };
				g.fillPolygon(xPoints, yPoints, 3);
				return image;
			}
		};
	}

	public static Marker[] defaultMarkers() {
		return new Marker[]{ CROSS, CIRCLE, LINE, DIAMOND, SQUARE, TRIANGLE };
	}

	abstract SmoothImage getImage(int imgSize, int canvasSize, Color color);

	SmoothImage getImage(int size, Color color) {
		return getImage(size, size, color);
	}

	Icon getIcon(int imgSize, int canvasSize, Color color) {
		return new ImageIcon(getImage(imgSize, canvasSize, color));
	}

	public Icon getIcon(int imgSize, int canvasSize) {
		return getIcon(imgSize, canvasSize, Color.BLACK);
	}

	@Override
	public String toString() {
		return "";
	}
}
