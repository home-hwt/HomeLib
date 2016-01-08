package com.guo.androidlib.http;

public interface HomeDownLoadListener {
	/**
	 * ��������
	 */
	public static int HOMEDOWNLOADING = 0;
	
	/**
	 * ���سɹ�
	 */
	public static int HOMEDOWNLOAD_SUCCESS = 1;
	
	/**
	 * ����ʧ��
	 */
	public static int HOMEDOWNLOAD_FAIL = -1;
	
	/**
	 * 
	 * @param downState  ����״̬�������������أ�������ɣ�����ʧ��
	 * @param total	�����ļ��ܴ�С
	 * @param progress ���ؽ���
	 * @param filePathOrErrorMsg ���سɹ����ļ�����·��������ʧ�ܵĴ�����Ϣ
	 */
	public void downProgress(int downState,long total,long progress,String filePathOrErrorMsg);
}
