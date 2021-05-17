package chrysostom.view.dialogs;

import chrysostom.model.entities.Anaphora;
import chrysostom.model.AnaphoraDictionary;

import java.awt.*;
import java.awt.event.ActionEvent;

import static javax.swing.JOptionPane.*;

public class AnaphoraCreator extends AnaphoraPresenter
{
	private AnaphoraDictionary dictionary;

	public AnaphoraCreator(Frame owner, AnaphoraDictionary dictionary) {
		super(owner, "Редактор анафор (создать)");
		this.dictionary = dictionary;
	}

	@Override
	void okButtonAction(ActionEvent e) {
		Anaphora newAnaphora = createAnaphora();
		String anaphoraName = newAnaphora.getName();
		if (!dictionary.containsName(anaphoraName) || getReplaceConfirmation(anaphoraName)) {
			dictionary.add(newAnaphora);
			dispose();
		}
	}

	private boolean getReplaceConfirmation(String anaphoraName) {
		String message = String.format("Анафора \"%s\" уже существует.\nЗаменить?", anaphoraName);
		int option = showConfirmDialog(AnaphoraCreator.this, message, getTitle(), YES_NO_OPTION);
		return option == YES_OPTION;
	}
}
