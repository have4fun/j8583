package ums.shaanxi.j8583;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;


public class FieldTest {
	
	private FieldFactory factory;
	
	@Before
	public void before(){
		
		factory=new FieldFactory();
	}

	
	@Test
	public void deepCloneTest(){
		
		Field field1=new Field();
		field1.setName("This is test field1");
		field1.setFormat(FieldFormat.LVAR);
		
		Field field2=(Field)field1.deepClone();
		field2.setName("This is test field2");
		field2.setFormat(FieldFormat.LLLVAR);
		assertNotEquals(field2.getName(),field1.getName());
		
		System.out.println(field2.getName());
		System.out.println(field1.getName());
		
		System.out.println(field2.getFormat().name());
		System.out.println(field1.getFormat().name());
		
		assertNotEquals(field2.getFormat(),field1.getFormat());
		
	}
	
	/**
	 * 测试：
	 * 第2域、第3域、第4域、第12域、第13域、第14域、第15域
	 * 
	 * 第48域
	 */
	@Test
	public void valueofTest() throws Exception{
		
		/*第2域*/
		Field field =factory.createFieldByPredefine(2, "62220237000155875980",19);
		
		assertEquals(field.toString(),"1962220237000155875980");
		assertEquals(field.getLength(),19);
		
		System.out.println("hexString:"+Tools.byte2hex(field.getValue()));
		System.out.println("tostring："+field.toString());
		System.out.println("length："+field.getLength());
		
		
		/*第3域*/
		field =factory.createFieldByPredefine(3, "190000",6);
		assertEquals(field.toString(),"190000");
		assertEquals(field.getLength(),6);
		System.out.println("hexString:"+Tools.byte2hex(field.getValue()));
		System.out.println("tostring："+field.toString());
		System.out.println("length："+field.getLength());
		
		/*第4域*/
		field =factory.createFieldByPredefine(4, new BigDecimal(100),12);
		assertEquals(field.toString(),"000000010000");
		assertEquals(field.getLength(),12);
		System.out.println("hexString:"+Tools.byte2hex(field.getValue()));
		System.out.println("tostring："+field.toString());
		System.out.println("length："+field.getLength());
		
		/*第12域*/
		field =factory.createFieldByPredefine(12, "140000",6);
		assertEquals(field.toString(),"140000");
		assertEquals(field.getLength(),6);
		System.out.println("hexString:"+Tools.byte2hex(field.getValue()));
		System.out.println("tostring："+field.toString());
		System.out.println("length："+field.getLength());
		
		/*第12域*/
		Date date=new Date();
		
		field =factory.createFieldByPredefine(12, date,FieldFormat.hhmmss);
		assertEquals(field.toString(),String.format("%1$tH%1$tM%1$tS",date));
		assertEquals(field.getLength(),6);
		System.out.println("hexString:"+Tools.byte2hex(field.getValue()));
		System.out.println("tostring："+field.toString());
		System.out.println("length："+field.getLength());
		
		/*第13域*/
		date=new Date();
		field =factory.createFieldByPredefine(13, date,FieldFormat.MMDD);
		assertEquals(field.toString(),String.format("%1$tm%1$td",date));
		assertEquals(field.getLength(),4);
		System.out.println("hexString:"+Tools.byte2hex(field.getValue()));
		System.out.println("tostring："+field.toString());
		System.out.println("length："+field.getLength());
		
		/*第15域*/
		date=new Date();
		field =factory.createFieldByPredefine(15, date,FieldFormat.YYMM);
		assertEquals(field.toString(),String.format("%1$ty%1$tm",date));
		assertEquals(field.getLength(),4);
		System.out.println("hexString:"+Tools.byte2hex(field.getValue()));
		System.out.println("tostring："+field.toString());
		System.out.println("length："+field.getLength());
		
		/*第48域*/
		String data="PADE200#";
		field =factory.createFieldByPredefine(48,data.getBytes("US-ASCII"),data.getBytes("US-ASCII").length);
		//assertEquals(field.toString(),);
		//assertEquals(field.getLength(),8);
		System.out.println("hexString:"+Tools.byte2hex(field.getValue()));
		System.out.println("tostring："+field.toString());
		System.out.println("length："+field.getLength());
		
		
	}
	
}
