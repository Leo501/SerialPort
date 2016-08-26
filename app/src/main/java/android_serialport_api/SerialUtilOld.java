package android_serialport_api;

import android.support.v4.util.Pools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2016/7/22.
 */
public class SerialUtilOld {
    private  SerialPort serialPort;
    private  InputStream inputStream;
    private  OutputStream outputStream;
    private  volatile int size=-1;
    private static final int MAX =512;
    private String path;

    public SerialUtilOld(String path, int baudrate, int flags ) throws NullPointerException{
        try {
            serialPort=new SerialPort(new File(path),baudrate,flags);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (SecurityException e) {
            e.printStackTrace();
        }
        if(serialPort!=null){
            //设置读、写
            inputStream=serialPort.getInputStream();
            outputStream=serialPort.getOutputStream();
        }else throw new NullPointerException("串口设置有误");
    }

    /**
     * 取得byte的长度
     * @return
     */
    public int getSize() {
        return size;
    }

    /**
     * 串口读数据
     * @return
     */
    public synchronized byte[] getData() throws NullPointerException{
        //上锁，每次只能一个线程在取得数据
        try {
            byte [] buffer=new byte[MAX];
            if (inputStream==null) throw new NullPointerException("inputStream is null");
            //一次最多可读Max的长度
            size=inputStream.read(buffer);
            if (size>0) return buffer;
            else return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public synchronized byte[] getDataByte()throws NullPointerException{
        byte [] buffer=new byte[MAX];
        if (inputStream==null) throw new NullPointerException("is null");
        try {
            if (inputStream.available()>0){
                inputStream.read(buffer);
                return buffer;
            }else return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 患上写数据
     * @param data 显示的16进制的字符串
     */
    public synchronized void setData(byte[] data) throws NullPointerException{
        if (outputStream==null) throw new NullPointerException("outputStream为空");
        try {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * byte转hexString
     * @param buffer 数据
     * @param size  字符数
     * @return
     */
    public static String bytesToHexString(final byte[] buffer, final int size){
        StringBuilder stringBuilder=new StringBuilder("");
        if (buffer==null||size<=0) return null;
        for (int i = 0; i <size ; i++) {
            String hex=Integer.toHexString(buffer[i]&0xff);
            if(hex.length()<2) stringBuilder.append(0);
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }

    /**
     * hexString转byte
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString){
        if (hexString==null||hexString.equals("")) return null;
        hexString=hexString.toUpperCase();
        int length=hexString.length()/2;
        char[] hexChars=hexString.toCharArray();
        byte[] d=new byte[length];
        for (int i = 0; i <length ; i++) {
            int pos=i*2;
            d[i]=(byte)(charToByte(hexChars[pos])<<4|charToByte(hexChars[pos+1]));
        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
