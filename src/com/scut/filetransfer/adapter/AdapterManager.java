package com.scut.filetransfer.adapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.scut.filetransfer.R;
import com.scut.filetransfer.util.FileUtil;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;

/**
 * ������������
 *
 */
public class AdapterManager {
	private Context mContext;
	private DeviceListAdapter mDeviceListAdapter;   //�豸�б� adapter
	private FileListAdapter mFileListAdapter;    //�ļ��б�adapter
	private List<BluetoothDevice> mDeviceList;   //�豸����
	private List<File> mFileList;    //�ļ�����
	private Handler mainHandler;   //���߳�Handler
	
	public AdapterManager(Context context){
		this.mContext = context;
	}
	
	/**
	 * ȡ���豸�б�adapter
	 */
	public DeviceListAdapter getDeviceListAdapter(){
		if(null == mDeviceListAdapter){
			mDeviceList = new ArrayList<BluetoothDevice>();
			mDeviceListAdapter = new DeviceListAdapter(mContext, mDeviceList, R.layout.device_list_item);
		}
		
		return mDeviceListAdapter;
	}
	
	/**
	 * ȡ���ļ��б�adapter
	 */
	public FileListAdapter getFileListAdapter(){
		if(null == mFileListAdapter){
			mFileList = new ArrayList<File>();
			mFileListAdapter = new FileListAdapter(mContext, mFileList, R.layout.file_list_item);
		}
		
		return mFileListAdapter;
	}
	
	/**
	 * �����豸�б�listView
	 */
	public void updateDeviceAdapter(){
				mDeviceListAdapter.notifyDataSetChanged();
	}
	
	/**
	 * ����豸�б�
	 */
	public void clearDevice(){
		if(null != mDeviceList){
			mDeviceList.clear();
		}
	}
	
	/**
	 * ����豸
	 */
	public void addDevice(BluetoothDevice bluetoothDevice){
		mDeviceList.add(bluetoothDevice);
	}
	
	/**
	 * �����豸��Ϣ
	 */
	public void changeDevice(int listId, BluetoothDevice bluetoothDevice){
		mDeviceList.remove(listId);
		mDeviceList.add(listId, bluetoothDevice);
	}
	
	/**
	 * �����ļ��б�
	 * @param path
	 */
	public void updateFileListAdapter(String path){
		mFileList.clear();
		mFileList.addAll(FileUtil.getFileList(path));
		if(null == mainHandler){
			mainHandler = new Handler(mContext.getMainLooper());
		}
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				mFileListAdapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * ȡ���豸�б�
	 */
	public List<BluetoothDevice> getDeviceList() {
		return mDeviceList;
	}

}