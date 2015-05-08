package com.wechat.filetransfer.services;


import com.wechat.filetransfer.bean.FileInfo;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

public class DownloadService extends Service{
	
	
	public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
	public static final String ACTION_START  = "ACTION_START";
	public static final String ACTION_STOP  = "ACTION_STOP";
	public static final String ACTION_UPDATE  = "ACTION_UPDATE";
	private int index;
	private DownloadTask[] downloadTask = new DownloadTask[100];
	private FileInfo fileInfo = null;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	//����Service
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//fileId�������±���ͬ
		fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
		index = fileInfo.getId();
		
		if (ACTION_START.equals(intent.getAction())) {
			//���������Ķ�����START
			if (downloadTask[index]!=null && downloadTask[index].isPause == true) {
				//�������أ������Resume()�����ָ�����
				downloadTask[index].Resume();
				System.out.println("DownloadService resume:"+fileInfo);
			}else{
				//��ʼ����
				System.out.println("DownloadService start:"+fileInfo);
				//���������߳̽�������
				downloadTask[index] = new DownloadTask(DownloadService.this, fileInfo);
				downloadTask[index].download();
			}
		}else if(ACTION_STOP.equals(intent.getAction())){
			//���������Ķ�����STOP������ͣ����
			System.out.println("DownloadService stop:"+fileInfo);
			if (downloadTask[index] != null) {
				downloadTask[index].Pause();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
}
