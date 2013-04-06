package ums.shaanxi.j8583;

public interface IMessageFactory<T extends IMessage>{
		
	public T createMessage();
	public T createMessage(byte[] data);
	public FieldFactory getFieldFactory();
	public void setFieldFactory(FieldFactory fieldFactory);

}
