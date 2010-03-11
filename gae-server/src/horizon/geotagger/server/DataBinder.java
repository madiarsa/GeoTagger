package horizon.geotagger.server;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class DataBinder
{
	public void bind(Object o, Map<String, String[]> params)
	{
		Class<?> clazz = o.getClass();
		for(Field field : clazz.getDeclaredFields())
		{
			String name = field.getName();
			if(!params.containsKey(name))
				throw new RuntimeException("Required parameter '" 
						+ name + "' was not provided");
			Object value = cast(field, params.get(name));
			try
			{
				set(clazz, field, o, value);
			}
			catch(Exception e)
			{
				throw new RuntimeException("Unable to bind parameter '" 
						+ name + "'", e);
			}
		}
	}
	
	private Object cast(Field field, String value[])
	{
		if(value == null || value.length == 0)
			return null;
		Class<?> clazz = field.getType();
		if(clazz.isPrimitive() && !clazz.isArray())
		{
			if(value[0] == null || value[0].length() == 0)
				return null;
			if(clazz == Boolean.TYPE)
				return Boolean.parseBoolean(value[0]);
			if(clazz == Byte.TYPE)
				return Byte.parseByte(value[0]);
			if(clazz == Character.TYPE)
				return value[0].charAt(0);
			if(clazz == Double.TYPE)
				return Double.parseDouble(value[0]);
			if(clazz == Float.TYPE)
				return Float.parseFloat(value[0]);
			if(clazz == Integer.TYPE)
				return Integer.parseInt(value[0]);
			if(clazz == Long.TYPE)
				return Long.parseLong(value[0]);
			if(clazz == Short.TYPE)
				return Short.parseShort(value[0]);
		}
		
		throw new RuntimeException("Could not convert parameter '"
				+ field.getName() + "' to type '" + clazz.getName() + "'");
	}
	
	private void set(Class<?> clazz, Field field, Object o, Object value) 
	throws	SecurityException,
			NoSuchMethodException,
			IllegalArgumentException,
			IllegalAccessException,
			InvocationTargetException
	{
		String setterName = "set" + Character.toUpperCase(field.getName().charAt(0));
		if(field.getName().length() > 1)
			setterName += field.getName().substring(1);
		
		Method method = clazz.getMethod(setterName, field.getType());
		method.invoke(o, new Object[]{value});
	}
}
