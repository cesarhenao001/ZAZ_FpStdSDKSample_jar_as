package com.ZHIANG;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.ZHIANG.R;
import com.ZHIANG.FPAPI;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FpStdSDKSample extends Activity {
	/** Called when the activity is first created. */

	private TextView tvMsg;
	private Button btnInitDevice;
	private Button btnUnInitDevice;
	private Button btnCaptureImage;
	//private Button btnCalibration;
	private Button btnGetImageQuality;
	private Button btnGetNFIQuality;
	private Button btnCreateAnsiTemp;
	private Button btnCreateIsoTemp;
	private Button btnCompareTemps;
	private TextView tvANSITemp;
	private TextView tvISOTemp;
//	private Button btnCompressWSQ;
//	private Button btnUnCompressWSQ;
	private ImageView ivShowFinger;

	private Libapi m_cFPAPI = null;
	 
	private int m_hDevice = 0;
	private byte[] m_image = new byte[FPAPI.WIDTH*FPAPI.HEIGHT];
	private byte[] m_ansi_template = new byte[FPAPI.FPINFO_STD_MAX_SIZE];
	private byte[] m_iso_template = new byte[FPAPI.FPINFO_STD_MAX_SIZE];
	private byte[] m_wsq = new byte[FPAPI.WIDTH*FPAPI.HEIGHT];
    private int[] RGBbits = new int[FPAPI.WIDTH*FPAPI.HEIGHT];

	public static final int MESSAGE_SHOW_TEXT = 100;
	public static final int MESSAGE_VIEW_ANSI_TEMPLATE = 101;
	public static final int MESSAGE_VIEW_ISO_TEMPLATE = 102;
	public static final int MESSAGE_SHOW_IMAGE = 103;
	public static final int MESSAGE_BTN_ENABLED = 104;
	public static final int MESSAGE_BTN_SETTEXT= 105;

	private boolean DEBUG = true;
	private volatile boolean bContinue = false;
	private int isflag = 0;
	Activity myThis;

    private Context mContext;
	private ScreenBroadcastReceiver mScreenReceiver;
 	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}
	@Override
	protected void onStart() 
	{
		super.onStart();
	}
	@Override
	protected void onPause() 
	{
		bContinue = false;
		super.onPause();
	}
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
	}
	@Override
	protected void onDestroy() 
	{
		bContinue = false;
		UNINIT_DEVICE();
		super.onDestroy();
	}

    private void registerListener() {
        if (mContext != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            mContext.registerReceiver(mScreenReceiver, filter);
        }
    }
 
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;
 
        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action))
            	onDestroy();
        }
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        tvMsg = (TextView) findViewById(R.id.msg);
        btnInitDevice = (Button)findViewById(R.id.btnInitDevice);
        btnUnInitDevice = (Button)findViewById(R.id.btnUnInitDevice);
        btnCaptureImage = (Button)findViewById(R.id.btnCaptureImage);
        //btnCalibration = (Button)findViewById(R.id.btnCalibration);
        btnGetImageQuality = (Button)findViewById(R.id.btnGetImageQuality);
        btnGetNFIQuality = (Button)findViewById(R.id.btnGetNFIQuality);
        btnCreateAnsiTemp = (Button)findViewById(R.id.btnCreateANSITemp);
        btnCreateIsoTemp = (Button)findViewById(R.id.btnCreateISOTemp);
        tvANSITemp = (TextView)findViewById(R.id.tvANSITemp);
        tvISOTemp = (TextView)findViewById(R.id.tvISOTemp);
        btnCompareTemps = (Button)findViewById(R.id.btnCompareTemps);
//        btnCompressWSQ = (Button)findViewById(R.id.btnCompressWSQ);
//        btnUnCompressWSQ = (Button)findViewById(R.id.btnUnCompressWSQ);
		ivShowFinger = (ImageView) findViewById(R.id.ivShowFinger);

		myThis = this;
		
		m_cFPAPI = new Libapi(this);
		
        EnableAllButtons(true,false);
        
        mContext = this;
        mScreenReceiver = new ScreenBroadcastReceiver();
        registerListener();
		
        btnInitDevice.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Runnable r = new Runnable() {
					public void run() {
						INIT_DEVICE ();
					}
				};
				Thread s = new Thread(r);
				s.start();
 			}
		});
        
        btnUnInitDevice.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Runnable r = new Runnable() {
					public void run() {
						UNINIT_DEVICE ();
					}
				};
				Thread s = new Thread(r);
				s.start();
 			}
		});
        
        btnCaptureImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isflag != 0) {
					m_fEvent.obtainMessage(MESSAGE_BTN_SETTEXT, R.id.btnCaptureImage, R.string.TXT_CAPTURE_IMAGE).sendToTarget();
					m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0, "CaptureImage() = Canceled."));
					bContinue = false; 
					isflag = 2; 
					return;
				}
				
				m_fEvent.obtainMessage(MESSAGE_BTN_SETTEXT, R.id.btnCaptureImage, R.string.TXT_STOP).sendToTarget();
				bContinue = true;
				Runnable r = new Runnable() {
					public void run() {
						isflag = 1;
						CAPTURE_IMAGE ();
					}
				};
				Thread s = new Thread(r);
				s.start(); 			
			}
		});

        /*
        btnCalibration.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CALIBRATE ();
 			}
		});
		*/

        btnGetImageQuality.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Runnable r = new Runnable() {
					public void run() {
						GET_IMAGE_QUALITY ();
					}
				};
				Thread s = new Thread(r);
				s.start();
 			}
		});

        btnGetNFIQuality.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Runnable r = new Runnable() {
					public void run() {
						GET_NFI_QUALITY ();
					}
				};
				Thread s = new Thread(r);
				s.start();
 			}
		});

        btnCreateAnsiTemp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Runnable r = new Runnable() {
					public void run() {
						CREATE_ANSI_TEMP ();
					}
				};
				Thread s = new Thread(r);
				s.start();
 			}
		});

        btnCreateIsoTemp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Runnable r = new Runnable() {
					public void run() {
						CREATE_ISO_TEMP ();
					}
				};
				Thread s = new Thread(r);
				s.start();
 			}
		});

        btnCompareTemps.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Runnable r = new Runnable() {
					public void run() {
						COMPARE_TEMPS ();
					}
				};
				Thread s = new Thread(r);
				s.start();
 			}
		});
        
//         btnCompressWSQ.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				Runnable r = new Runnable() {
//					public void run() {
//						COMPRESS_TO_WSQ ();
//					}
//				};
//				Thread s = new Thread(r);
//				s.start();
// 			}
//		});
//
//        btnUnCompressWSQ.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				Runnable r = new Runnable() {
//					public void run() {
//						UNCOMPRESS_FROM_WSQ ();
//					}
//				};
//				Thread s = new Thread(r);
//				s.start();
// 			}
//		});
    }

	protected void INIT_DEVICE() {
		String msg;
		m_hDevice = m_cFPAPI.OpenDevice();
		if (m_hDevice==0) msg = "OpenDevice() = Fail.";
		else {
			msg = "OpenDevice() = OK.";
	        EnableAllButtons (false,true);
		}
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0,msg));
	}
	
	protected void UNINIT_DEVICE() {
		String msg;
		if (m_hDevice != 0) {
    		m_cFPAPI.CloseDevice(m_hDevice);
        }
		msg = "CloseDevice() = OK.";
		m_hDevice = 0;
        EnableAllButtons (true,false);
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0,msg));
	}
	
	protected void CAPTURE_IMAGE() {
        EnableAllButtons(false,false);
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnCaptureImage, 1));
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0, "Put your finger ..."));
		for (int i = 0; i < FPAPI.WIDTH*FPAPI.HEIGHT; i++) m_image[i] = (byte)0xFF;
		m_fEvent.obtainMessage(MESSAGE_SHOW_IMAGE, FPAPI.WIDTH, FPAPI.HEIGHT, m_image).sendToTarget();
		while (isflag == 1) {
			int ret = m_cFPAPI.GetImage(m_hDevice, m_image);
			if (ret != FPAPI.TRUE) {
				m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0, "CaptureImage() = Fail.").sendToTarget();
				break;
			}
			m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_IMAGE, FPAPI.WIDTH, FPAPI.HEIGHT, m_image));
			//ret = m_ZA.GetQualityScore(m_image, FPAPI.WIDTH, FPAPI.HEIGHT);
			ret = m_cFPAPI.GetImageQuality(m_hDevice,m_image);
			if (ret >= FPAPI.DEF_QUALITY_SCORE) {
				m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0, "CaptureImage() = OK. Score:"+ret).sendToTarget();
				break;
			}
		}
		m_fEvent.obtainMessage(MESSAGE_BTN_SETTEXT, R.id.btnCaptureImage, R.string.TXT_CAPTURE_IMAGE).sendToTarget();
		if (!bContinue) m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0, "CaptureImage() = Canceled.").sendToTarget();
		bContinue = false;
		isflag = 0;
        EnableAllButtons(false,true);
	}
	
	protected void CALIBRATE() 
	{
		int ret;
		String msg = "";
		//sensor_mode = 0 --> for default,  = 1 --> for wet,  = 2 --> for dry 
		EnableAllBtn(false);
		ret = m_cFPAPI.Calibration(m_hDevice, 0);
		if (ret == FPAPI.TRUE) msg = String.format("Calibration(%d) = OK.", 0);
		else msg = String.format("Calibration(%d) = Fail.", 0);
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0,msg));
		EnableAllBtn(true);
	}

	protected void GET_IMAGE_QUALITY() 
	{
		int qr;
		String msg = "";
		EnableAllBtn(false);
		//	qr = m_ZA.GetQualityScore(m_image, FPAPI.WIDTH, FPAPI.HEIGHT); 
		qr = m_cFPAPI.GetImageQuality(m_hDevice,m_image);
		msg = String.format("GetImageQuality() = %d.", qr);
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0,msg));
		EnableAllBtn(true);
	}

	protected void GET_NFI_QUALITY() 
	{
		int qr;
		String msg = "";
		EnableAllBtn(false);
		String[] degree = {"excellent","very good","good","poor","fair"};
		qr = getimg_char(m_hDevice,m_image,m_iso_template);
		//qr = m_cFPAPI.GetNFIQuality(m_hDevice,m_image); 
		 msg = String.format("GetNFIQuality() = %d ", qr );
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0,msg));
		EnableAllBtn(true);
	}

	protected void CREATE_ANSI_TEMP() 
	{
		int i, templateLen = 0;
		String msg;
		EnableAllBtn(false); 
		templateLen = m_cFPAPI.CreateISOTemplate(m_hDevice,m_image, m_ansi_template); 
		//templateLen = m_cFPAPI.CreateANSITemplate(m_hDevice,m_image, m_ansi_template);
		if (templateLen == 0) msg = "Create fail.";
		else {
			//templateLen = RemoveExData(m_iso_template, templateLen, 0);
			msg = String.format("CreateANSITemplate() = %d.", templateLen);
		}
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0,msg));

		msg = "";
		for (i=0; i < templateLen; i ++) {
			msg += String.format("%02x", m_ansi_template[i]);
		}
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_VIEW_ANSI_TEMPLATE, 0, 0,msg));

		if (DEBUG)
		{
			String str = String.format("ZhiAng_FMR(ANSI).bin");
			SaveAsFile (str,  m_iso_template, templateLen);

			str = String.format("ZhiAng_FMR(ANSI).txt");
			SaveAsFile (str, msg.getBytes(), templateLen*2);
		}
		EnableAllBtn(true);
	}

	protected void CREATE_ISO_TEMP() 
	{
		int i, templateLen = 0;
		String msg;
		EnableAllBtn(false);
		templateLen = m_cFPAPI.CreateISOTemplate(m_hDevice,m_image, m_iso_template);
		if (templateLen == 0) msg = "CreateISOTemplate() = Fail.";
		else {
			//templateLen = RemoveExData(m_iso_template, templateLen, 1);
			msg = String.format("CreateISOTemplate() = %d.", templateLen);
		}
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0,msg));

		msg = "";
		for (i=0; i < templateLen; i ++) {
			msg += String.format("%02x", m_iso_template[i]);
		}
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_VIEW_ISO_TEMPLATE, 0, 0,msg));
		
		if (DEBUG)
		{
			String str = String.format("ZhiAng_FMR(ISO).bin");
			SaveAsFile (str,  m_iso_template, templateLen);

			str = String.format("ZhiAng_FMR(ISO).txt");
			SaveAsFile (str, msg.getBytes(), templateLen*2);
		}
		EnableAllBtn(true);
	}

	protected void COMPARE_TEMPS() 
	{
		int score;
		String msg;
		EnableAllBtn(false);
		score = m_cFPAPI.CompareTemplates(m_hDevice,m_ansi_template, m_iso_template);
		msg = String.format("CompareTemplates() = %d.", score);
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0,msg));
		EnableAllBtn(true);
	}

	protected void COMPRESS_TO_WSQ() 
	{
		long wsqsize = m_cFPAPI.CompressToWSQImage (m_hDevice, m_image, m_wsq);
		boolean res = SaveAsFile("image.wsq",m_wsq,(int)wsqsize);
		String msg = "";
		if (wsqsize <= 0 || !res ) msg = "CompressToWSQImage() = Fail.";
		else msg = String.format("CompressToWSQImage() = %d.", wsqsize); 
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0,msg));
	}

	protected void UNCOMPRESS_FROM_WSQ() 
	{
		String msg = "";
		long wsqsize = LoadAsFile("image.wsq",m_wsq);
		if (wsqsize != 0) {
			long imasize = m_cFPAPI.UnCompressFromWSQImage (m_hDevice, m_wsq, wsqsize, m_image);
			m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_IMAGE, FPAPI.WIDTH, FPAPI.HEIGHT,m_image));
			if (imasize != 0) msg = "UnCompressFromWSQImage() = OK."; 
			else msg = "UnCompressFromWSQImage() = Fail.";
		}
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0,msg));
	}
	
	public void EnableAllButtons(boolean bOpen, boolean bOther)
	{
		int iOther;
		if (bOpen) m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnInitDevice, 1));
		else m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnInitDevice, 0));
		if (bOther) iOther = 1; else iOther = 0;
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnUnInitDevice, iOther));
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnCaptureImage, iOther));
		//m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnCalibration, iOther));
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnGetImageQuality, iOther));
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnGetNFIQuality, iOther));
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnCreateANSITemp, iOther));
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnCreateISOTemp, iOther));
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnCompareTemps, iOther));
//		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnCompressWSQ, iOther));
//		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnUnCompressWSQ, iOther));
	}
	public void EnableAllBtn( boolean bOther)
	{
		int iOther;
		if (bOther) iOther = 1; else iOther = 0;
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnUnInitDevice, iOther));
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnCaptureImage, iOther));
		//m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnCalibration, iOther));
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnGetImageQuality, iOther));
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnGetNFIQuality, iOther));
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnCreateANSITemp, iOther));
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnCreateISOTemp, iOther));
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnCompareTemps, iOther));
//		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnCompressWSQ, iOther));
//		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_BTN_ENABLED, R.id.btnUnCompressWSQ, iOther));
	}

	private final Handler m_fEvent = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SHOW_TEXT:
				tvMsg.setText((String)msg.obj);
				break;
			case MESSAGE_VIEW_ANSI_TEMPLATE:
				tvANSITemp.setText((String)msg.obj);
				break;
			case MESSAGE_VIEW_ISO_TEMPLATE:
				tvISOTemp.setText((String)msg.obj);
				break;
			case MESSAGE_BTN_ENABLED:
				Button btn = (Button) findViewById(msg.arg1);
				if (msg.arg2 != 0) btn.setEnabled(true);
				else btn.setEnabled(false);
				break;
			case MESSAGE_BTN_SETTEXT:
				btn = (Button) findViewById(msg.arg1);
				btn.setText(msg.arg2);
				break;
			case MESSAGE_SHOW_IMAGE:
				ShowFingerBitmap ((byte[])msg.obj,msg.arg1,msg.arg2);
				break;
			}
		}
	};

	public boolean SaveAsFile (String filename, byte[] buffer, int len) {
    	boolean ret = true;
        File extStorageDirectory = Environment.getExternalStorageDirectory();
        File Dir = new File(extStorageDirectory, "Android"); 
        File file = new File(Dir, filename);                
        try { 
            FileOutputStream out = new FileOutputStream(file);                    
       		out.write(buffer,0,len);
            out.close();
         } catch (Exception e) { 
        	 ret = false;
        }
        return ret;
    }

    public long LoadAsFile (String filename, byte[] buffer) {
    	long ret = 0;
        File extStorageDirectory = Environment.getExternalStorageDirectory();
        File Dir = new File(extStorageDirectory, "Android"); 
        File file = new File(Dir, filename);                
        ret = file.length();
        try { 
            FileInputStream out = new FileInputStream(file);                    
       		out.read(buffer);
            out.close();
         } catch (Exception e) { 
         }
        return ret;
    }

    private void ShowFingerBitmap(byte[] image, int width, int height) {
		if (width==0) return;
		if (height==0) return;
		for (int i = 0; i < width * height; i++ ) {
			int v;
			if (image != null) v = image[i] & 0xff;
			else v= 0;
			RGBbits[i] = Color.rgb(v, v, v);
		}
		Bitmap bmp = Bitmap.createBitmap(RGBbits, width, height,Config.RGB_565);
		ivShowFinger.setImageBitmap(bmp);
		//红色图像
		//Bitmap png = m_cFPAPI.getTransparentBitmap(bmp,m_cFPAPI.color_range,0x00ff0000);		
		//ivShowFinger.setImageBitmap(png);
		//
	}

    public int RemoveExData(byte[] data, int len, int flag){
    	if (len == 0) return 0;
    	int i0, j0, i;

    	if (flag == 0) {											//ANSI
	    	j0 = (data[27] & 0xFF);									//number of minutia
	    	i0 = j0*6 + 28;											//start address of extended data;
	    	j0 = ((data[i0] & 0xFF) <<8) | (data[i0+1] & 0xFF);		//length of extended data
	    	data[i0] = (byte)0x00;
	    	data[i0+1] = (byte)0x00;
	    	i0 = ((data[8] & 0xFF) <<24) | ((data[9] & 0xFF) <<16)	| ((data[10] & 0xFF) <<8) | (data[11] & 0xFF);		
	    															//length of record
	    	i0 -= j0;
	    	data[11] = (byte)(i0 & 0xFF);
	    	data[10] = (byte)((i0 & 0xFF00)>>8);
	    	data[9] = (byte)((i0 & 0xFF0000)>>16);
	    	data[8] = (byte)((i0 & 0xFF000000)>>24);
	    	for (i = 0; i < j0; i++) data[i0+i] = (byte)0x00;
    	} else {													//ISO
    		j0 = (data[29] & 0xFF);									//number of minutia
    		i0 = j0*6 + 30;											//start address of extended data;
	    	j0 = ((data[i0] & 0xFF) <<8) | (data[i0+1] & 0xFF);		//length of extended data
	    	data[i0] = (byte)0x00;
	    	data[i0+1] = (byte)0x00;
	    	i0 = ((data[8] & 0xFF) << 8) | (data[9] & 0xFF);		//length of record
    		i0 -= j0;
    		data[9] = (byte)(i0 & 0xFF);
    		data[8] = (byte)((i0 & 0xFF00)>>8);
	    	for (i = 0; i < j0; i++) data[i0+i] = (byte)0x00;
    	}

    	return i0;
    }
    
    public int  getimg_char(int device, byte[] image,byte[] fpchar)
    {
    	int ret = m_cFPAPI.GetImage(device,image);
    	m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_IMAGE, FPAPI.WIDTH, FPAPI.HEIGHT, m_image));
    	if (ret != FPAPI.TRUE) { 
			return -1;//异常
		}
    	
    	ret = m_cFPAPI.GetQualityScore(image, FPAPI.WIDTH, FPAPI.HEIGHT);
    	if (ret < FPAPI.DEF_QUALITY_SCORE) {
			 return -2;//没有按手指
		}
    	ret = m_cFPAPI.CreateISOTemplate(device,image, fpchar); 
		if (ret == 0) 
		{
			return -2;//特征生成失败	 
		}
		String msg = "";
		for (int i=0; i < ret; i ++) {
			msg += String.format("%02x", m_ansi_template[i]);
		}
		m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_VIEW_ANSI_TEMPLATE, 0, 0,msg));
		return 0;//成功 
    }
     
    
}