package com.wechat.filetransfer.bluetooth.receiver;


import com.wechat.filetransfer.bluetooth.adapter.AdapterManager;
import com.wechat.filetransfer.bluetooth.application.BluetoothApplication;
import com.wechat.filetransfer.bluetooth.entity.TouchObject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * ���״̬�ı������
 * 
 *
 */
public class PairStateChangeReceiver extends BroadcastReceiver {
	private BluetoothApplication mApplication;
	private Activity mActivity;
	private AdapterManager mAdapterManager;
	private TouchObject mTouchObject;
	
	public PairStateChangeReceiver(Activity activity){
		this.mApplication = BluetoothApplication.getInstance();
		this.mActivity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
			//״̬�ı�
			if(null == mAdapterManager){
				mAdapterManager = mApplication.getAdapterManager();
			}
			if(null == mTouchObject){
				mTouchObject = mApplication.getTouchObject();
			}
			//ȡ��״̬�ı���豸�������豸�б���Ϣ �����״̬��
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			mAdapterManager.changeDevice(mTouchObject.clickDeviceItemId, device);
			mAdapterManager.updateDeviceAdapter();
			mActivity.unregisterReceiver(this);
		}
	}

}
