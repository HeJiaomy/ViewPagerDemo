package com.example.viewpagerdemo;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    ViewPager viewPager;
    List<ImageView> imageViewList;
    LinearLayout pointLayout;
    View pointView;
    LinearLayout.LayoutParams layoutParams;
    String[] contentDescs;
    TextView descText;
    int lastPosition;
    boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化布局
        initView();
        //model数据
        initData();
        //Controller控制器
        initAdapter();

        //开启轮询
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("ViewPager当前位置", viewPager.getCurrentItem() + "");
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    private void initView() {
        viewPager = findViewById(R.id.view_pager);
//        viewPager.setOffscreenPageLimit(1);
        pointLayout = findViewById(R.id.point_layout);
        viewPager.addOnPageChangeListener(this);
        descText = findViewById(R.id.desc_text);
    }

    private void initData() {
        //图片资源
        int[] imageResIds = {R.mipmap.a, R.mipmap.b, R.mipmap.c, R.mipmap.d, R.mipmap.e};
        // 文本描述
        contentDescs = new String[]{
                "巩俐不低俗，我就不能低俗",
                "扑树又回来啦！再唱经典老歌引万人大合唱",
                "揭秘北京电影如何升级",
                "乐视网TV版大派送",
                "热血屌丝的反杀"
        };

        //初始化要展示的ImageView
        imageViewList = new ArrayList<>();
        ImageView imageView;
        for (int i = 0; i < imageResIds.length; i++) {
            imageView = new ImageView(this);
//            imageView.setImageResource(imageResIds[i]);
            imageView.setBackgroundResource(imageResIds[i]);
            imageViewList.add(imageView);

            pointView = new View(this);
            pointView.setBackgroundResource(R.drawable.selector_bg_point);
            layoutParams = new LinearLayout.LayoutParams(8, 8);
            if (i != 0) {
                layoutParams.leftMargin = 20; //设置左边距为10
            }
            pointView.setEnabled(false);
            pointLayout.addView(pointView, layoutParams);
        }
    }

    private void initAdapter() {
        pointLayout.getChildAt(0).setEnabled(true); //设置选中第一个
        descText.setText(contentDescs[0]);
        viewPager.setAdapter(new MyAdapter());
        viewPager.setCurrentItem(5000000);  //设置当前为某一个位置，让ViewPager可以向左滑动
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //条目被选中时调用

        int newPosition = position % imageViewList.size();
        descText.setText(contentDescs[newPosition]);
//        for (int i=0; i<pointLayout.getChildCount();i++){
//            View childAt= pointLayout.getChildAt(position);
//            childAt.setEnabled(position== i);
//        }
        pointLayout.getChildAt(newPosition).setEnabled(true);
        pointLayout.getChildAt(lastPosition).setEnabled(false);
        lastPosition = newPosition;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        //3.指定复用的判断逻辑
        @Override
        public boolean isViewFromObject(View view, Object object) {
            //当滑到新的条目时，又返回来,View是否可以被复用
            return view == object;
        }

        //1.返回要显示的条目内容，创建条目
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.e("instantiateItem初始化", position + "");
            //container 容器：ViewPager
            //position 位置：当前要显示条目的位置

            int newPosition = position % imageViewList.size();
            ImageView imageView = imageViewList.get(newPosition);
            container.addView(imageView);
            return imageView;   //把View对象返回给框架，适配器。必须重写
        }

        //2.销毁条目
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.e("destroyItem销毁", position + "");
            container.removeView((View) object);
        }
    }
}
