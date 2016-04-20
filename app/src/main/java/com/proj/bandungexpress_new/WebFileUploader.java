package com.proj.bandungexpress_new;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Arrival Sentosa on 4/14/2016.
 */
public class WebFileUploader extends AsyncTask<Object, String, Object> {
    URL connectURL;
    String params;
    String responseString;
    String fileName;
    byte[] dataToServer;
    FileInputStream fileInputStream;
    TextView info;

    void setUrlAndFile(String urlString, String fileName, TextView info)
    {
        this.info = info;
        try
        {
            fileInputStream = new FileInputStream(fileName);
            connectURL = new URL(urlString);
        }
        catch(Exception e)
        {
            publishProgress(e.toString());
        }
        this.fileName = fileName;
    }

    synchronized void doUpload()
    {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try
        {
            publishProgress("Uploading...");
            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
            DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName +"\"" + lineEnd);
            dos.writeBytes(lineEnd);

            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            fileInputStream.close();
            dos.flush();

            InputStream is = conn.getInputStream();
            int ch;

            StringBuffer buff = new StringBuffer();
            while((ch=is.read())!=-1)
            {
                buff.append((char)ch);
            }
            publishProgress(buff.toString());
            dos.close();
        }
        catch (Exception e)
        {
            publishProgress(e.toString());
        }
    }

    @Override
    protected Object doInBackground(Object... arg0) {
        doUpload();
        return null;
    }

    protected void onProgressUpdate(String... progress) {
        this.info.setText(progress[0]);
    }
}
