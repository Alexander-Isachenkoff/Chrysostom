package chrysostom.model.search;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchEngineTest
{
    String text =
            "«Радуйся, обрадованнаа! Господь с тобою!» Радуйся, прерадованнаа, храме Божий душевный! Радуйся, обрадованнаа, небу и земли обрадованное жилище. Радуйся, благодатнаа, небеснааго класа нежненнаа ниво. Радуйся, благодатнаа, истиннаго винограда неложнаа Мати Дѣво. Радуйся, благодатнаа, непремѣннаго Божества достойное вмѣстилище. Радуйся, благодатнаа, невмѣстимаго естества село пространное. Радуйся, обрадованнаа, облистание темнымъ. Радуйся, обрадованнаа, овдовѣвшему миру невѣстьство нескверное. Радуйся, благодатнаа, исплетшиа нерукотворенаа вѣнца всей твари. Радуйся, благодатнаа, огню божественому жилище. Радуйся, благодатнаа, свѣта хранилище. Радуйся, благодатнаа, погрузившиа вложеснахъ смерть праматернюю. Радуйся, благодатнаа, убѣгшиа вселенныа неблудное възвращение. Радуйся, благодатнаа, гладомъ мрущему естеству хранителнице неиздаема. Господь с тобою!";
    
    @Test
    void searchRepeatedLetterCombinations() {
        Consumer<Float> progressConsumer = f -> {};
        SearchResults results = new SearchEngine().searchRepeatedLetterCombinations(text, 6, 10,
                5, progressConsumer);
        for (String s : results.keySet()) {
            System.out.println(s + ": ");
            for (SearchEntry searchEntry : results.get(s)) {
                System.out.println(
                        searchEntry.string + " " + "(" + searchEntry.start + "," + searchEntry.end + ")");
            }
        }
    }
    
    @Test
    void searchRepeatedWords() {
        SearchResults results = new SearchEngine().searchRepeatedWords(text, 5, 15, 3);
        
        assertTrue(results.containsKey("радуйся"));
        assertTrue(results.containsKey("обрадованнаа"));
        assertTrue(results.containsKey("благодатнааа"));
        
        for (String s : results.keySet()) {
            System.out.println(s + ": ");
            for (SearchEntry searchEntry : results.get(s)) {
                System.out.println(
                        searchEntry.string + " " + "(" + searchEntry.start + "," + searchEntry.end + ")");
            }
        }
    }
    
    @Test
    void search() {
        SearchResults results = new SearchEngine().search(text, "обрадованнаа");
        
        assertEquals(10, results.get("обрадованнаа").get(0).start);
        assertEquals(22, results.get("обрадованнаа").get(0).end);
        assertEquals(97, results.get("обрадованнаа").get(1).start);
        assertEquals(109, results.get("обрадованнаа").get(1).end);
        assertEquals(399, results.get("обрадованнаа").get(2).start);
        assertEquals(411, results.get("обрадованнаа").get(2).end);
        assertEquals(442, results.get("обрадованнаа").get(3).start);
        assertEquals(454, results.get("обрадованнаа").get(3).end);
        
        for (String s : results.keySet()) {
            System.out.println(s + ": ");
            for (SearchEntry searchEntry : results.get(s)) {
                System.out.println(
                        searchEntry.string + " " + "(" + searchEntry.start + "," + searchEntry.end + ")");
            }
        }
    }
}