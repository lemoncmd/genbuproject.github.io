package org.simpleframework.xml.core;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

final class Reflector {
    Reflector() {
    }

    private static Class getArrayClass(Type type) {
        Class cls = getClass(((GenericArrayType) type).getGenericComponentType());
        return cls != null ? Array.newInstance(cls, 0).getClass() : null;
    }

    private static Class getClass(ParameterizedType parameterizedType) {
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return actualTypeArguments.length > 0 ? getClass(actualTypeArguments[0]) : null;
    }

    private static Class getClass(Type type) {
        return type instanceof Class ? (Class) type : getGenericClass(type);
    }

    private static Class[] getClasses(ParameterizedType parameterizedType) {
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Class[] clsArr = new Class[actualTypeArguments.length];
        for (int i = 0; i < actualTypeArguments.length; i++) {
            clsArr[i] = getClass(actualTypeArguments[i]);
        }
        return clsArr;
    }

    public static Class getDependent(Field field) {
        ParameterizedType type = getType(field);
        return type != null ? getClass(type) : Object.class;
    }

    public static Class[] getDependents(Field field) {
        ParameterizedType type = getType(field);
        return type != null ? getClasses(type) : new Class[0];
    }

    private static Class getGenericClass(Type type) {
        return type instanceof GenericArrayType ? getArrayClass(type) : Object.class;
    }

    public static String getName(String str) {
        if (str.length() <= 0) {
            return str;
        }
        char[] toCharArray = str.toCharArray();
        char c = toCharArray[0];
        if (!isAcronym(toCharArray)) {
            toCharArray[0] = toLowerCase(c);
        }
        return new String(toCharArray);
    }

    public static Class getParameterDependent(Constructor constructor, int i) {
        ParameterizedType parameterType = getParameterType(constructor, i);
        return parameterType != null ? getClass(parameterType) : Object.class;
    }

    public static Class getParameterDependent(Method method, int i) {
        ParameterizedType parameterType = getParameterType(method, i);
        return parameterType != null ? getClass(parameterType) : Object.class;
    }

    public static Class[] getParameterDependents(Constructor constructor, int i) {
        ParameterizedType parameterType = getParameterType(constructor, i);
        return parameterType != null ? getClasses(parameterType) : new Class[0];
    }

    public static Class[] getParameterDependents(Method method, int i) {
        ParameterizedType parameterType = getParameterType(method, i);
        return parameterType != null ? getClasses(parameterType) : new Class[0];
    }

    private static ParameterizedType getParameterType(Constructor constructor, int i) {
        Type[] genericParameterTypes = constructor.getGenericParameterTypes();
        if (genericParameterTypes.length > i) {
            Type type = genericParameterTypes[i];
            if (type instanceof ParameterizedType) {
                return (ParameterizedType) type;
            }
        }
        return null;
    }

    private static ParameterizedType getParameterType(Method method, int i) {
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (genericParameterTypes.length > i) {
            Type type = genericParameterTypes[i];
            if (type instanceof ParameterizedType) {
                return (ParameterizedType) type;
            }
        }
        return null;
    }

    public static Class getReturnDependent(Method method) {
        ParameterizedType returnType = getReturnType(method);
        return returnType != null ? getClass(returnType) : Object.class;
    }

    public static Class[] getReturnDependents(Method method) {
        ParameterizedType returnType = getReturnType(method);
        return returnType != null ? getClasses(returnType) : new Class[0];
    }

    private static ParameterizedType getReturnType(Method method) {
        Type genericReturnType = method.getGenericReturnType();
        return genericReturnType instanceof ParameterizedType ? (ParameterizedType) genericReturnType : null;
    }

    private static ParameterizedType getType(Field field) {
        Type genericType = field.getGenericType();
        return genericType instanceof ParameterizedType ? (ParameterizedType) genericType : null;
    }

    private static boolean isAcronym(char[] cArr) {
        return (cArr.length >= 2 && isUpperCase(cArr[0])) ? isUpperCase(cArr[1]) : false;
    }

    private static boolean isUpperCase(char c) {
        return Character.isUpperCase(c);
    }

    private static char toLowerCase(char c) {
        return Character.toLowerCase(c);
    }
}
