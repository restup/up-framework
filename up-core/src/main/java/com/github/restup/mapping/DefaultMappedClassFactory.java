package com.github.restup.mapping;

import static com.github.restup.util.ReflectionUtils.getBeanInfo;

import com.github.restup.annotations.ApiName;
import com.github.restup.annotations.Plural;
import com.github.restup.annotations.Resource;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldFactory;
import com.github.restup.util.Assert;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default {@link MappedClassFactory} which will accept and build a {@link MappedClass} for any type
 * contained within packages defined by {@link com.github.restup.registry.settings.RegistrySettings#getPackagesToScan()}.
 * <p>
 * Fields will be mapped using {@link com.github.restup.registry.settings.RegistrySettings#getMappedFieldFactory()} and sorted using
 * {@link com.github.restup.registry.settings.RegistrySettings#getMappedFieldOrderComparator()}.
 * </p>
 * <p>
 * {@link MappedClass} names will be {@link Class#getName()} by default or {@link ApiName#value()}
 * if present. Plural {@link MappedClass} name will be {@link Plural#value()} if present or use
 * default pluralization, appending 's' to {@link MappedClass#getName()}
 * </p>
 */
public class DefaultMappedClassFactory implements MappedClassFactory {

    private final static Logger log = LoggerFactory.getLogger(DefaultMappedClassFactory.class);

    private final List<String> packagesToScan;
    private final List<String> packagesToIgnore;
    private final Comparator<MappedField<?>> fieldComparator;
    private final MappedFieldFactory mappedFieldFactory;
    private final MappedClassBuilderDecorator[] mappedClassBuilderDecorators;

    public DefaultMappedClassFactory(MappedFieldFactory mappedFieldFactory,
        List<String> packagesToScan,
        Comparator<MappedField<?>> mappedFieldComparator,
        MappedClassBuilderDecorator[] mappedClassBuilderDecorators) {
        this.mappedClassBuilderDecorators = mappedClassBuilderDecorators;
        Assert.notNull(mappedFieldFactory, "mappedFieldFactory is required");
        Assert.notNull(packagesToScan, "packagesToScan are required");
        Assert.notNull(mappedFieldComparator, "mappedFieldOrderComparator is required");
        this.packagesToScan = ImmutableList.copyOf(packagesToScan);
        fieldComparator = mappedFieldComparator;
        this.mappedFieldFactory = mappedFieldFactory;
        packagesToIgnore = ImmutableList.of("java");
    }

    public static String getName(Class<?> clazz) {
        ApiName apiName = clazz.getAnnotation(ApiName.class);
        if (apiName != null) {
            return apiName.value();
        }
        return clazz.getName();
    }

    public static String getPluralName(Class<?> clazz) {
        Plural plural = clazz.getAnnotation(Plural.class);
        if (plural != null) {
            return plural.value();
        }
        return null;
    }

    /**
     * @return true if type is in one of the packages defined by {@link #packagesToScan}, false
     *         otherwise
     */
    @Override
    public boolean isMappable(Class<?> type) {
        if (type instanceof Class) {
            return contains(packagesToScan, type);
        }
        return false;
    }

    private boolean contains(List<String> packages, Class<?> type) {
        return packages.stream()
                .filter(pkg -> type.getName().startsWith(pkg))
                .findFirst()
                .isPresent();
    }

    @Override
    public <T> MappedClass<T> getMappedClass(Class<T> clazz) {

        MappedClass<T> mappedClass = null;
        if (isMappable(clazz)) {

            MappedClass.Builder<T> builder =
                    MappedClass.builder(clazz)
                            .name(getName(clazz))
                            .pluralName(getPluralName(clazz))
                        .sortAttributesWith(fieldComparator);

            if (isMappable(clazz.getSuperclass())) {
                builder.parentType(clazz.getSuperclass());
            }

            Resource resource = clazz.getAnnotation(Resource.class);
            if (resource != null) {
                builder.indexedQueryOnly(resource.indexedQueryOnly());
                builder.createStrategy(resource.createStrategy());
                builder.updateStrategy(resource.updateStrategy());
                builder.deleteStrategy(resource.deleteStrategy());
            }

            BeanInfo<T> bi = getBeanInfo(clazz);

            if (mappedClassBuilderDecorators != null) {
                for (MappedClassBuilderDecorator decorator : mappedClassBuilderDecorators) {
                    decorator.decorate(builder, bi);
                }
            }

            bi.getPropertyDescriptors().forEach(pd -> {
                builder.addAttribute(mappedFieldFactory.getMappedField(bi, pd));
            });

            mappedClass = builder.build();
            log.debug("Created {}", mappedClass);
        } else if (log.isDebugEnabled()) {
            // just to avoid some nuisance logging
            if (!contains(packagesToIgnore, clazz)) {
                log.debug("Ignore {} is not included in packagesToScan {}", clazz.getName(),
                    packagesToScan);
            }
        }
        return mappedClass;
    }

}
