package com.ZHIANG;

import android.app.Activity;

public class Libapi {
	private FPAPI m_cFPAPI = null;
	private ZAandroid  m_ZA = null;
	public Libapi(Activity a) {
		m_cFPAPI = new FPAPI(a);
		m_ZA = new ZAandroid(a);
	}
	
	public int OpenDevice()	{
		return m_cFPAPI.OpenDevice();
	}
	public int CloseDevice(int device){
		return m_cFPAPI.CloseDevice(device);
	}	
	public int Calibration(int device, int mode){
		return m_cFPAPI.Calibration(device, mode);
	}
	public int GetImage(int device, byte[] image){
		return m_cFPAPI.GetImage(device, image);
	}
	public int IsFinger(int device,byte[] image){
		return m_cFPAPI.IsFinger(device, image);
	}
	public int GetImageQuality(int device,byte[] image){
		return m_cFPAPI.GetImageQuality(device, image);
	}
	public int GetNFIQuality(int device,byte[] image){
		return m_cFPAPI.GetNFIQuality(device, image);
	}
	public int CreateANSITemplate(int device,byte[] image, byte[] itemplate){
		return m_cFPAPI.CreateANSITemplate(device, image, itemplate);
	}
	public int CreateISOTemplate(int device,byte[] image,  byte[] itemplate){
		return m_cFPAPI.CreateISOTemplate(device, image, itemplate);
	}
	public int CompareTemplates(int device,byte[] itemplateToMatch, byte[] itemplateToMatched){
		return m_cFPAPI.CompareTemplates(device, itemplateToMatch, itemplateToMatched);
	}
	public int SearchingANSITemplates(int device, byte[] itemplateToSearch, 
		   	int numberOfDbTemplates, byte[] arrayOfDbTemplates, int scoreThreshold){
		return m_cFPAPI.SearchingANSITemplates(device, itemplateToSearch, numberOfDbTemplates, arrayOfDbTemplates, scoreThreshold);
	}
	public int SearchingISOTemplates(int device, byte[] itemplateToSearch, 
		   	int numberOfDbTemplates, byte[] arrayOfDbTemplates, int scoreThreshold){
		return m_cFPAPI.SearchingISOTemplates(device, itemplateToSearch, numberOfDbTemplates, arrayOfDbTemplates, scoreThreshold);
	}
	public int GetANSIImageRecord(int device,byte[] image, byte[] FIR){
		return m_cFPAPI.GetANSIImageRecord(device, image, FIR);
	}
	public int GetISOImageRecord(int device,byte[] image, byte[] FIR){
		return m_cFPAPI.GetISOImageRecord(device, image, FIR);
	}
	public long  CompressToWSQImage (int device,byte[] rawImage, byte[] wsqImage){
		return m_cFPAPI.CompressToWSQImage(device, rawImage, wsqImage);
	}
	public long  UnCompressFromWSQImage (int device,byte[] wsqImage, long wsqSize, byte[] rawImage){
		return m_cFPAPI.UnCompressFromWSQImage(device, wsqImage, wsqSize, rawImage);
	}
	
	public int GetQualityScore(byte[] rawImage,int weith,int height)	{
		return m_ZA.GetQualityScore(rawImage, weith, height);
	}
	
}
