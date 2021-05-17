/*
 * Copyright (c) 2019-2020. Александр Исаченков.
 *
 * Программа «Златоуст».
 * Программа для визуализации элементов внутритекстовой симметрии в текстах.
 *
 *
 *
 *
 */

package chrysostom;

import chrysostom.view.dialogs.ProjectsForm;

import javax.swing.*;

public class Main
{
    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), null, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void setRussianButtonsText() {
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        UIManager.put("OptionPane.cancelButtonText", "Отмена");
    }
    
    public static void main(String[] args) {
        setSystemLookAndFeel();
        setRussianButtonsText();
        
        ProjectsForm projectsForm = new ProjectsForm(null);
        projectsForm.showPreferably();
    }
}