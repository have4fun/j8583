package ums.shaanxi.j8583;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Administrator
 * UnionPayMessage64Factory,用来创建银联PPP（公共支付终端规范3.x）的message
 * 注意：
 * 对长度的处理：LLVAR 0-99 压缩为1个字 LVAR 0-9 1个字节 LLLVAR 0-999 压缩为2个字节
 * 
 * 在通过byte[]创建message时,如果遇到变长类型,则在field.length中存放变长的长度。
 * 
 * 预定义xml中length是指字符的数量
 * 
 * 例如：messagetype 长度为4，实际存储采用bcd码，只用2个字节。
 * 
 * 在解析byte[]时,遇到BCD码的域时，需要将预定义xml中的(length+1)/2来获取实际需要读取字节长度。
 * 
 * 而binary类型，也是同样的方式进行处理。(length/8)来获取实际需要读取的字节长度。
 *
 */
public class UnionPayMessage64Factory implements IMessageFactory <ISO64Message> {

	private static Logger logger = LoggerFactory.getLogger(UnionPayMessage64Factory.class);
	
	private FieldFactory fieldFactory;
	
	public FieldFactory getFieldFactory() {
		return fieldFactory;
	}

	public void setFieldFactory(FieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
	}

	@Override
	public ISO64Message createMessage() {
		return new ISO64Message();
	}
	

	@Override
	public ISO64Message createMessage(byte[] data) {
		ISO64Message message=null;
		ByteArrayInputStream input=null;
		Field field=null;
		byte []buffer=new byte[2048];
    	try{
    		message=new ISO64Message();
    		logger.debug("[data.length]={}",data.length);
    		logger.debug("[data]={}",Tools.byte2hex(data));
    		input=new ByteArrayInputStream(data);
    		/*读取isoHeader*/
    		input.read(buffer, 0, 6);
    		message.setIsoHeader(Tools.byte2hex(buffer,0,6));
    		logger.debug("[header]={}",message.getIsoHeader());
    		/*读取messageType*/
    		input.read(buffer,0,2);
    		Field messageType=fieldFactory.createFieldByPredefine(1,
    				Tools.BCD2ASCII(Arrays.copyOfRange(buffer,0,2),4));
    		
    		message.getFields().put(messageType.getNum(),messageType);
    		logger.debug("[messagetype]="+messageType.toString());
    		
    		/*读取bitmap*/
    		input.read(buffer,0,8);
    		byte[] bitmap=Arrays.copyOfRange(buffer, 0, 8);
    		logger.debug("[bitmap]="+Tools.byte2hex(bitmap));
    		Set<Integer> bitmap_set=bitMap2Set(bitmap);
    		
    		for(Integer one:bitmap_set){
    			
    			field=fieldFactory.createFieldByPredefine(one.intValue());
    			/*logger.debug("fieldNum={},fieldName={},type={},format={},length={}",
    					field.getNum(),field.getName(),field.getType(),field.getFormat(),field.getLength());*/
    			if(field.getFormat()==null){
    				switch(field.getType()){
	    				case ASCII:
	    					input.read(buffer,0,field.getLength());
	    					field.valueof(buffer, 0, field.getLength());
	    					break;
	    				case BCD:
	    					input.read(buffer,0,(field.getLength()+1)/2);
	    					field.valueof(Arrays.copyOfRange(buffer, 0, (field.getLength()+1)/2),field.getLength());
	    					break;
	    				case BINARY:
	    					input.read(buffer,0,field.getLength()/8);
	    					field.valueof(Arrays.copyOfRange(buffer, 0, field.getLength()/8));
	    					break;
	    				default:
	    					throw new RuntimeException();
    				}
    			}else{
    				int realLength=0;
    				switch(field.getFormat()){
	    				case LLVAR:
	    					input.read(buffer,0,1);
	    					/*报文中，LLVAR压缩完为1个字节，从BCD码转换为2个字节的ascii码，在转化为int类型*/
	    					realLength=Tools.BCD2INT(Arrays.copyOfRange(buffer, 0, 1), 2);
	    					/*将真实的长度存放在field.length*/
	    					field.setLength(realLength);
	    					//logger.debug("realLength={}",realLength);
	    					break;
	    				case LLLVAR:
	    					input.read(buffer,0,2);
	    					realLength=Tools.BCD2INT(Arrays.copyOfRange(buffer, 0, 2), 4);
	    					/*将真实的长度存放在field.length*/
	    					//logger.debug("realLength={}",realLength);
	    					field.setLength(realLength);
	    					break;
	    				case LVAR:
	    					input.read(buffer,0,1);
	    					realLength=Tools.BCD2INT(Arrays.copyOfRange(buffer, 0, 1), 1);
	    					/*将真实的长度存放在field.length*/
	    					field.setLength(realLength);
	    					break;
	    				default:
	    					realLength=field.getLength();
    				}
    				/*类型为BINARY，没有format属性*/
    				switch(field.getType()){
	    				case ASCII:
	    					input.read(buffer,0,realLength);
	    					field.valueof(buffer, 0, realLength);
	    					break;
	    				case BCD:
	    					/*读取BCD压缩的字节,即长度标示为i,实际的长度为(i+1)/2*/
	    					input.read(buffer, 0, (realLength+1)/2);
	    					field.valueof(Arrays.copyOfRange(buffer, 0, (realLength+1)/2),realLength);
	    					break;
	    				default:
	    					input.read(buffer,0,realLength);
	    					field.valueof(buffer, 0, realLength);
    				}
    			}
    			logger.debug("fieldNum={},fieldName={},value={},toString={},type={},format={},length={}",
    					field.getNum(),field.getName(),Tools.byte2hex(field.getValue()),field.toString(),field.getType(),field.getFormat(),field.getLength());
    			message.getFields().put(field.getNum(),field);
    		}
    		return message;
    	}catch(Exception ex){
    		logger.error("createMessage failed.",ex);
            throw new RuntimeException(ex);
    	}finally{
    		try{
    			input.close();
    		}catch(Exception ex){
    			logger.error("createMessage failed,reader close failed", ex);
    		}
    	}
	}
	
	/**
	 * create ISO64Message by hexdata
	 * @param hexdata
	 * @return
	 */
	/*
	public ISO64Message createMessage(String hexdata) {

		try{
			
		}catch(Exception ex){
    		logger.error("createMessage by hexdata failed." + ex);
            throw new RuntimeException(ex);
		}
	}
	*/
	private Set<Integer> bitMap2Set(byte[] bitmap){
		Set<Integer> sets=new TreeSet<Integer>();
		StringBuilder string_bitmap=new StringBuilder();
		for(byte b:bitmap){
			//logger.debug(String.format("%1$08d",Integer.parseInt(Integer.toBinaryString(b&0xFF))));
			string_bitmap.append(String.format("%1$08d",Integer.parseInt(Integer.toBinaryString(b&0xFF))));
		}
		//logger.debug("string_bitmap={}",string_bitmap);
		logger.debug("string_bitmap=["+string_bitmap+"]");
		for(int i=1;i<string_bitmap.toString().toCharArray().length;i++){
			if(string_bitmap.toString().toCharArray()[i]=='1')
			{
				sets.add(i+1);
			}
		}
		return sets;
	}


}
