package normal;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import ums.shaanxi.j8583.FieldType;

public class Normaltest {


	@Ignore
	public void enumTest(){
		System.out.println(FieldType.ASCII.toString());
		
		FieldType fieldType=FieldType.valueOf("ASCIII");
		
		switch  (fieldType) {
        case  ASCII:  
            System.out.println("This is a ASCII type");
            break ;  
        case  BCD:  
        	System.out.println("This is a BCD type");
            break ;  
        case  BINARY:  
        	System.out.println("This is a BINARY type");
            break ;  
        }  
	}
	
	@Test
	public void Short2byte(){
		System.out.println(Short.SIZE);
		byte[] length_byte=new byte[0];
		int[] buffer =new int [8];
		int[] fun ={1,2,3,4,5,6};
		int[] fun1={7,8};
		
		System.arraycopy(fun1, 0, buffer, 0, 2);
		System.arraycopy(fun, 0, buffer, 2, 6);
		System.out.println(Arrays.toString(buffer));
		
		System.out.println(length_byte.length);
		
		int[] buffer1 ={1,2,3,4,5,6,7,8};
		int[] fun3=new int [2];
		
		System.out.println(Arrays.toString(Arrays.copyOfRange(buffer1, 3, buffer1.length)));
		System.out.println(String.format("%1$0"+(3-1)*2+"d",104));
		System.out.println(String.format("%1$0"+(2-1)*2+"d",10));
		System.out.println(String.format("%1$0"+(1)*2+"d",9));
	}
}
