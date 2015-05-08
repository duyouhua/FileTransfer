package com.wechat.filetransfer.activity;

import java.util.ArrayList;
import java.util.List;

import com.wechat.filetransfer.R;
import com.wechat.filetransfer.adapter.ReceiveAdapter;
import com.wechat.filetransfer.bean.FileInfo;
import com.wechat.filetransfer.db.FileInfoDAO;
import com.wechat.filetransfer.db.FileInfoDAOImpl;
import com.wechat.filetransfer.services.DownloadService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PageLoad extends Fragment {

	private static List<FileInfo> list = null;
	private static ReceiveAdapter adapter = null;
	private static FileInfoDAO fileInfoDAO = null;
	private ListView lv_main = null;
	private IntentFilter filter = null;

	private static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			adapter.notifyDataSetChanged();
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page_load, container, false);
		// �ȴ����ݿ����ȡ�ļ���Ϣ���ϣ�����δ������ĺ����������
		fileInfoDAO = new FileInfoDAOImpl(getActivity());

		list = new ArrayList<FileInfo>();
		list.addAll(fileInfoDAO.getAllFileInfos());
		// ���ļ���Ϣ���Ϸ��͸�adapter
		lv_main = (ListView) view.findViewById(R.id.lv_main);
		adapter = new ReceiveAdapter(getActivity(), list, R.layout.item_load,
				lv_main);
		lv_main.setAdapter(adapter);
		// ע��㲥������
		filter = new IntentFilter();
		filter.addAction(DownloadService.ACTION_UPDATE);
		getActivity().registerReceiver(receiver, filter);
		return view;
	}

	public void unregisterReceiver() {
		getActivity().unregisterReceiver(receiver);
	}

	public static void ReceiveFileFromSender(FileInfo fileInfo) {
		// ���ļ����ڣ���δ���꣬����жϵ��������������load_item
		if (fileInfoDAO.isExists(fileInfo.getIP(), fileInfo.getPort(),
				fileInfo.getFileName())) {
			fileInfoDAO.updateFileInfo(fileInfo.getIP(), fileInfo.getPort(),
					fileInfo.getFileName(), "��������");
			handler.sendMessage(new Message());
		} else {
			// ���ļ������ڣ�������һ��load_item�������ء�
			list.add(fileInfo);
			handler.sendMessage(new Message());
		}
	}

	
	/**
	 * ���ݴ����fileInfo���鿴���ݿ��Ƿ��Ѿ��иü�¼
	 * �����ļ��Ѿ�������Ҵ�����ɣ��򷵻�true
	 * �����������ϵ������͵�һ�δ��䣩�򷵻�false
	 * @param fileInfo
	 * @return
	 */
	public static boolean isExistAndDoneFile(FileInfo fileInfo) {

		// ���ļ�ͬ����������ɣ��򷵻�true
		if (fileInfoDAO.isExists(fileInfo.getIP(), fileInfo.getPort(),fileInfo.getFileName())) {
			FileInfo fileInfo2 = fileInfoDAO.getFileInfo(fileInfo.getIP(), fileInfo.getPort(), fileInfo.getFileName());
			if (fileInfo2.getFinished() == 100) {
				return true;
			}else {
				return false;
			}
		} else {
			//���Ͷ˴򿪶˿ڼ�������
			return false;
		}


	}

	// �㲥��������
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
				FileInfo fileInfo = new FileInfo();
				fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
				int finished = fileInfo.getFinished();
				String fileName = fileInfo.getFileName();
				int position = fileInfo.getId();
				list.get(position).setFinished(finished);
				list.get(position).setFileName(fileName);
				adapter.myNotifyDataSetChanged(position);
				// adapter.notifyDataSetChanged();
			}
		}
	};
}
