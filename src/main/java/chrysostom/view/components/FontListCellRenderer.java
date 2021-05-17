package chrysostom.view.components;

import chrysostom.util.Fonts;
import chrysostom.io.IO;

import javax.swing.*;
import java.awt.*;

public class FontListCellRenderer extends DefaultListCellRenderer
{
	private Icon fontIcon = IO.readIcon("/icons/16/font.png");
	private final int fontSize = (int) Math.round(Fonts.toPixels(11));

	@Override
	public Component getListCellRendererComponent(
			JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		label.setIcon(fontIcon);
		label.setFont(new Font(label.getText(), Font.PLAIN, fontSize));
		return label;
	}
}
