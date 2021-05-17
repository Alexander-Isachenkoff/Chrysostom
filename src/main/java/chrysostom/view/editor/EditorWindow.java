package chrysostom.view.editor;

import chrysostom.io.IO;
import chrysostom.io.ProjectLoader;
import chrysostom.model.AnaphoraDictionary;
import chrysostom.model.Text;
import chrysostom.model.WordReport;
import chrysostom.model.access.ProjectDAO;
import chrysostom.model.chart.ChartFactory;
import chrysostom.model.entities.Anaphora;
import chrysostom.model.entities.Project;
import chrysostom.model.entities.Variant;
import chrysostom.model.search.SearchEntry;
import chrysostom.model.search.SearchResults;
import chrysostom.util.Colors;
import chrysostom.util.DocumentUpdater;
import chrysostom.util.Fonts;
import chrysostom.util.SimpleDocumentListener;
import chrysostom.view.chart.ChartForm;
import chrysostom.view.components.*;
import chrysostom.view.dialogs.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import search.SearchDialog;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.function.Consumer;

import static javax.swing.JOptionPane.*;

public class EditorWindow extends JFrame
{
    private final AnaphoraDictionary dictionary = new AnaphoraDictionary();
    
    private final ImageIcon readyIcon = IO.readIcon("/icons/16/tick.png");
    private final ImageIcon refreshIcon = IO.readIcon("/icons/16/refresh.png");
    private final SystemMenu menuBar = new SystemMenu();
    private final AnaphoraTextPane textPane = new AnaphoraTextPane(dictionary);
    private final AnaphoraCreator anaphoraCreator = new AnaphoraCreator(this, dictionary);
    private final AnaphoraEditor anaphoraEditor = new AnaphoraEditor(this, dictionary);
    private final SearchDialog searchDialog = new SearchDialog(this, textPane);
    private final AnaphoraTextPane secondaryAnaphoraTextPane = new AnaphoraTextPane(dictionary);
    private Project project;
    // region Компоненты
    private JComboBox<String> fontNameList;
    private JComboBox<Integer> fontSizeList;
    private JPanel contentPanel;
    private JLabel wordsQuantityLabel;
    private JLabel charsQuantityLabel;
    private JLabel readyLabel;
    private JButton newButton;
    private JButton openButton;
    private JButton saveButton;
    private JButton cutButton;
    private JButton copyButton;
    private JButton pasteButton;
    private JToggleButton anaphoraButton;
    private JButton createButton;
    private JButton searchButton;
    private JButton chartButton;
    private JScrollPane primaryScrollPane;
    private JButton appendButton;
    private JButton searchBtn;
    private JButton infoButton;
    private JButton undoButton;
    private JButton redoButton;
    private JSplitPane splitPane;
    private JButton deleteButton;
    private JButton editButton;
    private JButton propertiesButton;
    private JScrollPane anaphoraScrollPanel;
    private JButton createAnaphoraButton;
    private JButton closeAnaphoraBrowserButton;
    private JScrollPane secondaryScrollPane;
    private JPanel rightPanel;
    private JButton createAnaphoraFromSearchSelectionButton;
    private JToggleButton searchDuplicatesButton;
    private JSplitPane verticalSplitPane;
    private JButton hideSearchPanelButton;
    private JButton clearAnaphoraDictButton;
    private JToolBar searchButtonToolBar;
    private JToolBar searchPanelDividerToolbar;
    private JButton projectsButton;
    private ChartForm chartForm;
    // endregion
    private final ProjectsForm projectsForm = new ProjectsForm(this);
    
    public EditorWindow() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(IO.readIcon("/icons/icon.png").getImage());
        setContentPane(contentPanel);
        setMinimumSize(contentPanel.getMinimumSize());
        setJMenuBar(menuBar);
        initFontComponents();
        initToolbarButtonsListeners();
        initMenuItemsListeners();
        initTextEditor();
        initAnaphoraPanel();
        initSecondaryTextPane();
        
        SearchDuplicatesPanel searchDuplicatesPanel = new SearchDuplicatesPanel(textPane::getText, dictionary);
        
        searchDuplicatesPanel.setOnRepeatsFound(new Consumer<SearchResults>()
        {
            @Override
            public void accept(SearchResults searchResults) {
                AnaphoraDictionary dictionary = new AnaphoraDictionary();
                for (String key : searchResults.keySet()) {
                    for (SearchEntry searchEntry : searchResults.get(key)) {
                        dictionary.add(new Anaphora(searchEntry.string, searchEntry.string, new ArrayList<>(),
                                Colors.generateHexColorFor(key), true, project));
                    }
                }
                secondaryAnaphoraTextPane.setText(textPane.getText());
                secondaryAnaphoraTextPane.setDictionary(dictionary);
                secondaryAnaphoraTextPane.updateHighlighting(null);
            }
        });
        
        rightPanel.add(searchDuplicatesPanel, BorderLayout.CENTER);
        
        anaphoraButton.setSelected(true);
        
        searchDuplicatesButton.addActionListener(e -> setSearchPaneOpened(searchDuplicatesButton.isSelected()));
        hideSearchPanel();
        hideSearchPanelButton.addActionListener(e -> {
            searchDuplicatesButton.setSelected(false);
            hideSearchPanel();
        });
    }
    
    private void initAnaphoraPanel() {
        AnaphoraTable table = new AnaphoraTable(dictionary);
        anaphoraScrollPanel.setViewportView(table);
        
        createAnaphoraButton.addActionListener(anaphora -> anaphoraCreator.showDialog());
        
        editButton.addActionListener(e -> anaphoraEditor.showDialog(table.getSelectedValue()));
        
        deleteButton.addActionListener(e -> {
            Anaphora anaphora = table.getSelectedValue();
            if (getDeleteConfirmation(anaphora.getName())) {
                dictionary.removeByName(anaphora.getName());
            }
        });
        
        PropertiesDialog propertiesDialog = new PropertiesDialog(this);
        propertiesButton.addActionListener(e -> propertiesDialog.showDialog(table.getSelectedValue()));
        
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean enabled = (table.getSelectedRow() >= 0);
            editButton.setEnabled(enabled);
            deleteButton.setEnabled(enabled);
            propertiesButton.setEnabled(enabled);
        });
        
        closeAnaphoraBrowserButton.addActionListener(e -> {
            anaphoraButton.setSelected(false);
            hideAnaphoraPanel();
        });
        
        clearAnaphoraDictButton.addActionListener(e -> dictionary.clear());
    }
    
    private boolean getDeleteConfirmation(String anaphoraName) {
        String message = "Удалить анафору \"" + anaphoraName + "\"?";
        int option = showConfirmDialog(this, message, "Удаление", YES_NO_OPTION);
        return option == YES_OPTION;
    }
    
    private void setAnaphoraPaneOpened(boolean b) {
        if (b) {
            showAnaphoraPanel();
        } else {
            hideAnaphoraPanel();
        }
    }
    
    private void showAnaphoraPanel() {
        if (splitPane.getLeftComponent().isVisible()) {
            return;
        }
        splitPane.getLeftComponent().setVisible(true);
        splitPane.setDividerLocation(splitPane.getLastDividerLocation());
        splitPane.setDividerSize(2);
    }
    
    private void hideAnaphoraPanel() {
        splitPane.setLastDividerLocation(splitPane.getDividerLocation());
        splitPane.getLeftComponent().setVisible(false);
        splitPane.setDividerSize(0);
    }
    
    private void setSearchPaneOpened(boolean b) {
        if (b) {
            showSearchPanel();
        } else {
            hideSearchPanel();
        }
    }
    
    private void showSearchPanel() {
        if (verticalSplitPane.getBottomComponent().isVisible()) {
            return;
        }
        verticalSplitPane.getBottomComponent().setVisible(true);
        verticalSplitPane.setDividerLocation(verticalSplitPane.getLastDividerLocation());
        verticalSplitPane.setDividerSize(2);
    }
    
    private void hideSearchPanel() {
        verticalSplitPane.setLastDividerLocation(verticalSplitPane.getDividerLocation());
        verticalSplitPane.getBottomComponent().setVisible(false);
        verticalSplitPane.setDividerSize(0);
    }
    
    public void showPreferably() {
        pack();
        setLocationRelativeTo(null);
        textPane.requestFocus();
        setVisible(true);
    }
    
    public void showMaximized() {
        pack();
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        textPane.requestFocus();
        setVisible(true);
        requestFocus();
    }
    
    private void initTextEditor() {
        primaryScrollPane.setViewportView(textPane);
        textPane.setForeground(Color.BLACK);
        DocumentUpdater updater = new DocumentUpdater();
        updater.setDocumentChangedAction(e -> {
            textPane.updateHighlighting(e);
            undoButton.setEnabled(textPane.canUndo());
            redoButton.setEnabled(textPane.canRedo());
            updateCounters();
        });
        updater.setUpdateStartedAction(() -> setReady(false));
        updater.setUpdateFinishedAction(() -> setReady(true));
        textPane.getDocument().addDocumentListener(updater);
        textPane.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            undoButton.setEnabled(textPane.canUndo());
            redoButton.setEnabled(textPane.canRedo());
        });
        
        undoButton.addActionListener(e -> textPane.undo());
        redoButton.addActionListener(e -> textPane.redo());
        
        textPane.addCaretListener(e -> appendButton.setEnabled(textPane.getSelectedText() != null));
    }
    
    private void initSecondaryTextPane() {
        DocumentUpdater updater = new DocumentUpdater();
        updater.setDocumentChangedAction(secondaryAnaphoraTextPane::updateHighlighting);
        secondaryAnaphoraTextPane.getDocument().addDocumentListener(updater);
        secondaryScrollPane.setViewportView(secondaryAnaphoraTextPane);
        secondaryAnaphoraTextPane.setEditable(false);
        secondaryAnaphoraTextPane.setForeground(Color.BLACK);
        secondaryAnaphoraTextPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        
        searchButtonToolBar.setBorder(new MatteBorder(1, 0, 0, 0, new Color(187, 187, 187)));
        searchPanelDividerToolbar.setBorder(new MatteBorder(0, 0, 1, 0, new Color(187, 187, 187)));
        
        
    }
    
    private void initFontComponents() {
        Fonts.getNames().forEach(fontName -> fontNameList.addItem(fontName));
        Fonts.getSizes().forEach(size -> fontSizeList.addItem(size));
        fontNameList.addActionListener(e -> onFontNameChanged());
        fontSizeList.addActionListener(e -> onFontSizeChanged());
        fontNameList.setSelectedItem(textPane.getFont().getName());
        fontSizeList.setSelectedItem(textPane.getFont().getSize());
        fontNameList.setRenderer(new FontListCellRenderer());
    }
    
    private void onFontNameChanged() {
        String selectedName = (String) fontNameList.getSelectedItem();
        if (Fonts.getNames().contains(selectedName)) {
            setFont(new Font(selectedName, Font.PLAIN, getFont().getSize()));
        } else {
            String message = String.format("Шрифт \"%s\" недоступен.", selectedName);
            showMessageDialog(this, message, getTitle(), WARNING_MESSAGE);
            fontNameList.setSelectedItem(getFont().getName());
        }
    }
    
    private void onFontSizeChanged() {
        try {
            int pt = (int) fontSizeList.getSelectedItem();
            if (pt > 0) {
                int px = (int) Math.round(Fonts.toPixels(pt));
                setFont(new Font(getFont().getName(), Font.PLAIN, px));
            } else {
                throw new IllegalArgumentException();
            }
        } catch (ClassCastException | IllegalArgumentException ex) {
            int pt = (int) Math.round(Fonts.toPoints(getFont().getSize()));
            fontSizeList.setSelectedItem(pt);
        }
    }
    
    private void initToolbarButtonsListeners() {
        searchBtn.addActionListener(e -> searchDialog.showDialog());
        newButton.addActionListener(e -> createNew());
        openButton.addActionListener(e -> load());
        saveButton.addActionListener(e -> saveProject());
        projectsButton.addActionListener(e -> projectsForm.showPreferably());
        cutButton.addActionListener(e -> textPane.cut());
        copyButton.addActionListener(e -> textPane.copy());
        pasteButton.addActionListener(e -> textPane.paste());
        createButton.addActionListener(e -> createAnaphoraFromSelection());
        anaphoraButton.addActionListener(e -> {
            setAnaphoraPaneOpened(anaphoraButton.isSelected());
        });
        
        // TODO: Функция добавления варианта
        appendButton.addActionListener(e -> {
            String variant = textPane.getSelectedText();
            if (variant == null) {
                return;
            }
            SelectionDialog<Anaphora> dialog = new SelectionDialog<>();
            dialog.setCellRenderer(new
                    AnaphoraListCellRenderer());
            dialog.setItems(dictionary.getAllAnaphora());
            dialog.setTitle("Добавление варианта");
            dialog.setMessage(String.format(
                    "<html>Добавить вариант <b>\"%s\"</b> к анафоре...</html>", variant));
            dialog.showDialog(this);
            Anaphora anaphora = dialog.getSelectedItem();
            if (anaphora != null) {
                anaphora.getVariants().add(new Variant(variant));
                textPane.updateHighlighting(null);
            }
        });
        chartButton.addActionListener(e -> openChartForm());
        searchButton.addActionListener(e -> {
            searchDuplicatesButton.setSelected(true);
            showSearchPanel();
        });
        infoButton.addActionListener(e -> new InfoForm(this).showDialog());
        
        createAnaphoraFromSearchSelectionButton.addActionListener(e -> createAnaphoraFromSearchSelection());
    }
    
    private String getHTMLText() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            HTMLEditorKit hk = new HTMLEditorKit();
            hk.write(baos, textPane.getStyledDocument(), 0, textPane.getDocument().getLength());
        } catch (IOException | BadLocationException e) {
            e.printStackTrace();
        }
        return baos.toString();
    }
    
    private void exportToWord() {
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                WordReport report = new WordReport(textPane.getText(), dictionary);
                try (OutputStream os = new FileOutputStream("test.docx")) {
                    XWPFDocument document = report.createDocument();
                    document.write(os);
                    showMessageDialog(EditorWindow.this, "Сохранено!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
    
    private void exportAsHtml() {
        Document document = Jsoup.parse(getHTMLText());
        Elements spans = document.getElementsByTag("span");
        for (Element span : spans) {
            for (Anaphora anaphora : dictionary.getAllAnaphora()) {
                for (String variant : anaphora.getAllVariants()) {
                    if (variant.contains(span.text().trim())) {
                        String style = span.attr("style");
                        String newStyle =
                                style + "; background: #" + anaphora.getHexColor();
                        span.attr("style", newStyle);
                    }
                }
            }
        }
        try {
            new FileWriter(new File("test_html.html")).write(document.html());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void initMenuItemsListeners() {
        menuBar.setCreateCommand(this::createNew);
        menuBar.setOpenCommand(this::load);
        menuBar.setSaveCommand(this::saveProject);
        menuBar.setSaveToFileCommand(this::saveToFile);
        menuBar.setWordExportCommand(this::exportToWord);
        menuBar.setHtmlExportCommand(this::exportAsHtml);
        menuBar.setExitCommand(this::dispose);
        menuBar.setCutCommand(textPane::cut);
        menuBar.setPasteCommand(textPane::paste);
        menuBar.setCopyCommand(textPane::copy);
        menuBar.setClearCommand(() -> textPane.setText(""));
        menuBar.setAnaphoraCommand(() -> {
            anaphoraButton.setSelected(true);
            showAnaphoraPanel();
        });
        menuBar.setChartCommand(this::openChartForm);
        menuBar.setSearchDuplicatesCommand(() -> {
            searchDuplicatesButton.setSelected(true);
            showSearchPanel();
        });
        menuBar.setHighlightingOnCommand(() -> textPane.setHighlighting(true));
        menuBar.setHighlightingOffCommand(() -> textPane.setHighlighting(false));
        menuBar.setInfoCommand(() -> new InfoForm(this).showDialog());
        menuBar.setUndoCommand(textPane::undo);
        menuBar.setRedoCommand(textPane::redo);
    }
    
    @Override
    public Font getFont() {
        return textPane.getFont();
    }
    
    @Override
    public void setFont(Font f) {
        textPane.setFont(f);
    }
    
    private void createNew() {
        String message = "Создать новый документ?";
        if (showConfirmDialog(this, message, getTitle(), YES_NO_OPTION) == YES_OPTION) {
            clearAll();
        }
    }
    
    private void clearAll() {
        dictionary.clear();
        textPane.setText("");
    }
    
    private void setReady(boolean ready) {
        readyLabel.setIcon(ready ? readyIcon : refreshIcon);
    }
    
    private void updateCounters() {
        Text text = getText();
        charsQuantityLabel.setText("Символов: " + text.countCharacters());
        wordsQuantityLabel.setText("Слов: " + text.countWords());
    }
    
    private void openChartForm() {
        if (chartForm == null) {
            chartForm = new ChartForm();
        }
        chartForm.setData(ChartFactory.createChart(dictionary, textPane.getText()).getData());
        if (!chartForm.isVisible()) {
            chartForm.showPreferably();
        } else {
            chartForm.requestFocus();
        }
    }
    
    private void createAnaphoraFromSelection() {
        String selectedText = textPane.getSelectedText();
        String color = Colors.generateHexColorFor(selectedText);
        Anaphora initialAnaphora = new Anaphora(selectedText, selectedText, new ArrayList<>(),
                color, false, project);
        anaphoraCreator.showDialog(initialAnaphora);
    }
    
    private void createAnaphoraFromSearchSelection() {
        String selectedText = secondaryAnaphoraTextPane.getSelectedText();
        String color = Colors.generateHexColorFor(selectedText);
        Anaphora initialAnaphora = new Anaphora(selectedText, selectedText, new ArrayList<>(),
                color, false, project);
        anaphoraCreator.showDialog(initialAnaphora);
    }
    
    public Text getText() {
        return new Text(textPane.getText());
    }
    
    public void setProject(Project project) {
        this.project = project;
        setTitle(project.getName() + " - Проект \"Златоуст\"");
        clearAll();
        dictionary.addAll(project.getAnaphoraList());
        textPane.setText(project.getText());
        textPane.updateHighlighting(null);
    }
    
    private void saveProject() {
        updateProject();
        new ProjectDAO().saveOrUpdate(project);
    }
    
    private void updateProject() {
        project.setText(textPane.getText());
        project.getAnaphoraList().clear();
        project.getAnaphoraList().addAll(dictionary.getAllAnaphora());
        for (Anaphora anaphora : project.getAnaphoraList()) {
            anaphora.setProject(project);
        }
    }
    
    private void saveToFile() {
        FileDialog fileDialog = new FileDialog(this, "Сохранение", FileDialog.SAVE);
        fileDialog.setVisible(true);
        String path = fileDialog.getFile();
        if (path != null) {
            if (!path.endsWith(".chr")) {
                path += ".chr";
            }
            updateProject();
            try {
                IO.writeProject(project, new File(path));
            } catch (IOException ex) {
                showErrorMessage(ex.getMessage());
            }
        }
    }
    
    private void load() {
        ProjectLoader loader = new ProjectLoader(this);
        loader.load(() -> setProject(loader.getLoadedProject()),
                () -> showErrorMessage("Ошибка закгрузки файла"));
    }
    
    private void showErrorMessage(String message) {
        showMessageDialog(this, message, getTitle(), ERROR_MESSAGE);
    }
}

