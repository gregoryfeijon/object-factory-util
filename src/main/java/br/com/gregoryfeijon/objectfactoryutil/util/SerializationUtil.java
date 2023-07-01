package br.com.gregoryfeijon.objectfactoryutil.util;

import br.com.gregoryfeijon.objectfactoryutil.exception.ObjectFactoryUtilException;
import com.google.gson.Gson;

import java.io.*;
import java.util.Collection;

/**
 * 27 de fev de 2020
 *
 * @author gregory.feijon
 */
public final class SerializationUtil {

    private SerializationUtil() {}

    public static ByteArrayOutputStream deserialize(byte[] serializedObjects) throws ObjectFactoryUtilException {
        InputStream input = new ByteArrayInputStream(serializedObjects);
        ByteArrayOutputStream baosRetorno;
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

    public static Object getObject(byte[] byteArr) throws ObjectFactoryUtilException {
        InputStream input = new ByteArrayInputStream(byteArr);
        Object retorno;
        try (ObjectInputStream in = new ObjectInputStream(input)) {
            retorno = in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw new ObjectFactoryUtilException("Erro ao serializar objetos!", ex);
        }
        return retorno;
    }

    public static <T> ByteArrayOutputStream serializaObjeto(T entity) throws ObjectFactoryUtilException {
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

    public static <T> ByteArrayOutputStream serializaObjeto(Collection<T> entities) throws ObjectFactoryUtilException {
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

    public static <T> byte[] serializaObjetoGetAsByte(T entity) throws ObjectFactoryUtilException {
        return serializaObjeto(entity).toByteArray();
    }

    public static <T> byte[] serializaObjetoGetAsByte(Collection<T> entities) throws ObjectFactoryUtilException {
        return serializaObjeto(entities).toByteArray();
    }

    public static Object getDesserealizedObject(byte[] serializedObjects) throws ObjectFactoryUtilException {
        return getObject(serializedObjects);
    }

    public static String getDesserealizedObjectAsString(byte[] serializedObjects) throws ObjectFactoryUtilException {
        return (String) getObject(serializedObjects);
    }
}
