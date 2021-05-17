package chrysostom.view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class InfoForm extends JDialog
{
    private JPanel contentPanel;
    private JButton OKButton;
    private JLabel emailLabel;
    private JTextArea descriptionTextArea;
    
    public InfoForm(Frame owner) {
        super(owner, "О программе", true);
        setContentPane(contentPanel);
        setMinimumSize(contentPanel.getMinimumSize());
        setResizable(false);
        OKButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        emailLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        emailLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().mail(new URI("mailto:isachenkoff.alexander@yandex.ru"));
                } catch (URISyntaxException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        descriptionTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }
    
    public void showDialog() {
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}
