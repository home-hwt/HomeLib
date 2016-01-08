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
	 * SD卡是否可用
	 * @return
	 */
	public boolean getExternalStorageState(){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return true;
		}
		return false;
	}
	
	/**
	 * 创建文件或文件夹
	 * @param filePath
	 * @return 成功返回true 否者返回false（当文件或文件夹存在时返回false）
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
	 * 创建文件夹
	 * @param file
	 * @return 成功返回true 否者返回false（当文件夹存在时返回true）
	 * @throws IOException 
	 */
	public boolean createDir(File file) throws IOException{
		if(getExternalStorageState()){
			if(file != null && !file.exists()){
				return file.mkdirs();
			}
			if(file.exists()) return true;
		}else{
			GHLog.gHLog(GHLog.LOG_ERROR, "SD 卡不可用");
		}
		return false;
	}
	
	/**
	 * 创建文件
	 * @param file
	 * @return 成功返回true 否者返回false（当文件在时返回true）
	 * @throws IOException 
	 */
	public boolean createFile(File file) throws IOException{
		if(getExternalStorageState()){
			if(file != null && !file.exists()){
				return file.createNewFile();
			}
			if(file.exists()) return true;
		}else{
			GHLog.gHLog(GHLog.LOG_ERROR, "SD 卡不可用");
		}
		return false;
	}
}
