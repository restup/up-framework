package com.github.restup.bind.converter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import com.google.common.collect.ImmutableTable;

public interface ConverterFactory {

    <F, T> Function<F, T> getConverter(Type from, Type to);
	
    <T> Map<Type, Function<T, ?>> getConverters(Class<T> from);

    <F, T> T convert(F from, Type to);

    static Builder builder() {
		return new Builder();
	}

    static Builder withDefaults() {
		return builder().addDefaults();
	}

    static class Builder {
		private ImmutableTable.Builder<Type,Type,Function<?,?>> table = ImmutableTable.builder();

		private Builder() {
		}
		
		private Builder me() {
			return this;
		}

		public Builder addDefaults() {

			add(byte[].class, s -> new BigInteger(s), BigInteger.class);
			add(String.class, s -> new BigInteger(s), BigInteger.class);
			add(String.class, s -> new BigDecimal(s), BigDecimal.class);
			add(char[].class, s -> new BigDecimal(s), BigDecimal.class);
			add(String.class, Byte::valueOf, Byte.class, Byte.TYPE);
			add(String.class, Double::valueOf, Double.class, Double.TYPE);
			add(String.class, Float::valueOf, Float.class, Float.TYPE);
			add(String.class, Integer::valueOf, Integer.class, Integer.TYPE);
			add(String.class, Long::valueOf, Long.class, Long.TYPE);
			add(String.class, Short::valueOf, Short.class, Short.TYPE);

			add(String.class, StringToBooleanConverter::toBoolean, Boolean.class, Boolean.TYPE);
			add(String.class, ConversionUtils::toCharacter, Character.class, Character.TYPE);
			add(String.class, ConversionUtils::toLocalTime, LocalTime.class);
			add(String.class, ConversionUtils::toLocalDate, LocalDate.class);
			
			// dates & time
			StringToZonedDateTimeConverter zdt = new StringToZonedDateTimeConverter();
			add(String.class, zdt, ZonedDateTime.class);
			add(String.class, new StringToDateConverter(zdt), Date.class);
			add(String.class, new StringToLocalDateTimeConverter(), LocalDateTime.class);

			Set<Class<? extends Number>> numerics = ConversionUtils.allNumericTypes();
			numerics.forEach( c -> addNe(c, ConversionUtils::toByte, Byte.class));
			numerics.forEach( c -> addNe(c, ConversionUtils::toShort, Short.class));
			numerics.forEach( c -> addNe(c, ConversionUtils::toInteger, Integer.class));
			numerics.forEach( c -> addNe(c, ConversionUtils::toLong, Long.class));
			numerics.forEach( c -> addNe(c, ConversionUtils::toFloat, Float.class));
			numerics.forEach( c -> addNe(c, ConversionUtils::toDouble, Double.class));
			numerics.forEach( c -> addNe(c, ConversionUtils::toBigInteger, BigInteger.class));
			numerics.forEach( c -> addNe(c, ConversionUtils::toBigDecimal, BigDecimal.class));

			return me();
		}

		<T, R> void addNe(Class<T> from, Function<T, R> converter, Class<R> to) {
			if ( from != to ) {
				add(from, converter, to);
			}
		}

		public <T, R> void add(Class<T> from, Function<T, R> converter, Class<R> to) {
			table.put(from, to, converter);
		}

		@SuppressWarnings("unchecked")
		public <T, R> void add(Class<T> from, Function<T, R> converter, Class<R> to, Type primitive) {
			add(from, converter, to);
			add(from, converter, (Class<R>)primitive);
		}
		
		public ConverterFactory build() {
            return new DefaultConverterFactory(table);
		}
	}
}
