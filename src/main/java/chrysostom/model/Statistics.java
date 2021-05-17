package chrysostom.model;

import chrysostom.model.entities.Anaphora;
import chrysostom.model.entities.Variant;

import java.util.Collections;
import java.util.List;

public final class Statistics
{
	public static int getMaxStart(String text, String substring, int offset) {
		int start = -1;
		int index = 0;
		while (true) {
			index = text.indexOf(substring, index);
			if (index == -1 || index > offset) {
				break;
			}
			if (index > start) {
				start = index;
			}
			index++;
		}
		return start;
	}

	public static int getMinEnd(String text, String substring, int offset) {
		int end = -1;
		int index = offset;
		while (true) {
			index = text.indexOf(substring, index);
			if (index == -1) {
				break;
			}
			if (index < end || end == -1) {
				end = index;
			}
			index++;
		}
		return end;
	}

	public static int getMaxAnaphoraLength(List<Anaphora> anaphoraList) {
		int maxLength = 0;
		for (Anaphora anaphora : anaphoraList) {
			for (String variant : anaphora.getAllVariants()) {
				if (variant.length() > maxLength) {
					maxLength = variant.length();
				}
			}
		}
		return maxLength;
	}

	public static List<Double> getCoordinates(Anaphora anaphora, Text text) {
		List<Double> coordinates = text.getRelativeStringCoordinates(anaphora.getDescription());
		for (Variant variant : anaphora.getVariants()) {
			coordinates.addAll(text.getRelativeStringCoordinates(variant.getText()));
		}
		Collections.sort(coordinates);
		return coordinates;
	}

	public static double roundTo(double number, int accuracy) {
		double order = Math.pow(10, accuracy);
		return Math.round(number * order) / order;
	}
}
