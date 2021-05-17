package chrysostom.model;

import chrysostom.model.entities.Anaphora;

import java.io.Serializable;
import java.util.*;

public class AnaphoraDictionary extends Observable implements Serializable
{
    private static final long serialVersionUID = -5309481711754727247L;
    private final Map<String, Anaphora> anaphoraMap = new TreeMap<>();
    
    public void addAll(List<Anaphora> anaphoraList) {
        for (Anaphora anaphora : anaphoraList) {
            anaphoraMap.put(anaphora.getName(), anaphora);
        }
        setChanged();
        notifyObservers();
    }
    
    public boolean containsName(String name) {
        return anaphoraMap.containsKey(name);
    }
    
    public Set<String> getNames() {
        return anaphoraMap.keySet();
    }
    
    public Anaphora getByName(String name) {
        return anaphoraMap.get(name);
    }
    
    public void removeByName(String name) {
        anaphoraMap.remove(name);
        setChanged();
        notifyObservers();
    }
    
    public void replace(String name, Anaphora anaphora) {
        if (anaphoraMap.remove(name) == null) {
            return;
        }
        add(anaphora);
    }
    
    public void add(Anaphora anaphora) {
        anaphoraMap.put(anaphora.getName(), anaphora);
        setChanged();
        notifyObservers();
    }
    
    public void clear() {
        anaphoraMap.clear();
        setChanged();
        notifyObservers();
    }
    
    public List<Anaphora> getAllAnaphora() {
        return new ArrayList<>(anaphoraMap.values());
    }
}
