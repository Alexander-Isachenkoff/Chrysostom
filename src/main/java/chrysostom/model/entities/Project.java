package chrysostom.model.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Project")
public class Project implements Serializable
{
    private static final long serialVersionUID = 6409425755503607717L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "text")
    private String text;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Anaphora> anaphoraList = new ArrayList<>();
    
    public Project() {}
    
    public Project(String name, String text) {
        this.name = name;
        this.text = text;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public List<Anaphora> getAnaphoraList() {
        return anaphoraList;
    }
}
