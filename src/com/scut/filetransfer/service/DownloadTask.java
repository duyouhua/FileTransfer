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
import android.util.Log;

import com.scut.filetransfer.bean.FileInfo;
import com.scut.filetransfer.database.FileInfoDAO;
import com.scut.filetransfer.database.FileInfoDAOImpl;
import com.scut.filetransfer.util.AESUtil;
import com.scut.filetransfer.util.LogUtil;

/**
 * ����������
 * 
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

	public void Pause() {
		isPause = true;
	}

	public void Resume() {
		isPause = false;
	}

	public DownloadTask(Context context, FileInfo fileInfo) {
		super();
		this.context = context;
		this.fileInfo = fileInfo;
		fileInfoDAO = new FileInfoDAOImpl(context);
		aesUtil = AESUtil.getInstance();
	}

	public void download() {

		// �״�ʹ�ã����������ļ�Ŀ¼
		File dir = new File(DownloadService.DOWNLOAD_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// �������ؽ���
		downloadThread = new DownloadThread();
		downloadThread.start();
	}

	/**
	 * �����߳�(�Ȼ�ȡ�ļ���Ϣ��������)
	 * 
	 * @author Wise
	 * 
	 */
	class DownloadThread extends Thread {

		// SOCKET������Ҫ����
		Socket socket = null;
		RandomAccessFile raf = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;

		public DownloadThread() {
			super();
		}


		public void run() {

			try {
				Log.i("DownloadTask", "start download...");
				socket = new Socket(fileInfo.getIP(), fileInfo.getPort());
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				// ����ӷ��Ͷ˻�ȡ�ļ����ͳ���
				fileInfo.setFileName(dis.readUTF());
				fileInfo.setLength(dis.readInt());
				// �����ݿ��в����Ƿ���ڸ��ļ�������δ���꣨�ϵ�������
				if (fileInfoDAO.isExists(fileInfo.getIP(), fileInfo.getPort(),
						fileInfo.getFileName())) {
					fileInfo = fileInfoDAO.getFileInfo(fileInfo.getIP(),
							fileInfo.getPort(), fileInfo.getFileName());
					LogUtil.i("DownloadTask", fileInfo.toString());
				}
				// ���߷������ļ���ʼ�����λ��
				dos.writeInt(fileInfo.getStart());
				
				dos.flush();
				// �����ļ�д��λ��
				int start = fileInfo.getStart();
				File file = new File(DownloadService.DOWNLOAD_PATH,
						fileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				// ����һ�δ����ļ��������ļ���С���������ݿ�
				if (!fileInfoDAO.isExists(fileInfo.getIP(), fileInfo.getPort(),
						fileInfo.getFileName())) {
					raf.setLength(fileInfo.getLength());
					fileInfo.setStatus("��ʼ����");
					fileInfoDAO.insertFileInfo(fileInfo);
				}
				// �ļ��ϵ�����д��λ��
				raf.seek(start);
				// ��ʼ��buff
				byte[] result = null;
				int len = -1;
				long oldProgressBar = 0;
				long progressBar = 0;
				//��ʼ����
				while (true) {
					int bufferSize = dis.readInt();
					byte[] buffer = new byte[bufferSize];
					len = dis.read(buffer);
					// �������ݿ��Զ�ȡ������ѭ��
					if (len == -1) {
						break;
					}
					try {
						result = aesUtil.decrypt(buffer);
					} catch (Exception e) {
						e.printStackTrace();
					}
					raf.write(result, 0, result.length);
					start += result.length;
					// long�ͷ�ֹ�������
					progressBar = (long) start * 100
							/ (long) fileInfo.getLength();
					// �����ļ����Ⱥʹ���λ��
					fileInfo.setFinished((int) progressBar);
					fileInfo.setStart(start);
					// �����ؽ��ȷ��͹㲥��Activity
					if (oldProgressBar != progressBar) {
						oldProgressBar = progressBar;
						Intent intent = new Intent(
								DownloadService.ACTION_UPDATE);
						intent.putExtra("fileInfo", fileInfo);
						LogUtil.i("DownloadTask", fileInfo.getFileName() + "������"+ progressBar + "%");
						context.sendBroadcast(intent);
					}
					// �ڵ����ͣʱ���������ؽ��ȵ����ݿ�
					if (isPause) {
						fileInfoDAO.updateFileInfo(fileInfo.getIP(),
								fileInfo.getPort(), fileInfo.getId(),
								fileInfo.getFinished(), fileInfo.getStart(),
								fileInfo.getLength(), fileInfo.getFileName(),
								"ֹͣ����");
						LogUtil.i("DownloadTask","DownloadTask isPause:" + fileInfo);
						
						// �������ͣ������������ѭ��
						while (isPause) {
						}
						
						// ����������أ��������ݿ�
						fileInfoDAO.updateFileInfo(fileInfo.getIP(),
								fileInfo.getPort(), fileInfo.getId(),
								fileInfo.getFinished(), fileInfo.getStart(),
								fileInfo.getLength(), fileInfo.getFileName(),
								"��������");
					}
				}
				// �������,�������ݿ�
				fileInfoDAO.updateFileInfo(fileInfo.getIP(),
						fileInfo.getPort(), fileInfo.getId(), 100,
						fileInfo.getStart(), fileInfo.getLength(),
						fileInfo.getFileName(), "�����");
				LogUtil.i("DownloadTask", "finished:" + fileInfo);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (socket !=null) {
						socket.close();
					}
					if (dos != null) {
						dos.close();
					}
					if (dis != null) {
						dis.close();
					}

					if (raf != null) {
						raf.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
