package br.com.gregoryfeijon.objectfactoryutil.util;

import java.util.function.Function;

/**
 * 25/02/2021
 *
 * @author gregory.feijon
 */
public final class LambdaExceptionUtil {

    private LambdaExceptionUtil() {}

    @FunctionalInterface
    public interface FunctionWithException<T, R, E extends Exception> {

        R apply(T t) throws E;
    }

    public static <T, R, E extends Exception> Function<T, R> rethrowFunction(FunctionWithException<T, R, E> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception ex) {
                throwActualException(ex);
                return null;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <E extends Exception> void throwActualException(Exception exception) throws E {
        throw (E) exception;
    }
}
