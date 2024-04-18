package com.example.birdbrain.Utilities;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Toast;
import com.example.birdbrain.Entities.LogEntry;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PDFGenerator {

    public static void generateLogReport(Context context, List<LogEntry> logs) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Paint paint = new Paint();

        int y = 25; // vertical position for drawing text
        for (LogEntry log : logs) {
            page.getCanvas().drawText("Date/Time: " + log.getDateTime(), 10, y, paint);
            y += 10;
            page.getCanvas().drawText("Action: " + log.getAction(), 10, y, paint);
            y += 10;
            page.getCanvas().drawText("Details: " + log.getDetails(), 10, y, paint);
            y += 40;
        }

        document.finishPage(page);

        // Writing the document content to an app-specific directory
        File file = new File(context.getExternalFilesDir(null), "MyAppLogs");
        if (!file.exists() && !file.mkdirs()) {
            Log.e("PDFGenerator", "Failed to create directory");
            return;  // Stop further execution if directory creation failed
        }

        String targetPdf = file.getAbsolutePath() + "/logReport.pdf";
        File filePath = new File(targetPdf);

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            document.writeTo(outputStream);
            Toast.makeText(context, "PDF file generated successfully in app-specific directory.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error in PDF creation: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        document.close();
    }
}
