package chrysostom.view.components;

import chrysostom.io.IO;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.ActionListener;

class TextPanePopupMenu extends JPopupMenu
{
    private static final String iconsDir = "/icons/16/";
    private JTextPane textPane = new JTextPane();
    private JMenuItem cut = createItem("Вырезать", "cut.png", e -> textPane.cut());
    private JMenuItem copy = createItem("Копировать", "copy.png", e -> textPane.copy());
    private JMenuItem paste = createItem("Вставить", "paste.png", e -> textPane.paste());
    
    TextPanePopupMenu() {
        addPopupMenuListener(new PopupMenuListener()
        {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                boolean enabled = textPane.getSelectedText() != null;
                copy.setEnabled(enabled);
                cut.setEnabled(enabled);
            }
            
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        add(cut);
        add(copy);
        add(paste);
    }
    
    private static JMenuItem createItem(String text, String iconName, ActionListener action) {
        JMenuItem item = new JMenuItem(text, IO.readIcon(iconsDir + iconName));
        item.addActionListener(action);
        return item;
    }
    
    void setTextPane(JTextPane textPane) {
        this.textPane = textPane;
        textPane.setComponentPopupMenu(this);
    }
}
