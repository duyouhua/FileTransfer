package com.scut.filetransfer.activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.scut.filetransfer.R;
import com.scut.filetransfer.adapter.AdapterManager;
import com.scut.filetransfer.application.BluetoothApplication;
import com.scut.filetransfer.entity.MyMenuItem;
import com.scut.filetransfer.entity.TouchObject;
import com.scut.filetransfer.listener.DeviceListCCMenuListener;
import com.scut.filetransfer.listener.SearchDeviceBtnClickListener;
import com.scut.filetransfer.listener.SelectFileBtnClickListener;
import com.scut.filetransfer.listener.SetVisibleBtnClickListener;
import com.scut.filetransfer.receiver.PairStateChangeReceiver;

public class PageBlueTooth extends Fragment {
	
	public static final String SEND_FILE_NAME = "sendFileName";
	public static final int RESULT_CODE = 100; // ѡ���ļ� ������
	public static final int REQUEST_CODE = 101; // ѡ���ļ� ������
	public static final int REQUEST_ENABLE = 10000; // ������ ������
	
	
	
	private BluetoothApplication mApplication;
	private AdapterManager mAdapterManager; // Adapter������
	private TouchObject mTouchObject; // ��ǰ��������
	
	private PairStateChangeReceiver mPairStateChangeReceiver; // ���״̬�ı�㲥������
	private BluetoothSocket socket; // ��������socket
	private Handler mOthHandler; // �����߳�Handler
	private SearchDeviceBtnClickListener mSearchDeviceBtnClickListener; // �����豸��ť������

	
	
	ListView mDeviceListView;
	TextView mSendFileNameTV;
	TextView mSearchBtnText;
	LinearLayout mSetVisibleBtn;
	LinearLayout mSearchDeviceBtn;
	LinearLayout mSelectFileBtn;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.page_bluetooth, container, false);
		
		mDeviceListView = (ListView) view.findViewById(R.id.deviceListView);
		mSetVisibleBtn = (LinearLayout) view.findViewById(R.id.setDeviceVisibleBtn);
		mSearchDeviceBtn = (LinearLayout) view.findViewById(R.id.searchDeviceBtn);
		mSelectFileBtn = (LinearLayout) view.findViewById(R.id.cancelSearchBtn);
		mSendFileNameTV = (TextView) view.findViewById(R.id.sendFileTV);
		mSearchBtnText = (TextView) view.findViewById(R.id.searchBtnText);
		
		mApplication = BluetoothApplication.getInstance();
		mTouchObject = mApplication.getTouchObject();
		// ʵ����Adapter�����������õ�Application
		mAdapterManager = new AdapterManager(getActivity());
		mApplication.setAdapterManager(mAdapterManager);
		mDeviceListView.setAdapter(mAdapterManager.getDeviceListAdapter());
		mSearchDeviceBtnClickListener = new SearchDeviceBtnClickListener(this);
		
		mDeviceListView.setOnCreateContextMenuListener(new DeviceListCCMenuListener(mDeviceListView));
		mSetVisibleBtn.setOnClickListener(new SetVisibleBtnClickListener(this));
		mSearchDeviceBtn.setOnClickListener(mSearchDeviceBtnClickListener);
		mSelectFileBtn.setOnClickListener(new SelectFileBtnClickListener(this));
		
		return view;
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("PageBlueTooth", requestCode+" "+resultCode);
		if (requestCode == REQUEST_ENABLE) {
			// ����Ϊ "������"
			if (resultCode == Activity.RESULT_OK) {
				// �������ɹ�
				mSearchDeviceBtnClickListener.beginDiscovery();
			} else {
				// ������ʧ��
				Toast.makeText(getActivity(), "������ʧ�ܣ�", Toast.LENGTH_LONG).show();
			}
		} else if (resultCode == RESULT_CODE) {
			// ����Ϊ "ѡ���ļ�"
				// ȡ��ѡ����ļ���
				String sendFileName = data.getStringExtra(SEND_FILE_NAME);
				Log.e("PageBlueTooth", sendFileName);
				mSendFileNameTV.setText(sendFileName);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getGroupId() == MyMenuItem.MENU_GROUP_DEVICE) {
			switch (item.getItemId()) {
			case MyMenuItem.MENU_ITEM_PAIR_ID: // ���
				doPair();
				break;

			case MyMenuItem.MENU_ITEM_SEND_ID: // �����ļ�
				doSendFileByBluetooth();
				break;

			default:
				break;
			}
		}
		return true;
	}
	
	/**
	 * ���
	 */
	private void doPair() {
		if (mTouchObject.bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
			// δ����豸���
			if (null == mPairStateChangeReceiver) {
				mPairStateChangeReceiver = new PairStateChangeReceiver(getActivity());
			}
			// ע���豸���״̬�ı������
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
			getActivity().registerReceiver(mPairStateChangeReceiver, intentFilter);
			if (null == mOthHandler) {
				HandlerThread handlerThread = new HandlerThread("other_thread");
				handlerThread.start();
				mOthHandler = new Handler(handlerThread.getLooper());
			}
			mOthHandler.post(new Runnable() {

				@Override
				public void run() {
					initSocket(); // ȡ��socket
					try {
						socket.connect(); // �������
						// mAdapterManager.updateDeviceAdapter();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			// �Ѿ�����豸���
			Toast.makeText(getActivity(), "���豸����ԣ������ظ�������",
					Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * ͨ�����������ļ�
	 */
	private void doSendFileByBluetooth() {
		// ȡ���ļ�ȫ·��
		String filePath = mSendFileNameTV.getText().toString().trim();
		if (!filePath.equals("null")) {
			if (null == mOthHandler) {
				HandlerThread handlerThread = new HandlerThread("other_thread");
				handlerThread.start();
				mOthHandler = new Handler(handlerThread.getLooper());
			}
			mOthHandler.post(new Runnable() {

				@Override
				public void run() {
					//��׿4.0�Ժ���÷�
					 Intent intent = new Intent(Intent.ACTION_SEND);
					 intent.setType("image/*");
					 intent.setType("video/*");
					 intent.setType("audio/*");
					 intent.setType("text/*");
					 intent.setType("application/*");
					 intent.setType("message/*");
					 intent.setType("x-world/*");
					 intent.setComponent(new ComponentName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity"));
					 String filePath = mSendFileNameTV.getText().toString().trim();
					 intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
					 startActivity(intent);
					 
					try {
						if (null != socket) {
							socket.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
		} else {
			Toast.makeText(getActivity(), "��ѡ��Ҫ���͵��ļ�!",
					Toast.LENGTH_LONG).show();
		}
	}
	
	
	/**
	 * ȡ��BluetoothSocket
	 */
	private void initSocket() {
		BluetoothSocket temp = null;
		try {
			Method m = mTouchObject.bluetoothDevice.getClass().getMethod(
					"createRfcommSocket", new Class[] { int.class });
			temp = (BluetoothSocket) m.invoke(mTouchObject.bluetoothDevice, 1);
			// ������� ֱ�Ӹ�ֵ��socket,��socket�������ܳ����쳣�� Ҫͨ���м����temp��ֵ��socket
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		socket = temp;
	}
	
	/**
	 * �ı䰴ť��ʾ����
	 */
	public void changeSearchBtnText() {
		mSearchBtnText.setText("��������");
	}
	
}
