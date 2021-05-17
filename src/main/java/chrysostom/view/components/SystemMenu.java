package chrysostom.view.components;

import chrysostom.interfaces.Command;
import chrysostom.io.IO;

import javax.swing.*;

public class SystemMenu extends JMenuBar
{
    private static final String iconsDir = "/icons/16/";
    
    private Command exitCommand = () -> {};
    private Command createCommand = () -> {};
    private Command openCommand = () -> {};
    private Command saveCommand = () -> {};
    private Command saveToFileCommand = () -> {};
    private Command wordExportCommand = () -> {};
    private Command htmlExportCommand = () -> {};
    private Command undoCommand = () -> {};
    private Command redoCommand = () -> {};
    private Command cutCommand = () -> {};
    private Command copyCommand = () -> {};
    private Command pasteCommand = () -> {};
    private Command clearCommand = () -> {};
    private Command anaphoraCommand = () -> {};
    private Command searchDuplicatesCommand = () -> {};
    private Command chartCommand = () -> {};
    private Command highlightingOnCommand = () -> {};
    private Command highlightingOffCommand = () -> {};
    private Command infoCommand = () -> {};
    
    public SystemMenu() {
        add(createFileMenu());
        add(createEditMenu());
        add(createViewMenu());
        add(createInfoMenu());
        setBorderPainted(false);
    }
    
    private static JMenuItem createItem(String text, String iconName, Command command) {
        JMenuItem item = new JMenuItem(text, IO.readIcon(iconsDir + iconName));
        item.addActionListener(e -> command.execute());
        return item;
    }
    
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.add(createItem("Создать", "new.png", () -> createCommand.execute()));
        fileMenu.add(createItem("Открыть...", "open.png", () -> openCommand.execute()));
        fileMenu.add(createItem("Сохранить", "save.png", () -> saveCommand.execute()));
        fileMenu.add(createItem("Сохранить в файл", "save.png", () -> saveToFileCommand.execute()));
        fileMenu.addSeparator();
        JMenu exportMenu = new JMenu("Экпортировать");
        fileMenu.add(exportMenu);
        exportMenu.add(createItem("Документ MS Word (.docx)", "document-word.png",
                () -> wordExportCommand.execute()));
        exportMenu.add(createItem("HTML-документ (.html)", "document-attribute-h.png",
                () -> htmlExportCommand.execute()));
        fileMenu.addSeparator();
        fileMenu.add(createItem("Выход", "exit.png", () -> exitCommand.execute()));
        return fileMenu;
    }
    
    private JMenu createEditMenu() {
        JMenu editMenu = new JMenu("Правка");
        editMenu.add(createItem("Отменить", "undo_arrow.png", () -> undoCommand.execute()));
        editMenu.add(createItem("Повторить", "redo_arrow.png",
                () -> redoCommand.execute()));
        editMenu.addSeparator();
        editMenu.add(createItem("Вырезать", "cut.png", () -> cutCommand.execute()));
        editMenu.add(createItem("Копировать", "copy.png", () -> copyCommand.execute()));
        editMenu.add(createItem("Вставить", "paste.png", () -> pasteCommand.execute()));
        editMenu.addSeparator();
        editMenu.add(createItem("Очистить", "broom.png", () -> clearCommand.execute()));
        return editMenu;
    }
    
    private JMenu createViewMenu() {
        JMenu textMenu = new JMenu("Вид");
        textMenu.add(createItem("Анафоры", "book.png", () -> anaphoraCommand.execute()));
        textMenu.add(createItem("График", "graph.png", () -> chartCommand.execute()));
        textMenu.add(createItem("Поиск повторов", "duplicates.png",
                () -> searchDuplicatesCommand.execute()));
        textMenu.addSeparator();
        JCheckBoxMenuItem highlighting = new JCheckBoxMenuItem("Подсветка", true);
        highlighting.addChangeListener(e -> {
            if (highlighting.isSelected()) {
                highlightingOnCommand.execute();
            } else {
                highlightingOffCommand.execute();
            }
        });
        textMenu.add(highlighting);
        highlighting.setIcon(IO.readIcon("/icons/16/highlighter-text.png"));
        return textMenu;
    }
    
    private JMenu createInfoMenu() {
        JMenu infoMenu = new JMenu(" ? ");
        infoMenu.add(createItem("О программе", "info.png", () -> infoCommand.execute()));
        return infoMenu;
    }
    
    public void setExitCommand(Command exitCommand) {
        this.exitCommand = exitCommand;
    }
    
    public void setCreateCommand(Command createCommand) {
        this.createCommand = createCommand;
    }
    
    public void setOpenCommand(Command openCommand) {
        this.openCommand = openCommand;
    }
    
    public void setSaveCommand(Command saveCommand) {
        this.saveCommand = saveCommand;
    }
    
    public void setSaveToFileCommand(Command saveToFileCommand) {
        this.saveToFileCommand = saveToFileCommand;
    }
    
    public void setWordExportCommand(Command wordExportCommand) {
        this.wordExportCommand = wordExportCommand;
    }
    
    public void setHtmlExportCommand(Command htmlExportCommand) {
        this.htmlExportCommand = htmlExportCommand;
    }
    
    public void setCutCommand(Command cutCommand) {
        this.cutCommand = cutCommand;
    }
    
    public void setCopyCommand(Command copyCommand) {
        this.copyCommand = copyCommand;
    }
    
    public void setPasteCommand(Command pasteCommand) {
        this.pasteCommand = pasteCommand;
    }
    
    public void setAnaphoraCommand(Command anaphoraCommand) {
        this.anaphoraCommand = anaphoraCommand;
    }
    
    public void setSearchDuplicatesCommand(Command searchDuplicatesCommand) {
        this.searchDuplicatesCommand = searchDuplicatesCommand;
    }
    
    public void setChartCommand(Command chartCommand) {
        this.chartCommand = chartCommand;
    }
    
    public void setHighlightingOnCommand(Command highlightingOnCommand) {
        this.highlightingOnCommand = highlightingOnCommand;
    }
    
    public void setHighlightingOffCommand(Command highlightingOffCommand) {
        this.highlightingOffCommand = highlightingOffCommand;
    }
    
    public void setInfoCommand(Command infoCommand) {
        this.infoCommand = infoCommand;
    }
    
    public void setClearCommand(Command clearCommand) {
        this.clearCommand = clearCommand;
    }
    
    public void setUndoCommand(Command undoCommand) {
        this.undoCommand = undoCommand;
    }
    
    public void setRedoCommand(Command redoCommand) {
        this.redoCommand = redoCommand;
    }
}
