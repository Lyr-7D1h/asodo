package com.example.ninja.Domain.exporter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.Toast;

import com.example.ninja.Domain.httpRequests.AsodoRequester;
import com.example.ninja.Domain.httpRequests.CustomListener;
import com.example.ninja.Domain.util.UserUtils;
import com.example.ninja.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Exporter {
    private Date start;
    private Date end;
    private Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
    private File file = null;
    private Context context;

    public Exporter(Date[] dates, Context context) {
        // Switch dates if one is greater than another
        if (dates[0].after(dates[1])) {
            Date end = dates[0];
            dates[0] = dates[1];
            dates[1] = end;
        }
        this.start = dates[0];
        this.end = dates[1];
        this.context = context;

        createPath();
    }

    public void createPath() {
        String fileName = "ASODO_"+new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").format(new Date())+".pdf";
        System.out.println("Writing to " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + fileName);
        File dir =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        this.file = new File(dir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                writePdf();
            } catch (IOException e) {
                Toast.makeText(this.context, "Permission denied to write to your External storage", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void createPdf(JsonObject jsonResponse) {
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
            Drawable d = this.context.getDrawable(R.drawable.logo);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            try {
                Image image = Image.getInstance(stream.toByteArray());
                image.scaleToFit(850,100);
                image.setAlignment(Element.ALIGN_CENTER);
                System.out.println(image);
                document.add(image);
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonArray trips = jsonResponse.get("trips").getAsJsonArray();
            for (int i=0; i<trips.size(); i++) {
                JsonObject obj = trips.get(i).getAsJsonObject();
                int distanceDriven = obj.get("distanceDriven").getAsInt();
                String startDate = obj.get("tripStarted").getAsJsonObject().get("date").getAsString();
                startDate = startDate.split("\\.")[0];

                String output = String.format("\n%s\nKilometers gereden:      %d", startDate, distanceDriven);
                document.add(new Paragraph(output));
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.close();
        Toast.makeText((Activity) this.context, "PDF Download Successful!", Toast.LENGTH_LONG).show();
    }

    // Get api data with dates provided
    public void writePdf() {
        String userID = UserUtils.getUserID(this.context);
        String startDate = new SimpleDateFormat("YYY-MM-dd").format(start);
        String endDate = new SimpleDateFormat("YYY-MM-dd").format(end);
        String jsonString = String.format("{\"startDate\":\"%s\", \"endDate\": \"%s\", \"userID\": \"%s\"}", startDate, endDate, userID );
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();

        AsodoRequester.newRequest("getTrips", json, (Activity) this.context, new CustomListener() {
            @Override
            public void onResponse(JsonObject jsonResponse) {
                createPdf(jsonResponse);
            }
        });
    }
}
