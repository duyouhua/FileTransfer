package com.scut.filetransfer.receiver;
import com.scut.filetransfer.adapter.AdapterManager;
import com.scut.filetransfer.application.FileTransferApplication;
import com.scut.filetransfer.entity.TouchObject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * ���״̬�ı������
 * 
 *
 */
public class PairStateChangeReceiver extends BroadcastReceiver {
	private FileTransferApplication mApplication;
	private Activity mActivity;
	private AdapterManager mAdapterManager;
	private TouchObject mTouchObject;
	
	public PairStateChangeReceiver(Activity activity){
		this.mApplication = FileTransferApplication.getInstance();
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
			Log.i("PairStateChangeReceiver", "BluetoothDevice.ACTION_BOND_STATE_CHANGED");
			mActivity.unregisterReceiver(this);
		}
	}

}
