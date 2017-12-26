package com.github.restup.bind.converter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.github.restup.errors.ErrorFactory;
import com.google.common.collect.ImmutableMap;

public class ParameterConverterFactory {
	
	private final Map<Type,ParameterConverter<String,?>> converters;
	private final ParameterConverter<String,?> noOpConverter;

	private ParameterConverterFactory(Map<Type,ParameterConverter<String,?>> converters
			, ParameterConverter<String,?> noOpConverter) {
		this.converters = ImmutableMap.copyOf(converters);
		this.noOpConverter = noOpConverter;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> ParameterConverter<String,T> getConverter(Type to) {
		ParameterConverter<String,?> converter = converters.get(to);
		if ( converter == null ) {
			if ( String.class == to ) {
				return (ParameterConverter) noOpConverter;
			}
			throw new UnsupportedOperationException("Converter does not exist from String to "+to);
		}
		return (ParameterConverter) converter;
	}
	
	public static Builder builder(ErrorFactory errorFactory) {
		return new Builder(errorFactory);
	}

	public final static class Builder {
		private Map<Type,ParameterConverter<String,?>> converters = new HashMap<>();
		private ErrorFactory errorFactory;
		
		private Builder(ErrorFactory errorFactory) {
			this.errorFactory = errorFactory;
		}
		
		private Builder me() {
			return this;
		}

		public Builder addAll(Map<Type, Function<String, ?>> converters) {
			if ( converters != null ) {
				converters.entrySet().forEach( e -> add(e.getKey(), e.getValue()));
			}
			return me();
		}

		public Builder add(Type to, Function<String, ?> f) {
			return add(to, toConverter(f));
		}

		public Builder add(Type to, ParameterConverter<String,?> converter) {
			converters.put(to, converter);
			return me();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private <T> ParameterConverter<T, ?> toConverter(Function<T, ?> f) {
			return new FunctionalParameterConverter(f, errorFactory);
		}
		
		public ParameterConverterFactory build() {
			ParameterConverter<String,?> noOpConverter = toConverter(a -> a);
			return new ParameterConverterFactory(converters, noOpConverter);
		}
	}
	
}
