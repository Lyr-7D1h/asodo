package com.example.ninja.Domain.exporter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.widget.Toast;

import com.example.ninja.Domain.httpRequests.AsodoRequester;
import com.example.ninja.Domain.httpRequests.CustomListener;
import com.example.ninja.Domain.util.UserUtils;
import com.example.ninja.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
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
    private File file = null;
    private Context context;
    private String path;
    private String fileName;
    private String category;


    public Exporter(Date[] dates, Context context, String category) {
        // Switch dates if one is greater than another
        if (dates[0].after(dates[1])) {
            Date end = dates[0];
            dates[0] = dates[1];
            dates[1] = end;
        }
        this.start = dates[0];
        this.end = dates[1];
        this.context = context;
        this.category = category.toLowerCase();

        createPath();
    }

    public void createPath() {

        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + context.getString(R.string.app_name_cap));
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            System.out.println("Folder created at " + folder.getAbsolutePath());
        } else {
            Toast.makeText(this.context, context.getString(R.string.exporter_no_folder), Toast.LENGTH_SHORT);
        }
        String fileName = context.getString(R.string.app_name_cap) + "_"
                + new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").format(new Date())+".pdf";
        File dir = new File(folder.getAbsolutePath());
        this.fileName = fileName;
        this.file = new File(dir, fileName);
        System.out.println("Writing to "+dir.getAbsoluteFile() + "/" + fileName);
        this.path = dir.getAbsoluteFile() + "/";
        if (!file.exists()) {
            try {
                file.createNewFile();
                file.setReadable(true, false);
                writePdf();
            } catch (IOException e) {
                Toast.makeText(this.context, context.getString(R.string.exporter_no_permission), Toast.LENGTH_SHORT).show();
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
//                System.out.println(image);
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
                int btint = obj.get("businessTrip").getAsInt();
                String description = "";
                if (obj.has("desDeviation")) {
                    description = obj.get("desDeviation").getAsString();
                }
                String businessTrip = "";
                switch (btint) {
                    case 1:
                        businessTrip = "Business Trip";
                        break;
                    case 0:
                        businessTrip = "Personal Trip";
                        break;
                }
                String startDate = obj.get("tripStarted").getAsString();
                if (distanceDriven != 0) {
                    if (description != null && !description.isEmpty()) {
                        String output = String.format("\n%s\n%s\nKilometers gereden:      %d\nDescription: %s", startDate, businessTrip, distanceDriven, description);
                        document.add(new Paragraph(output));
                    } else {
                        String output = String.format("\n%s\n%s\nKilometers gereden:      %d", startDate, businessTrip, distanceDriven);
                        document.add(new Paragraph(output));
                    }
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        document.close();

        sendPdf();

        Toast.makeText((Activity) this.context, context.getString(R.string.exporter_download_successful), Toast.LENGTH_LONG).show();
    }

    public void sendPdf() {
        System.out.println("Sending email");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        String[] TO = {""};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("application/pdf");

        String startDate = new SimpleDateFormat("dd/MM/YYY").format(this.start);
        String endDate = new SimpleDateFormat("dd/MM/YYY").format(this.end);

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, String.format(context.getString(R.string.exporter_subject_message), startDate, endDate));
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(this.file));
        emailIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.exporter_automated_message));

        try {
            this.context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            System.out.println("Finished sending email...");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this.context,
                    context.getString(R.string.exporter_no_email_client), Toast.LENGTH_SHORT).show();
        }
    }

    // Get api data with dates provided
    public void writePdf() {
        String userID = UserUtils.getUserID(this.context);
        String startDate = new SimpleDateFormat("YYY-MM-dd").format(this.start);
        String endDate = new SimpleDateFormat("YYY-MM-dd").format(this.end);
        String jsonString = String.format("{\"startDate\":\"%s\", \"endDate\": \"%s\", \"userID\": \"%s\"}", startDate, endDate, userID );
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();

        AsodoRequester.newRequest("getTrips", json, (Activity) this.context, new CustomListener() {
            @Override
            public void onResponse(JsonObject jsonResponse) {
            System.out.println(category);
            System.out.println(jsonResponse);
            if (category.equals(context.getString(R.string.activity_export_business_personal))) {
                createPdf(jsonResponse);
            } else if (category.equals(context.getString(R.string.activity_export_business))) {
                createPdf(filterJson(jsonResponse, 1));
            } else {
                createPdf(filterJson(jsonResponse, 0));
            }
            }
        });
    }

    public JsonObject filterJson(JsonObject input, int businesstrip) {
        JsonArray trips = input.get("trips").getAsJsonArray();

        JsonObject newJsonResponse = new JsonObject();
        newJsonResponse.add("trips", new JsonArray());
        JsonArray output = newJsonResponse.get("trips").getAsJsonArray();

        for (int i=0; i<trips.size(); i++) {
            JsonObject obj = trips.get(i).getAsJsonObject();
            if (obj.get("businessTrip").getAsInt() == businesstrip) {
                System.out.println("ADD");
                output.add(obj);
            }
        }
        System.out.println(output);
        System.out.println(newJsonResponse.getAsJsonObject());
        return newJsonResponse.getAsJsonObject();
    };
}
