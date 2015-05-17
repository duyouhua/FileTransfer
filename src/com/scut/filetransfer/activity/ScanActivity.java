package com.scut.filetransfer.activity;

import java.util.Random;
import com.scut.filetransfer.R;
import com.scut.filetransfer.bean.User;
import com.scut.filetransfer.service.ConnectionManager;
import com.scut.filetransfer.util.RSAUtil;
import com.scut.filetransfer.util.RandomNum;
import com.scut.filetransfer.view.CircleImageView;
import com.scut.filetransfer.view.ScanView;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ScanActivity extends Activity {

	private ScanView scanView;
	private Button exitBtn;
	private Thread scanThread;
	private RelativeLayout layout;
	private CircleImageView selfHead;
	private RSAUtil rsaUtil;

	private ConnectionManager connectionManager = null;

	/**
	 * ��¼�Ƿ�ִ��ɨ���߳�
	 */
	private boolean isScan = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity.server.setContext(this);
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		Window window = this.getWindow();
		window.setFlags(flag, flag);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.scan_activity);
		initView();
		startScan();
		rsaUtil = RSAUtil.getInstance();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initView() {
		layout = (RelativeLayout) findViewById(R.id.layout);
		scanView = (ScanView) findViewById(R.id.scan);
		selfHead = (CircleImageView) findViewById(R.id.self_head);
		selfHead.setVisibility(View.VISIBLE);
		exitBtn = (Button) findViewById(R.id.exit);
		exitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isScan) {
					isScan = false;
					finish();
				}
			}
		});
		connectionManager = ConnectionManager.getInstance(this);
	}

	public void addImage(int resId, int left, int top, final User user) {
		CircleImageView imageview = new CircleImageView(this);
		imageview.setImageResource(resId);
		int imageRadio = dip2px(this, 80);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				imageRadio, imageRadio);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.leftMargin = left;
		lp.topMargin = top;
		imageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showLinkDialog(user);
			}
		});
		imageview.setLayoutParams(lp);
		layout.addView(imageview);
		imageview.showImage();
	}

	private void showLinkDialog(final User user) {
		AlertDialog.Builder builder = new Builder(ScanActivity.this);
		final Dialog dialog = new Dialog(ScanActivity.this);
		StringBuffer buffer = new StringBuffer();
		buffer.append(getString(R.string.ip_ddress));
		buffer.append(user.getIpAddress());
		buffer.append("\n");
		buffer.append(getString(R.string.distance));
		buffer.append(MainActivity.server.getDistance(user) + "m");
		buffer.append("\n");
		buffer.append(getString(R.string.check_code));
		final String code = new RandomNum(4).getCode();
		MainActivity.server.setCode(code);
		buffer.append(code);
		builder.setMessage(buffer.toString());
		builder.setTitle(user.getPhoneModel());
		builder.setPositiveButton(getString(R.string.comfirm),
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new Thread() {
							public void run() {
								ConnectionManager.closeThreadPool();
								Intent intent = ScanActivity.this.getIntent();
								intent.putExtra("ip", user.getIpAddress());
								ScanActivity.this.setResult(2, intent);
								/**
								 * ���͹�Կ
								 */
								ConnectionManager.sendMsg(user.getIpAddress(),
										ConnectionManager.getIpAddress() + ","
												+ rsaUtil.getPublicKey());
							};
						}.start();
					}
				});
		builder.setNegativeButton(getString(R.string.cancel),
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	/**
	 * ����ɨ���߳�
	 */
	private void startScan() {
		scanView.setVisibility(View.VISIBLE);
		scanThread = new Thread(new ScanThread());
		scanThread.start();

		new Thread() {
			public void run() {
				if (isScan)
					connectionManager.scan();
			};
		}.start();
	}

	/**
	 * ��д���ؼ������·��ؼ�ʱ�߳�Ӧ����ֹ
	 */
	@Override
	public void onBackPressed() {
		if (isScan)
			isScan = false;
		connectionManager.getLocationManager().stop();
		ConnectionManager.closeThreadPool();
		finish();
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		connectionManager.getLocationManager().stop();
		ConnectionManager.closeThreadPool();
		super.onDestroy();
	}

	public RSAUtil getRSAUtil() {
		return rsaUtil;
	}

	/**
	 * ����תdp
	 */
	private int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public Point randomPoint() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		int height = metric.heightPixels;
		int x = new Random().nextInt(width - 115);
		int y = new Random().nextInt(height - 115);
		Point point = new Point(x, y);
		return point;
	}

	/**
	 * ����ɨ�趯�����߳�
	 * 
	 * @author ccz
	 * 
	 */
	private class ScanThread implements Runnable {

		@Override
		public void run() {
			while (isScan) {
				try {
					scanView.postInvalidate();
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.getStackTrace();
				}
			}
		}
	}
}