package chrysostom.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public interface SimpleDocumentListener extends DocumentListener
{
    void documentChanged(DocumentEvent e);
    
    @Override
    default void insertUpdate(DocumentEvent e) {
        documentChanged(e);
    }
    
    @Override
    default void removeUpdate(DocumentEvent e) {
        documentChanged(e);
    }
    
    @Override
    default void changedUpdate(DocumentEvent e) {
        documentChanged(e);
    }
}
