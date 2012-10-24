package org.wicketstuff.pageserializer.kryo2.inspecting;

import org.apache.wicket.util.lang.Bytes;
import org.wicketstuff.pageserializer.kryo2.KryoSerializer;
import org.wicketstuff.pageserializer.kryo2.inspecting.listener.ISerializationListener;

import com.esotericsoftware.kryo.Kryo;

/**
 * serializer with serialization process hooks
 * 
 * @author mosmann
 */
public class InspectingKryoSerializer extends KryoSerializer
{

	private final ISerializationListener serializingListener;

	/**
	 * 
	 * @param size
	 *            buffer size, write will fail if buffer is to small
	 * @param serializingListener
	 *            serialization listener
	 */
	public InspectingKryoSerializer(Bytes size, ISerializationListener serializingListener)
	{
		super(size);
		this.serializingListener = serializingListener;
	}

	@Override
	protected Kryo createKryo()
	{
		return new InspectingKryo(this);
	}

	@Override
	public byte[] serialize(Object object)
	{
		RuntimeException exceptionIfAny = null;
		byte[] ret;
		try
		{
			serializingListener.begin(object);
			ret = super.serialize(object);
		}
		catch (RuntimeException ex)
		{
			exceptionIfAny = ex;
			throw ex;
		}
		finally
		{
			serializingListener.end(object, exceptionIfAny);
		}
		return ret;
	}

	protected final ISerializationListener serializingListener()
	{
		return serializingListener;
	}
}