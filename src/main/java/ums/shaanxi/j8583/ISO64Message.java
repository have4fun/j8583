package ums.shaanxi.j8583;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISO64Message extends AbstractMessage {

	private static Logger logger = LoggerFactory.getLogger(ISO64Message.class);
	
	public ISO64Message(){
		super();
		this.fields=new TreeMap<Integer,Field>();
	}
	
	private final static byte[][] BITMAP_INIT=
	{
		{(byte) 0x00,0x40,0x20,0x10,0x08,0x04,0x02,0x01},//1
		{(byte) 0x80,0x40,0x20,0x10,0x08,0x04,0x02,0x01},//2
		{(byte) 0x80,0x40,0x20,0x10,0x08,0x04,0x02,0x01},//3
		{(byte) 0x80,0x40,0x20,0x10,0x08,0x04,0x02,0x01},//4
		{(byte) 0x80,0x40,0x20,0x10,0x08,0x04,0x02,0x01},//5
		{(byte) 0x80,0x40,0x20,0x10,0x08,0x04,0x02,0x01},//6
		{(byte) 0x80,0x40,0x20,0x10,0x08,0x04,0x02,0x01},//7
		{(byte) 0x80,0x40,0x20,0x10,0x08,0x04,0x02,0x01} //8
	};
	
	@Override
	public byte[] packeage() {
		byte[] buffer =new byte[2048];
		ByteArrayOutputStream output=null;
		try{
			output=new ByteArrayOutputStream();
			/*ISOheader*/
			output.write(Tools.hexStringToByte(this.isoHeader));
			/*messageType*/
			output.write(fields.get(1).toByte());
			/*bitmap*/
			this.bitmap=createbitMap();
			output.write(this.bitmap);
			/*其他域值*/
			for(Entry<Integer,Field> one :fields.entrySet()){
				if(one.getKey()==1)continue;
				output.write(one.getValue().toByte());
			}
			return output.toByteArray();
				
		}catch(Exception ex){
			logger.error("",ex);
			throw new RuntimeException(ex);
		}finally{
			try{
				output.close();
			}catch(Exception ex){
				logger.error("",ex);
				throw new RuntimeException(ex);
			}
		}
	}
	@Override
	public byte[] createbitMap() {
		return this.createbitMap(this.fields.keySet());
	}
	
	/**
	 * 创建bitmap
	 * @param sets
	 * @return
	 * 创建bitmap,根据初始化的BITMAP_INIT与当前bitmap做或运算，得到bitmap
	 * 注意：默认第一域的bit位是0，不设置为1
	 * 64域报文的bitmap[]=new bitmap[8]
	 * 算法实力如下：{2,3,4,11} 此集合表示2,3,4,11域有值
	 * 计算bitmap时：
	 * 第二域 bitmap[0]=BITMAP_INIT[0][1]|bitmap[0]=0x40|0x00=0x40
	 * 第三域 bitmap[0]=BITMAP_INIT[0][2]|bitmap[0]=0x20|0x40=0x60
	 * 第四域 bitmap[0]=BITMAP_INIT[0][3]|bitmap[0]=0x60|0x10=0x70
	 * 第11域 bitmap[1]=BITMAP_INIT[1][2]|bitmap[1]=0x20|0x00=0x20
	 * 
	 * 得到结果为 0x40,0x20,0x00,0x00,0x00,0x00,0x00,x00
	 */
	public byte[] createbitMap(Set<Integer> sets){
		byte []bitMap=new byte[8];
		for(Integer one:sets){
			int i=(one.intValue()-1)/8;
			int j=(one.intValue()-1)%8;
			logger.debug("one.intValue=[{}]",one.intValue());
			logger.debug("one.intValue()/8=[{}]",i);
			logger.debug("one.intValue()%8=[{}]",j);
			logger.debug("bitMap[{}]=[{}]",i,Integer.toBinaryString(bitMap[i]&0xFF));
			bitMap[i]=(byte) (BITMAP_INIT[i][j]|bitMap[i]);
		    logger.debug("i=[{}]{}",i,String.format("%1$08d",Integer.parseInt(Integer.toBinaryString(bitMap[i]&0xFF))));
		}
		return bitMap;
	}
}
