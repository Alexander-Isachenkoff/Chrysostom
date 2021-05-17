package chrysostom.util;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public final class Fonts
{
	private static final String[] names;
	private static final Integer[] sizes;
	private static final double coefficient = 1.338307;

	static {
		sizes = new Integer[]{ 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72 };
		names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	}

	private Fonts() {}

	public static List<String> getNames() {
		return Arrays.asList(names);
	}

	public static List<Integer> getSizes() {
		return Arrays.asList(sizes);
	}

	public static double toPixels(double points) {
		return points * coefficient;
	}

	public static double toPoints(double pixels) {
		return pixels / coefficient;
	}

	public static String ellipse(String string, Font font, int requiredWidth) {
		int currentWidth = new JLabel().getFontMetrics(font).stringWidth(string);
		if (currentWidth > requiredWidth) {
			string = string.replace("...", "");
			string = new StringBuilder(string)
					.replace(string.length() - 1, string.length(), "...").toString();
			return ellipse(string, font, requiredWidth);
		}
		return string;
	}
}
