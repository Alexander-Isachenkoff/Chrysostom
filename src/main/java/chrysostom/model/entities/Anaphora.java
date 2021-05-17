package chrysostom.model.entities;

import javax.persistence.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Anaphora")
public class Anaphora implements Serializable
{
	private static final long serialVersionUID = 4339006272052931853L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;

	@OneToMany(mappedBy = "anaphora", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Variant> variants;
	
	@Column(name = "hex_color")
	private String hexColor;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Project project;
	
	@Column(name = "inner_words_excluded")
	private boolean innerWordsExcluded;
	
	public Anaphora() {}

	public Anaphora(String name, String description, List<Variant> variants, String hexColor,
	                boolean innerWordsExcluded, Project project) {
		this.name = name;
		this.description = description;
		this.innerWordsExcluded = innerWordsExcluded;
		this.variants = variants;
		this.project = project;
		this.hexColor = hexColor;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Project getProject() {
		return project;
	}
	
	public void setProject(Project project) {
		this.project = project;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<Variant> getVariants() {
		return variants;
	}
	
	public void setVariants(List<Variant> variants) {
		this.variants = variants;
	}
	
	public boolean isInnerWordsExcluded() {
		return innerWordsExcluded;
	}

	public String getHexColor() {
		return hexColor;
	}
	
	public void setHexColor(String hexColor) {
		this.hexColor = hexColor;
	}
	
	public Color getColor() {
		return Color.decode(hexColor);
	}

	public List<String> getAllVariants() {
		List<String> allVariants = new ArrayList<>();
		allVariants.add(description);
		for (Variant variant : variants) {
			allVariants.add(variant.getText());
		}
		return allVariants;
	}
	
	public Color getVariantsColor() {
		return getColor().brighter();
	}
}
