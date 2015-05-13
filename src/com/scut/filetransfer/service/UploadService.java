package com.scut.filetransfer.service;
import com.scut.filetransfer.bean.FileInfo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UploadService extends Service{


	public static final String ACTION_START  = "ACTION_UPLOAD_START";
	public static final String ACTION_STOP  = "ACTION_STOP";
	public static final String ACTION_UPDATE  = "ACTION_UPDATE";
	private static int index = 0;
	private String filePath;
	private static final int MAX_NUM=100;
	private UploadTask[] uploadTasks = new UploadTask[MAX_NUM];
	private FileInfo fileInfo = null;
	

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		for (int i = 0; i<MAX_NUM; i++) {
			if (uploadTasks[i] != null) {
				uploadTasks[i].uploadThread.onDestroySocketConnection();
			}
		}
		super.onDestroy();
	}

	//����Service
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//��������ɱ��ʱ�����Զ�����onStartCommand����,��ֹ��ָ���쳣
		if (intent != null) {
			//���������Ķ�����START
			if (ACTION_START.equals(intent.getAction())) {
				//fileID�������±���ͬ
				filePath = intent.getStringExtra("filePath");
				fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
				//��ʼ�ϴ�
				System.out.println("UploadService start:"+fileInfo);
				//�����ϴ��߳̽�������
				uploadTasks[index] = new UploadTask(UploadService.this, fileInfo,filePath);
				uploadTasks[index].upload();
				index ++;
			}
		}else {
			
		}
		return super.onStartCommand(intent, flags, startId);
	}
}