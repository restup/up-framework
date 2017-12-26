package com.github.restup.bind.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import com.google.common.collect.Sets;

public final class ConversionUtils {
	
	static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
	static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);


	@SuppressWarnings("unchecked")
	public static Set<Class<? extends Number>> allNumericTypes() {
		return Sets.newHashSet(Byte.class, Short.class, Integer.class, Long.class, 
				Float.class, Double.class,
				BigInteger.class, BigDecimal.class);
	}
	
    public static LocalTime toLocalTime(String s) {
        if (s.charAt(2) != ':') {
            // pad hour with 0 if needed
            return LocalTime.parse("0" + s);
        } else {
            return LocalTime.parse(s);
        }
    }
    
    private static void overflowCheck(Number n, Class<? extends Number> targetClass, long minValue, long maxValue) {
		long value = n.longValue();
		if (value < minValue|| value > maxValue) {
			throwOverflowException(n, targetClass);
		}
	}
    
    private static void longOverflowCheck(BigInteger n) {
    		if (n != null && (n.compareTo(LONG_MIN) < 0 || n.compareTo(LONG_MAX) > 0)) {
    			throwOverflowException(n, Long.class);
		}
	}
    
	private static void throwOverflowException(Number number, Class<?> targetClass) {
		throw new IllegalArgumentException("Numeric overflow: cannot convert " + number + " of type [" +
				number.getClass().getName() + "] to [" + targetClass.getName() );
	}
    
    public static Byte toByte(Number n) {
    		overflowCheck(n, Byte.class, Byte.MIN_VALUE, Byte.MAX_VALUE);
    		return n.byteValue();
    }

	public static Short toShort(Number n) {
		overflowCheck(n, Short.class, Short.MIN_VALUE, Short.MAX_VALUE);
    		return n.shortValue();
    }
    
    public static Integer toInteger(Number n) {
		overflowCheck(n, Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE);
    		return n.intValue();
    }
    
    public static Long toLong(Number n) {
    		if ( n instanceof BigInteger ) {
    			longOverflowCheck((BigInteger) n);
    		} else if ( n instanceof BigDecimal ) {
    			longOverflowCheck(((BigDecimal) n).toBigInteger());
    		}
    		return n.longValue();
    }

	public static Float toFloat(Number n) {
    		return n.floatValue();
    }
    
    public static Double toDouble(Number n) {
    		return n.doubleValue();
    }
    
    public static BigInteger toBigInteger(Number n) {
    		if ( n instanceof BigDecimal ) {
    			return ((BigDecimal) n).toBigInteger();
    		}
    		return BigInteger.valueOf(n.longValue());
    }
    
    public static BigDecimal toBigDecimal(Number n) {
    		return new BigDecimal(n.toString());
    }

    public static LocalDate toLocalDate(String s) {
        if (s.length() > 10) {
            return LocalDate.parse(s.substring(0, 10));
        }
        return LocalDate.parse(s);
    }

    public static Character toCharacter(String from) {
    		if ( from == null || from.length() != 1 ) {
    			throw new IllegalArgumentException("String length must be 1");
    		}
        return from.charAt(0);
    }

    
    private ConversionUtils() {
    	
    }

}
