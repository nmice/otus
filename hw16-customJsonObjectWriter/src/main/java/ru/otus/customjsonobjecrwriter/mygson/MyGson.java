package ru.otus.customjsonobjecrwriter.mygson;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MyGson {

    // 1) Проанализировать объект, который передали
    // 2) На основе анализа сформировать строку
    public String toJson(Object o) {
        if (o == null || ClassUtils.isPrimitiveOrWrapper(o.getClass())) {
            return String.valueOf(o);
        }
        Class<?> objClass = o.getClass();
        if (String.class.equals(objClass)) {
            return "\"" + o + "\"";
        }
        if (objClass.isArray()) {
            return arrayToJsonFormatString(o);
        }
        if (Collection.class.isAssignableFrom(objClass)) {
            return collectionToJsonFormatString(o);
        }
        return objectToJsonFormatString(o);
    }

    private String arrayToJsonFormatString(Object o) {
        List<String> stringList;
        if (o.getClass().getComponentType().isPrimitive()) {
            stringList = primitiveArrayToStringList(o);
        } else {
            Object[] array = (Object[]) o;
            stringList = Arrays.stream(array).map(this::toJson).collect(Collectors.toList());
        }
        return "[" + String.join(",", stringList) + "]";
    }

    private List<String> primitiveArrayToStringList(Object o) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Array.getLength(o); i++) {
            final var elem = Array.get(o, i);
            result.add(elem == null ? null : String.valueOf(elem));
        }
        return result;
    }

    private String collectionToJsonFormatString(Object o) {
        return "[" + ((Collection<Object>) o).stream()
                .map(this::toJson)
                .collect(Collectors.joining(",")) + "]";
    }

    private String objectToJsonFormatString(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        List<String> jsonFormatFieldsList = new ArrayList<>();
        for (Field field : fields) {
            Object fieldValue = null;
            try {
                field.setAccessible(true);
                fieldValue = field.get(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (fieldValue != null) {
                jsonFormatFieldsList.add("\"" + field.getName() + "\":" + toJson(fieldValue));
            }
        }
        return "{" + String.join(",", jsonFormatFieldsList) + "}";
    }
}
