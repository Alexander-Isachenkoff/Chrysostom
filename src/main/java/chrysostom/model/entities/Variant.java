package chrysostom.model.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Variant")
public class Variant implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "text")
    private String text;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Anaphora_id")
    private Anaphora anaphora;
    
    public Variant() {}
    
    public Variant(String text) {
        this.text = text;
    }
    
    public int getId() {
        return id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public void setAnaphora(Anaphora anaphora) {
        this.anaphora = anaphora;
    }
    
    public Anaphora getAnaphora() {
        return anaphora;
    }
}
