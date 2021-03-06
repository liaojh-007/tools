package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.ExcelField;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bean tools
 *
 * @author Gjing
 **/
public final class BeanUtils {
    /**
     * Set the value of a field of an object
     *
     * @param o     object
     * @param field field
     * @param value value
     */
    public static void setFieldValue(Object o, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(o, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the value in the field
     *
     * @param o     object
     * @param field field
     * @return Object
     */
    public static Object getFieldValue(Object o, Field field) {
        try {
            field.setAccessible(true);
            return field.get(o);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Gets all annotated fields of the parent and child classes
     *
     * @param excelClass Excel mapped entity
     * @param ignores    The exported field is to be ignored
     * @param headerArr  excel header array
     * @return Excel fields
     */
    public static List<Field> getExcelFields(Class<?> excelClass, String[] ignores, List<String[]> headerArr) {
        Field[] declaredFields = excelClass.getDeclaredFields();
        List<Field> fieldList = new ArrayList<>(Arrays.asList(declaredFields));
        Class<?> superclass = excelClass.getSuperclass();
        while (superclass != Object.class) {
            fieldList.addAll(Arrays.asList(superclass.getDeclaredFields()));
            superclass = superclass.getSuperclass();
        }
        fieldList = fieldList.stream()
                .filter(e -> e.isAnnotationPresent(ExcelField.class))
                .sorted(Comparator.comparing(e -> e.getAnnotation(ExcelField.class).order()))
                .filter(e -> {
                    String[] headNames = e.getAnnotation(ExcelField.class).value();
                    for (String name : headNames) {
                        if (ParamUtils.contains(ignores, name)) {
                            return false;
                        }
                    }
                    if (headerArr != null) {
                        headerArr.add(headNames);
                    }
                    return true;
                })
                .collect(Collectors.toList());
        return fieldList;
    }

    /**
     * Get the class of a generic in a generic interface
     *
     * @param source        A class that implements a generic interface
     * @param typeInterface A generic interface
     * @param paramIndex    Parameter subscript, starting at 0
     * @return Class
     */
    public static Class<?> getInterfaceType(Class<?> source, Class<?> typeInterface, int paramIndex) {
        Type[] genericInterfaces = source.getGenericInterfaces();
        for (Type type : genericInterfaces) {
            if (type.getTypeName().startsWith(typeInterface.getName())) {
                ParameterizedType pt = (ParameterizedType) type;
                return (Class<?>) pt.getActualTypeArguments()[paramIndex];
            }
        }
        return null;
    }

    /**
     * Gets the corresponding enum by value
     *
     * @param enumClass The enum class to get
     * @param value     Enum value
     * @return Enum
     */
    public static Enum<?> getEnum(Class<? extends Enum<?>> enumClass, String value) {
        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        for (Enum<?> constant : enumConstants) {
            if (value.equals(constant.name())) {
                return constant;
            }
        }
        throw new NullPointerException("Not found your enum");
    }
}
