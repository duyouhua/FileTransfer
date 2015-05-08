package com.wechat.filetransfer.services;


import com.wechat.filetransfer.bean.FileInfo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UploadService extends Service{


	public static final String ACTION_START  = "ACTION_UPLOAD_START";
	public static final String ACTION_STOP  = "ACTION_STOP";
	public static final String ACTION_UPDATE  = "ACTION_UPDATE";
	private static int index = 0;
	private String filePath;
	private UploadTask[] uploadTasks = new UploadTask[100];
	private FileInfo fileInfo = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	//����Service
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
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
		return super.onStartCommand(intent, flags, startId);
	}
}
