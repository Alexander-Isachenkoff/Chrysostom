package chrysostom.model.search;

public class SearchEntry
{
    public String string;
    public int start;
    public int end;
    
    public SearchEntry(String string, int start, int end) {
        this.string = string;
        this.start = start;
        this.end = end;
    }
}
