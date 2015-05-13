package com.scut.filetransfer.receiver;
import com.scut.filetransfer.activity.PageBlueTooth;
import com.scut.filetransfer.adapter.AdapterManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

/**
 * ����ɨ�������
 * @author 210001001427
 *
 */
public class ScanBluetoothReceiver extends BroadcastReceiver {
	private PageBlueTooth mPageBlueTooth;
	private AdapterManager mAdapterManager;
	private ProgressDialog mProgressDialog;
	private boolean isFirstSearch = true;
	
	public ScanBluetoothReceiver(Fragment fragment, AdapterManager adapterManager, ProgressDialog progressDialog){
		this.mPageBlueTooth = (PageBlueTooth) fragment;
		this.mAdapterManager = adapterManager;
		this.mProgressDialog = progressDialog;
	}

	@Override
	public void onReceive(Context context, final Intent intent) {
		if(intent.getAction().equals(BluetoothDevice.ACTION_FOUND)){
			//ɨ�赽����
			//ȡ��ɨ�赽����������ӵ��豸�б������б�
			BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			mAdapterManager.addDevice(bluetoothDevice);
			mAdapterManager.updateDeviceAdapter();
		}else if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
			//ɨ���豸����
			Log.i("BluetoothDemo", "over");
			mProgressDialog.dismiss();
			if(mAdapterManager.getDeviceList().size() == 0){
				//ɨ�赽���豸��Ϊ0
				Toast.makeText(mPageBlueTooth.getActivity(), "û���ҵ����������豸��", Toast.LENGTH_LONG).show();
			}
			if(isFirstSearch){
				//��һ�β��Һ� ���ð�ť��ʾ�ı�Ϊ "���²���"
				mPageBlueTooth.changeSearchBtnText();
				isFirstSearch = false;
			}
			//ȡ������
			mPageBlueTooth.getActivity().unregisterReceiver(this);
		}
	}

}
