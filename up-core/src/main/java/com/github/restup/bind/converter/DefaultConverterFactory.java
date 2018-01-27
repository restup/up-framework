package com.github.restup.bind.converter;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;
import com.google.common.collect.ImmutableTable;

class DefaultConverterFactory implements ConverterFactory {

	private final ImmutableTable<Type,Type,Function<?,?>> converters;
	private final Function<?,?> noOpConverter;

    DefaultConverterFactory(ImmutableTable.Builder<Type, Type, Function<?, ?>> table) {
		this(table.build());
	}

    DefaultConverterFactory(ImmutableTable<Type, Type, Function<?, ?>> table) {
		this.converters = table;
		this.noOpConverter = a -> a;
	}

	@Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public <F, T> Function<F, T> getConverter(Type from, Type to) {
		Function<?,?> converter = converters.get(from, to);
		if ( converter == null ) {
			if ( to instanceof Class && from instanceof Class 
					&& ((Class)to).isAssignableFrom((Class) from) ) {
				return (Function) noOpConverter;
			}
			throw new UnsupportedOperationException("Converter does not exist from "+from+" to "+to);
		}
		return (Function) converter;
	}
	
	@Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> Map<Type, Function<T,?>> getConverters(Class<T> from) {
		return (Map) converters.row(from);
	}

	@Override
    @SuppressWarnings("unchecked")
	public <F, T> T convert(F from, Type to) {
		return (T) getConverter((Class<F>) from.getClass(), to).apply(from);
	}

}
