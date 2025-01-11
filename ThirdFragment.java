package com.bhd.helloworld;


import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * description: FirstFragment <br>
 * date: 2021/5/10 14:36 <br>
 * author: DaTao <br>
 * version: 1.0 <br>
 */
public class ThirdFragment extends Fragment {

    //xml存储ip和端口
    private SharedPreferences mSharedPreference;
    private static final String SP_TEST = "sp_test";
    private ImageView bathroomDpxs;
    //打开和关闭客厅灯按钮
    private Button btnOpenBathroom;
    private Button btnCloseBathroom;
    //ip和端口
    private String ip;
    private Integer port;
    //定时按钮和显示组件
    private Button timeSelectBathroom;
    private TextView txtTimeBathroom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.bathroom_fragment, null);

        //获取xml取值都西昂
        mSharedPreference = this.getActivity().getSharedPreferences(SP_TEST, Context.MODE_PRIVATE);

        btnOpenBathroom = view.findViewById(R.id.btn_open_bathroom);
        btnCloseBathroom = view.findViewById(R.id.btn_close_bathroom);
        bathroomDpxs = view.findViewById(R.id.bathroom_dpxs);

        timeSelectBathroom = view.findViewById(R.id.time_select_bathroom);
        txtTimeBathroom= view.findViewById(R.id.txt_time_bathroom);



        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ip = get("ip");
        String portStr = get("port");
        if(portStr!=null){
            port = Integer.parseInt(portStr);
        }else{
            port = 7016;
        }

        //开灯
        btnOpenBathroom.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                sendMsg(ip,port,"LVRMXXOPEN");
            }
        });
        //关灯
        btnCloseBathroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg(ip,port,"LVRMXXCLOSE");
            }
        });

        //定时
        timeSelectBathroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar= Calendar.getInstance(Locale.CHINA);
                showTimePickerDialog(getActivity(),  3, txtTimeBathroom, calendar);
            }
        });

    }

    private String get(String key){
        return mSharedPreference.getString(key, null);
    }

    public  void showTimePickerDialog(Activity activity, int themeResId, final TextView tv, Calendar calendar) {
        // Calendar c = Calendar.getInstance();
        // 创建一个TimePickerDialog实例，并把它显示出来
        // 解释一哈，Activity是context的子类
        new TimePickerDialog( activity,themeResId,
                // 绑定监听器
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tv.setText("Time set to：" + hourOfDay + "Hr(S)" + minute  + "min(s)");

                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR,hourOfDay);
                        c.set(Calendar.MINUTE,minute);
                        c.set(Calendar.SECOND,0);
                        c.set(Calendar.MILLISECOND,0);


                        final Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                sendMsg(ip,port,"LVRMXXOPEN");
                                System.out.println("Set time reached....");
                                timer.cancel();
                            }
                        },c.getTime());

                    }
                }
                // 设置初始时间
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                // true表示采用24小时制
                ,true).show();
    }

    private void sendMsg(final String ip, final int port, final String msg){

        new Thread(){
            @Override
            public void run() {

                Socket socket = null;

                try {
                    SocketAddress saAdd = new InetSocketAddress(ip.trim(), port);
                    socket = new Socket();
                    socket.connect(saAdd,1000);
//                    socket = new Socket(ip,port);

                    final OutputStream outputStream = socket.getOutputStream();
                    // 将String转换成byte[]传输数据，使用UTF-8编码，服务端也使用UTF-8转换，支持中文
                    outputStream.write(msg.getBytes(StandardCharsets.UTF_8));

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if("LVRMXXOPEN".equals(msg)){
                                bathroomDpxs.setImageResource(R.drawable.dp_light);
                                Toast.makeText(getActivity().getApplicationContext(), "Bathroom lights turned ON", Toast.LENGTH_SHORT).show();
                            }else{
                                bathroomDpxs.setImageResource(R.drawable.dp_dark);
                                Toast.makeText(getActivity().getApplicationContext(), "Bathroom lights turned OFF", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    outputStream.close();
                } catch (UnknownHostException e) {
                    sendStatus("Unknown IP");
                }catch (SocketTimeoutException e) {
                    sendStatus("Connection timed out");
                }catch (IOException e) {
                    sendStatus("Sending Failed");
                    //e.printStackTrace();
                }catch (Exception e) {
                    sendStatus("Sending Failed");
                }finally {
                    try {
                        if(socket != null) {
                            socket.close();
                        }
                    }catch (Exception e) {
                    }
                }
            }
        }.start();
    }
    //提示消息
    private void sendStatus(final String status){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity().getApplicationContext(), status, Toast.LENGTH_SHORT).show();
            }
        });
    }

}