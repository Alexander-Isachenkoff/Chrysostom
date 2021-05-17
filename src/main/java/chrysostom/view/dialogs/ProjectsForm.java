package chrysostom.view.dialogs;

import chrysostom.model.access.ProjectDAO;
import chrysostom.model.entities.Project;
import chrysostom.view.editor.EditorWindow;
import chrysostom.io.ProjectLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

import static javax.swing.JOptionPane.*;

public class ProjectsForm extends JDialog
{
    private final JList<Project> projectsList = new JList<>();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JButton openButton;
    private JButton createButton;
    private JButton deleteButton;
    private JButton loadButton;
    private JButton exitButton;
    private EditorWindow editor;
    
    public ProjectsForm(EditorWindow editor) {
        super(editor, "Златоуст (Проекты)", true);
        this.editor = editor;
        setContentPane(contentPanel);
        setMinimumSize(contentPanel.getMinimumSize());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        scrollPane.setViewportView(projectsList);
        
        projectsList.addListSelectionListener(new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean enabled = projectsList.getSelectedValue() != null;
                openButton.setEnabled(enabled);
                deleteButton.setEnabled(enabled);
            }
        });
        
        projectsList.setFixedCellHeight(25);
        projectsList.setSelectionBackground(new Color(190, 235, 117));
        projectsList.setSelectionForeground(Color.BLACK);
        projectsList.setCellRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list,
                        ((Project) value).getName(), index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 1, 0, new Color(228, 228, 228)),
                        new EmptyBorder(new Insets(0, 4, 0, 4))
                ));
                return label;
            }
        });
        
        openButton.addActionListener(e -> onOpen());
        createButton.addActionListener(e -> onCreate());
        loadButton.addActionListener(e -> onLoad());
        deleteButton.addActionListener(e -> onDelete());
        exitButton.addActionListener(e -> dispose());
    }
    
    private void onOpen() {
        Project project = projectsList.getSelectedValue();
        if (editor == null) {
            EditorWindow editor = new EditorWindow();
            editor.setProject(project);
            editor.showMaximized();
        } else {
            int option = showConfirmDialog(this, "Открыть проект в новом окне?");
            if (option == OK_OPTION) {
                EditorWindow editor = new EditorWindow();
                editor.setProject(project);
                editor.showMaximized();
            } else if (option == NO_OPTION) {
                editor.setProject(project);
            } else if (option == CANCEL_OPTION) {
                return;
            }
        }
        dispose();
    }
    
    private void onCreate() {
        String name = showInputNameDialog();
        if (name != null) {
            if (nameExists(name)) {
                showErrorMessage("Проект с названием \"" + name + "\" уже существует");
            } else {
                projectDAO.save(new Project(name, ""));
                updateProjectsList();
            }
        }
    }
    
    private boolean nameExists(String name) {
        return projectDAO.selectAll().stream().map(Project::getName).anyMatch(n -> n.equals(name));
    }
    
    private String showInputNameDialog() {
        return showInputDialog(this, "Введите название проекта:", "",
                PLAIN_MESSAGE);
    }
    
    private void onLoad() {
        ProjectLoader loader = new ProjectLoader(editor);
        loader.load(() -> {
                    Project project = loader.getLoadedProject();
                    String name = showInputNameDialog();
                    project.setName(name);
                    if (nameExists(name)) {
                        showErrorMessage("Проект с названием \"" + name + "\" уже существует");
                    } else {
                        projectDAO.save(new Project(name, ""));
                        updateProjectsList();
                    }
                },
                () -> showErrorMessage("Ошибка закгрузки файла"));
    }
    
    private void showErrorMessage(String message) {
        showMessageDialog(this, message, getTitle(), ERROR_MESSAGE);
    }
    
    private void onDelete() {
        Project selectedProject = projectsList.getSelectedValue();
        if (selectedProject != null) {
            if (getDeleteConfirmation(selectedProject.getName())) {
                projectDAO.delete(selectedProject);
                updateProjectsList();
            }
        }
    }
    
    private boolean getDeleteConfirmation(String name) {
        String message = "Удалить проект \"" + name + "\"?";
        int option = showConfirmDialog(this, message, "Удаление", YES_NO_OPTION);
        return option == YES_OPTION;
    }
    
    public void showPreferably() {
        updateProjectsList();
        pack();
        setLocationRelativeTo(editor);
        setVisible(true);
    }
    
    private void updateProjectsList() {
        projectsList.setListData(projectDAO.selectAll().toArray(new Project[0]));
    }
}
