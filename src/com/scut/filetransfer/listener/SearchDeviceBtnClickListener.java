package com.scut.filetransfer.listener;

import com.scut.filetransfer.R;
import com.scut.filetransfer.activity.PageBlueTooth;
import com.scut.filetransfer.adapter.AdapterManager;
import com.scut.filetransfer.application.FileTransferApplication;
import com.scut.filetransfer.receiver.ScanBluetoothReceiver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
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
 * 
 */
public class SearchDeviceBtnClickListener implements OnClickListener {
	private Context mContext;
	private PageBlueTooth mPageBlueTooth;
	private AdapterManager mAdapterManager;

	private BluetoothAdapter mBluetoothAdapter;
	private ScanBluetoothReceiver mScanBluetoothReceiver; // ����ɨ�������
	private AlertDialog mAlertDialog; // ȷ�������� dialog
	private ProgressDialog mProgressDialog;

	public SearchDeviceBtnClickListener(Fragment fragment) {
		this.mPageBlueTooth = (PageBlueTooth) fragment;
		this.mAdapterManager = FileTransferApplication.getInstance().getAdapterManager();
		this.mContext = FileTransferApplication.getInstance().getApplicationContext();
	}

	@Override
	public void onClick(View v) {
		// ��������豸�б�
		mAdapterManager.clearDevice();
		mAdapterManager.updateDeviceAdapter();
		if (null == mBluetoothAdapter) {
			// ȡ��ϵͳ����������
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		if (!mBluetoothAdapter.isEnabled()) {
			// ����δ��, ��������ʾ��
			if (null == mAlertDialog) {
				mAlertDialog = new AlertDialog.Builder(mPageBlueTooth.getActivity())
						.setPositiveButton(mContext.getString(R.string.ensure), new Dialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// �������󣬴�����
								Intent startBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
								mPageBlueTooth.startActivityForResult(startBluetoothIntent,PageBlueTooth.REQUEST_ENABLE);
							}

						}).setNeutralButton(mContext.getString(R.string.cancel), new Dialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,int which) {
								mAlertDialog.dismiss();
							}

						}).create();
			}
			mAlertDialog.setTitle(mContext.getString(R.string.open_bluetooth));
			mAlertDialog.setMessage(mContext.getString(R.string.is_open_bluetooth));
			mAlertDialog.show();
		} else {
			// �����Ѵ򿪣� ��ʼ�����豸
			Log.i("SearchDeviceBtnClickListener", "beginDiscovery()");
			beginDiscovery();
		}
	}

	/**
	 * ��ʼ�����豸...
	 */
	public void beginDiscovery() {
		if (null == mProgressDialog) {
			mProgressDialog = new ProgressDialog(mPageBlueTooth.getActivity());
			mProgressDialog.setMessage(mContext.getString(R.string.searching_device));
		}
		mProgressDialog.show();
		// ע������ɨ�������
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		if (null == mScanBluetoothReceiver) {
			mScanBluetoothReceiver = new ScanBluetoothReceiver(mContext,mPageBlueTooth,mAdapterManager, mProgressDialog);
		}
		mPageBlueTooth.getActivity().registerReceiver(mScanBluetoothReceiver,intentFilter);
		//��ʼ����
		mBluetoothAdapter.startDiscovery();
	}

}