package com.github.restup.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.restup.annotations.operations.AutoWrapDisabled;
import com.github.restup.errors.RequestErrorException;
import com.github.restup.mapping.UntypedClass;
import com.googlecode.gentyref.GenericTypeReflector;

public class ReflectionUtils {

    private static final Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

    private ReflectionUtils() {

    }

    public static boolean classesExist(String... classes) {
        try {
            for (String clazz : classes) {
                ReflectionUtils.class.getClassLoader().loadClass(clazz);
            }
        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    public static <T extends AccessibleObject> T makeAccessible(T target) {
        if (target != null && !target.isAccessible()) {
            target.setAccessible(true);
        }
        return target;
    }

    /**
     * Create a new instance, catching exceptions and rethrowing using
     * {@link RequestErrorException#rethrow(Throwable)}
     * 
     * @param c class to create
     * @param <T> type of object to create
     * @return a new instance of c
     */
    public final static <T> T newInstance(Class<T> c) {
        if (c != null) {
            try {
                Constructor<T> constructor = c.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
                if (!c.isInterface()) {
                    RequestErrorException.rethrow(e);
                }
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
	public final static <T> T newInstance(Type c) {
    		if ( c instanceof Class) {
    			return newInstance((Class<T>) c);
    		} else if ( c instanceof UntypedClass<?> ) {
    			return (T) ((UntypedClass<?>) c).newInstance();
    		}
    		throw new IllegalArgumentException("Unable to create an instance of type");
    }

    public static <T> BeanInfo<T> getBeanInfo(Class<T> c) {
        BeanInfo<T> beanInfo = new BeanInfo<T>(c);
        beanInfo.addFields(FieldUtils.getAllFields(c));
        Class<?> cur = c;
        while (cur != null && !cur.equals(Object.class)) {
            beanInfo.addMethods(cur.getMethods());
            cur = cur.getSuperclass();
        }
        return beanInfo;
    }

    public static Class<?> getReturnType(PropertyDescriptor pd, Class<?> clazz) {
        Method getter = pd.getGetter();
        if (getter != null) {
        	
            Type type = GenericTypeReflector.getExactReturnType(getter, clazz);
            return GenericTypeReflector.erase(type);
        }
        Method setter = pd.getSetter();
        if (setter != null) {
            Type[] types = GenericTypeReflector.getExactParameterTypes(setter, clazz);
            if (types != null && types.length == 1) {
                return GenericTypeReflector.erase(types[0]);
            }
        }
        Field f = pd.getField();
        if (f != null) {
            Type type = GenericTypeReflector.getExactFieldType(f, clazz);
            return GenericTypeReflector.erase(type);
        }
        return null;
    }

    public static Type getGenericReturnType(PropertyDescriptor pd) {
        Method getter = pd.getGetter();
        if (getter != null) {
            return getActualType(getter.getGenericReturnType());
        }
        Method setter = pd.getSetter();
        if (setter != null) {
            Parameter[] params = setter.getParameters();
            if (params != null && params.length == 1) {
                return getActualType(setter.getParameters()[0].getParameterizedType());
            }
        }
        Field f = pd.getField();
        if (f != null) {
            return getActualType(f.getGenericType());
        }
        return null;
    }

    private static Type getActualType(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        return type;
    }

    /**
     * @param annClass annotation class
     * @param p property descriptor
     * @param <T> type of annotation passed
     * @return true if the annotation exists on any of the {@link AnnotatedElement}s
     */
    public static <T extends Annotation> boolean hasAnnotation(Class<T> annClass, PropertyDescriptor p) {
        return getAnnotation(annClass, p) != null;
    }

    public static <T extends Annotation> T getAnnotation(Class<T> annClass, PropertyDescriptor p) {
        T ann = getAnnotation(p.getGetter(), annClass);
        if (ann != null) {
            return ann;
        }
        ann = getAnnotation(p.getField(), annClass);
        if (ann != null) {
            return ann;
        }
        if (p.getGetterOverrides() != null) {
            for (Method m : p.getGetterOverrides()) {
                ann = getAnnotation(m, annClass);
                if (ann != null) {
                    return ann;
                }
            }
        }
        ann = getAnnotation(p.getSetter(), annClass);
        if (ann != null) {
            return ann;
        }
        if (p.getSetterOverrides() != null) {
            for (Method m : p.getSetterOverrides()) {
                ann = getAnnotation(m, annClass);
                if (ann != null) {
                    return ann;
                }
            }
        }
        return null;
    }

    /**
     * Nullsafe {@link AnnotatedElement#getAnnotation(Class)}
     * 
     * @param <T> type of annotation passed get an {@link Annotation} from the {@link AnnotatedElement}
     * @param f annotated element
     * @param annotationClass annotation to get
     * @return annotation if present, null otherwise
     */
    public static <T extends Annotation> T getAnnotation(AnnotatedElement f, Class<T> annotationClass) {
        if (f != null) {
            return f.getAnnotation(annotationClass);
        }
        return null;
    }

    public static boolean hasAnnotatedMethod(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return findAnnotatedMethod(clazz, annotationClass) != null;
    }

    public static Method findAnnotatedMethod(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Method result = null;
        for (Method m : clazz.getMethods()) {
            if (m.isAnnotationPresent(annotationClass)) {
                if (result != null) {
                    log.error("{} and {} are both annotated with {}", result.getName(), m.getName(), annotationClass.getCanonicalName());
                    throw new IllegalStateException("Expected a single annotated method, found two");
                }
                result = m;
            }
        }
        if (result != null) {
            return result;
        }
        for (Class<?> c : clazz.getInterfaces()) {
            result = findAnnotatedMethod(c, annotationClass);
            if (result != null) {
                return result;
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {
            result = findAnnotatedMethod(superClass, annotationClass);
            if (result != null) {
                return result;
            }
        }
        return result;
    }

    public static boolean isAutoWrapDisabled(Method repositoryMethod) {
        AutoWrapDisabled autoWrapDisabled = getAnnotation(repositoryMethod, AutoWrapDisabled.class);
        boolean disableAutoWrap = false;
        if (autoWrapDisabled != null) {
            disableAutoWrap = autoWrapDisabled.value();
        }
        return disableAutoWrap;
    }

    /**
     * Because Jackson is more lenient than java bean convention, we can't reliably use {@link java.beans.PropertyDescriptor}. We have to examine the fields and methods directly.
     *
     * @author andybuttaro
     */
    public final static class BeanInfo<T> {

        private final Class<T> type;
        private Map<String, PropertyDescriptor> map = new HashMap<String, ReflectionUtils.PropertyDescriptor>();

        public BeanInfo(Class<T> type) {
            this.type = type;
        }

        public Class<T> getType() {
            return type;
        }

        public void addMethods(Method[] arr) {
            if (arr != null) {
                List<Method> methods = Arrays.asList(arr);
                Collections.sort(methods, new Comparator<Method>() {
                    @Override
                    public int compare(Method a, Method b) {
                        int result = a.getName().compareTo(b.getName());
                        if (result == 0) {
                            Class<?> c = a.getDeclaringClass();
                            Class<?> d = b.getDeclaringClass();
                            if (!c.equals(d)) {
                                result = c.isAssignableFrom(d) ? 1 : -1;
                            }
                        }
                        return result;
                    }
                });
                for (Method m : methods) {
                    addGetter(m);
                }
                // add setters second to check types
                for (Method m : methods) {
                    addSetter(m);
                }
            }
        }

        public void addFields(Field[] allFields) {
            if (allFields != null) {
                for (Field f : allFields) {
                    addField(f);
                }
            }
        }

        private void addField(Field f) {
            if (!Modifier.isStatic(f.getModifiers())) {
                add(new PropertyDescriptor(f));
            }
        }

        private void addGetter(Method m) {
            if (!Modifier.isStatic(m.getModifiers()) && m.getParameterCount() == 0) {
                if (!"getClass".equals(m.getName())) {
                    String fieldName = getFieldNameFromMethod(m, "get", "is");
                    if (fieldName != null) {
                        PropertyDescriptor p = find(fieldName);
                        if (p == null) {
                            p = new PropertyDescriptor(fieldName);
                            add(p);
                        }
                        if (acceptNewValue(p.getGetter(), m)) {
                            p.setGetter(m);
                        } else {
                            p.addGetterOverrides(m);
                        }
                    }
                }
            }
        }

        //

        private boolean acceptNewValue(Method a, Method b) {
            if (a != null && b != null) {
                if (!a.getReturnType().equals(b.getReturnType())) {
                    return a.getReturnType().equals(Object.class);
                }
                Class<?> ac = a.getDeclaringClass();
                Class<?> bc = b.getDeclaringClass();
                if (!ac.isAssignableFrom(bc)) {
                    // method a is defined in a super class
                    return false;
                }
            }
            return true;
        }

        private PropertyDescriptor find(String fieldName) {
            PropertyDescriptor p = map.get(fieldName);
            if (p == null) {
                p = map.get("_" + fieldName);
                if (p != null) {
                    p.setName(fieldName);
                }
            }
            return p;
        }

        private void addSetter(Method m) {
            if (!Modifier.isStatic(m.getModifiers()) && m.getParameterCount() == 1) {
                String fieldName = getFieldNameFromMethod(m, "set");
                if (fieldName != null) {
                    PropertyDescriptor p = find(fieldName);
                    if (p == null) {
                        p = new PropertyDescriptor(fieldName);
                        add(p);
                    }
                    if (p.getGetter() != null) {
                        // TODO for generic this may not work. For example if a
                        // superclass
                        // has T getT() and set(T t) and if a sub class
                        // overrides only getMethod
                        // String getT(), then the return times will not equal
                        // the parameter type
                        Class<?> returnType = m.getParameterTypes()[0];
                        if (!p.getGetter().getReturnType().equals(returnType)) {
                            return;
                        }
                    }
                    if (acceptNewValue(p.getSetter(), m)) {
                        p.setSetter(m);
                    } else {
                        p.addSetterOverrides(m);
                    }
                }
            }
        }

        private String getFieldNameFromMethod(Method m, String... possiblePrefixes) {
            String name = m.getName();
            for (String pre : possiblePrefixes) {
                if (name.startsWith(pre)) {
                    String pname = name.substring(pre.length());
                    return StringUtils.uncapitalize(pname);
                }
            }
            return null;
        }

        private void add(PropertyDescriptor pd) {
            String key = pd.getName();
            PropertyDescriptor existing = map.get(key);
            if (existing != null && existing.getDeclaringClass() != null && pd.getDeclaringClass() != null) {
                if (pd.getDeclaringClass().isAssignableFrom(existing.getDeclaringClass())) {
                    return;
                }
            }
            map.put(pd.getName(), pd);
        }

        public Collection<PropertyDescriptor> getPropertyDescriptors() {
            return map.values();
        }

        public PropertyDescriptor getPropertyDescriptor(String name) {

            for (PropertyDescriptor pd : getPropertyDescriptors()) {
                if (pd.getName().equals(name)) {
                    return pd;
                }
            }
            return null;
        }
    }

    public final static class PropertyDescriptor {

        private String name;
        private final Field field;
        private Method getter;
        private Method setter;
        private List<Method> getterOverrides;
        private List<Method> setterOverrides;

        private PropertyDescriptor(Field f) {
            super();
            name = f.getName();
            field = f;
        }

        private PropertyDescriptor(String fieldName) {
            this.name = fieldName;
            this.field = null;
        }

        public void addGetterOverrides(Method m) {
            if (getterOverrides == null) {
                getterOverrides = new ArrayList<Method>();
            }
            getterOverrides.add(m);
        }

        public void addSetterOverrides(Method m) {
            if (setterOverrides == null) {
                setterOverrides = new ArrayList<Method>();
            }
            setterOverrides.add(m);
        }

        public List<Method> getGetterOverrides() {
            return getterOverrides;
        }

        public List<Method> getSetterOverrides() {
            return setterOverrides;
        }

        public String getName() {
            return name;
        }

        public Field getField() {
            return field;
        }

        public Method getGetter() {
            return getter;
        }

        private void setGetter(Method getter) {
            this.getter = getter;
        }

        public Method getSetter() {
            return setter;
        }

        private void setSetter(Method setter) {
            this.setter = setter;
        }
        
        private void setName(String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            PropertyDescriptor other = (PropertyDescriptor) obj;
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            return true;
        }

        public Method getWriteMethod() {
            return setter;
        }

        public Method getReadMethod() {
            return getter;
        }

        public Class<?> getDeclaringClass() {
            if (field != null) {
                return field.getDeclaringClass();
            }
            if (getter != null) {
                return getter.getDeclaringClass();
            }
            if (setter != null) {
                return setter.getDeclaringClass();
            }
            return null;
        }
    }
}
