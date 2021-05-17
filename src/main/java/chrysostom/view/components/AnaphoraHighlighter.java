package chrysostom.view.components;

import chrysostom.model.AnaphoraDictionary;
import chrysostom.model.Highlight;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.util.Comparator;
import java.util.List;

class AnaphoraHighlighter
{
    private final JTextPane textPane;
    private final AnaphoraDictionary dictionary;
    
    AnaphoraHighlighter(JTextPane textPane, AnaphoraDictionary dictionary) {
        this.textPane = textPane;
        this.dictionary = dictionary;
    }
    
    void highlightAll() {
        highlight(0, textPane.getText().length());
        System.out.println("highlight all");
    }
    
    /**
     * Подсвечивает в тексте анафоры и их варианты.
     * Варианты подсвечиваются более светлым оттенком.
     *
     * @param start Позиция начала выделения.
     * @param end   Позиция конца выделения.
     */
    void highlight(int start, int end) {
        long time = System.currentTimeMillis();
        clearHighlight(start, end - start);
        StyledDocument doc = textPane.getStyledDocument();
        MutableAttributeSet attributeSet = new SimpleAttributeSet();
        List<Highlight> highlights = Highlight.getHighlights(textPane.getText(), dictionary);
        highlights.sort(Comparator.comparingInt(Highlight::getLength));
        for (Highlight highlight : highlights) {
            if (highlight.getPosition() >= start && highlight.getPosition() <= end) {
                StyleConstants.setBackground(attributeSet, highlight.getColor());
                doc.setCharacterAttributes(highlight.getPosition(), highlight.getLength(),
                        attributeSet, false);
            }
        }
        System.out.println("highlight, start:" + start + ", end: " + end);
        System.out.println("Time for highlight = " + (System.currentTimeMillis() - time) + " mills");
    }
    
    void clearHighlight() {
        clearHighlight(0, textPane.getText().length());
        System.out.println("clear all highlight");
    }
    
    /**
     * Удаляет подсветку текста.
     *
     * @param offset Позиция, с которой начинается снятие выделения.
     * @param length Длина снятия выделения.
     */
    private void clearHighlight(int offset, int length) {
        MutableAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setBackground(attributeSet, textPane.getBackground());
        StyledDocument doc = textPane.getStyledDocument();
        doc.setCharacterAttributes(offset, length, attributeSet, false);
        System.out.println("clear highlight. offset:" + offset + ", length: " + length);
    }
}
