package chrysostom.model.chart;

public class ChartSettings
{
	public final static int INLINE = 1;
	public final static int BLOCK = 2;

	public Marker marker;
	public int markerSize;
	public int view;
	public boolean arrows;
	public int fontSize;
	public boolean specialPositions;

	ChartSettings(ChartSettings chartSettings) {
		this.marker = chartSettings.marker;
		this.markerSize = chartSettings.markerSize;
		this.view = chartSettings.view;
		this.arrows = chartSettings.arrows;
		this.fontSize = chartSettings.fontSize;
		this.specialPositions = chartSettings.specialPositions;
	}

	public ChartSettings(Marker marker, int markerSize, int view, boolean arrows, int fontSize, boolean specialPositions) {
		this.marker = marker;
		this.markerSize = markerSize;
		this.view = view;
		this.arrows = arrows;
		this.fontSize = fontSize;
		this.specialPositions = specialPositions;
	}
}
