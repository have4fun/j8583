package ums.shaanxi.j8583;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

public class FieldFactoryTest {
	
	@Test
	public void FieldFactoryTest(){
		//按照默认classpath下的ISO8583Def.xml
		FieldFactory factory = new FieldFactory();
		
		Map<Integer,Field> define =factory.getDefine();
		
		for(Entry<Integer,Field> one :define.entrySet()){
			System.out.println(one.getKey()+"\t"+
			one.getValue().getName()+"\t"+one.getValue().getFormat()+"\t"+one.getValue().getType()+"\t"+one.getValue().getLength());
		}
	}
}
