package com.wechat.filetransfer.bluetooth.application;


import com.wechat.filetransfer.bluetooth.adapter.AdapterManager;
import com.wechat.filetransfer.bluetooth.entity.TouchObject;

import android.app.Application;

public class BluetoothApplication extends Application {
	/**
	 * Applicationʵ��
	 */
	private static BluetoothApplication application;
	
	/**
	 * 
	 */
	private AdapterManager mAdapterManager;
	
	/**
	 * ��ǰ�����Ķ���;
	 */
	private TouchObject mTouchObject;

	@Override
	public void onCreate() {
		super.onCreate();
		if(null == application){
			application = this;
		}
		mTouchObject = new TouchObject();
	}
	
	/**
	 * ��ȡApplicationʵ��
	 * @return
	 */
	public static BluetoothApplication getInstance(){
		return application;
	}

	public AdapterManager getAdapterManager() {
		return mAdapterManager;
	}

	public void setAdapterManager(AdapterManager adapterManager) {
		this.mAdapterManager = adapterManager;
	}

	public TouchObject getTouchObject() {
		return mTouchObject;
	}

	public void setTouchObject(TouchObject touchObject) {
		this.mTouchObject = touchObject;
	}

}
