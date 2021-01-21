package br.com.gregoryfeijon.objectfactoryutil.util;

import br.com.gregoryfeijon.objectfactoryutil.exception.ObjectFactoryUtilException;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 12/05/2020 as 17:19:35
 *
 * @author gregory.feijon
 */
public final class FieldUtil {

    private FieldUtil() {
    }

    public static <T> void setProtectedFieldValue(String destFieldName, T dest, Object sourceValue) {
        Optional<Field> opField = getFieldByName(destFieldName, dest);
        if (opField.isPresent()) {
            Field field = opField.get();
            try {
                field.setAccessible(true);
                field.set(dest, sourceValue);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new ObjectFactoryUtilException("Erro ao copiar romaneio!", ex);
            }
        }
    }

    public static Object getProtectedFieldValue(String protectedFieldName, Object object) {
        Optional<Field> opField = getFieldByName(protectedFieldName, object);
        if (opField.isPresent()) {
            Field field = opField.get();
            try {
                field.setAccessible(true);
                return field.get(object);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new ObjectFactoryUtilException("Erro ao copiar romaneio!", ex);
            }
        }
        return null;
    }

    private static Optional<Field> getFieldByName(String fieldName, Object object) {
        Optional<Field> opField = ReflectionUtil.getFieldsAsCollection(object).stream()
                .filter(streamField -> streamField.getName().equalsIgnoreCase(fieldName)).findAny();
        return opField;
    }
}