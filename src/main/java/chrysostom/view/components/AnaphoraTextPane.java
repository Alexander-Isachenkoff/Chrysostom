package chrysostom.view.components;

import chrysostom.model.entities.Anaphora;
import chrysostom.model.AnaphoraDictionary;
import chrysostom.model.Statistics;

import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class AnaphoraTextPane extends UndoableTextPane implements Observer
{
    private AnaphoraDictionary dictionary;
    private boolean highlighting = true;
    private AnaphoraHighlighter highlighter;
    
    public AnaphoraTextPane(AnaphoraDictionary dictionary) {
        setDictionary(dictionary);
        setMargin(new Insets(8, 10, 8, 10));
        new TextPanePopupMenu().setTextPane(this);
    }
    
    public void setDictionary(AnaphoraDictionary dictionary) {
        if (this.dictionary != null) {
            this.dictionary.deleteObserver(this);
        }
        this.dictionary = dictionary;
        this.dictionary.addObserver(this);
        highlighter = new AnaphoraHighlighter(this, dictionary);
    }
    
    public void setHighlighting(boolean highlighting) {
        this.highlighting = highlighting;
        updateHighlighting(null);
    }
    
    public void updateHighlighting(DocumentEvent e) {
        if (highlighting) {
            if (e == null) {
                highlighter.highlightAll();
            } else {
                Bounds bounds = calcUpdateBounds(e);
                highlighter.highlight(bounds.start, bounds.end);
            }
        } else {
            highlighter.clearHighlight();
        }
    }
    
    private Bounds calcUpdateBounds(DocumentEvent event) {
        Bounds bounds = new Bounds(-1, -1);
        String text = getText();
        for (Anaphora anaphora : dictionary.getAllAnaphora()) {
            for (String variant : anaphora.getAllVariants()) {
                int s = Statistics.getMaxStart(text, variant, event.getOffset());
                int e = Statistics.getMinEnd(text, variant, event.getOffset() + event.getLength());
                if (s > bounds.start) {
                    bounds.start = s;
                }
                if (e < bounds.end || bounds.end == -1) {
                    bounds.end = e;
                }
            }
        }
        int maxLength = Statistics.getMaxAnaphoraLength(dictionary.getAllAnaphora());
        
        if (bounds.start == -1) {
            bounds.start = event.getOffset() - maxLength;
        }
        if (bounds.end == -1) {
            bounds.end = event.getOffset() + event.getLength() + maxLength;
        }
        
        return bounds;
    }
    
    @Override
    public String getText() {
        return super.getText().replace(System.lineSeparator(), "\n");
    }
    
    @Override
    public void update(Observable o, Object arg) {
        updateHighlighting(null);
    }
    
    private static class Bounds
    {
        private int start;
        private int end;
        
        private Bounds(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
