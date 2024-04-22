package com.example.birdbrain.Utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.graphics.Paint;
import android.graphics.Color;
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
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, 1).create(); // Width increased to 600
        PdfDocument.Page page = document.startPage(pageInfo);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK); // white looked weird for some reason
        paint.setTextSize(12);

        int dateTimeX = 10;  // x-coordinate for Date/Time column
        int actionX = 150;   // x-coordinate for Action column (increased space)
        int detailsX = 350;  // x-coordinate for Details column (increased space)
        int y = 25; // Initial vertical position

        // Draw column titles
        paint.setFakeBoldText(true); // Make title bold
        page.getCanvas().drawText("Date/Time", dateTimeX, y, paint);
        page.getCanvas().drawText("Action", actionX, y, paint);
        page.getCanvas().drawText("Details", detailsX, y, paint);
        paint.setFakeBoldText(false); // Reset bold

        y += 40; // Increase this value to create more space between titles and first log entry

        for (LogEntry log : logs) {
            // Draw Date/Time
            drawTextInColumn(page.getCanvas(), "Date/Time: " + log.getDateTime(), dateTimeX, y, 130, paint);
            drawTextInColumn(page.getCanvas(), "Action: " + log.getAction(), actionX, y, 190, paint);
            drawTextInColumn(page.getCanvas(), "Details: " + log.getDetails(), detailsX, y, 250, paint);

            y += 40; // Increment y to move to the next line for the next log entry
        }


        document.finishPage(page);

        // Check and create the directory if it doesn't exist
        File directory = new File(context.getFilesDir(), "MyAppLogs");
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e("PDFGenerator", "Failed to create directory");
            Toast.makeText(context, "Failed to create directory", Toast.LENGTH_SHORT).show();
            return;
        }

        File filePath = new File(directory, "logReport.pdf");

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            document.writeTo(outputStream);
            Toast.makeText(context, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
            Log.i("PDFGenerator", "File: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error in PDF creation: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        document.close();
    }
    private static void drawTextInColumn(Canvas canvas, String text, int x, int y, int maxWidth, Paint paint) {
        if (paint.measureText(text) > maxWidth) {
            String[] words = text.split(" ");
            StringBuilder line1 = new StringBuilder();
            StringBuilder line2 = new StringBuilder();
            for (String word : words) {
                if (paint.measureText(line1.toString() + word + " ") < maxWidth) {
                    line1.append(word).append(" ");
                } else {
                    line2.append(word).append(" ");
                }
            }
            canvas.drawText(line1.toString().trim(), x, y, paint);
            if (line2.length() > 0) {
                canvas.drawText(line2.toString().trim(), x, y + 20, paint); // Draw second line below
            }
        } else {
            canvas.drawText(text, x, y, paint);
        }
    }
}
