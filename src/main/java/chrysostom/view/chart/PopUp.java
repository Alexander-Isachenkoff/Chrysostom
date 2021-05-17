package chrysostom.view.chart;

import javax.swing.*;
import java.awt.*;

public class PopUp extends JWindow
{
	private JLabel label;
	
	PopUp() {
		initLabel();
		setLayout(null);
		add(label);
	}

	private void initLabel() {
		label = new JLabel();
		label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		label.setHorizontalAlignment(SwingConstants.CENTER);
	}

	public void setText(String text) {
		label.setText(text);
		int width = label.getFontMetrics(label.getFont()).stringWidth(text) + 8;
		int height = label.getFontMetrics(label.getFont()).getHeight() + 2;
		label.setSize(width, height);
		setSize(label.getSize());
	}
}
