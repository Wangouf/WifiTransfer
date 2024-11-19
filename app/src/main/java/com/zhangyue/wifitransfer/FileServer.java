package com.zhangyue.wifitransfer;

import android.content.res.AssetManager;
import android.os.Environment;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import fi.iki.elonen.NanoHTTPD;

import java.io.*;

import java.net.URLDecoder;
import java.util.Map;

public class FileServer extends NanoHTTPD {

    String UPLOAD_DIR = Environment.getExternalStorageDirectory()+"/";
    AssetManager assetManager;
    MainActivity activity;
    public FileServer(int port, MainActivity act) throws IOException {
        super(port);
        assetManager = act.getAssets();
        activity = act;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }
    private String readAssetFile(String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            // 打开 assets 文件夹中的文件
            InputStream inputStream = this.assetManager.open(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n'); // 添加换行符以保留原始文件的格式
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return stringBuilder.toString();
    }


    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();

        if (Method.GET.equals(method) && "/".equals(uri)) {
            try {
                return newFixedLengthResponse(getUploadForm());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (Method.POST.equals(method) && "/upload".equals(uri)) {
            return handleFileUpload(session);
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not Found");
    }

    private String getUploadForm() throws IOException {
        String msg = readAssetFile("a.html");
        return msg;
    }

    private Response handleFileUpload(IHTTPSession session) {
        Map<String, String> files = new java.util.HashMap<>();
        String filename;
        try {
            session.parseBody(files);
            filename = URLDecoder.decode(session.getHeaders().get("filename"), "UTF-8");

        } catch (IOException | ResponseException e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "Internal Server Error");
        }

        String fileData = files.get("file");
        if (fileData != null) {
            try {
                File file = new File(UPLOAD_DIR + filename);
                Log.d("filename",file.getName());
                FileOutputStream fos = new FileOutputStream(file);
                try (FileInputStream fis = new FileInputStream(fileData)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
                fos.close();
                String result = filename +"上传成功";
                Message msg = activity.handler.obtainMessage();
                msg.obj = result;
                activity.handler.sendMessage(msg);
                return newFixedLengthResponse("{ \"success\": true }");

            } catch (IOException e) {
                e.printStackTrace();
                String result = filename +"上传失败";
                Message msg = activity.handler.obtainMessage();
                msg.obj = result;
                activity.handler.sendMessage(msg);

                return newFixedLengthResponse("{ \"success\": false }");
            }
        } else {
            return newFixedLengthResponse("{ \"success\": false }");
        }
    }
}
