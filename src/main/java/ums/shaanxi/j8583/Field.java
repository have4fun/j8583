package ums.shaanxi.j8583;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;

/**
 * ISO8583 Field，其中value采用ascii码行进数据存储
 * 注意：this.length与value.length，
 * format=LVAR,LLVAR,LLLVAR 存放了这个域的最大值
 * format=hhmmss,MMDD,YYMM,DEFAULT 存放了这个域的固定值
 * 
 * this.length 指字符、数字数量
 * value.length 指实际存放字节的存储空间大小
 * 
 * 例如：
 * if (type==BCD) (this.length+1)/2==value.length
 * if (type==ASCII) this.length==value.length
 * if (type==BINARY) (this.length/8)==value.length
 */
public class Field implements Cloneable,Serializable {
	
	private static final long serialVersionUID = 1836187927830339302L;
	
	private int num;
	private String name;
	private byte[] value;
	private FieldFormat format;
	private FieldType type;
	private int length;
	
	@Override
	public String toString(){
		switch(type){
			case BCD:
				return Tools.byte2hex(toByte());
			case ASCII:
				return Tools.byte2hex(toByte());
			case BINARY:
				return Tools.byte2hex(toByte());
			case DEFAULT:
				return new String(toByte(),Charset.forName("US-ASCII"));
			default:
				return Tools.byte2hex(toByte());
		}
	}
	/**
	 * 按照field的format，来进行字节编码
	 * LLVAR,LLLVAR,LVAR,hhmmss,MMDD,YYMM
	 * 
	 * 对长度的处理
	 * 长度=3939,压缩为bcd码得到长度99
	 * 长度=30313939 压缩为bcd码0199
	 * format=bcd码时，其存储长度为(this.length+1)/2
	 * format=binary时，其存储长度为this.length/8
	 * @return
	 */
	public  byte[] toByte(){
		byte [] length_byte=null;
		byte[] buffer=null;
		if(format==null) this.format=FieldFormat.DEFAULT;
		try{
			switch(format){
				case LLVAR:
					length_byte=Tools.INT2BCD(this.length, format);
					break;
				case LLLVAR:
					length_byte=Tools.INT2BCD(this.length, format);
					break;
				case LVAR:
					length_byte=Tools.INT2BCD(this.length, format);
					break;
				case DEFAULT:
					length_byte=new byte[0];
					break;
				default:
					length_byte=new byte[0];
			}
			
			if(type==null) throw new RuntimeException("type is null");
			
			byte[] tmp=null;
			
			switch(type){
				case BCD:
					tmp= Tools.ASCII2BCD(this.value);
					buffer=new byte[length_byte.length+(this.length+1)/2];
					break;
				case ASCII:
					tmp= this.value;
					buffer=new byte[length_byte.length+this.length];
					break;
				case BINARY:
					tmp= this.value;
					buffer=new byte[length_byte.length+this.length/8];
					break;
				case DEFAULT:
					tmp= this.value;
					break;
				default:
					tmp= this.value;
			}
			
			System.arraycopy(length_byte, 0, buffer, 0, length_byte.length);
			System.arraycopy(tmp, 0, buffer, length_byte.length, tmp.length);
			
			
			//System.out.println(length_byte.length);
			//System.out.println(Tools.byte2hex(length_byte));
			//System.out.println(Tools.byte2hex(buffer));

			return buffer;
			
		}catch(Exception ex){
			
			throw new RuntimeException(ex);
		}
	}
	
	public void valueof(Date value,FieldFormat format ){
		switch(format){
			case hhmmss:
				this.valueof(String.format("%1$tH%1$tM%1$tS",value).getBytes(Charset.forName("US-ASCII")));
				break;
			case MMDD:
				this.valueof(String.format("%1$tm%1$td",value).getBytes(Charset.forName("US-ASCII")));
				break;
			case YYMM:
				this.valueof(String.format("%1$ty%1$tm",value).getBytes(Charset.forName("US-ASCII")));
				break;
			default:
				throw new RuntimeException();
		}
	}
	
	/**
	 * @param bcdString
	 * @param length
	 *  bcdString 先转换成ascii,存放到value
	 *  
	 */
	public void valueof(String bcdString,int length){
		byte[] value=Tools.BCD2ASCII(Tools.hexStringToByte(bcdString), length);
		this.valueof(value);
	}
	
	
	
	/**
	 * 通过16进制字符串创建Field
	 * @param hexString
	 */
	public void valueof(String hexString){
		byte[] value=Tools.hexStringToByte(hexString);
		this.valueof(value);
	}
	
	/**
	 * 通过BigDecimal创建Field,默认采用四舍五入,金额一般作为BCD码进行放入
	 * @param number
	 */
	public void valueof(BigDecimal value,int length){
		/*四舍五入*/
		try{
			value=value.setScale(2, BigDecimal.ROUND_HALF_UP);
			this.valueof(String.format("%1$0"+length+"d",value.multiply(BigDecimal.valueOf(100)).intValue()).getBytes("US-ASCII"));
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
	public void valueof(byte[]bcd_value,int length){
		this.valueof(Tools.BCD2ASCII(bcd_value, length));
	}
	
	/**
	 * Field 中使用ascii来储存数据,对于边长字段
	 * 
	 * 一旦值被初始化，则将边长存放到.legth
	 * 
	 * @param asc_value
	 */
	public void valueof(byte[] asc_value){
		if(format==null){
			switch(type){
				case BINARY:
					if(asc_value.length!=this.length/8) throw new RuntimeException("Set value's length ("+ asc_value.length +") doesn't equal with predefining length (" + this.length/8 +")" );
					break;
				default:
					if(asc_value.length!=this.length) throw new RuntimeException("Set value's length ("+ asc_value.length +") doesn't equal with predefining length (" + this.length +")");
			}
			this.value=asc_value;
		}
		else{
			switch(format){
				case LLVAR:
					if(asc_value.length>this.length) throw new RuntimeException("Set value's length greater than (>) predefining length ");
					break;
				case LLLVAR:
					if(asc_value.length>this.length) throw new RuntimeException("Set value's length greater than (>) predefining length ");
					break;
				case LVAR:
					if(asc_value.length>this.length) throw new RuntimeException("Set value's length greater than (>) predefining length ");
					break;
				default:
					if(asc_value.length!=this.length) throw new RuntimeException("Set value's length doesn't equal with predefining length ");
			}
			this.value=asc_value;
		}
	}
	
	
	public void valueof(byte[] asc_value,int off,int length){
		byte[] data=Arrays.copyOfRange(asc_value, off, off+length);
		valueof(data);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getValue() {
		return value;
	}
	public void setValue(byte[] value) {
		this.value = value;
	}
	public FieldFormat getFormat() {
		return format;
	}
	public void setFormat(FieldFormat format) {
		this.format = format;
	}
	public FieldType getType() {
		return type;
	}
	public void setType(FieldType type) {
		this.type = type;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	/**
	 * 浅克隆
	 */
	@Override
	public Object clone(){
		Object o = null;
		try {
			o = (Field) super.clone();// Object中的clone()识别出你要复制的是哪一// 个对象。
		} catch (CloneNotSupportedException e) {
			System.out.println(e.toString());
		}
		return o;
	}
	
	/**
	 * 通过对象的序列化完成对象深度克隆
	 * @return
	 */
	public Object deepClone(){
		try{
			//将对象写到流里
			ByteArrayOutputStream bo=new ByteArrayOutputStream();
			ObjectOutputStream oo=new ObjectOutputStream(bo);
			oo.writeObject(this);
			//从流里读出来
			ByteArrayInputStream bi=new ByteArrayInputStream(bo.toByteArray());
			ObjectInputStream oi=new ObjectInputStream(bi);
			return(oi.readObject());
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}

	
}
