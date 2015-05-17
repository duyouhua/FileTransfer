package com.scut.filetransfer.adapter;
import java.util.ArrayList;
import java.util.List;

import com.scut.filetransfer.R;
import com.scut.filetransfer.bean.FileInfo;
import com.scut.filetransfer.service.DownloadService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiveAdapter extends CommonAdapter<FileInfo> {
	
	private ListView lv_main;
	private List<Integer> listButton;	//��ʾ����ɵ�button

	public ReceiveAdapter(Context context, List<FileInfo> list, int layoutId,ListView lv_main) {
		super(context,list,layoutId);
		this.lv_main = lv_main;
		listButton = new ArrayList<Integer>();
	}


	@Override
	public void getView(CommonViewHolder holder,final FileInfo fileInfo) {

		//��ȡlist������ݣ������õ������
		TextView  tvFileName = holder.getView(R.id.tvFileName);
		final Button btnButton = holder.getView(R.id.btnButton);
		ProgressBar pbProgress = holder.getView(R.id.pbProgress);
		
		tvFileName.setText(fileInfo.getFileName());
		pbProgress.setMax(100);
		pbProgress.setProgress(fileInfo.getFinished());
		btnButton.setText(fileInfo.getStatus());
		btnButton.setVisibility(Button.VISIBLE);
		btnButton.setBackgroundColor(Color.LTGRAY);
		
		//���ļ�������ɣ�����button���ɼ������������button������
		if (fileInfo.getFinished() == 100 && listButton.contains(holder.getPosition())) {
			//btnButton.setVisibility(Button.INVISIBLE);
			btnButton.setBackgroundColor(Color.TRANSPARENT);
			btnButton.setTextColor(Color.BLACK);
			btnButton.setText("�����");
			btnButton.setOnClickListener(null);
		}
		if (fileInfo.getFinished() == 100 && "�����".equals(btnButton.getText().toString())) {
			btnButton.setBackgroundColor(Color.TRANSPARENT);
			btnButton.setTextColor(Color.BLACK);
			btnButton.setText("�����");
			btnButton.setOnClickListener(null);
		}
		
		//���ļ�id��Ϊposition
		fileInfo.setId(holder.getPosition());
		Log.i("ReceiveAdapter", "holder.getPosition() and fileInfo.getId():"+fileInfo.getId());
		btnButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent intent = new Intent(context,DownloadService.class);
				
				if ("ֹͣ����".equals(btnButton.getText().toString())) {
					//�������ֹͣ���ء���ť
					//����Serviceִ��STOP�����������ļ���Ϣ����ȥ
					intent.setAction(DownloadService.ACTION_STOP);
					intent.putExtra("fileInfo", fileInfo);
					context.startService(intent);
					btnButton.setText("��������");
					fileInfo.setStatus("��������");
				}else if ("��������".equals(btnButton.getText().toString())) {
					//��������������ء���ť
					//����Serviceִ��START�����������ļ���Ϣ����ȥ
					intent.setAction(DownloadService.ACTION_START);
					intent.putExtra("fileInfo", fileInfo);
					context.startService(intent);
					btnButton.setText("ֹͣ����");
					fileInfo.setStatus("ֹͣ����");
				}else if ("��ʼ����".equals(btnButton.getText().toString())) {
					//���������ʼ���ء���ť
					//����Serviceִ��START�����������ļ�ID����Ϣ����ȥ
					intent.setAction(DownloadService.ACTION_START);
					intent.putExtra("fileInfo", fileInfo);
					context.startService(intent);
					btnButton.setText("ֹͣ����");
					fileInfo.setStatus("ֹͣ����");
				}
			}
		});
		
		
		
	}


	/**
	 * �Զ�����½��������ļ����ķ���
	 * ��Ϊʹ��ϵͳ��notifyDataSetChanged�����getView()
	 * ����������ĺ�ʱ
	 * @param position
	 */
	public void myNotifyDataSetChanged(int position) {
		//System.out.println("MyAdapter myNotifyDataSetChanged()");
		//�õ���1������ʾ�ؼ���λ��,��ס�ǵ�1������ʾ�ؼ��ޡ������ǵ�1���ؼ�
		int visiblePosition = lv_main.getFirstVisiblePosition(); 
		//�õ�����Ҫ����item��View,���ؼ�������ʾ��Χ���򲻽��и���
		if (position > lv_main.getLastVisiblePosition() || position < lv_main.getFirstVisiblePosition()) {
			return ;
		}	
		View item = lv_main.getChildAt(position - visiblePosition);
		FileInfo fileInfo = list.get(position);
		
		ProgressBar pbProgress = (ProgressBar) item.findViewById(R.id.pbProgress);
		TextView tvFileName =  (TextView) item.findViewById(R.id.tvFileName);
		Button btnButton =  (Button) item.findViewById(R.id.btnButton);
		
		pbProgress.setProgress(fileInfo.getFinished());
		tvFileName.setText(fileInfo.getFileName());
		
		if (fileInfo.getFinished() == 100) {
			btnButton.setBackgroundColor(Color.TRANSPARENT);
			btnButton.setTextColor(Color.BLACK);
			btnButton.setText("�����");
			btnButton.setOnClickListener(null);
			listButton.add(position);
			Toast.makeText(context, "�ļ��ѱ�����"+DownloadService.DOWNLOAD_PATH, Toast.LENGTH_SHORT).show();
		}
	}



}
