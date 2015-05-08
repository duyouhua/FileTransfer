package com.wechat.filetransfer.bluetooth.listener;

import com.wechat.filetransfer.activity.PageBlueTooth;
import com.wechat.filetransfer.bluetooth.adapter.AdapterManager;
import com.wechat.filetransfer.bluetooth.application.BluetoothApplication;
import com.wechat.filetransfer.bluetooth.receiver.ScanBluetoothReceiver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * �����豸��ť������
 * 
 * @author 210001001427
 * 
 */
public class SearchDeviceBtnClickListener implements OnClickListener {
	private PageBlueTooth mPageBlueTooth;
	private AdapterManager mAdapterManager;

	private BluetoothAdapter mBluetoothAdapter;
	private ScanBluetoothReceiver mScanBluetoothReceiver; // ����ɨ�������
	private AlertDialog mAlertDialog; // ȷ�������� dialog
	private ProgressDialog mProgressDialog;

	public SearchDeviceBtnClickListener(Fragment fragment) {
		this.mPageBlueTooth = (PageBlueTooth) fragment;
		this.mAdapterManager = BluetoothApplication.getInstance()
				.getAdapterManager();
	}

	@Override
	public void onClick(View v) {
		// ��������豸�б�
		mAdapterManager.clearDevice();
		mAdapterManager.updateDeviceAdapter();
		if (null == mBluetoothAdapter) {
			// ȡ������������
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		if (!mBluetoothAdapter.isEnabled()) {
			// ����δ��, ������
			if (null == mAlertDialog) {
				mAlertDialog = new AlertDialog.Builder(
						mPageBlueTooth.getActivity()).setTitle("������")
						.setPositiveButton("ȷ��", new Dialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// �������󣬴�����
								Intent startBluetoothIntent = new Intent(
										BluetoothAdapter.ACTION_REQUEST_ENABLE);
								mPageBlueTooth.getActivity()
										.startActivityForResult(
												startBluetoothIntent,
												PageBlueTooth.REQUEST_ENABLE);
							}

						}).setNeutralButton("ȡ��", new Dialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mAlertDialog.dismiss();
							}

						}).create();
			}
			mAlertDialog.setMessage("����δ�򿪣��Ƿ�򿪣�");
			mAlertDialog.show();
			;
		} else {
			//�����Ѵ򿪣� ��ʼ�����豸
			beginDiscovery();
			Log.i("BluetoothDemo", "begin");
		}
	}

	/**
	 * ��ʼ�����豸...
	 */
	public void beginDiscovery() {
		if (null == mProgressDialog) {
			mProgressDialog = new ProgressDialog(mPageBlueTooth.getActivity());
			mProgressDialog.setMessage("�����豸��...");
		}
		mProgressDialog.show();
		//ע������ɨ�������
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		if (null == mScanBluetoothReceiver) {
			mScanBluetoothReceiver = new ScanBluetoothReceiver(mPageBlueTooth,
					mAdapterManager, mProgressDialog);
		}
		mPageBlueTooth.getActivity().registerReceiver(mScanBluetoothReceiver,
				intentFilter);
		mBluetoothAdapter.startDiscovery();
	}

}
