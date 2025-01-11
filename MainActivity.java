package com.bhd.helloworld;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity {

    private Button first;
    private Button second;
    private Button third;
    private Button fourth;


    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main, null);

        first = view.findViewById(R.id.first);
        second = view.findViewById(R.id.second);
        third = view.findViewById(R.id.third);
        fourth = view.findViewById(R.id.fourth);

        return view;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //初始化组件
       // radioGroup1 = findViewById(R.id.radio_group1);

        //radioGroup1.setOnCheckedChangeListener(new MyOnCheckedChangeListener());


        //获取Fragment 列表
        //List<Fragment> mFragments = initFragment();



        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建Intent对象
                Intent intent1 = new Intent(MainActivity.this, BeamActivity.class);
                //启动intent对应的Activity
                startActivity(intent1);
            }
        });

        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建Intent对象
                Intent intent2 = new Intent(MainActivity.this, VentilationActivity.class);
                //启动intent对应的Activity
                startActivity(intent2);
            }
        });

        third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建Intent对象
                Intent intent3 = new Intent(MainActivity.this, ApplianceActivity.class);
                //启动intent对应的Activity
                startActivity(intent3);
            }
        });

        fourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建Intent对象
                Intent intent4 = new Intent(MainActivity.this, SecurityActivity.class);
                //启动intent对应的Activity
                startActivity(intent4);
            }
        });

    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //判断是否为待处理的结果
        if (requestCode == 0x11 && resultCode == 0x11) {
            //获取传递的数据包
            Bundle bundle = data.getExtras();

            //获取选择的头像ID
            port = bundle.getInt("port");
            ip = bundle.getString("ip");
        }
    }*/

    /*private List<Fragment> initFragment(){
        FirstFragment f1 = new FirstFragment();
        SecondFragment f2 = new SecondFragment();
        ThirdFragment f3 = new ThirdFragment();
        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(f1);
        mFragments.add(f2);
        mFragments.add(f3);
        return mFragments;
    }*/

    /*class MyPageAdapter extends FragmentStatePagerAdapter {


        private List<Fragment> mFragments;

        public MyPageAdapter(FragmentManager fm, List<Fragment> mFragments) {
            super(fm);
            this.mFragments = mFragments;
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }


    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int currentIndex = 0;
            switch (checkedId) {
                case R.id.first:
                    currentIndex = 0;
                    break;
                case R.id.second:
                    currentIndex = 1;
                    break;
                case R.id.third:
                    currentIndex = 2;
                    break;
                case R.id.fourth:
                    currentIndex = 3;
                    break;

            }
        }
    }*/
}




