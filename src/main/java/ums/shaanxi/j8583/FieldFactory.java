package ums.shaanxi.j8583;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * date 2013/4/1
 * 
 * FieldFactory 用来创建Field。
 * 可以通过
 * createField(int number,
			String fieldName,int length,
			FieldType type,FieldFormat format,String hexString )
			
 * 来直接创建
 * 
 * 也可以根据预定义的predefineXml来预定义，来完成创建
 * 
 * 通过FieldFactory来创建Field、是通过一个更加直观的方式来创建Field
 * 
 * 
 */
public class FieldFactory {
	
	public Map<Integer,Field>  define =new TreeMap<Integer,Field>();
	
	private static Logger logger = LoggerFactory.getLogger(FieldFactory.class);
	
	private String predefineXml;
	
	/**
	 * 初始化FieldFactory
	 */
	public FieldFactory(){
		/*默认采用classpath下面的ISO8583Def.xml,默认采用utf-8进行处理*/
		
		InputStreamReader inputStreamReader=null;
		try{
			inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("ISO8583Def.xml")
					,Charset.forName("utf-8"));
			predefine(inputStreamReader);
			
			logger.debug("FieldFactory complete,define size={}",define.size());
		}catch(Exception ex){
			logger.error("",ex);
			throw new RuntimeException(ex);
		}finally{
			try{
				inputStreamReader.close();
			}catch(Exception e){
				logger.error("",e);
				throw new RuntimeException(e);
			}
		}
		
	}
	/**
	 * @param predefineXml classpath下的predefineXml
	 */
	public FieldFactory(String predefineXml,String charset){
		if(predefineXml==null||charset==null) throw new IllegalArgumentException("predefineXml is null or charset is null");
		
		InputStreamReader inputStreamReader=null;
		try{
			inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(predefineXml)
					,Charset.forName(charset));
			predefine(inputStreamReader);
		}catch(Exception ex){
			logger.error("",ex);
			throw new RuntimeException(ex);
		}finally{
			try{
				inputStreamReader.close();
			}catch(Exception e){
				logger.error("",e);
				throw new RuntimeException(e);
			}
		}
		
	}
	
	/**
	 * 
	 * @param reader 直接构造reader
	 */
	public FieldFactory(Reader reader){
		predefine(reader);
	}

	/**
	 * 默认采用classpath的ISO8583Def
	 */
	public void predefine(Reader inputXml){
		SAXReader saxReader = null;
		try{
			saxReader=new SAXReader();
	        Document document = saxReader.read(inputXml);
	        /*获取根节点*/
	        Element rootElm = document.getRootElement();
	        
	        /*循环遍历xml*/
	        for(Iterator it=rootElm.elementIterator();it.hasNext();){  
	        	/*
	        	 * 	private int num;
					private String name;
					private byte[] value;
					private FieldFormat format;
					private FieldType type;
					private int length;
	        	 */
                Element element = (Element) it.next();
                Field field=new Field();
                field.setNum(element.attributeValue("num")!=null?Integer.parseInt(element.attributeValue("num")):0);
                field.setName(element.attributeValue("name"));
                String format=element.attributeValue("format");
                if(format!=null){
                	FieldFormat fieldFormat =FieldFormat.valueOf(format);
                	field.setFormat(fieldFormat);
                }
                String type=element.attributeValue("type");
                if(type!=null){
                	FieldType fieldType =FieldType.valueOf(type);
                	field.setType(fieldType);
                }
                field.setLength(element.attributeValue("length")!=null?Integer.parseInt(element.attributeValue("length")):0);
                
                define.put(field.getNum(), field);
            }
        
		}catch(Exception ex){
			logger.error("",ex);
			throw new RuntimeException(ex);
		}finally{
		}
        
	}
	
	
	public Field createField(int number,
			String fieldName,int length,
			FieldType type,FieldFormat format,String hexString ){
		
		Field field =new Field();
		field.setName(fieldName);
		field.setValue(Tools.hexStringToByte(hexString));
		field.setType(type);
		field.setFormat(format);
		return field;
	}
	
	public  Field createField(int number,
			String fieldName,int length,
			FieldType type,FieldFormat format,byte[] data ){
		
		Field field =new Field();
		field.setName(fieldName);
		field.setValue(data);
		field.setType(type);
		field.setFormat(format);
		return field;
	}
	
	public  Field createField(int number,
			String fieldName,int length,
			FieldType type,FieldFormat format,byte[] b,int off,int len ){
		
		byte[] data=Arrays.copyOfRange(b, off, off+len);
		
		Field field =new Field();
		field.setName(fieldName);
		field.setValue(data);
		field.setType(type);
		field.setFormat(format);
		return field;
	}
	
	/**
	 * 采用ascii byte数组，创建field,非变长
	 * @param number
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 */
	public  Field createFieldByPredefine(int number,byte[] b,int off,int len ){
		
		byte[] data=Arrays.copyOfRange(b, off, off+len);
		
		return createFieldByPredefine(number,data);
	}
	/**
	 * 采用ascii byte数组，创建field,非变长
	 * @param number
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 */
	public Field createFieldByPredefine(int number,byte[]data){
		
		Field field =(Field)this.define.get(number).deepClone();
		
		field.valueof(data);
		
		return field;
	}
	
	
	
	/**
	 * 采用ascii byte数组，创建field,使用与域为变长的方式。
	 * @param number
	 * @param data    数据
	 * @param length  字数个数，一些情况下 !=data.length
	 * 
	 * BCD：(length+1)/2=data.length
	 * binary:(length)/8=data.length
	 * ascii:length=data.length
	 * 
	 * @return
	 */
	public Field createFieldByPredefine(int number,byte[]data,int length){
		
		Field field =(Field)this.define.get(number).deepClone();
		
		field.valueof(data);
		
		switch(field.getType()){
			case BCD:
				if((length+1)/2!=data.length) throw new RuntimeException("Set value's length ("+ data.length +") doesn't equal with predefining length (" + (length+1)/2 +")" );
				break;
			case ASCII:
				if(length!=data.length) throw new RuntimeException("Set value's length ("+ data.length +") doesn't equal with predefining length (" + length +")");
				break;
			case BINARY:
				if((length/8)!=data.length) throw new RuntimeException("Set value's length ("+ data.length +") doesn't equal with predefining length (" + (length/8) +")");
				break;	
		}
		field.setLength(length);
		return field;
	}
	
	public Field createFieldByPredefine(int number){
		
		Field field =(Field)this.define.get(number).deepClone();
	
		return field;
	}
	
	/**
	 * 使用bcd码16进制字符串，来创建field
	 * 
	 * @param number
	 * @param bcdString
	 * @param length
	 * @return
	 */
	public Field createFieldByPredefine(int number,String bcdString,int length){
		
		Field field =(Field)this.define.get(number).deepClone();
		
		field.valueof(bcdString, length);

		return field;
	}
	
	/**
	 * 使用BigDecimal，来创建field
	 * 
	 * @param number
	 * @param amount
	 * @param length
	 * @return
	 */
	public Field createFieldByPredefine(int number,BigDecimal amount,int length){
		
		Field field =(Field)this.define.get(number).deepClone();
		
		field.valueof(amount, length);

		return field;
	}
	
	/**
	 * 使用Date，来创建field
	 * 
	 * @param number
	 * @param amount
	 * @param length
	 * @return
	 */
	public Field createFieldByPredefine(int number,Date dateTime,FieldFormat format){
		
		Field field =(Field)this.define.get(number).deepClone();
		
		field.valueof(dateTime, format);

		return field;
	}
	

	
	/**
	 * 使用ascii十六进制字符串，来创建field
	 * 
	 * @param number
	 * @param hexString
	 * @return
	 */
	public Field createFieldByPredefine(int number,String string){
		
		Field field =(Field)this.define.get(number).deepClone();
		
		field.valueof(string);

		return field;
	}
	
	
	
	public Map<Integer, Field> getDefine() {
		return define;
	}
	public void setDefine(Map<Integer, Field> define) {
		this.define = define;
	}
	public String getPredefineXml() {
		return predefineXml;
	}
	public void setPredefineXml(String predefineXml) {
		this.predefineXml = predefineXml;
	}
	
}
