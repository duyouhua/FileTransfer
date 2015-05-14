package com.scut.filetransfer.application;
import com.scut.filetransfer.adapter.AdapterManager;
import com.scut.filetransfer.entity.TouchObject;

import android.app.Application;

public class FileTransferApplication extends Application {
	/**
	 * Applicationʵ��--����ģʽ
	 */
	private static FileTransferApplication application;
	
	/**
	 * AdapterManagerʵ��
	 */
	private AdapterManager mAdapterManager;
	
	/**
	 * ��ǰ�����Ķ���ʵ��
	 */
	private TouchObject mTouchObject;

	@Override
	public void onCreate() {
		super.onCreate();
		if(application == null){
			application = this;
		}
		mTouchObject = new TouchObject();
		mAdapterManager = new AdapterManager(getApplicationContext());
	}
	
	/**
	 * ��ȡApplicationʵ��
	 */
	public static FileTransferApplication getInstance(){
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