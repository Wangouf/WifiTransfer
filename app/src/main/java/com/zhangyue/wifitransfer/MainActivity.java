package com.zhangyue.wifitransfer;

import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import com.google.zxing.WriterException;

public class MainActivity extends AppCompatActivity implements DialogCallback{

    private Button startStopButton;
    private TextView serverAddressTextView;
    private FileServer fileServer;
    private ImageView qrCodeImageView;
    private boolean isServerRunning = false;
    private static final int SERVER_PORT = 8888;
    @Override
    public void showMyDialog(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = (String) msg.obj;
            showMyDialog(message);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startStopButton = findViewById(R.id.startStopButton);
        serverAddressTextView = findViewById(R.id.serverAddressTextView);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 显示警告消息或采取其他措施
            showMyDialog("This version of the app is not supported on Android 10 and above.");
        }
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServerRunning) {
                    stopServer();
                } else {
                    startServer();
                }
            }
        });
    }

    private void startServer() {
        try {
            fileServer = new FileServer(SERVER_PORT, this);
            isServerRunning = true;
            startStopButton.setText("关闭服务");
            serverAddressTextView.setText("服务器地址: http://" + getLocalIpAddress() + ":" + SERVER_PORT);
            String url = "http://" + getLocalIpAddress() + ":" + SERVER_PORT;
            try {
                qrCodeImageView.setImageBitmap(QRcode.generateQRCode(url,256));
                showMyDialog("服务启动");
            } catch (WriterException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
            serverAddressTextView.setText("启动服务器失败");
        }
    }

    private void stopServer() {
        if (fileServer != null) {
            fileServer.stop();
            fileServer = null;
            isServerRunning = false;
            startStopButton.setText("开启服务");
            serverAddressTextView.setText("");
            qrCodeImageView.setImageDrawable(null);
        }
    }

    private String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        return Formatter.formatIpAddress(ipAddress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopServer();
    }
}