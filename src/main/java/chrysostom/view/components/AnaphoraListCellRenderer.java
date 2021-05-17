package chrysostom.view.components;

import chrysostom.model.entities.Anaphora;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class AnaphoraListCellRenderer implements ListCellRenderer<Anaphora>
{
    private DefaultListCellRenderer renderer = new DefaultListCellRenderer();
    
    private static Icon createColoredCircle(int size, Color color) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0,true));
        g.fillRect(0, 0, size-1, size-1);
        g.setColor(color);
        g.fillOval(0, 0, size-1, size-1);
        g.setColor(Color.BLACK);
        g.drawOval(0, 0, size-1, size-1);
        return new ImageIcon(image);
    }
    
    private static Icon createColoredRect(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width - 1, height - 1);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);
        return new ImageIcon(image);
    }
    
    @Override
    public Component getListCellRendererComponent(JList<? extends Anaphora> list, Anaphora anaphora,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) renderer.getListCellRendererComponent(list, anaphora, index, isSelected,
                cellHasFocus);
        Color color = anaphora.getColor();
        label.setText(anaphora.getName());
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 15));
        label.setIcon(createColoredCircle(15, color));
        return label;
    }

}
