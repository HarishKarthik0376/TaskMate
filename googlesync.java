package com.HkCodes.Todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.client.http.FileContent;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

public class googlesync {
    private final Drive googleDriveService;
    private final Context context;

    public googlesync(Drive googleDriveService, Context context) {
        this.googleDriveService = googleDriveService;
        this.context = context;
    }

    public Task<String> uploadFile(java.io.File file, String mimeType) {
        return Tasks.call(() -> {
            try {
                com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
                fileMetadata.setName(file.getName());

                FileContent mediaContent = new FileContent(mimeType, file);

                com.google.api.services.drive.model.File uploadedFile = googleDriveService.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();

                if (uploadedFile == null) {
                    throw new IOException("Null result when requesting file upload.");
                }

                String fileId = uploadedFile.getId();
                Toast.makeText(context, "File ID: " + fileId, Toast.LENGTH_SHORT).show();
                return fileId;
            } catch (IOException e) {
                Log.e("googlesync", "Error uploading file: ", e);
                Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show();
                throw e;
            }
        });
    }

    public Task<Void> retrieveFile(String fileId, java.io.File outputFile) {
        return Tasks.call(() -> {
            try {
                googleDriveService.files().get(fileId)
                        .executeMediaAndDownloadTo(new FileOutputStream(outputFile));
            } catch (IOException e) {
                Log.e("googlesync", "Failed to retrieve file from Drive", e);
                throw new RuntimeException("Failed to retrieve file from Drive", e);
            }
            return null;
        });
    }

    public Task<String> retrieveAndParseFile(String fileId) {
        java.io.File tempFile = new java.io.File(context.getCacheDir(), "TasksData.json");
        return retrieveFile(fileId, tempFile).continueWith(task -> {
            if (task.isSuccessful()) {
                try {
                    // Read the file content
                    String jsonString = readFile(tempFile);
                    tempFile.delete(); // Clean up temp file
                    return jsonString;
                } catch (IOException e) {
                    Log.e("googlesync", "Failed to read file", e);
                    Toast.makeText(context, "Failed to read file", Toast.LENGTH_SHORT).show();
                    throw new RuntimeException("Failed to read file", e);
                }
            } else {
                throw task.getException();
            }
        });
    }

    private String readFile(java.io.File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        int bytesRead = inputStream.read(buffer);
        inputStream.close();
        return new String(buffer, 0, bytesRead);
    }

    public Task<Void> listFilesInDrive() {
        return Tasks.call(() -> {
            try {
                FileList result = googleDriveService.files().list()
                        .setPageSize(10)
                        .setFields("nextPageToken, files(id, name)")
                        .execute();
                for (File file : result.getFiles()) {
                    Log.d("googlesync", "Found file: " + file.getName() + " (" + file.getId() + ")");
                }
            } catch (IOException e) {
                Log.e("googlesync", "Failed to list files in Drive", e);
                throw new RuntimeException("Failed to list files in Drive", e);
            }
            return null;
        });
    }

}
