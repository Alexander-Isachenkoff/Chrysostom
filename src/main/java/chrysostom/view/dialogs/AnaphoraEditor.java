package chrysostom.view.dialogs;

import chrysostom.model.entities.Anaphora;
import chrysostom.model.AnaphoraDictionary;

import java.awt.*;
import java.awt.event.ActionEvent;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class AnaphoraEditor extends AnaphoraPresenter
{
	private Anaphora initialAnaphora;
	private AnaphoraDictionary dictionary;

	public AnaphoraEditor(Frame owner, AnaphoraDictionary dictionary) {
		super(owner, "Редактор анафор (изменить)");
		this.dictionary = dictionary;
	}

	@Override
	public void showDialog(Anaphora initialAnaphora) {
		super.showDialog(initialAnaphora);
		this.initialAnaphora = initialAnaphora;
	}

	@Override
	void okButtonAction(ActionEvent e) {
		Anaphora newAnaphora = createAnaphora();
		String oldName = initialAnaphora.getName();
		if (!dictionary.containsName(oldName)) {
			showAnaphoraNotFoundError(oldName);
			return;
		}
		dictionary.replace(oldName, newAnaphora);
		dispose();
	}

	private void showAnaphoraNotFoundError(String name) {
		String message = String.format("Анафора \"%s\" не найдена.", name);
		showMessageDialog(AnaphoraEditor.this, message, getTitle(), ERROR_MESSAGE);
	}
}
