package com.github.restup.bind.converter;

import static com.github.restup.util.UpUtils.unmodifiableMap;
import java.lang.reflect.Type;
import java.util.Map;

class DefaultParameterConverterFactory implements ParameterConverterFactory {
	
	private final Map<Type,ParameterConverter<String,?>> converters;
	private final ParameterConverter<String,?> noOpConverter;

	DefaultParameterConverterFactory(Map<Type,ParameterConverter<String,?>> converters
			, ParameterConverter<String,?> noOpConverter) {
		this.converters = unmodifiableMap(converters);
		this.noOpConverter = noOpConverter;
	}

	@Override
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
	
}
