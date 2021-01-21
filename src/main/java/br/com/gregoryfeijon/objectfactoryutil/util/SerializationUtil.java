package br.com.gregoryfeijon.objectfactoryutil.util;

import br.com.gregoryfeijon.objectfactoryutil.exception.ObjectFactoryUtilException;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

/**
 * 27 de fev de 2020
 *
 * @author gregory.feijon
 */
public final class SerializationUtil {

    private static final LoggerUtil LOG = LoggerUtil.getLog(SerializationUtil.class);

    private SerializationUtil() {}

    public static ByteArrayOutputStream deserialize(byte[] serializedObjects) {
        InputStream input = new ByteArrayInputStream(serializedObjects);
        ByteArrayOutputStream baosRetorno = new ByteArrayOutputStream();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (BufferedInputStream bis = new BufferedInputStream(input)) {
                int aByte;
                while ((aByte = bis.read()) != -1) {
                    baos.write(aByte);
                }
            }
            baosRetorno = baos;
        } catch (IOException ex) {
            throw new ObjectFactoryUtilException("Erro ao serializar objetos!", ex);
        }
        return baosRetorno;
    }

    public static Object getObject(byte[] byteArr) {
        InputStream input = new ByteArrayInputStream(byteArr);
        Object retorno = null;
        try (ObjectInputStream in = new ObjectInputStream(input)) {
            retorno = in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw new ObjectFactoryUtilException("Erro ao serializar objetos!", ex);
        }
        return retorno;
    }

    public static <T> ByteArrayOutputStream serializaObjeto(T entity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Gson gson = GsonUtil.getGson();
        String json = gson.toJson(entity);
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(json);
        } catch (IOException ex) {
            throw new ObjectFactoryUtilException("Erro ao serializar objetos!", ex);
        }
        return baos;
    }

    public static <T> ByteArrayOutputStream serializaObjeto(Collection<T> entities) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Gson gson = GsonUtil.getGson();
        String json = gson.toJson(entities);
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(json);
        } catch (IOException ex) {
            throw new ObjectFactoryUtilException("Erro ao serializar objetos!", ex);
        }
        return baos;
    }

    public static <T> byte[] serializaObjetoGetAsByte(T entity) {
        return serializaObjeto(entity).toByteArray();
    }

    public static <T> byte[] serializaObjetoGetAsByte(Collection<T> entities) {
        return serializaObjeto(entities).toByteArray();
    }

    public static Object getDesserealizedObject(byte[] serializedObjects) {
        return getObject(serializedObjects);
    }

    public static String getDesserealizedObjectAsString(byte[] serializedObjects) {
        return (String) getObject(serializedObjects);
    }
}