package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ParameterConverterFactory {

    private final Map<Class<?>, Map<Class<?>, ParameterConverter<?, ?>>> converters;
    private final NoOpConverter noOpConverter;

    public ParameterConverterFactory(ErrorFactory errorFactory, ParameterConverter<?, ?>... converters) {
        this(errorFactory, true, converters);
    }

    public ParameterConverterFactory(ErrorFactory errorFactory, boolean includeDefaults, ParameterConverter<?, ?>... converters) {
        Map<Class<?>, Map<Class<?>, ParameterConverter<?, ?>>> map = new HashMap<Class<?>, Map<Class<?>, ParameterConverter<?, ?>>>();
        if (includeDefaults) {
            // add defaults, they will be overwritten by any passed
            // math
            add(map, new StringToBigDecimalConverter(errorFactory));
            add(map, new StringToBigIntegerConverter(errorFactory));

            // primitives
            add(map, new StringToBooleanConverter(errorFactory));
            add(map, new StringToByteConverter(errorFactory));
            add(map, new StringToCharConverter(errorFactory));
            add(map, new StringToDoubleConverter(errorFactory));
            add(map, new StringToFloatConverter(errorFactory));
            add(map, new StringToIntegerConverter(errorFactory));
            add(map, new StringToLongConverter(errorFactory));
            add(map, new StringToShortConverter(errorFactory));

            // dates & time
            StringToZonedDateTimeConverter zdt = new StringToZonedDateTimeConverter(errorFactory);
            add(map, zdt);
            add(map, new StringToDateConverter(errorFactory, zdt));
            add(map, new StringToLocalDateConverter(errorFactory));
            add(map, new StringToLocalDateTimeConverter(errorFactory));
            add(map, new StringToLocalTimeConverter(errorFactory));
        }
        if (converters != null) {
            for (ParameterConverter<?, ?> converter : converters) {
                add(map, converter);
            }
        }
        // finalize maps
        for (Map.Entry<Class<?>, Map<Class<?>, ParameterConverter<?, ?>>> e : map.entrySet()) {
            map.put(e.getKey(), Collections.unmodifiableMap(e.getValue()));
        }
        this.converters = Collections.unmodifiableMap(map);
        noOpConverter = new NoOpConverter();
    }

    public void add(Map<Class<?>, Map<Class<?>, ParameterConverter<?, ?>>> map, ParameterConverter<?, ?> converter) {
        for (Class<?> from : converter.getConvertsFrom()) {
            Map<Class<?>, ParameterConverter<?, ?>> toMap = map.get(from);
            if (toMap == null) {
                toMap = new HashMap<Class<?>, ParameterConverter<?, ?>>();
                map.put(from, toMap);
            }
            for (Class<?> to : converter.getConvertsTo()) {
                toMap.put(to, converter);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <F, T> ParameterConverter<F, T> getConverter(Class<F> from, Class<T> to) {
        Map<Class<?>, ParameterConverter<?, ?>> toMap = converters.get(from);
        if (toMap == null) {
            return noOpConverter;
        }
        ParameterConverter converter = toMap.get(to);
        return converter == null ? noOpConverter : converter;
    }

}
