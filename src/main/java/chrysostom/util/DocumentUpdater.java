package chrysostom.util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

public class DocumentUpdater implements SimpleDocumentListener
{
    private DocumentAction documentChangedAction = (e) -> {};
    private SimpleAction updateStartedAction = () -> {};
    private SimpleAction updateFinishedAction = () -> {};
    private Timer updateTimer;
    private DocumentEvent docEvent;
    
    public DocumentUpdater() {
        updateTimer = new Timer(0, e -> {
            documentChangedAction.performAction(docEvent);
            updateFinishedAction.performAction();
        });
        updateTimer.setRepeats(false);
    }
    
    public void setDocumentChangedAction(DocumentAction documentChangedAction) {
        this.documentChangedAction = documentChangedAction;
    }
    
    public void setUpdateStartedAction(SimpleAction updateStartedAction) {
        this.updateStartedAction = updateStartedAction;
    }
    
    public void setUpdateFinishedAction(SimpleAction updateFinishedAction) {
        this.updateFinishedAction = updateFinishedAction;
    }
    
    @Override
    public void documentChanged(DocumentEvent event) {
        this.docEvent = event;
        updateStartedAction.performAction();
        updateTimer.restart();
    }
    
    public interface DocumentAction
    {
        void performAction(DocumentEvent e);
    }
    
    public interface SimpleAction
    {
        void performAction();
    }
}
