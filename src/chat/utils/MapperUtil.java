package chat.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.OneToOne;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ManyToAny;

import javax.persistence.OneToMany;

import com.google.gson.JsonPrimitive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public final class MapperUtil {

	public MapperUtil() {

	}

	@SuppressWarnings({ "unchecked", "unused" })
	public static final <T> T mapForField(Object object) {
		if (object == null) {
			return null;
		}
		if (object.getClass().getTypeName() == JsonPrimitive.class.getTypeName()) {
			return (T) mapByJsonPrimitive((JsonPrimitive) object);
		}
		try {
			return (T) mapDate(object);
		} catch (ParseException e) {
			return (T) mappOrtherDate(object.getClass(), String.valueOf(object));
		}
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public static final <T> T mapForField(Class<T> clazz, Object object) {
		if (object == null) {
			return null;
		}
		if (object.getClass().getTypeName() == JsonPrimitive.class.getTypeName()) {
			return (T) mapByJsonPrimitive((JsonPrimitive) object);
		}
		try {
			return (T) mapDate(object);
		} catch (ParseException e) {
			return (T) mappOrtherDate(clazz, String.valueOf(object));
		}

	}

	public static final Date mapDate(Object object) throws ParseException {
		try {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(String.valueOf(object));
		} catch (ParseException e) {
			try {
				return new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(object));
			} catch (ParseException ex) {
				throw ex;
			}
		}
	}

	private static final <T> Object mappOrtherDate(Class<T> clazz, String value) {
		if (clazz == Character.class) {
			return value.charAt(0);
		}
		if (clazz == Integer.class || clazz == Long.class) {
			long returnValue = Long.valueOf(value);
			return returnValue;
		}
		if (clazz == Float.class || clazz == Double.class) {
			double returnValue = Double.valueOf(value);
			return returnValue;
		}
		if (clazz == Byte.class) {
			return Byte.parseByte(value);
		}
		if (clazz == Boolean.class) {
			return Boolean.parseBoolean(value);
		}
		if (value.chars().allMatch(Character::isDigit)) {
			if (isNotRealNumber(value)) {
				return mappOrtherDate(Long.class, value);
			}
			return mappOrtherDate(Double.class, value);
		} else {
			return value;
		}
	}

	private static final <T> Object mapByJsonPrimitive(final JsonPrimitive jsonPrimitive) {
		if (jsonPrimitive.isString()) {
			return jsonPrimitive.getAsString();
		} else if (jsonPrimitive.isBoolean()) {
			return jsonPrimitive.getAsBoolean();
		} else if (jsonPrimitive.isNumber()) {
			return jsonPrimitive.getAsLong();
		}
		return null;
	}

	private static boolean isNotRealNumber(String value) {
		for (char c : value.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface NumberButString {
		String value() default "";
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface DontMap {
		String value() default "";
	}
	
	@Data
	@AllArgsConstructor
	@Getter
	@Setter
	public static final class MapColumn {
		private String[] fieldNames;
		private Object[] values;
		
		public MapColumn(String[] fieldNames, Object values) {
			this.fieldNames = fieldNames;
			this.values = (Object[]) values;
		}
		
		public Object get(String field) {
			for(int i = 0; i < fieldNames.length; i++) {
				if(fieldNames[i].equals(field)) {
					return values[i];
				}
			}
			return null;
		}
		
		public Map<String, Object> toMap(){
			Map<String, Object> map = new HashMap<String, Object>();
			for(int i = 0; i < fieldNames.length; i++) {
				map.put(fieldNames[i], values[i]);
			}
			return map;
		}
	}

	public static final void mapForClass(Object object, Class<?> clazz, Map<String, Object> mapFieldValue) {
		Object value;
		Method method;
		Column columnAnnotation;
		while (true) {
			for (Field field : clazz.getDeclaredFields()) {
				if (notSimpleType(field)) {
					method = getMethod(clazz, field.getName());
					columnAnnotation = field.getAnnotation(Column.class);
					value = columnAnnotation != null
							? mapFieldValue.get(columnAnnotation.name()) == null ? mapFieldValue.get(field.getName())
									: mapFieldValue.get(columnAnnotation.name())
							: mapFieldValue.get(field.getName());
					if (method != null && value != null) {
						try {
							if (field.getAnnotation(NumberButString.class) != null) {
								method.invoke(object, String.valueOf(value));
							} else {
								method.invoke(object, mapForField(field.getType(), value));
							}
						} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
							e.printStackTrace();
						}
					}
				}
			}
			clazz = clazz.getSuperclass();
			if (clazz == Object.class) {
				return;
			}
		}
	}

//	private static final String GETTER_PREFIX = "get";
	private static final String SETTER_PREFIX = "set";

	private static final Method getMethod(Class<?> clazz, String fieldname) {
		for (Method method : clazz.getDeclaredMethods()) {
			// Method found:

			if ((method.getName().startsWith(SETTER_PREFIX))
					&& (method.getName().length() == (fieldname.length() + SETTER_PREFIX.length()))) {
				if (method.getName().toLowerCase().endsWith(fieldname.toLowerCase())) {
					return method;
				}
			}
		}
		return null;
	}

	private static final boolean notSimpleType(Field field) {
		if (field.getAnnotation(OneToMany.class) != null)
			return false;
		if (field.getAnnotation(ManyToAny.class) != null)
			return false;
		if (field.getAnnotation(ManyToOne.class) != null)
			return false;
		if (field.getAnnotation(OneToOne.class) != null)
			return false;
		if (field.getAnnotation(ManyToMany.class) != null)
			return false;
		if (field.getAnnotation(DontMap.class) != null)
			return false;
		return true;
	}
}
