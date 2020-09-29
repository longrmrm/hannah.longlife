package com.hannah.common.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

/**
 * @author longrm
 * @date 2012-3-30
 */
public class ObjectUtil {

	public static byte[] objectToBytes(Object obj) {
		if (obj == null)
			return null;

		byte[] data = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			data = bos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try {
				oos.close();
				bos.close();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return data;
	}

	/**
	 * @param bytes objectToBytes() results
	 * @return
	 */
	public static Object bytesToObject(byte[] bytes) {
		Object object = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bis);
			object = ois.readObject();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try {
				ois.close();
				bis.close();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return object;
	}

	public static String objectToBase64String(Object obj) throws UnsupportedEncodingException {
		if (obj == null)
			return "";
		return BaseCoder.encodeBASE64(objectToBytes(obj));
	}

	public static Object base64StringToObject(String base64) throws IOException {
		return bytesToObject(BaseCoder.decodeBASE64(base64));
	}

	/**
	 * deep copy（obj.clone() is shallow copy, only copy properties what obj has）
	 * @param obj
	 * @return
	 */
	public static Object deepCopy(Object obj) {
		return bytesToObject(objectToBytes(obj));
	}

	/**
	 * @param obj implements Serializable interface，transient and static
	 *            variables will ignore
	 * @param output
	 * @throws IOException
	 */
	public static void writeObject(Object obj, OutputStream output) throws IOException {
		if (obj != null) {
			ObjectOutputStream objectOutput = new ObjectOutputStream(output);
			objectOutput.writeObject(obj);
			objectOutput.flush();
			objectOutput.close();
		}
	}

	public static Object readObject(InputStream input) throws IOException, ClassNotFoundException {
		ObjectInputStream objectInput = new ObjectInputStream(input);
		Object object = objectInput.readObject();
		objectInput.close();
		return object;
	}

	/**
	 * get public、protected、private method from self or super class
	 * @param cls
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	public static Method getDeclaredMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
		Method method = null;
		for (Class<?> clazz = cls; clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				method = clazz.getDeclaredMethod(methodName, parameterTypes);
				return method;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static Object invokeMethod(Object obj, String methodName, Object... params) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return invokeMethod(null, obj, methodName, params);
	}

	/**
	 * invoke obj.method(params)
	 * @param cls
	 * @param obj
	 * @param methodName
	 * @param params
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static Object invokeMethod(Class<?> cls, Object obj, String methodName, Object... params) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (cls == null)
			cls = obj.getClass();
		
		Method method = null;
		if (params == null || params.length == 0)
			method = getDeclaredMethod(cls, methodName);
		else {
			Class<?>[] paramTypes = new Class<?>[params.length];
			for (int i = 0; i < params.length; i++)
				paramTypes[i] = params[i].getClass();
			method = getDeclaredMethod(cls, methodName, paramTypes);
		}
		method.setAccessible(true);
		return method.invoke(obj, params);
	}

	public static Object getProperty(Object obj, String propertyName) {
		try {
			BeanInfo info = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
			for (PropertyDescriptor property : descriptors) {
				if (property.getName().equals(propertyName))
					return property.getReadMethod().invoke(obj, new Object[] {});
			}
			return null;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void setProperty(Object obj, String propertyName, Object value) {
		try {
			BeanInfo info = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
			for (PropertyDescriptor property : descriptors) {
				if (property.getName().equals(propertyName)) {
					property.getWriteMethod().invoke(obj, new Object[] { value });
					return;
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String objectToString(Object obj) {
		if (obj == null)
			return null;

		String result = null;
		// return yyyy-MM-dd
		if (obj instanceof Date)
			result = DateUtil.dateToDdString((Date) obj);
		// return 1,231,321.00
		else if (obj instanceof BigDecimal) {
			NumberFormat numberFormat = NumberFormat.getNumberInstance();
			numberFormat.setMinimumFractionDigits(2);
			result = numberFormat.format(obj);
		} else
			result = obj.toString();
		return result;
	}

	public static boolean compareEqual(Object obj1, Object obj2) {
		if (obj1 == null)
			return obj2 == null;
		else
			return obj1.equals(obj2);
	}

	/**
	 * compare two bean objects whether they are equal (compare property of two bean objects)
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static boolean compareBeanEqual(Object obj1, Object obj2) {
		if (obj1 == null)
			return obj2 == null;
		else if (obj2 == null)
			return false;

		if (!(obj1.getClass() == obj2.getClass()))
			return false;

		try {
			BeanInfo info = Introspector.getBeanInfo(obj1.getClass());
			PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
			for (int i = 0; i < descriptors.length; i++) {
				PropertyDescriptor property = descriptors[i];
				Object value1 = property.getReadMethod().invoke(obj1, new Object[] {});
				Object value2 = property.getReadMethod().invoke(obj2, new Object[] {});
				if (!compareEqual(value1, value2))
					return false;
			}
			return true;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * compare two obj arrays whether they are equal
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static boolean compareEqual(Object[] array1, Object[] array2) {
		if (array1.length != array2.length)
			return false;

		for (int i = 0; i < array1.length; i++) {
			if (!(array1[i] == null ? array2[i] == null : array1[i].equals(array2[i])))
				return false;
		}
		return true;
	}

	public static boolean compareEqual(List<Object> list1, List<Object> list2) {
		return compareEqual(list1.toArray(), list2.toArray());
	}

	/**
	 * boolean、byte、char、short、int、long、float and double.
	 * @param className
	 * @return
	 */
	public static Class<?> getRealClass(String className) {
		if ("boolean".equals(className))
			return Boolean.class;
		if ("byte".equals(className))
			return Byte.class;
		if ("char".equals(className))
			return Character.class;
		if ("short".equals(className))
			return Short.class;
		else if ("int".equals(className))
			return Integer.class;
		else if ("long".equals(className))
			return Long.class;
		else if ("float".equals(className))
			return Float.class;
		else if ("double".equals(className))
			return Double.class;
		else
			return Object.class;
	}

	/**
	 * 获得基本类型
	 * @param cls
	 * @return
	 */
	public static Class<?> getBaseClass(Class<?> cls) {
		if (cls == Boolean.class)
			return boolean.class;
		else if (cls == Byte.class)
			return byte.class;
		else if (cls == Character.class)
			return char.class;
		else if (cls == Short.class)
			return short.class;
		else if (cls == Integer.class)
			return int.class;
		else if (cls == Long.class)
			return long.class;
		else if (cls == Float.class)
			return float.class;
		else if (cls == Double.class)
			return double.class;
		return null;
	}

	/**
	 * judge obj is primitive or not
	 * @param obj
	 * @return
	 */
	public static boolean isPrimitive(Object obj) {
		if (obj == null)
			return true;
		if (obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj instanceof Character)
			return true;
		return false;
	}

	/**
	 * get value of class cls
	 * @param value
	 * @param cls
	 * @return
	 */
	public static Object getValueOfClass(Object value, Class<?> cls) {
		if (value == null || value.getClass() == cls)
			return value;
		
		if (cls == String.class)
			return value.toString();
		// char：return first char
		else if (cls == char.class || cls == Character.class)
			return value.toString().charAt(0);
		// boolean：support 1
		else if (cls == boolean.class || cls == Boolean.class) {
			if (value.toString().equals("1"))
				return true;
			else
				return Boolean.valueOf(value.toString());
		}
		// Number
		else if (Number.class.isAssignableFrom(cls)) {
			if (!(value instanceof Number)) {
				if (value.toString().trim().length() == 0)
					value = 0;
				else
					value = value.toString().trim();
			}

			if (cls == byte.class || cls == Byte.class) {
				value = value instanceof Number ? ((Number) value).byteValue() : Byte.parseByte(value.toString());
			}
			else if (cls == short.class || cls == Short.class) {
				value = value instanceof Number ? ((Number) value).shortValue() : Short.parseShort(value.toString());
			}
			else if (cls == int.class || cls == Integer.class) {
				value = value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(value.toString());
			}
			else if (cls == long.class || cls == Long.class) {
				value = value instanceof Number ? ((Number) value).longValue() : Long.parseLong(value.toString());
			}
			else if (cls == float.class || cls == Float.class) {
				value = value instanceof Number ? ((Number) value).floatValue() : Float.parseFloat(value.toString());
			}
			else if (cls == double.class || cls == Double.class) {
				value = value instanceof Number ? ((Number) value).doubleValue() : Double.parseDouble(value.toString());
			}
			else if (cls == BigInteger.class) {
				value = new BigInteger(value.toString());
			}
			else if (cls == BigDecimal.class) {
				value = new BigDecimal(value.toString());
			}
			return value;
		}
		else
			return value;
	}

}
