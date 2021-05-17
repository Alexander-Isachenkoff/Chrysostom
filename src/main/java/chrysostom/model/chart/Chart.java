package chrysostom.model.chart;

import chrysostom.interfaces.Command;
import chrysostom.util.Fonts;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Chart
{
    private final static int DEFAULT_FONT_SIZE = 12;
    private final static int MARGIN_RIGHT = 18;
    private final static int TITLES_AREA_WIDTH = 100;
    private final static double STEP = 0.1;
    private final static int GAP = 5;
    private final static double[] SPECIAL_POSITIONS = new double[]{ 0.146, 0.236, 0.618, 0.944, 1 };
    private final List<DataRay> data = new ArrayList<>();
    private int marginTop;
    // Параметры
    private ChartSettings settings =
            new ChartSettings(Marker.CIRCLE, 10, ChartSettings.BLOCK, true, DEFAULT_FONT_SIZE, true);
    private List<MarkerPoint> markerMap;
    private int length;
    private int x0;
    private int y0;
    private int space;
    
    private Graphics2D g;
    private Command onDataChanged = () -> {};
    
    public ChartSettings getSettings() {
        return new ChartSettings(settings);
    }
    
    public void setSettings(ChartSettings settings) {
        this.settings = new ChartSettings(settings);
        onDataChanged.execute();
    }
    
    public void setOnDataChanged(Command onDataChanged) {
        this.onDataChanged = onDataChanged;
    }
    
    public List<DataRay> getData() {
        return data;
    }
    
    public void add(String title, DataRay dataRay) {
        data.add(new DataRay(title, dataRay));
        onDataChanged.execute();
    }
    
    public void removeAllData() {
        data.clear();
        onDataChanged.execute();
    }
    
    private void drawAxes() {
        g.setColor(Color.BLACK);
        g.drawLine(x0, y0, x0, marginTop);
        g.drawLine(x0, y0, x0 + length, y0);
    }
    
    private void drawSections() {
        for (double i = STEP; i <= 1 - STEP; i += STEP) {
            int x = (int) Math.round(x0 + i * length);
            g.drawLine(x, y0, x, y0 + 5);
        }
    }
    
    private void drawArrows() {
        g.setColor(Color.BLACK);
        int arrowWidth = 10;
        int arrowLength = 10;
        g.drawLine(x0, marginTop, x0 - arrowWidth / 2, marginTop + arrowLength);
        g.drawLine(x0, marginTop, x0 + arrowWidth / 2, marginTop + arrowLength);
        g.drawLine(x0 + length, y0, x0 + length - arrowLength, y0 - arrowWidth / 2);
        g.drawLine(x0 + length, y0, x0 + length - arrowLength, y0 + arrowWidth / 2);
    }
    
    private void drawNumbers() {
        Font font = g.getFont();
        g.setFont(new Font(font.getName(), Font.PLAIN, settings.fontSize));
        final Color numbersColor = new Color(64, 64, 64);
        g.setColor(numbersColor);
        for (double i = 0; i <= 1; i += STEP) {
            String string = String.valueOf(Math.round(i * 100) / (double) 100);
            int x = (int) Math.round(x0 + i * length) - g.getFontMetrics(g.getFont()).stringWidth(string) / 2;
            g.drawString(string, x, y0 + settings.fontSize + GAP);
        }
    }
    
    private void drawTitles() {
        int y = y0;
        Font font = g.getFont();
        g.setFont(new Font(font.getName(), Font.PLAIN, settings.fontSize));
        int rectSize = Math.max(5, settings.markerSize);
        for (DataRay dataRay : data) {
            if (y >= marginTop) {
                g.setColor(dataRay.color);
                g.fillRect(rectSize, y - rectSize / 2, rectSize, rectSize);
                g.setColor(Color.BLACK);
                g.drawRect(rectSize, y - rectSize / 2, rectSize, rectSize);
                String title = Fonts.ellipse(dataRay.name, font, TITLES_AREA_WIDTH - rectSize * 4);
                g.drawString(title, rectSize * 3, y + g.getFont().getSize() / 2 - 1);
                y -= (rectSize + space);
            }
        }
    }
    
    private void drawNet() {
        g.setColor(Color.LIGHT_GRAY);
        int h = settings.markerSize + space;
        for (int i = 1; i < data.size(); i++) {
            int y = y0 - i * h;
            if (y >= marginTop) {
                g.drawLine(x0 + 1, y, x0 + length, y);
            }
        }
        if (data.size() > 0) {
            int yTop = y0 - (data.size() - 1) * h;
            if (yTop < marginTop) {
                int height = (y0 - marginTop);
                yTop = y0 - (height - height % h);
            }
            for (double j = STEP; j <= 1; j += STEP) {
                int x = (int) Math.round(x0 + j * length);
                g.drawLine(x, y0 - 1, x, yTop);
            }
        }
    }
    
    private void drawSpecialPositions() {
        g.setColor(Color.BLACK);
        Font font = g.getFont();
        g.setFont(new Font(font.getName(), Font.BOLD, settings.fontSize));
        for (double position : SPECIAL_POSITIONS) {
            int x = x0 + (int) Math.round(position * length);
            g.fillRect(x, marginTop, 2, y0 - marginTop);
            String string = String.valueOf(position);
            int stringWidth = g.getFontMetrics().stringWidth(string);
            g.drawString(string, x - stringWidth / 2, marginTop - GAP);
        }
    }
    
    private void drawMarkers() {
        int level = y0;
        for (DataRay dataRay : data) {
            for (double d : dataRay.data) {
                int x = x0 + (int) Math.round(d * length);
                int y = (settings.view == ChartSettings.BLOCK) ? level : y0;
                if (y >= marginTop) {
                    g.drawImage(settings.marker.getImage(settings.markerSize, dataRay.color), x - settings.markerSize / 2,
                            y - settings.markerSize / 2, null);
                }
            }
            level -= (settings.markerSize + space);
        }
    }
    
    public BufferedImage getImage(int width, int height, Color background) {
        length = width - (TITLES_AREA_WIDTH + MARGIN_RIGHT);
        x0 = TITLES_AREA_WIDTH;
        y0 = height - (settings.fontSize + 2 * GAP);
        marginTop = settings.fontSize + 2 * GAP;
        space = Math.max(10, settings.markerSize * 2);
        SmoothImage image = new SmoothImage(width, height, background);
        g = image.getGraphics();
        if (settings.view == ChartSettings.BLOCK) {
            drawNet();
        }
        if (settings.specialPositions) {
            drawSpecialPositions();
        }
        drawAxes();
        drawSections();
        drawNumbers();
        if (settings.arrows) {
            drawArrows();
        }
        drawTitles();
        drawMarkers();
        return image;
    }
    
    public List<MarkerPoint> getMarkerPoints() {
        markerMap = new ArrayList<>();
        int titlesY = y0;
        for (DataRay dataRay : data) {
            for (double d : dataRay.data) {
                double coordinate = Math.round(d * 100) / (double) 100;
                int x = x0 + (int) Math.round(d * length);
                int y = (settings.view == ChartSettings.BLOCK) ? titlesY : y0;
                if (y > marginTop) {
                    markerMap.add(new MarkerPoint(x, y, String.valueOf(coordinate)));
                }
            }
            titlesY -= (settings.markerSize + space);
        }
        return markerMap;
    }
}

