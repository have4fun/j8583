package ums.shaanxi.j8583;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UnionPayMessage64FactoryTest {
	
	private byte[] data;
	private IMessageFactory<ISO64Message> factory;
	private String data1;
	
	
	@Before
	public void before() throws Exception{
		/*
		String data1="6021000000000200702004c030c198"+
		"0119622202370001558759701900000000000010"+
		"0000338202100006376222023700015587597d49"+
		"1212030899912880010499622202370001558759"+
		"7d15615600000000000000033089992160000491"+
		"2000000000000000000000d000000000000d0000"+
		"0000313035303032303338393836313830343133"+
		"3130303032002450414445303530413033323031"+
		"3330343031303733323032313536c7b6cc5fd81a"+
		"c18c26000000000000007e40bb5bee8fb639";
		*/
		/*String data1="60220000000008000020000000c00012002960323934313533303138393836313830353938333031353300110000000000100003303120";
		*/
		
		data1="6022000000000200202004c030c1981131000000410602100006376228482941096991511d491212077093600000010499622848" +
	    "2941096991511d156156000000000000000000000011414144912dd000000000000d000000000000d092347000000000323934323239303138393836313830353" +
	    "93833303232390000313536c8e14e9c5b92918d1000000000000000000801000000317fe662045b4d48";
		
		/*初始化FildFactory*/
		FieldFactory fieldFactory =new FieldFactory();
		/*初始化IMessageFactory*/
		factory=new UnionPayMessage64Factory();
		
		factory.setFieldFactory(fieldFactory);

		this.data=Tools.hexStringToByte(data1);

	}
	
	
	@Test
	public void createMessageTest(){
		
		ISO64Message message = factory.createMessage(data);
		byte[]t =message.packeage();
		
		System.out.println(Tools.byte2hex(t));
		
		assertEquals(data1.toUpperCase(),Tools.byte2hex(t));
		
	}
}
