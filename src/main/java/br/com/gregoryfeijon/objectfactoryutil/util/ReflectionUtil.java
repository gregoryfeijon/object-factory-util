package br.com.gregoryfeijon.objectfactoryutil.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Time;
import java.text.Format;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 03/02/2020 as 11:09:57
 *
 * @author gregory.feijon
 */
public final class ReflectionUtil {

    private ReflectionUtil() {
    }

    private static final Set<Class<?>> WRAPPER_TYPES;
    private static final Map<Class<?>, Object> DEFAULT_VALUES = new HashMap<>();

    static {
        WRAPPER_TYPES = getWrapperTypes();
        criaMapaDefaultValues();
    }

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
        if (getFromSuperclass && clazz.getSuperclass() != null) {
            clazz = clazz.getSuperclass();
            while (clazz != null) {
                methods.addAll(Arrays.stream(clazz.getDeclaredMethods()).collect(Collectors.toList()));
                clazz = clazz.getSuperclass();
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
        if (getFromSuperclass && clazz.getSuperclass() != null) {
            clazz = clazz.getSuperclass();
            while (clazz != null) {
                fields.addAll(Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toList()));
                clazz = clazz.getSuperclass();
            }

        }
        return fields;
    }


    /**
     * <strong>Método responsável por verificar se o tipo do valor sendo copiado
     * é um wrapper.</strong>
     *
     * @param clazz {@link Class}&lt ? &gt
     * @return {@link Boolean}
     */
    public static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz)
                || WRAPPER_TYPES.stream().anyMatch(wrapper -> wrapper.isAssignableFrom(clazz));
    }

    @SuppressWarnings("unchecked")
    public static <T> T defaultValueFor(Class<T> clazz) {
        return (T) DEFAULT_VALUES.get(clazz);
    }

    private static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> wrappers = new HashSet<>();
        wrappers.add(Boolean.class);
        wrappers.add(Byte.class);
        wrappers.add(UUID.class);
        wrappers.addAll(numberTypes());
        wrappers.addAll(dateTypes());
        wrappers.addAll(textTypes());
        return wrappers;
    }

    /**
     * <strong>Método responsável por criar o {@link Set} com os <i>wrapper
     * types</i> de textos.</strong>
     *
     * @return {@link Set}&lt {@link Class}&lt ? &gt &gt
     */
    private static Set<Class<?>> textTypes() {
        Set<Class<?>> aux = new HashSet<>();
        aux.add(String.class);
        aux.add(Character.class);
        aux.add(Format.class);
        return aux;
    }

    /**
     * <strong>Método responsável por criar o {@link Set} com os <i>wrapper
     * types</i> de datas/horas.</strong>
     *
     * @return {@link Set}&lt {@link Class}&lt ? &gt &gt
     */
    private static Set<Class<?>> dateTypes() {
        Set<Class<?>> aux = new HashSet<>();
        aux.add(Date.class);
        aux.add(Time.class);
        aux.add(LocalDateTime.class);
        aux.add(LocalDate.class);
        aux.add(LocalTime.class);
        aux.add(Temporal.class);
        aux.add(Instant.class);
        return aux;
    }

    /**
     * <strong>Método responsável por criar o {@link Set} com os <i>wrapper
     * types</i> de números.</strong>
     *
     * @return {@link Set}&lt {@link Class}&lt ? &gt &gt
     */
    private static Set<Class<?>> numberTypes() {
        Set<Class<?>> aux = new HashSet<>();
        aux.add(Integer.class);
        aux.add(Double.class);
        aux.add(Float.class);
        aux.add(Long.class);
        aux.add(Number.class);
        return aux;
    }

    private static void criaMapaDefaultValues() {
        DEFAULT_VALUES.put(boolean.class, Boolean.FALSE);
        DEFAULT_VALUES.put(byte.class, (byte) 0);
        DEFAULT_VALUES.put(short.class, (short) 0);
        DEFAULT_VALUES.put(int.class, 0);
        DEFAULT_VALUES.put(long.class, 0L);
        DEFAULT_VALUES.put(char.class, '\0');
        DEFAULT_VALUES.put(float.class, 0.0F);
        DEFAULT_VALUES.put(double.class, 0.0D);
    }
}