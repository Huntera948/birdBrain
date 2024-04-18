package com.example.birdbrain.Activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;

import com.example.birdbrain.R;

import java.io.File;
import java.io.IOException;

public class PDFViewerActivity extends Activity {

    private ImageView pdfImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);

        pdfImageView = findViewById(R.id.pdfImageView);

        // Assume the PDF file path is passed via intent extras
        String filePath = getIntent().getStringExtra("filePath");
        checkFileExists(filePath);
        renderPdf(filePath);
    }
    private void checkFileExists(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Log.i("PDFViewerActivity", "File exists: " + filePath);
        } else {
            Log.e("PDFViewerActivity", "File not found: " + filePath);
        }
    }
    private void renderPdf(String fileName) {

        File file = new File(getFilesDir(), fileName);
        if (!file.exists()) {
            Log.e("PDFViewerActivity", "File not found: " + file.getAbsolutePath());
            return;
        }

        try {
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(fileDescriptor);
            if (renderer.getPageCount() > 0) {
                PdfRenderer.Page page = renderer.openPage(0);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(),
                        Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                pdfImageView.setImageBitmap(bitmap);
                page.close();
            }
            renderer.close();
            fileDescriptor.close();
        } catch (IOException e) {
            Log.e("PDFViewerActivity", "Error opening PDF", e);
        }
    }

}
