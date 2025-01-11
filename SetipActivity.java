package com.bhd.helloworld;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 设置ip和端口的页面
 */
public class SetipActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextIp;
    private EditText editTextPort;
    private Button btnSave;
    private Button btnReset;

    private String ip;
    private int port;


    private SharedPreferences mSharedPreference;
    private static final String SP_TEST = "sp_test";


    Calendar calendar= Calendar.getInstance(Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove title bar  即隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();// 隐藏ActionBar
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notification bar  即全屏

        setContentView(R.layout.activity_setip);
        //初始化
        this.initView();
        //获取xml保存数据的对象，并获取ip和端口
        mSharedPreference = getSharedPreferences(SP_TEST, Context.MODE_PRIVATE);
        ip = get("ip");
        String portStr = get("port");
        if (portStr != null) {
            port = Integer.parseInt(portStr);
            editTextPort.setText(portStr);
        } else {
            port = 7016;
        }

        if(ip!=null && !"".equals(ip)){
            editTextIp.setText(ip);
        }
    }

    /**
     * 初始化
     */
    private void initView() {

        btnSave = findViewById(R.id.btn_save);
        btnReset = findViewById(R.id.btn_reset);

        editTextIp = findViewById(R.id.editText_ip);
        editTextPort = findViewById(R.id.editText_port);

        btnSave.setOnClickListener(this);
        btnReset.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        ip = editTextIp.getText().toString();
        String paramPort = editTextPort.getText().toString();

        //验证ip
        String ipReg = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern ipPattern = Pattern.compile(ipReg);
        boolean flag = ipPattern.matcher(ip).matches();
        if(!flag){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SetipActivity.this, "请输入合法的ip地址", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        //验证端口
        port = Integer.parseInt(paramPort);
        if(port > 65535 || port<1024){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SetipActivity.this, "请输入1024-65535之间的合法端口", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        switch (v.getId()) {
            //保存并跳转到主页
            case R.id.btn_save:

                save("ip",ip);
                save("port",String.valueOf(port));

                String mac = getNewMac();
                System.out.println(mac);
                String message = "ADD";
                if(mSharedPreference!=null){
                    message = "UPDATE";
                }
                message += "17"+mac+"15"+ip;

                sendMsg(ip,port,message);
                //获取Intent对象
                Intent intent = getIntent();
                //实例化要传递的数据包
                Bundle bundle = new Bundle();
                // 显示选中的图片
                bundle.putString("ip", ip);
                bundle.putInt("port",port);
                //将数据包保存到intent中
                intent.putExtras(bundle);
                //设置返回的结果码，并返回调用该Activity的Activity
                setResult(0x11, intent);
                //关闭当前Activity
                finish();

                break;
                //重置
            case R.id.btn_reset:
                editTextIp.setText("");
                editTextPort.setText("");
                break;
        }

    }

    /**
     * 保存key和shuju
     * @param key
     * @param data
     */
    private void save(String key, String data){
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putString(key, data);
        editor.apply(); // 或者 // editor.commit(); apply会刷新内存中的值
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SetipActivity.this, "ip和端口已经更新", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //获取key对应的value
    private String get(String key) {
        return mSharedPreference.getString(key, null);
    }


    /**
     * 通过网络接口取
     * @return
     */
    private static String getNewMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /**
     * 发送消息
     *
     * @param ip
     * @param port
     * @param msg
     */
    private void sendMsg(final String ip, final int port, final String msg) {

        new Thread() {
            @Override
            public void run() {

                Socket socket = null;

                try {
                    SocketAddress saAdd = new InetSocketAddress(ip.trim(), port);
                    socket = new Socket();
                    socket.connect(saAdd, 1000);


                    final OutputStream outputStream = socket.getOutputStream();
                    // 将String转换成byte[]传输数据，使用UTF-8编码，服务端也使用UTF-8转换，支持中文
                    outputStream.write(msg.getBytes(StandardCharsets.UTF_8));

                    outputStream.close();
                } catch (UnknownHostException e) {
                    sendStatus("未知IP");
                } catch (SocketTimeoutException e) {
                    sendStatus("连接超时");
                } catch (IOException e) {
                    sendStatus("发送失败");
                    //e.printStackTrace();
                } catch (Exception e) {
                    sendStatus("发送失败");
                } finally {
                    try {
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }
    //用Toast显示状态
    private void sendStatus(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SetipActivity.this, status, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
