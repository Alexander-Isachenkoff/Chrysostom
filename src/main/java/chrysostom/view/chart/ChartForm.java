package chrysostom.view.chart;

import chrysostom.files.DirectoryNotExistsException;
import chrysostom.files.FileNameException;
import chrysostom.files.Inspector;
import chrysostom.io.IO;
import chrysostom.model.chart.ChartSettings;
import chrysostom.model.chart.DataRay;
import chrysostom.util.SystemClipboard;
import dialog.ChooseDataDialog;

import javax.imageio.ImageIO;
import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.*;

import static javax.swing.JOptionPane.*;

public class ChartForm extends JFrame
{
    private final ChartPanel chartPanel = new ChartPanel();
    private JPanel contentPanel;
    private JButton saveButton;
    private JButton copyButton;
    private JButton settingsButton;
    private JButton dataButton;
    private JLabel imageSavedLabel;
    private JPanel chartWrapper;
    
    private Map<String, DataRay> data = new LinkedHashMap<>();
    private Set<String> displayableData = new LinkedHashSet<>();
    private SaveImageDialog saveImageDialog;
    
    public ChartForm() {
        initForm();
        initButtonListeners();
        chartWrapper.add(chartPanel, BorderLayout.CENTER);
    }
    
    private void initForm() {
        setTitle("График");
        setIconImage(IO.readIcon("/icons/16/graph.png").getImage());
        setContentPane(contentPanel);
        setMinimumSize(contentPanel.getMinimumSize());
    }
    
    private void initButtonListeners() {
        copyButton.addActionListener(e -> onCopyClick());
        saveButton.addActionListener(e -> onSaveClick());
        settingsButton.addActionListener(e -> onSettingsClick());
        dataButton.addActionListener(e -> onChooseDataClick());
    }
    
    private void onCopyClick() {
        BufferedImage image = chartPanel.getImage();
        SystemClipboard.setContent(image);
        showImageSavedLabel();
    }
    
    private void showImageSavedLabel() {
        imageSavedLabel.setVisible(true);
        Timer timer = new Timer(1000, e -> imageSavedLabel.setVisible(false));
        timer.setRepeats(false);
        timer.start();
    }
    
    private void onSaveClick() {
        saveImageDialogLazyInit();
        saveImageDialog.setSaveAction(() -> {
            if (checkCanSaveImage()) {
                saveImage(saveImageDialog.getSaveParameters());
                saveImageDialog.dispose();
            }
        });
        saveImageDialog.setCurrentFile("New");
        saveImageDialog.setCurrentDirectory(new File("").getAbsolutePath());
        saveImageDialog.showDialog(this);
    }
    
    private void saveImageDialogLazyInit() {
        if (saveImageDialog == null) {
            saveImageDialog = new SaveImageDialog();
        }
    }
    
    private boolean checkCanSaveImage() {
        Inspector inspector = new Inspector();
        String dir = saveImageDialog.getDirName();
        String file = saveImageDialog.getFileName();
        String ext = saveImageDialog.getFormat();
        try {
            inspector.check(file, dir, ext);
        } catch (FileNameException ex) {
            showWarning(saveImageDialog, ex.getMessage());
            return false;
        } catch (DirectoryNotExistsException ex) {
            String message = ex.getMessage() + "\nСоздать?";
            if (showConfirmDialog(saveImageDialog, message) != JOptionPane.OK_OPTION) {
                return false;
            }
        } catch (FileAlreadyExistsException ex) {
            String message = ex.getMessage() + "\nПерезаписать?";
            if (showConfirmDialog(saveImageDialog, message) != JOptionPane.OK_OPTION) {
                return false;
            }
        }
        return true;
    }
    
    private void showWarning(Component parent, String message) {
        showMessageDialog(parent, message, getTitle(), WARNING_MESSAGE);
    }
    
    private int showConfirmDialog(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, getTitle(), JOptionPane.OK_CANCEL_OPTION);
    }
    
    private void onSettingsClick() {
        ChartSettings oldSettings = chartPanel.getChart().getSettings();
        ChartSettings newSettings = SettingsDialog.showDialog(this, oldSettings);
        if (newSettings != null) { // todo Убрать возвращение null
            chartPanel.getChart().setSettings(newSettings);
        }
    }
    
    private void onChooseDataClick() {
        ChooseDataDialog<String> dialog = new ChooseDataDialog<>();
        List<String> unusedData = new ArrayList<>();
        for (String title : data.keySet()) {
            if (!displayableData.contains(title)) {
                unusedData.add(title);
            }
        }
        List<String> newData = dialog.showDialog(this, unusedData, displayableData);
        try {
            setDisplayableData(newData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void saveImage(SaveParameters parameters) {
        File file = parameters.getFile();
        File dir = file.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            String message = "Не удалось создать папку \"" + dir.getAbsolutePath() + "\"";
            showMessageDialog(this, message, getTitle(), ERROR_MESSAGE);
            return;
        }
        Color bg = parameters.getTransparency() ? null : Color.WHITE;
        BufferedImage image = chartPanel.getChart().getImage(parameters.getWidth(), parameters.getHeight(), bg);
        try {
            ImageIO.write(image, parameters.getFormat(), file);
        } catch (IOException ex) {
            showMessageDialog(this, ex.getMessage(), getTitle(), ERROR_MESSAGE);
            return;
        }
        String message = "Изображение сохранено";
        showMessageDialog(this, message, getTitle(), INFORMATION_MESSAGE);
    }
    
    private void updateDisplayableData() {
        chartPanel.getChart().removeAllData();
        for (String title : displayableData) {
            chartPanel.getChart().add(title, data.get(title));
        }
    }
    
    public void setDisplayableData(Collection<String> titles) throws Exception {
        if (!data.keySet().containsAll(titles)) {
            throw new Exception();
        }
        displayableData.clear();
        displayableData.addAll(titles);
        updateDisplayableData();
    }
    
    public Map<String, DataRay> getData() {
        return new LinkedHashMap<>(data);
    }
    
    public void setData(List<DataRay> newData) {
        data = new LinkedHashMap<>();
        for (DataRay dataRay : newData) {
            data.put(dataRay.name, dataRay);
        }
        Set<String> newDisplayableData = new LinkedHashSet<>();
        for (String title : displayableData) {
            if (data.containsKey(title)) {
                newDisplayableData.add(title);
            }
        }
        displayableData = newDisplayableData;
        updateDisplayableData();
    }
    
    public void showPreferably() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public void addData(String title, DataRay dataRay) {
        data.put(title, new DataRay(title, dataRay));
        updateDisplayableData();
    }
    
    public void removeData(String title) {
        data.remove(title);
        displayableData.remove(title);
        updateDisplayableData();
    }
    
    public void removeAllData() {
        data.clear();
        displayableData.clear();
        chartPanel.getChart().removeAllData();
    }
    
    public void setAllDataDisplayable() {
        try {
            setDisplayableData(data.keySet());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
