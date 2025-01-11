package com.bhd.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class BeamActivity extends FragmentActivity {

    private RadioGroup radioGroup2;
    private ViewPager viewPager;
    private ImageView imageView;
    private String ip;
    private Integer port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beam_fragment);

        //初始化组件
        viewPager = findViewById(R.id.view_pager);
        radioGroup2 = findViewById(R.id.radio_group2);
        imageView = findViewById(R.id.setip_img);

        //获取Fragment 列表
        List<Fragment> mFragments = initFragment();
        //给组件添加 Fragment 列表
        viewPager.setAdapter( new BeamActivity.MyPageAdapter(getSupportFragmentManager(), mFragments) );

        //添加监听事件
        radioGroup2.setOnCheckedChangeListener(new BeamActivity.MyOnCheckedChangeListener());

        //给图片添加事件
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建Intent对象
                Intent intent = new Intent(BeamActivity.this, SetipActivity.class);
                //启动intent对应的Activity
                startActivityForResult(intent, 0x11);
            }
        });

    }

    @Override
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
    }

    /**
     * 初始化Fragment数据
     */
    private List<Fragment> initFragment(){
        FirstFragment f1 = new FirstFragment();
        SecondFragment f2 = new SecondFragment();
        ThirdFragment f3 = new ThirdFragment();
        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(f1);
        mFragments.add(f2);
        mFragments.add(f3);
        return mFragments;
    }

    /**
     * FragmentPagerAdapter:
     * FragmentPagerAdapter 继承自 PagerAdapter。
     * 相比通用的 PagerAdapter，该类更专注于每一页均为 Fragment 的情况。
     * 如文档所述，该类内的每一个生成的 Fragment 都将保存在内存之中，因此适用于那些相对静态的页，数量也比较少的那种；如果需要处理有很多页，并且数据动态性较大、占用内存较多的情况，
     * 应该使用FragmentStatePagerAdapter。
     *
     *
     * FragmentStatePagerAdapter:
     * FragmentStatePagerAdapter 和前面的 FragmentPagerAdapter 一样，是继承子 PagerAdapter。
     * 但是，和 FragmentPagerAdapter 不一样的是，正如其类名中的 'State' 所表明的含义一样，该 PagerAdapter 的实现将只保留当前页面，当页面离开视线后，就会被消除，
     * 释放其资源；而在页面需要显示时，生成新的页面(就像 ListView 的实现一样)。这么实现的好处就是当拥有大量的页面时，不必在内存中占用大量的内存。
     *
     */
    class MyPageAdapter extends FragmentStatePagerAdapter {


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


    /**
     * 按钮组选择的事件处理
     */
    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int currentIndex = 0;
            switch (checkedId){
                case R.id.one :
                    currentIndex = 0;
                    break;
                case R.id.two :
                    currentIndex = 1;
                    break;
                case R.id.three :
                    currentIndex = 2;
                    break;            }
            if( viewPager.getCurrentItem() != currentIndex ){
                viewPager.setCurrentItem(currentIndex);
            }
        }
    }



}


