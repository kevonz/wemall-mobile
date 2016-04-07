package com.inuoer.wemall;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class ShakeActivity extends Activity {
    //摇一摇上下两张图片
    private ImageView imageView_main_logoup;
    private ImageView imageView_main_logodown;
    //声音池
    private SoundPool soundPool;
    private int soundId;
    //震动类
    private Vibrator vibrator;
    //传感器管理类
    SensorManager mSensorManager;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
//                popShopWindow();
            }
        }
    };
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (x>=15 || y>=15 || z>=15) {

//                if (mDialog != null){
//                    mDialog.dismiss();
//                }
                startAnimationByJava();
                startSound();
                startVibrator();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        initView();
        initSensorManager();
        initVibrator();
        initSoundPool();
    }

    private void initView() {
        imageView_main_logoup = (ImageView) findViewById(R.id.imageView_main_logoup);
        imageView_main_logodown = (ImageView) findViewById(R.id.imageView_main_logodown);
    }

    public void click(View view){
        if (view.getId() == R.id.button_back){
            ShakeActivity.this.finish();
        }
    }

    /**
     * 加速度传感器的初始化,设置监听器
     */
    private void initSensorManager(){
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(sensorEventListener, sensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * 初始化声音池,不同版本不同方法
     */
    private void initSoundPool(){
        //如果系统版本21执行这一段代码
        if (Build.VERSION.SDK_INT >= 21) {

        }else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        soundId = soundPool.load(this, R.raw.awe, 1);
    }
    //初始化震动加速度传感器
    private void initVibrator(){
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * 开始震动
     */
    private void startVibrator(){
        // 定义震动
        // 只有1个参数的时候，第一个参数用来指定振动的毫秒数。
        // 要传递2个参数的时候，第1个参数用来指定振动时间的样本，第2个参数用来指定是否需要循环，-1为不重复，非-1则从pattern的指定下标开始重复
        // 振动时间的样本是指振动时间和等待时间的交互指定的数组，即节奏数组。
        // ※下面的例子，在程序起动后等待3秒后，振动1秒，再等待2秒后，振动5秒，再等待3秒后，振动1秒
        // long[] pattern = {3000, 1000, 2000, 5000, 3000, 1000};
        // 震动节奏分别为：OFF/ON/OFF/ON…
        vibrator.vibrate(new long[]{500, 200, 500, 200}, -1);
    }

    /**
     * 开始播放声音
     */
    private void startSound(){
        soundPool.play(soundId, 1, 1, 1, 0, 1);
    }


    /**
     * 定义摇一摇动画动画,java动画
     */
    public void startAnimationByJava() {
        AnimationSet animup = new AnimationSet(true);
        TranslateAnimation mup0 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -0.5f);
        mup0.setDuration(1000);
        TranslateAnimation  mup1 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, +0.5f);
        mup1.setDuration(1000);

        //延迟执行1秒
        mup1.setStartOffset(1000);
        animup.addAnimation( mup0);
        animup.addAnimation( mup1);
        //上图片的动画效果的添加
        imageView_main_logoup.startAnimation(animup);


        AnimationSet animdn = new AnimationSet(true);
        TranslateAnimation mdn0 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, +0.5f);
        mdn0.setDuration(1000);
        TranslateAnimation  mdn1 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -0.5f);
        mdn1.setDuration(1000);

        //延迟执行1秒
        mdn1.setStartOffset(1000);
        animdn.addAnimation(mdn0);
        animdn.addAnimation(mdn1);
        //下图片动画效果的添加
        imageView_main_logodown.startAnimation(animdn);

        //动画的监听，当动画结束后在里面进行操作
        animdn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束后开始发送消息，显示菜单窗体
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(sensorEventListener);
            mSensorManager = null;
        }
    }
}