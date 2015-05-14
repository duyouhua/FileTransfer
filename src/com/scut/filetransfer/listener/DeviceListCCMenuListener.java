package com.scut.filetransfer.listener;
import com.scut.filetransfer.R;
import com.scut.filetransfer.application.FileTransferApplication;
import com.scut.filetransfer.entity.MyMenuItem;
import com.scut.filetransfer.entity.TouchObject;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * ListViewԪ�س����¼�������
 *
 */
public class DeviceListCCMenuListener implements OnCreateContextMenuListener {
	private TouchObject mTouchObject;
	private ListView mDeviceListView;
	private Context mContext;
	
	public DeviceListCCMenuListener(ListView deviceListView){
		this.mDeviceListView = deviceListView;
		mTouchObject = FileTransferApplication.getInstance().getTouchObject();
		mContext = FileTransferApplication.getInstance().getApplicationContext();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		mTouchObject.clickDeviceItemId = info.position;
		mTouchObject.bluetoothDevice = (BluetoothDevice) mDeviceListView.getAdapter().getItem(info.position);
		menu.setHeaderTitle(mContext.getString(R.string.please_select_do_what));
		//��һ���һ��Ϊ ����ԡ�
		menu.add(MyMenuItem.MENU_GROUP_DEVICE, 
				 MyMenuItem.MENU_ITEM_PAIR_ID, 
				 MyMenuItem.MENU_ITEM_PAIR_ORDER, 
				 MyMenuItem.MENU_ITEM_PAIR_TITLE);
		//��һ��ڶ���Ϊ �������ļ���
		menu.add(MyMenuItem.MENU_GROUP_DEVICE, 
				 MyMenuItem.MENU_ITEM_SEND_ID, 
				 MyMenuItem.MENU_ITEM_SEND_ORDER, 
				 MyMenuItem.MENU_ITEM_SEND_TITLE);
	}

}
