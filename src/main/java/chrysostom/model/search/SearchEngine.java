package chrysostom.model.search;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchEngine
{
    private static final String wordsSplitRegExp = "[\"«»,;:.!?()\\[\\]\\s]+";
    private static final Pattern wordsSplitPattern = Pattern.compile(wordsSplitRegExp);
    
    public SearchResults searchRepeatedLetterCombinations(String text, int minLen, int maxLen,
                                                          int minCount,
                                                          Consumer<Float> progressConsumer) {
        SearchResults repeats = new SearchResults();
        text = text.toLowerCase();
        Set<String> combinations = extractLetterCombinations(text, minLen, maxLen);
        int count = 0;
        for (String el : combinations) {
            String regex = el.replaceAll(" ", ".{0,3}");
            SearchResults results = searchFiltered(text, regex,
                    s -> s.length() >= minLen && s.length() <= maxLen);
            
            if (!results.isEmpty() && results.get(regex).size() >= minCount) {
                repeats.putAll(results);
            }
            count++;
            progressConsumer.accept(count / (float) combinations.size());
        }
        List<String> invalid =
                repeats.keySet().stream().filter(key -> repeats.get(key).size() < minCount).collect(Collectors.toList());
        for (String s : invalid) {
            repeats.remove(s);
        }
        return repeats;
    }
    
    private Set<String> extractLetterCombinations(String text, int minLen, int maxLen) {
        Set<String> combinations = new HashSet<>();
        for (int i = 0; i < text.length(); i++) {
            for (int j = i + minLen; j < text.length() && j - i <= maxLen; j++) {
                String combination =
                        wordsSplitPattern.matcher(text.substring(i, j)).replaceAll(" ").trim();
                combination = combination.replaceAll(" {2,}", " ");
                if (combination.length() >= minLen && combination.length() <= maxLen) {
                    combinations.add(combination);
                }
            }
        }
        return combinations;
    }
    
    public SearchResults searchRepeatedWords(String text, int minLen, int maxLen, int minCount) {
        SearchResults repeats = new SearchResults();
        text = text.toLowerCase();
        List<String> words = parseWords(text).stream()
                .filter(s -> s.length() >= minLen && s.length() <= maxLen)
                .collect(Collectors.toList());
        for (String word : words) {
            SearchResults results = search(text, word);
            if (results.get(word).size() >= minCount) {
                repeats.putAll(results);
            }
        }
        return repeats;
    }
    
    public SearchResults search(String text, String regex) {
        return searchFiltered(text, regex, s -> true);
    }
    
    public SearchResults searchFiltered(String text, String regex, Predicate<String> predicate) {
        SearchResults results = new SearchResults();
        Matcher matcher = Pattern.compile(regex).matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String substring = text.substring(start, end);
            if (predicate.test(substring)) {
                if (!results.containsKey(regex)) {
                    results.put(regex, new ArrayList<>());
                }
                results.get(regex).add(new SearchEntry(substring, start, end));
            }
        }
        return results;
    }
    
    private List<String> parseWords(String text) {
        String[] words = text.split(wordsSplitRegExp);
        Stream<String> notEmptyWords = Arrays.stream(words).filter(s -> !s.isEmpty());
        return notEmptyWords.collect(Collectors.toList());
    }
}
