package com.example.ninja;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class Exporter {
    private Date start;
    private Date end;
    private String path = "test";
    private Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
    private File file = null;

    public Exporter(Date start, Date end) {
        this.start = start;
        this.end = end;

        createPath();
    }

    public void createPath() {
        String fpath = "/sdcard/" + this.path + ".pdf";
        this.file = new File(fpath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                writePdf();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writePdf() {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document,
            new FileOutputStream(file.getAbsoluteFile()));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        document.open();

        try {
            document.add(new Paragraph("My First Pdf !"));
            document.add(new Paragraph("Hello World"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.close();
    }
}
