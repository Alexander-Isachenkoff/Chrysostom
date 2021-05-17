package chrysostom.view.components;

import chrysostom.model.entities.Anaphora;
import chrysostom.model.AnaphoraDictionary;
import chrysostom.model.Statistics;
import chrysostom.model.search.SearchEngine;
import chrysostom.model.search.SearchEntry;
import chrysostom.model.search.SearchResults;
import chrysostom.util.Colors;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SearchDuplicatesPanel extends JPanel
{
    private final AnaphoraDictionary dictionary;
    private final CellPopupMenu cellPopupMenu = new CellPopupMenu();
    private final Supplier<String> textSupplier;
    private SearchResults repeats = new SearchResults();
    private JPanel contentPanel;
    private JSpinner minLengthSpinner;
    private JButton searchButton;
    private JTable wordsTable;
    private JTable coordinatesTable;
    private JSpinner countSpinner;
    private JRadioButton wordsRB;
    private JRadioButton letterCombinationsRB;
    private JSpinner maxLengthSpinner;
    private DefaultTableModel wordsTableModel;
    private DefaultTableModel coordinatesTableModel;
    private Consumer<SearchResults> onRepeatsFound = (duplicates) -> {};
    private final Runnable searchWordsRun = new Runnable()
    {
        @Override
        public void run() {
            int minLength = (int) minLengthSpinner.getValue();
            int maxLength = (int) maxLengthSpinner.getValue();
            int minCount = (int) countSpinner.getValue();
            repeats = new SearchEngine().searchRepeatedWords(textSupplier.get(), minLength, maxLength,
                    minCount);
            updateWordsTable();
            onRepeatsFound.accept(repeats);
        }
    };
    private final Runnable searchLetterCombinationRun = new Runnable()
    {
        @Override
        public void run() {
            int minLength = (int) minLengthSpinner.getValue();
            int minCount = (int) countSpinner.getValue();
            int maxLength = (int) maxLengthSpinner.getValue();
            UIManager.put("ProgressMonitor.progressText", "Поиск");
            ProgressMonitor pm = new ProgressMonitor(SearchDuplicatesPanel.this, "",
                    "", 0, 100);
            
            pm.setMillisToDecideToPopup(100);
            pm.setMillisToPopup(100);
            SearchEngine searchEngine = new SearchEngine();
            repeats = searchEngine.searchRepeatedLetterCombinations(textSupplier.get(), minLength,
                    maxLength, minCount, f -> {
                        int progress = (int) (f * pm.getMaximum());
                        pm.setNote("<html>Завершено на <b>" + progress + "%</b></html>");
                        pm.setProgress(progress);
                    });
            updateWordsTable();
            onRepeatsFound.accept(repeats);
        }
    };
    private Thread searchThread = new Thread(searchLetterCombinationRun);
    
    public SearchDuplicatesPanel(Supplier<String> textSupplier, AnaphoraDictionary dictionary) {
        this.textSupplier = textSupplier;
        initDialog();
        initLengthSpinners();
        initCountSpinner();
        initSearchButtonListener();
        initCellPopupMenuListener();
        initWordsTable();
        initCoordinatesTable();
        this.dictionary = dictionary;
    }
    
    private void initDialog() {
        setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
        setPreferredSize(contentPanel.getPreferredSize());
        setMinimumSize(contentPanel.getMinimumSize());
    }
    
    private void initLengthSpinners() {
        minLengthSpinner.setModel(new SpinnerNumberModel(2, 1, Integer.MAX_VALUE, 1));
        maxLengthSpinner.setModel(new SpinnerNumberModel(30, 1, Integer.MAX_VALUE, 1));
    }
    
    private void initCountSpinner() {
        countSpinner.setModel(new SpinnerNumberModel(2, 2, Integer.MAX_VALUE, 1));
    }
    
    private void initSearchButtonListener() {
        searchButton.addActionListener(e -> {
            if (wordsRB.isSelected()) {
                searchWords();
            } else if (letterCombinationsRB.isSelected()) {
                searchLetterCombinations();
            }
        });
    }
    
    public void setOnRepeatsFound(Consumer<SearchResults> onRepeatsFound) {
        this.onRepeatsFound = onRepeatsFound;
    }
    
    private void searchWords() {
        if (!searchThread.isAlive()) {
            searchThread = new Thread(searchWordsRun);
            searchThread.start();
        }
    }
    
    private void searchLetterCombinations() {
        if (!searchThread.isAlive()) {
            searchThread = new Thread(searchLetterCombinationRun);
            searchThread.start();
        }
    }
    
    private void updateWordsTable() {
        wordsTableModel.setRowCount(repeats.size());
        int row = 0;
        for (Map.Entry<String, List<SearchEntry>> duplicate : repeats.entrySet()) {
            wordsTableModel.setValueAt(duplicate.getKey(), row, 0);
            wordsTableModel.setValueAt(duplicate.getValue().size(), row, 1);
            row++;
        }
    }
    
    private void updateCoordinatesTable() {
        int selectedRow = wordsTable.getSelectedRow();
        if (selectedRow == -1) {
            coordinatesTableModel.setRowCount(0);
            return;
        }
        String word = (String) wordsTable.getValueAt(selectedRow, 0);
        List<Double> coordinates =
                repeats.get(word).stream().map(searchEntry -> searchEntry.start / (double) textSupplier.get().length()).collect(Collectors.toList()); // TODO: 28.09.2020 убрать это дерьмо
        coordinatesTableModel.setRowCount(coordinates.size());
        for (int row = 0; row < coordinates.size(); row++) {
            coordinatesTableModel.setValueAt(row + 1, row, 0);
            coordinatesTableModel.setValueAt(Statistics.roundTo(coordinates.get(row), 6), row, 1);
        }
    }
    
    private void initCellPopupMenuListener() {
        cellPopupMenu.addPopupMenuListener(new PopupMenuListener()
        {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                cellPopupMenu.addItem.setEnabled(wordsTable.getSelectedRowCount() != 0);
                cellPopupMenu.addAllItem.setEnabled(wordsTable.getRowCount() != 0);
            }
            
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });
    }
    
    private void initWordsTable() {
        wordsTableModel = new DefaultTableModel()
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return new Class<?>[]{ String.class, Integer.class }[columnIndex];
            }
        };
        wordsTable.setModel(wordsTableModel);
        wordsTableModel.addColumn("Слово");
        wordsTableModel.addColumn("Кол-во повторов");
        wordsTable.setAutoCreateRowSorter(true);
        wordsTable.getSelectionModel().addListSelectionListener(e -> updateCoordinatesTable());
        wordsTable.setComponentPopupMenu(cellPopupMenu);
    }
    
    private void initCoordinatesTable() {
        coordinatesTableModel = new DefaultTableModel()
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        coordinatesTable.setModel(coordinatesTableModel);
        coordinatesTableModel.addColumn("№");
        coordinatesTableModel.addColumn("Координата");
    }
    
    private String[] getAllWords() {
        String[] words = new String[wordsTable.getRowCount()];
        for (int i = 0; i < words.length; i++) {
            words[i] = wordsTable.getValueAt(i, 0).toString();
        }
        return words;
    }
    
    private String[] getSelectedWords() {
        int[] selectedRows = wordsTable.getSelectedRows();
        String[] words = new String[selectedRows.length];
        for (int i = 0; i < words.length; i++) {
            words[i] = wordsTable.getValueAt(selectedRows[i], 0).toString();
        }
        return words;
    }
    
    private class CellPopupMenu extends JPopupMenu
    {
        private final JMenuItem addItem = new JMenuItem("Добавить");
        private final JMenuItem addAllItem = new JMenuItem("Добавить всё");
        
        private CellPopupMenu() {
            add(addItem);
            add(addAllItem);
            addAllItem.addActionListener(e -> addAnaphorasByWords(getAllWords()));
            addItem.addActionListener(e -> addAnaphorasByWords(getSelectedWords()));
        }
        
        private void addAnaphorasByWords(String[] words) {
            for (String word : words) {
                if (!dictionary.containsName(word)) {
                    dictionary.add(new Anaphora(word, word, new ArrayList<>(),
                            Colors.generateHexColorFor(word), true, null));
                }
            }
        }
    }
}
