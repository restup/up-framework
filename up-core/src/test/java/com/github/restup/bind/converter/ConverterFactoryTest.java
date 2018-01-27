package com.github.restup.bind.converter;

import static com.github.restup.bind.converter.ConversionUtils.LONG_MAX;
import static com.github.restup.test.assertions.Assertions.assertThrows;
import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.junit.Test;

public class ConverterFactoryTest {

	private ConverterFactory factory = ConverterFactory.withDefaults().build();

    
    @Test
    public void testNoOpConversion() {
        Function<Object,Object> f = factory.getConverter(ArrayList.class, List.class);
        assertEquals(Arrays.asList("a","b"), f.apply(Arrays.asList("a","b")));
    }
    
    @Test
    public void testUnsupportedConversion() {
        assertThrows(()->factory.getConverter(ArrayList.class, Map.class), UnsupportedOperationException.class)
        .hasNoCause();
    }

	@Test
	public void testBasicStringConversion() {
		test("1", Integer.class, 1);
		test("2", Long.class, 2l);
		test("123", Double.class, 123d);
		test("4", Float.class, 4f);
		test("5", byte.class, (byte)5);
		test("t", Boolean.class, true);
		test("f", Boolean.class, false);
		test("2014-06-03",LocalDate.class, LocalDate.of(2014, 6, 3));
	}
	
	@Test
	public void testBasicNumberConversion() {
		test(1, Long.class, 1l);
		test(1l, Byte.class, (byte)1);
		test(1l, Short.class, (short)1);
		test(1l, Integer.class, 1);
		test(1l, Long.class, 1l);
		test(1l, Float.class, 1f);
		test(1l, Double.class, 1d);
		test(1l, BigInteger.class, BigInteger.ONE);
		test(1l, BigDecimal.class, BigDecimal.ONE);

		test(new byte[] {BigInteger.TEN.byteValue()}, BigInteger.class, BigInteger.TEN);
		
		test(BigDecimal.valueOf(1), Long.class, 1l);
		test(1, BigDecimal.class, BigDecimal.valueOf(1));
		
		test(LONG_MAX, Long.class, Long.MAX_VALUE);
	}

	
	@Test
	public void testOverflow() {
		overflow(Long.MAX_VALUE, Integer.class, 1);
		overflow(Integer.MAX_VALUE, Short.class, (short)1);
		overflow(Short.MAX_VALUE, Byte.class, (byte)1);
		overflow(LONG_MAX.add(BigInteger.ONE), Long.class, 1l);
	}

	private <T> void test(Object from, Class<T> to, T expected) {
		assertEquals(expected, factory.convert(from, to));
	}
	
	private <T> void overflow(Object from, Class<T> to, T expected) {
		assertThrows(()->test(from, to, expected), IllegalArgumentException.class)
        .hasNoCause();
	}
	
}
