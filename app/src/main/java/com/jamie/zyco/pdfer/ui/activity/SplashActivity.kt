package com.jamie.zyco.pdfer.ui.activity

import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.jamie.zyco.pdfer.MainActivity
import com.jamie.zyco.pdfer.R
import com.jamie.zyco.pdfer.base.BaseActivity
import com.jamie.zyco.pdfer.databinding.ActivitySplashBinding
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    override fun initView() {
        mSplashIconIv.alpha = 0f
        mSplashIconIv.animate().alphaBy(1f).alpha(1f).duration = 2500
        mSplashIconIv.animate().start()
    }

    override fun initData() {
        val handler = Handler()
        handler.postDelayed({
            go2Activity(this@SplashActivity, MainActivity::class.java)
        }, 2500)
    }

    override fun getLayoutId() = R.layout.activity_splash
}