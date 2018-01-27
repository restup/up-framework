package com.github.restup.bind.converter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import com.github.restup.errors.ErrorFactory;

public interface ParameterConverterFactory {
	

	<T> ParameterConverter<String,T> getConverter(Type to);
	
	static Builder builder(ErrorFactory errorFactory) {
		return new Builder(errorFactory);
	}

	final static class Builder {
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
			return new DefaultParameterConverterFactory(converters, noOpConverter);
		}
	}
	
}
