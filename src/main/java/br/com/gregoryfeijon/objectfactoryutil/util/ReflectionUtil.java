package br.com.gregoryfeijon.objectfactoryutil.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 03/02/2020 as 11:09:57
 *
 * @author gregory.feijon
 */
public final class ReflectionUtil {

    private ReflectionUtil() {}

    /**
     * <strong>Método para obter todos os campos de um objeto, incluido os
     * provenientes de herança.</strong>
     *
     * @param object {@link Object}
     * @return {@link List}&lt{@linkplain Field}&gt
     */
    public static Collection<Method> getMethodsAsCollection(Object object) {
        return getMethodsAsCollection(object, true);
    }

    /**
     * <strong> Método para obter os campos de uma classe.</strong>
     *
     * @param object            {@link Object} - objeto do qual serão obtidos os métodos
     * @param getFromSuperclass {@link Boolean} - boolean para especificar se
     *                          deve ou não verificar as super classes para obter seus métodos.
     * @return {@linkplain Collection}&lt{@linkplain Field}&gt
     */
    public static Collection<Method> getMethodsAsCollection(Object object, boolean getFromSuperclass) {
        Class<?> clazz = object.getClass();
        Collection<Method> methods = Arrays.stream(clazz.getDeclaredMethods()).collect(Collectors.toList());
        if (getFromSuperclass) {
            if (clazz.getSuperclass() != null) {
                clazz = clazz.getSuperclass();
                while (clazz != null) {
                    methods.addAll(Arrays.stream(clazz.getDeclaredMethods()).collect(Collectors.toList()));
                    clazz = clazz.getSuperclass();
                }
            }
        }
        return methods;
    }

    /**
     * <strong>Método para obter todos os campos de um objeto, incluido os
     * provenientes de herança.</strong>
     *
     * @param object - {@linkplain Object}
     * @return {@linkplain List}&lt{@linkplain Field}&gt
     */
    public static Collection<Field> getFieldsAsCollection(Object object) {
        return getFieldsAsCollection(object, true);
    }

    /**
     * <strong> Método para obter os campos de uma classe.</strong>
     *
     * @param object            {@link Object} - objeto do qual serão obtidos os atributos
     * @param getFromSuperclass {@link Boolean} - boolean para especificar se
     *                          deve ou não verificar as super classes para obter seus atributos.
     * @return {@linkplain Collection}&lt{@linkplain Field}&gt
     */
    public static Collection<Field> getFieldsAsCollection(Object object, boolean getFromSuperclass) {
        Class<?> clazz = object.getClass();
        Collection<Field> fields = Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toList());
        if (getFromSuperclass) {
            if (clazz.getSuperclass() != null) {
                clazz = clazz.getSuperclass();
                while (clazz != null) {
                    fields.addAll(Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toList()));
                    clazz = clazz.getSuperclass();
                }
            }
        }
        return fields;
    }
}