package chrysostom.model.chart;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DataRay
{
	public String name;
	public Color color;
	public List<Double> data;

	private DataRay(String name, List<Double> list, Color color) {
		this.name = name;
		this.data = new ArrayList<>(list);
		this.color = new Color(color.getRGB());
	}

	public DataRay(List<Double> list, Color color) {
		this(null, list, color);
	}

	public DataRay(String name, DataRay dataRay) {
		this(name, dataRay.data, dataRay.color);
	}
}
