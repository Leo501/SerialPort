/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;
/**
 * @author Administrator
 *
 */
public class SerialPort {

	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {

		/* Check access permission */
		//// ����Ƿ��ȡ��ָ�����ڵĶ�дȨ��
		if (!device.canRead() || !device.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				// ���û�л�ȡָ�����ڵĶ�дȨ�ޣ���ͨ�����ڵ�linux�ķ�ʽ�޸Ĵ��ڵ�Ȩ��Ϊ�ɶ�д
				Process su;
				su = Runtime.getRuntime().exec("su");
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}

		mFd = open(device.getAbsolutePath(), baudrate, flags);
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	// JNI
	// JNI������java���ؽӿڣ�ʵ�ִ��ڵĴ򿪺͹ر�
    /**
     * �����������Ҫ�Ĳ����������豸���������ʣ�����λ������λ��ֹͣλ
     * ���м���λһ��Ĭ��λNONE,����λһ��Ĭ��Ϊ8��ֹͣλĬ��Ϊ1
     * @param path �����豸�ľݶ�·��
     * @param baudrate ������
     * @param flags �����������ʱ��Ϊ��У��λ�� ���ڱ���Ŀ������������岻��
     * @return
     */
	private native static FileDescriptor open(String path, int baudrate, int flags);//�򿪴���
	public native void close();//�رմ���
	static {
		System.loadLibrary("serial_port");
	}
}
