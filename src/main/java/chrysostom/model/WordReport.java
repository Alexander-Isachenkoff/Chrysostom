package chrysostom.model;

import chrysostom.model.chart.Chart;
import chrysostom.model.chart.ChartFactory;
import chrysostom.model.entities.Anaphora;
import chrysostom.util.Colors;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordReport
{
    private final String text;
    private final AnaphoraDictionary dictionary;
    
    public WordReport(String text, AnaphoraDictionary dictionary) {
        this.text = text;
        this.dictionary = dictionary;
    }
    
    public XWPFDocument createDocument() throws IOException {
        XWPFDocument document = new XWPFDocument();
        addText(document);
        addAnaphoraInfo(document);
        addChart(document);
        return document;
    }
    
    private void addText(XWPFDocument document) {
        for (String para : text.split("\n")) {
            XWPFParagraph documentParagraph = document.createParagraph();
            
            List<Highlight> highlights = Highlight.getHighlights(para, dictionary);
            List<Integer> runsPositions = new ArrayList<>();
            
            runsPositions.add(0);
            for (Highlight highlight : highlights) {
                runsPositions.add(highlight.getPosition());
                runsPositions.add(highlight.getPosition() + highlight.getLength());
            }
            Collections.sort(runsPositions);
            
            for (int i = 0; i < runsPositions.size(); i++) {
                Integer runsPosition = runsPositions.get(i);
                XWPFRun run = documentParagraph.createRun();
                int endPos = (i < runsPositions.size() - 1) ? runsPositions.get(i + 1) :
                        para.length();
                run.setText(para.substring(runsPositions.get(i), endPos));
                for (Highlight highlight : highlights) {
                    if (highlight.getPosition() == runsPosition) {
                        CTShd cTShd = run.getCTR().addNewRPr().addNewShd();
                        cTShd.setVal(STShd.CLEAR);
                        cTShd.setColor("auto");
                        cTShd.setFill(Colors.toHex(highlight.getColor()).replace("#", ""));
                    }
                }
            }
        }
    }
    
    private void addAnaphoraInfo(XWPFDocument document) {
        addAnaphoraBlockHeader(document);
        for (Anaphora anaphora : dictionary.getAllAnaphora()) {
            addDescriptionBlock(document, anaphora);
            addCoordinatesTable(document, anaphora);
        }
    }
    
    private void addAnaphoraBlockHeader(XWPFDocument document) {
        XWPFParagraph header = document.createParagraph();
        header.setSpacingBefore(360);
        header.setSpacingAfter(120);
        XWPFRun headerRun = header.createRun();
        headerRun.setText("Анафоры");
        headerRun.setBold(true);
        headerRun.setFontSize(16);
    }
    
    private void addDescriptionBlock(XWPFDocument document, Anaphora anaphora) {
        XWPFParagraph namePara = document.createParagraph();
        namePara.setSpacingBefore(120);
        namePara.setSpacingAfter(0);
        XWPFRun run = namePara.createRun();
        run.setText("Название: ");
        run.setBold(true);
        run = namePara.createRun();
        run.setText(anaphora.getName());
        
        XWPFParagraph descriptionPara = document.createParagraph();
        descriptionPara.setSpacingAfter(0);
        run = descriptionPara.createRun();
        run.setText("Описание: ");
        run.setBold(true);
        run = descriptionPara.createRun();
        run.setText(anaphora.getDescription());
        
        XWPFParagraph variantsPara = document.createParagraph();
        variantsPara.setSpacingAfter(0);
        run = variantsPara.createRun();
        run.setText("Варианты: ");
        run.setBold(true);
        
        for (int i = 0; i < anaphora.getAllVariants().size(); i++) {
            XWPFParagraph p = document.createParagraph();
            p.setIndentationLeft(715);
            p.setSpacingAfter(0);
            p.createRun().setText(anaphora.getAllVariants().get(i));
        }
    }
    
    private void addCoordinatesTable(XWPFDocument document, Anaphora anaphora) {
        List<Double> coords = Statistics.getCoordinates(anaphora, new Text(text));
        XWPFTable table = document.createTable(coords.size() + 1, 2);
        table.setCellMargins(0, 40, 0, 40);
        
        XWPFTableCell headCell = table.getRow(0).getCell(0);
        XWPFParagraph p = headCell.getParagraphs().get(0);
        p.setSpacingAfter(60);
        p.setSpacingBefore(60);
        p.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r = p.createRun();
        r.setText("№");
        r.setBold(true);
        
        headCell = table.getRow(0).getCell(1);
        p = headCell.getParagraphs().get(0);
        p.setSpacingAfter(60);
        p.setSpacingBefore(60);
        p.setAlignment(ParagraphAlignment.CENTER);
        r = p.createRun();
        r.setText("Координата");
        r.setBold(true);
        
        for (int i = 0; i < coords.size(); i++) {
            XWPFTableCell cell = table.getRow(i + 1).getCell(0);
            cell.getParagraphs().get(0).setSpacingAfter(0);
            cell.getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            cell.setText(String.valueOf(i + 1));
            
            cell = table.getRow(i + 1).getCell(1);
            cell.getParagraphs().get(0).setSpacingAfter(0);
            cell.getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            
            String coordText = String.valueOf(Math.round(coords.get(i) * 1000) / (double) 1000);
            cell.setText(coordText);
        }
    }
    
    private void addChart(XWPFDocument document) throws IOException {
        Chart chart = ChartFactory.createChart(dictionary, text);
        BufferedImage image = chart.getImage(600, 400, Color.WHITE);
        
        File imgFile = new File("temp_img.png");
        try {
            ImageIO.write(image, "png", imgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        try (InputStream stream = new FileInputStream(imgFile)) {
            run.addPicture(stream, XWPFDocument.PICTURE_TYPE_PNG, "temp_img.png",
                    Units.toEMU(image.getWidth() * 0.75),
                    Units.toEMU(image.getHeight() * 0.75));
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        imgFile.delete();
    }
}
