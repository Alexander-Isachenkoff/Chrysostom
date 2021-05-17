package chrysostom.model.chart;

import java.awt.*;
import java.awt.image.BufferedImage;

class SmoothImage extends BufferedImage
{
	private Graphics2D g;

	{
		g = super.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	SmoothImage(int width, int height) {
		this(width, height, null);
	}

	SmoothImage(int width, int height, Color background) {
		super(width, height, (background == null) ? (BufferedImage.TYPE_INT_ARGB) : (BufferedImage.TYPE_INT_RGB));
		g.setColor((background != null) ? background : new Color(0, true));
		g.fillRect(0, 0, width, height);
	}

	SmoothImage(int size) {
		this(size, size);
	}

	@Override
	public Graphics2D getGraphics() {
		return this.g;
	}
}
