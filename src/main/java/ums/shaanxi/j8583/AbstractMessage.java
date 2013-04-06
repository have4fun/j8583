package ums.shaanxi.j8583;

import java.util.Map;

public abstract class AbstractMessage implements IMessage  {

	protected String isoHeader;
	protected byte[] bitmap;
	protected Map<Integer,Field> fields;

	@Override
	public abstract byte[] packeage();

	@Override
	public abstract byte[] createbitMap();

	@Override
	public String getIsoHeader() {
		return isoHeader;
	}
	
	@Override
	public void setIsoHeader(String isoHeader) {
		this.isoHeader = isoHeader;
	}
	
	@Override
	public byte[] getBitmap() {
		return bitmap;
	}
	
	@Override
	public void setBitmap(byte[] bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public Map<Integer, Field> getFields() {
		return fields;
	}

	@Override
	public void setFields(Map<Integer, Field> fields) {
		this.fields = fields;
	}
	
	@Override
	public void setField(Field field){
		fields.put(field.getNum(),field);
	}
	
	@Override
	public Field getField(int num){
		return fields.get(num);
	}
	
}
