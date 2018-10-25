/**
 * 
 */
package org.snowjak.rl1.util;

import java.io.File;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * @author snowjak88
 *
 */
public class Converters {
	
	private static final Table<Class<?>, Class<?>, Converter<?, ?>> converters = HashBasedTable.create();
	
	static {
		converters.put(String.class, Integer.class, stringToInt());
		converters.put(Integer.class, String.class, intToString());
		
		converters.put(String.class, Long.class, stringToLong());
		converters.put(Long.class, String.class, longToString());
		
		converters.put(String.class, Float.class, stringToFloat());
		converters.put(Float.class, String.class, floatToString());
		
		converters.put(String.class, Double.class, stringToDouble());
		converters.put(Double.class, String.class, doubleToString());
		
		converters.put(String.class, File.class, stringToFile());
		converters.put(File.class, String.class, fileToString());
	}
	
	@SuppressWarnings("unchecked")
	public static <T, V> V convert(T value, Class<V> toType) {
		
		if (toType.isAssignableFrom(value.getClass()))
			return (V) value;
		
		final Class<T> fromType = (Class<T>) value.getClass();
		return get(fromType, toType).convert(value);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, V> Converter<T, V> get(Class<T> from, Class<V> to) {
		
		return (Converter<T, V>) converters.row(from).get(to);
	}
	
	public static Converter<String, Integer> stringToInt() {
		
		return (s) -> Integer.parseInt(s);
	}
	
	public static Converter<Integer, String> intToString() {
		
		return (i) -> Integer.toString(i);
	}
	
	public static Converter<Long, String> longToString() {
		
		return (l) -> Long.toString(l);
	}
	
	public static Converter<String, Long> stringToLong() {
		
		return (s) -> Long.parseLong(s);
	}
	
	public static Converter<Float, String> floatToString() {
		
		return (f) -> Float.toString(f);
	}
	
	public static Converter<String, Float> stringToFloat() {
		
		return (s) -> Float.parseFloat(s);
	}
	
	public static Converter<Double, String> doubleToString() {
		
		return (f) -> Double.toString(f);
	}
	
	public static Converter<String, Double> stringToDouble() {
		
		return (s) -> Double.parseDouble(s);
	}
	
	public static Converter<String, File> stringToFile() {
		
		return (s) -> new File(s);
	}
	
	public static Converter<File, String> fileToString() {
		
		return (f) -> f.getPath();
	}
	
	@FunctionalInterface
	public interface Converter<T, V> {
		
		public V convert(T value);
	}
}
