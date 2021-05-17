package chrysostom.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Text
{
    private static final String wordsSplitRegExp = "[\"«»,;:.!?()\\[\\]\\s]+";
    private final String text;
    private List<String> words;
    
    public Text(String text) {
        this.text = text;
    }
    
    private static List<String> parseByWords(String text) {
        String[] words = text.split(wordsSplitRegExp);
        Stream<String> notEmptyWords = Arrays.stream(words).filter(s -> !s.isEmpty());
        return notEmptyWords.collect(Collectors.toList());
    }
    
    public List<Double> getRelativeStringCoordinates(String string) {
        List<Integer> absCoords = getAbsoluteStringCoordinates(string);
        List<Double> relativeCoords = new ArrayList<>(absCoords.size());
        for (Integer coord : absCoords) {
            relativeCoords.add(coord / (double) countWords());
        }
        return relativeCoords;
    }
    
    public List<Double> getWordCoordinatesByWords(String word) {
        List<Double> coordinates = new ArrayList<>();
        List<String> words = words();
        if (!words.contains(word)) {
            return new ArrayList<>();
        }
        for (int wordPos = 0; wordPos < words.size(); wordPos++) {
            if (words.get(wordPos).equalsIgnoreCase(word)) {
                coordinates.add((wordPos + 1) / (double) words.size());
            }
        }
        return coordinates;
    }
    
    private List<Integer> getAbsoluteStringCoordinates(String string) {
        List<Integer> coordsByChars = getStringIndexes(string);
        List<Integer> coordsByWords = new ArrayList<>(coordsByChars.size());
        for (Integer charIndex : coordsByChars) {
            Text prevText = subText(0, charIndex);
            coordsByWords.add(prevText.countWords() + 1);
        }
        return coordsByWords;
    }
    
    private Text subText(int startPos, int endPos) {
        return new Text(text.substring(startPos, endPos));
    }
    
    public List<Integer> getStringIndexes(String substring) {
        return getStringIndexes(substring, 0, text.length());
    }
    
    public List<Integer> getStringIndexes(String string, int start, int end) {
        List<Integer> indexes = new ArrayList<>();
        String lowerCaseText = text.toLowerCase();
        string = string.toLowerCase();
        int index = start;
        while (true) {
            index = lowerCaseText.indexOf(string, index);
            if (index != -1 && index <= end) {
                indexes.add(index++);
            } else break;
        }
        return indexes;
    }
    
    public List<Integer> getWordIndexes(String string, int start, int end) {
        List<Integer> stringIndexes = getStringIndexes(string, start, end);
        List<Integer> wordIndexes = new ArrayList<>();
        for (Integer index : stringIndexes) {
            if (index == 0 || !Character.isAlphabetic(text.charAt(index - 1))) {
                if (index == text.length() - string.length() ||
                        !Character.isAlphabetic(text.charAt(index + string.length()))) {
                    wordIndexes.add(index);
                }
            }
        }
        return wordIndexes;
    }
    
    public List<Integer> getWordIndexes(String substring) {
        return getWordIndexes(substring, 0, text.length());
    }
    
    public int countCharacters() {
        String textCopy = text;
        for (char c : System.lineSeparator().toCharArray()) {
            textCopy = textCopy.replace(String.valueOf(c), "");
        }
        return textCopy.length();
    }
    
    public int countWords() {
        return words().size();
    }
    
    public List<String> words() {
        if (words != null) {
            return words;
        } else {
            words = parseByWords(text);
        }
        return words;
    }
    
    @Override
    public String toString() {
        return text;
    }
}
