package com.scut.filetransfer.service;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.Intent;

import com.scut.filetransfer.bean.FileInfo;
import com.scut.filetransfer.database.FileInfoDAO;
import com.scut.filetransfer.database.FileInfoDAOImpl;
import com.scut.filetransfer.util.AESUtil;
/**
 * ����������
 * @author Wise
 *
 */
public class DownloadTask {
	private Context context = null;
	private FileInfo fileInfo = null;
	private FileInfoDAO fileInfoDAO = null;
	public boolean isPause = false;
	private AESUtil aesUtil;
	public DownloadThread downloadThread;
	
	public void Pause(){
		isPause = true;
	}
	
	public void Resume(){
		isPause = false;
	}
	
	public DownloadTask(Context context, FileInfo fileInfo) {
		super();
		this.context = context;
		this.fileInfo = fileInfo;
		fileInfoDAO = new FileInfoDAOImpl(context);
		aesUtil = AESUtil.getInstance();
	}
	
	public void download(){
		//�������ؽ���
		downloadThread = new DownloadThread();
		downloadThread.start();
	}
	
	
	
	
	/**
	 * �����߳�(�Ȼ�ȡ�ļ���Ϣ��������)
	 * @author Wise
	 *
	 */
	class DownloadThread extends Thread{
		
		//SOCKET������Ҫ����
		Socket socket = null;
		RandomAccessFile raf = null;
		DataInputStream dis = null;
		DataOutputStream dos=null;
		
		public DownloadThread() {
			super();
		}
		
		/**
		 * ��Service��ǿ�ƹر�ʱ�������÷�����������������
		 */
		public void onDestroySocketConnection(){
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		public void run(){
			
			
			try {
				socket = new Socket(fileInfo.getIP(),fileInfo.getPort());
				dis = new DataInputStream(socket.getInputStream()); 
				dos=new DataOutputStream(socket.getOutputStream());
				//����ӷ�������ȡ�ļ����ͳ���
				fileInfo.setFileName(dis.readUTF());
				fileInfo.setLength(dis.readInt());
				//�����ݿ��в����Ƿ���ڸ��ļ�������δ���꣨�ϵ�������
				if (fileInfoDAO.isExists(fileInfo.getIP(), fileInfo.getPort(), fileInfo.getFileName())) {
					fileInfo = fileInfoDAO.getFileInfo(fileInfo.getIP(), fileInfo.getPort(), fileInfo.getFileName());
				}
				//���߷������ļ���ʼ�����λ�� 
				dos.writeInt(fileInfo.getStart());
				dos.flush(); 
				//�����ļ�д��λ��
				int start = fileInfo.getStart();
				File file = new File(DownloadService.DOWNLOAD_PATH, fileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				//�ļ��ϵ���������ͣ�ظ���д��λ��
				raf.seek(start);
				//�������ݿ�
				if (!fileInfoDAO.isExists(fileInfo.getIP(), fileInfo.getPort(), fileInfo.getId())) {
					fileInfoDAO.insertFileInfo(fileInfo);
				}
				//����buff
				int bufferSize = 1024; 
				byte[] buffer = new byte[bufferSize]; 
				int len = -1;
				long oldProgressBar = 0;
				long progressBar = 0;
				while ((len = dis.read(buffer)) != -1) {
					//�������ݿ��Զ�ȡ��д���ļ�
					try {
						aesUtil.decrypt(buffer);
					} catch (Exception e) {
						e.printStackTrace();
					}
					raf.write(buffer,0,len);
					start += len;
					//long�ͷ�ֹ�������
					progressBar = (long)start * 100 / (long)fileInfo.getLength();
					//�����ļ����Ⱥʹ���λ��
					fileInfo.setFinished((int)progressBar);
					fileInfo.setStart(start);
					//�����ؽ��ȷ��͹㲥��Activity
					if (oldProgressBar != progressBar) {
						oldProgressBar = progressBar;
						Intent intent = new Intent(DownloadService.ACTION_UPDATE);
						intent.putExtra("fileInfo", fileInfo);
						System.out.println(fileInfo.getFileName()+"������"+progressBar+"%");
						context.sendBroadcast(intent);
					}
					//�ڵ����ͣʱ���������ؽ��ȵ����ݿ�
					if (isPause) {
						fileInfoDAO.updateFileInfo(fileInfo.getIP(), fileInfo.getPort(), fileInfo.getId(), fileInfo.getFinished(), fileInfo.getStart(),fileInfo.getLength(),fileInfo.getFileName(),"ֹͣ����");
						System.out.println("DownloadTask isPause:" + fileInfo);
						//����Ƕ���ͣ������������ѭ��
						while(isPause){
						}
						//�ָ����أ��������ݿ�
						fileInfoDAO.updateFileInfo(fileInfo.getIP(), fileInfo.getPort(), fileInfo.getId(), fileInfo.getFinished(), fileInfo.getStart(),fileInfo.getLength(),fileInfo.getFileName(),"��������");
					}
				}
				//�������,�������ݿ�
				fileInfoDAO.updateFileInfo(fileInfo.getIP(), fileInfo.getPort(), fileInfo.getId(), 100, fileInfo.getStart(), fileInfo.getLength(), fileInfo.getFileName(),"�����");
				System.out.println("DownloadTask finished:"+fileInfo);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					socket.close();
					dis.close();
					raf.close();
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
