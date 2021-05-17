package chrysostom.model;

import chrysostom.model.entities.Anaphora;
import chrysostom.model.entities.Variant;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Highlight
{
    private final int position;
    private final int length;
    private final Color color;
    
    public Highlight(int position, int length, Color color) {
        this.position = position;
        this.length = length;
        this.color = color;
    }
    
    public static List<Highlight> getHighlights(String string, AnaphoraDictionary dictionary) {
        Text text = new Text(string);
        java.util.List<Highlight> highlights = new ArrayList<>();
        for (Anaphora anaphora : dictionary.getAllAnaphora()) {
            java.util.List<Integer> descrIndexes;
            if (anaphora.isInnerWordsExcluded()) {
                descrIndexes = text.getWordIndexes(anaphora.getDescription());
            } else {
                descrIndexes = text.getStringIndexes(anaphora.getDescription());
            }
            for (Integer index : descrIndexes) {
                highlights.add(new Highlight(index, anaphora.getDescription().length(),
                        anaphora.getColor()));
            }
            for (Variant variant : anaphora.getVariants()) {
                List<Integer> varIndexes;
                if (anaphora.isInnerWordsExcluded()) {
                    varIndexes = text.getWordIndexes(variant.getText());
                } else {
                    varIndexes = text.getStringIndexes(variant.getText());
                }
                for (Integer index : varIndexes) {
                    highlights.add(new Highlight(index, variant.getText().length(),
                            anaphora.getVariantsColor()));
                }
            }
        }
        return highlights;
    }
    
    public int getPosition() {
        return position;
    }
    
    public int getLength() {
        return length;
    }
    
    public Color getColor() {
        return color;
    }
}
