package chrysostom.view.chart;

import chrysostom.model.chart.Chart;
import chrysostom.model.chart.MarkerPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.List;

public class ChartPanel extends JPanel
{
    private final Chart chart = new Chart();
    private List<MarkerPoint> markerMap;
    private final PopUp popUp = new PopUp();
    
    public ChartPanel() {
        initPanelView();
        addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseMoved(MouseEvent e) {
                MarkerPoint markerPoint = getHoveredMarker(e.getPoint());
                if (markerPoint != null) {
                    showPopup(markerPoint);
                } else {
                    hidePopup();
                }
            }
        });
        chart.setOnDataChanged(this::repaint);
    }
    
    public Chart getChart() {
        return chart;
    }
    
    private void initPanelView() {
        final Color borderColor = new Color(81, 81, 81);
        setBorder(BorderFactory.createLineBorder(borderColor));
        setBackground(Color.WHITE);
    }
    
    private void showPopup(MarkerPoint markerPoint) {
        popUp.setText(markerPoint.title);
        int x = getLocationOnScreen().x + markerPoint.x - popUp.getWidth();
        int y = getLocationOnScreen().y + markerPoint.y - popUp.getHeight();
        popUp.setLocation(x, y);
        popUp.setVisible(true);
    }
    
    private void hidePopup() {
        popUp.setVisible(false);
    }
    
    private MarkerPoint getHoveredMarker(Point p) {
        for (MarkerPoint markerPoint : markerMap) {
            if (isOnMarker(p, markerPoint)) {
                return markerPoint;
            }
        }
        return null;
    }
    
    private boolean isOnMarker(Point p, MarkerPoint markerPoint) {
        int r = chart.getSettings().markerSize / 2;
        return Math.abs(p.x - markerPoint.x) <= r &&
                Math.abs(p.y - markerPoint.y) <= r;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        draw(g);
    }
    
    private void draw(Graphics g) {
        g.drawImage(getImage(), 0, 0, null);
        markerMap = chart.getMarkerPoints();
    }
    
    BufferedImage getImage() {
        Color bg = isOpaque() ? getBackground() : null;
        return chart.getImage(getWidth(), getHeight(), bg);
    }
}
