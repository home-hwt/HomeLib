package com.guo.androidlib.file;

import java.io.File;
import java.io.IOException;
import com.guo.androidlib.util.GHLog;
import android.os.Environment;
import android.text.TextUtils;

public class FileUtil {
	private static FileUtil fileUtil;
	public static FileUtil getInstance(){
		if(fileUtil == null){
			fileUtil = new FileUtil();
		}
		return fileUtil;
	}
	
	/**
	 * SD���Ƿ����
	 * @return
	 */
	public boolean getExternalStorageState(){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return true;
		}
		return false;
	}
	
	/**
	 * �����ļ����ļ���
	 * @param filePath
	 * @return �ɹ�����true ���߷���false�����ļ����ļ��д���ʱ����false��
	 * @throws IOException 
	 */
	public boolean createDir(String filePath) throws IOException{
		if(TextUtils.isEmpty(filePath)){
			GHLog.gHLog(GHLog.LOG_ERROR, "filePath is null");
			return false;
		}
		return createDir(new File(filePath));
	}
	
	/**
	 * �����ļ���
	 * @param file
	 * @return �ɹ�����true ���߷���false�����ļ��д���ʱ����true��
	 * @throws IOException 
	 */
	public boolean createDir(File file) throws IOException{
		if(getExternalStorageState()){
			if(file != null && !file.exists()){
				return file.mkdirs();
			}
			if(file.exists()) return true;
		}else{
			GHLog.gHLog(GHLog.LOG_ERROR, "SD ��������");
		}
		return false;
	}
	
	/**
	 * �����ļ�
	 * @param file
	 * @return �ɹ�����true ���߷���false�����ļ���ʱ����true��
	 * @throws IOException 
	 */
	public boolean createFile(File file) throws IOException{
		if(getExternalStorageState()){
			if(file != null && !file.exists()){
				return file.createNewFile();
			}
			if(file.exists()) return true;
		}else{
			GHLog.gHLog(GHLog.LOG_ERROR, "SD ��������");
		}
		return false;
	}
}
