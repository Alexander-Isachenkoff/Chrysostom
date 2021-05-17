package chrysostom.view.components;

import javax.swing.*;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;

class UndoableTextPane extends JTextPane
{
    private UndoManager undoManager = new UndoManager();
    
    UndoableTextPane() {
        getDocument().addUndoableEditListener(event -> {
            if (!event.getEdit().getPresentationName().equals("style change")) {
                System.out.println(event.getEdit());
                undoManager.addEdit(event.getEdit());
            }
        });
        
        getActionMap().put("Undo", new AbstractAction("Undo")
        {
            public void actionPerformed(ActionEvent evt) {
                undo();
            }
        });
        
        getActionMap().put("Redo", new AbstractAction("Redo")
        {
            public void actionPerformed(ActionEvent evt) {
                redo();
            }
        });
        
        getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
    }
    
    public void undo() {
        try {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        } catch (CannotUndoException e) {
            e.printStackTrace();
        }
    }
    
    public void redo() {
        try {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        } catch (CannotUndoException e) {
            e.printStackTrace();
        }
    }
    
    public boolean canUndo() {
        return undoManager.canUndo();
    }
    
    public boolean canRedo() {
        return undoManager.canRedo();
    }
}
