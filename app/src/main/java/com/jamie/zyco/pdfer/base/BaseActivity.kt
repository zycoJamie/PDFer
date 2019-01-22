package com.jamie.zyco.pdfer.base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {

    protected lateinit var dataBinding: T

    abstract fun initView()

    abstract fun initData()

    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, getLayoutId()) as T
    }

    protected fun <T : ViewModel> obtainViewModel(activity: AppCompatActivity, clazz: Class<T>) = ViewModelProviders.of(activity).get(clazz)

    protected fun <V : AppCompatActivity> go2Activity(context: Context, clazz: Class<V>) {
        startActivity(Intent(context, clazz))
    }

    protected fun go2Activity(intent: Intent) {
        startActivity(intent)
    }

    protected fun toast(msg: String, length: Int) {
        when (length) {
            Toast.LENGTH_SHORT -> {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
            Toast.LENGTH_LONG -> {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
        }
    }
}