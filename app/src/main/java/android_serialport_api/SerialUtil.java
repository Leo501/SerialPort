package android_serialport_api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**单例模式下的串口通信类
 * Created by Administrator on 2016/8/26.
 */
public enum SerialUtil {
    INIT;
    private SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;
    private static final int MAX =512;
    private byte[] buffer;
    private int size=-1;

    /**
     *
     * @param path 路径
     * @param baudrate 波特率
     * @param flags 标志位
     * @throws NullPointerException 有错误的话，抛出异常
     */
    public void init(String path, int baudrate, int flags)throws NullPointerException {
        try {
            serialPort=new SerialPort(new File(path),baudrate,flags);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (SecurityException e){
            e.printStackTrace();
        }
        if (serialPort!=null){
            inputStream=serialPort.getInputStream();
            outputStream=serialPort.getOutputStream();
        }else throw new NullPointerException("串口设置有误");
    }
    /**
     * 串口读数据,直接把Byte类型转成String类型
     * @return 直接返回String
     */
    public String getData() throws NullPointerException{
        //上锁，每次只能一个线程在取得数据
        try {
            byte [] buffer=new byte[MAX];
            if (inputStream==null) throw new NullPointerException("inputStream is null");
            size=inputStream.read(buffer);
            if (size>0) return buffer.toString();
            else return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     *
     * @return byte[] 返回一个接收的数据
     * @throws NullPointerException 如果inputStream为空，就抛出异常
     */
    public byte[] getDataByte()throws NullPointerException{
        if (inputStream==null) throw new NullPointerException("is null");
        try {
            int size=inputStream.available();
            if (size>0){
                buffer=new byte[size];
                inputStream.read(buffer);
                return buffer;
            }else return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 发送数据
     * @param bytes 显示的16进制的字符串
     */
    public boolean setData(byte[] bytes) throws NullPointerException{
        if (outputStream==null) throw new NullPointerException("outputStream为空");
        try {
            outputStream.write(bytes);
            return true;//发送成功
        } catch (IOException e) {
            e.printStackTrace();
            return false;//发送失败
        }
    }
    /**
     * 关闭串口
     */
    public void closeSerialport(){
        if (serialPort!=null){
            serialPort.close();
            serialPort=null;
        }

    }
}
