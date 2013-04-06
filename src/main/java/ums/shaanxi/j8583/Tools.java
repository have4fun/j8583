package ums.shaanxi.j8583;

import java.nio.ByteBuffer;
import java.util.Arrays;


/**
 * @author Administrator
 * 工具类，用于各种BCD码、ASCII码、二进制码的转换
 */
public abstract class Tools {
	
	 public static byte[] shortToByteArray(short s) {
		  byte[] shortBuf = new byte[2];
		  for(int i=0;i<2;i++) {
		     int offset = (shortBuf.length - 1 -i)*8;
		     shortBuf[i] = (byte)((s>>>offset)&0xff);
		  }
		  return shortBuf;
	 }
	
	
	//ByteToHexString
	public static String byte2hex(byte[] b) {
		
		if(b==null) return null;
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}
	public static String byte2hex(byte[] b,int off,int len) {
		
		byte[] data=Arrays.copyOfRange(b, off, off+len);
		
		return byte2hex(data);
	}
	
	
	//HexStringToByte
	public static byte[] hexStringToByte(String hex) {
		hex=hex.toUpperCase();
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}
	
	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
		
	private static byte abcd_to_asc(byte abyte)
	{
		if (abyte <= 9)
			abyte = (byte) (abyte + '0');
		else
			abyte = (byte) (abyte + 'A' - 10);
		return (abyte);
	}

	public static int BCD2INT(byte []bcd_buf,int len){
		try{
			return Integer.parseInt(new String(BCD2ASCII(bcd_buf,len),"US-ASCII"));
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
		
	}
	
	public static byte[] BCD2ASCII(byte [] bcd_buf,int len)
	{
		int i,n;
		n=len;
		ByteBuffer asc_buf=ByteBuffer.allocate(n);
		byte tmp;
		for (i = 0; i < n/2; i++) {
			tmp = (byte) ((bcd_buf[i] & 0xf0) >> 4);
			tmp = abcd_to_asc(tmp);
			asc_buf.put(tmp);
			tmp = (byte) (bcd_buf[i] & 0x0f);
			tmp = abcd_to_asc(tmp);
			asc_buf.put(tmp);
		}
		if (n % 2!=0) {
			tmp = (byte) ((bcd_buf[i] & 0xf0) >> 4);
			tmp = abcd_to_asc(tmp);
			asc_buf.put(tmp);
		}
		asc_buf.flip();
	    byte[] res=null;
    	res=new byte[asc_buf.remaining()];
    	asc_buf.get(res, 0, res.length);
    	return res;
	}

	private static byte aasc_to_bcd(byte asc)
	{
		byte bcd;

		if ((asc >= '0') && (asc <= '9'))
			bcd = (byte) (asc - '0');
		else if ((asc >= 'A') && (asc <= 'F'))
			bcd = (byte) (asc - 'A' + 10);
		else if ((asc >= 'a') && (asc <= 'f'))
			bcd = (byte) (asc - 'a' + 10);
		else if ((asc > 0x39) && (asc <= 0x3f))
			bcd = (byte) (asc - '0');
		else {
			/*		printf( "\f[Warning] : Bad HEX digid" );
					bcd = 0;
			*/ 		
			bcd = 0x0f;
		}
		return bcd;
	}
	//ASCII2BCD	
	public static byte[] ASCII2BCD(byte[] asc_buf)
	{
		int j=0;
		int n=asc_buf.length;
		byte []bcd_buf=new byte[(n+1)/2];
		for (int i = 0; i < (n+1)/2; i++) {
			bcd_buf[i] = aasc_to_bcd(asc_buf[j++]);
			bcd_buf[i] = (byte) (((j >= n) ? 0x00 : aasc_to_bcd(asc_buf[j++]))+ (bcd_buf[i] << 4));
		}
		return bcd_buf;
	}
	public static byte[] ASCII2BCD(byte[] asc_buf,int off,int len)
	{
		byte[] data=Arrays.copyOfRange(asc_buf, off, off+len);
		
		return ASCII2BCD(data);
	}
	
	
	/**
	 * LLVAR 压缩为1个字节
	 * LLVAR 压缩为2个字节
	 * LVAR  压缩为1个字节
	 * 
	 * 例如：
	 * LLVAR
	 * int =1,--->"01"-asc2bcd--->hex 01
	 * 
	 * @param i
	 * @param format
	 * @return
	 * @throws Exception
	 */
	public static byte[] INT2BCD(int i,FieldFormat format) throws Exception{
		switch(format){
			case LLVAR:
				return ASCII2BCD(String.format("%1$02d",i).getBytes("US-ASCII"));
			case LLLVAR:
				return ASCII2BCD(String.format("%1$04d",i).getBytes("US-ASCII"));
			case LVAR:
				return ASCII2BCD(String.format("%1$02d",i).getBytes("US-ASCII"));
			default:
				throw new RuntimeException();
		}	
		
	}
}
