package com.github.restup.mapping;

import static com.github.restup.util.ReflectionUtils.getBeanInfo;

import java.util.Collection;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.restup.annotations.ApiName;
import com.github.restup.annotations.Plural;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldFactory;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.util.Assert;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;

/**
 * Default {@link MappedClassFactory} which will accept and build a {@link MappedClass} for any type contained within packages defined by {@link RegistrySettings#packagesToScan}. <p> Fields will be mapped using {@link RegistrySettings#mappedFieldFactory} and sorted using {@link RegistrySettings#mappedFieldOrderComparator}. <p> {@link MappedClass} names will be {@link Class#getName()} by default or {@link ApiName#value()} if present. Plural {@link MappedClass} name will be {@link Plural#value()} if present or use default pluralization, appending 's' to {@link MappedClass#getName()}
 */
public class DefaultMappedClassFactory implements MappedClassFactory {

    private final static Logger log = LoggerFactory.getLogger(DefaultMappedClassFactory.class);

    private final String[] packagesToScan;
    private final String[] packagesToIgnore;
    private final Comparator<MappedField<?>> fieldComparator;
    private final MappedFieldFactory mappedFieldFactory;

    /**
     * Requires settings to define packagesToScan, fieldComparator, and mappedFieldFactory.
     */
    public DefaultMappedClassFactory(RegistrySettings settings) {
        Assert.notNull(settings.getMappedFieldFactory(), "mappedFieldFactory is required");
        Assert.notNull(settings.getPackagesToScan(), "packagesToScan are required");
        Assert.notNull(settings.getMappedFieldOrderComparator(), "mappedFieldOrderComparator is required");
        this.packagesToScan = settings.getPackagesToScan();
        this.fieldComparator = settings.getMappedFieldOrderComparator();
        this.mappedFieldFactory = settings.getMappedFieldFactory();
        this.packagesToIgnore = new String[]{"java"};
    }

    /**
     * @return true if type is in one of the packages defined by {@link #packagesToScan}, false otherwise
     */
    public boolean isMappable(Class<?> type) {
        if (type == null) {
            return false;
        }
        return contains(packagesToScan, type);
    }

    private boolean contains(String[] packages, Class<?> type) {
        for (String pkg : packages) {
            if (type.getName().startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

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

            BeanInfo<T> bi = getBeanInfo(clazz);

            Collection<PropertyDescriptor> descriptors = bi.getPropertyDescriptors();

            for (PropertyDescriptor pd : descriptors) {
                builder.addAttribute(mappedFieldFactory.getMappedField(bi, pd));
            }

            mappedClass = builder.build();
            log.debug("Created {}", mappedClass);
        } else if (log.isDebugEnabled()) {
            // just to avoid some nuisance logging
            if (!contains(packagesToIgnore, clazz)) {
                log.debug("Ignore {} is not included in packagesToScan {}", clazz.getName(), packagesToScan);
            }
        }
        return mappedClass;
    }

    protected String getName(Class<?> clazz) {
        ApiName apiName = clazz.getAnnotation(ApiName.class);
        if (apiName != null) {
            return apiName.value();
        }
        return clazz.getName();
    }

    protected String getPluralName(Class<?> clazz) {
        Plural plural = clazz.getAnnotation(Plural.class);
        if (plural != null) {
            return plural.value();
        }
        return null;
    }

}
