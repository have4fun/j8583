package ums.shaanxi.j8583;

import java.util.Set;
import java.util.TreeSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;

public class ISO64MessageTest {
	
	private ISO64Message message;

	@Before
	public void before(){
		this.message=new ISO64Message();
	}
	
	@Test
	public void createbitMapTest(){
		
		Set<Integer> sets =new TreeSet<Integer>();
		
		for(int i=1;i<10;i++){
			sets.add(i);
		}
		
		byte []predictResult={0x7F,(byte) 0x80,0x00,0x00,0x00,0x00,0x00,0x00};
		
		byte[] result=this.message.createbitMap(sets);
		
		assertArrayEquals(result,predictResult);

	}
	
	
	
	
	
		
}
