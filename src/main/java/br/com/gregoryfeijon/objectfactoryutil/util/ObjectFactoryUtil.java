package br.com.gregoryfeijon.objectfactoryutil.util;

import br.com.gregoryfeijon.objectfactoryutil.annotation.ObjectConstructor;
import br.com.gregoryfeijon.objectfactoryutil.exception.ObjectFactoryUtilException;
import com.google.api.client.util.IOUtils;
import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 12/05/2020 as 16:56:03
 *
 * @author gregory.feijon
 * @see ObjectConstructor
 */
public final class ObjectFactoryUtil {

    private static final Gson GSON;
    private static final Predicate<Field> PREDICATE_MODIFIERS;

    static {
        GSON = GsonUtil.getGson();
        PREDICATE_MODIFIERS = criaPredicateModifiers();
    }

    private ObjectFactoryUtil() {
    }

    public static <T> List<T> copyAllObjectsFromCollection(Collection<T> entitiesToCopy) throws ObjectFactoryUtilException {
        verifyCollection(entitiesToCopy);
        return entitiesToCopy.stream().map(createCopy()).collect(Collectors.toList());
    }

    /**
     * <strong>Método que retorna uma cópia de uma lista de objetos.</strong>
     *
     * <p>
     * O Tipo da coleção retornada não precisa ser igual ao tipo da coleção
     * copiada, basta que os objetos possuam atributos com o mesmo nome, que os
     * valores desses atributos serão copiados.
     * <p>
     *
     * @param <T>            tipo dos objetos da lista de retorno
     * @param entitiesToCopy - {@linkplain Collection}&lt?&gt
     * @param returnType     - {@linkplain Class}&ltT&gt
     * @return {@linkplain List}&ltT&gt
     * @throws ObjectFactoryUtilException - Exception interna lançada quando ocorrerem erros
     */
    public static <T> List<T> copyAllObjectsFromCollection(Collection<?> entitiesToCopy, Class<T> returnType) throws ObjectFactoryUtilException {
        verifyCollection(entitiesToCopy);
        return entitiesToCopy.stream().map(createCopy(returnType)).collect(Collectors.toList());
    }

    public static <T, U extends Collection<T>> U copyAllObjectsFromCollection(Collection<T> entitiesToCopy, Supplier<U> supplier) throws ObjectFactoryUtilException {
        verifyCollectionAndSupplier(entitiesToCopy, supplier);
        return entitiesToCopy.stream().map(createCopy()).collect(Collectors.toCollection(supplier));
    }

    /**
     * <strong> Método para copiar todos os elementos de uma {@linkplain Collection
     * coleção} e retornar em um tipo escolhido de {@linkplain Collection
     * coleção}.</strong>
     *
     * <p>
     * O Tipo da coleção retornada não precisa ser igual ao tipo da coleção
     * copiada, basta que os objetos possuam atributos com o mesmo nome, que os
     * valores desses atributos serão copiados.
     * <p>
     *
     * @param <T>            tipo dos objetos da lista de retorno
     * @param <U>            tipo de lista a ser retornada
     * @param entitiesToCopy - {@linkplain Collection}&lt?&gt
     * @param supplier       - {@linkplain Supplier}&ltU&gt
     * @param returnType     - {@linkplain Class}&ltT&gt
     * @return U
     * @throws ObjectFactoryUtilException - Exception interna lançada quando ocorrerem erros
     */
    public static <T, U extends Collection<T>> U copyAllObjectsFromCollection(Collection<?> entitiesToCopy,
                                                                              Supplier<U> supplier, Class<T> returnType) throws ObjectFactoryUtilException {
        verifyCollectionAndSupplier(entitiesToCopy, supplier);
        return entitiesToCopy.stream().map(createCopy(returnType)).collect(Collectors.toCollection(supplier));
    }

    private static <T> Function<T, T> createCopy() {
        return LambdaExceptionUtil.rethrowFunction(ObjectFactoryUtil::createFromObject);
    }

    /**
     * <strong>Function executada para criar a cópia dos objetos da lista
     * passada, considerando o tipo de retorno especificado.</strong>
     *
     * @param <T> tipo dos objetos copiados
     * @param <S> tipo dos objetos retornados
     * @return {@linkplain Function}&ltT, S&gt
     */
    private static <T, S> Function<T, S> createCopy(Class<S> returnType) {
        return LambdaExceptionUtil.rethrowFunction(i -> createFromObject(i, returnType));
    }

    private static <T, U> void verifyCollectionAndSupplier(Collection<T> entitiesToCopy, Supplier<U> supplier) throws ObjectFactoryUtilException {
        verifyCollection(entitiesToCopy);
        if (supplier == null) {
            throw new ObjectFactoryUtilException("O tipo de coleção especificada para retorno é nulo.");
        }
    }

    private static <T> void verifyCollection(Collection<T> entitiesToCopy) throws ObjectFactoryUtilException {
        if (CollectionUtils.isEmpty(entitiesToCopy)) {
            throw new ObjectFactoryUtilException("A lista a ser copiada não possui elementos.");
        }
    }

    /**
     * <strong>Método que retorna um objeto copiado à partir de outro de tipo
     * DIFERENTE.Utiliza a lógica do
     * {@linkplain #createFromObject(Object, Object) createFromObject} para
     * copiar </strong>
     *
     * @param <T>        tipo do objeto de retorno
     * @param <S>        tipo do objeto copiado
     * @param source     S
     * @param returnType {@linkplain Class}&ltT&gt
     * @return T
     * @throws ObjectFactoryUtilException - Exception interna lançada quando ocorrerem erros
     */
    @SuppressWarnings("unchecked")
    public static <T, S> T createFromObject(S source, Class<T> returnType) throws ObjectFactoryUtilException {
        verifySourceObject(source);
        Object dest;
        try {
            dest = instanciateClass(returnType);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | IllegalArgumentException |
                 InvocationTargetException ex) {
            throw new ObjectFactoryUtilException("Erro ao criar instância da classe copiada na ObjectFactoryUtil.", ex);
        }
        createFromObject(source, dest);
        return (T) dest;
    }

    /**
     * <strong>Método para retornar um novo objeto criado. Mesma lógica de cópia
     * do {@link #createFromObject(Object, Object) createFromObject}.</strong>
     *
     * @param <T>    - Tipo do retorno e do objeto copiado
     * @param source T
     * @return {@link Object}
     * @throws ObjectFactoryUtilException - Exception interna lançada quando ocorrerem erros
     */
    @SuppressWarnings("unchecked")
    public static <T> T createFromObject(T source) throws ObjectFactoryUtilException {
        verifySourceObject(source);
        Object dest;
        try {
            dest = instanciateClass(source.getClass());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException ex) {
            throw new ObjectFactoryUtilException("Erro ao criar instância da classe copiada na ObjectFactoryUtil.", ex);
        }
        createFromObject(source, dest);
        return (T) dest;
    }

    /**
     * <strong>Método para criar um novo objeto (dest) a partir de um outro
     * objeto do mesmo tipo (source).</strong>
     *
     * <p>
     * Primeiramente, obtém a lista dos campos do objeto de origem, dos quais os
     * valores serão copiados, utilizando o método
     * {@link #getFieldsToCopy(Object, Object) getFieldsToCopy}, excluindo os
     * campos definidos no parâmetro {@link ObjectConstructor#exclude()
     * exclude} Annotation {@link ObjectConstructor}. Em seguida, utiliza o
     * método {@link ReflectionUtil#getFieldsAsCollection(Object)
     * getFieldsAsCollection} para obter os campos do objeto destino e setar em
     * cada um dos correspondentes os valores dos campos obtidos anteriormente,
     * utilizando os métodos
     * {@link FieldUtil#setProtectedFieldValue(String, Object, Object) setProtectedFieldValue}
     * e
     * {@link FieldUtil#getProtectedFieldValue(String, Object) getProtectedFieldValue}.
     * <p>
     *
     * @param <T>    method type definer
     * @param <S>    method type definer
     * @param source &lt T &gt
     * @param dest   &lt T &gt
     * @throws ObjectFactoryUtilException - Exception interna lançada quando ocorrerem erros
     */
    public static <T, S> void createFromObject(S source, T dest) throws ObjectFactoryUtilException {
        verifySourceAndDestObjects(source, dest);
        List<Field> sourceFields = getFieldsToCopy(source, dest);
        for (Field sourceField : sourceFields) {
            Optional<Field> opDestField = ReflectionUtil.getFieldsAsCollection(dest).stream()
                    .filter(destField -> destField.getName().equalsIgnoreCase(sourceField.getName()))
                    .findAny();
            if (opDestField.isPresent()) {
                Field destField = opDestField.get();
                Object sourceValue = verifyValue(sourceField, destField, source);
                FieldUtil.setProtectedFieldValue(destField.getName(), dest, sourceValue);
            }
        }
    }

    private static <T, S> void verifySourceAndDestObjects(S source, T dest) throws ObjectFactoryUtilException {
        verifySourceObject(source);
        if (dest == null) {
            throw new ObjectFactoryUtilException("O objeto de destino é nulo!");
        }
    }

    private static <S> void verifySourceObject(S source) throws ObjectFactoryUtilException {
        if (source == null) {
            throw new ObjectFactoryUtilException("O objeto a ser copiado é nulo!");
        }
    }

    /**
     * <strong>Método que obtém todos os campos que deverão ser copiados do
     * objeto de origem (source).</strong>
     *
     * <p>
     * Primeiramente, utiliza-se o método {@link ReflectionUtil#getFieldsAsCollection(Object)
     * getFieldsAsCollection} para obter todos os campos do objeto de origem dos
     * dados. A partir dessa lista, é criada uma outra lista dos campos a
     * remover, verificando os campos final, que nãos erão trabalhados.
     * Posteriormente, o objeto de destino é verificado e, caso existam campos
     * definidos no {@link ObjectConstructor#exclude() exclude} da annotation
     * {@link ObjectConstructor}, também serão adicionados à lista de exclusão.
     * Os campos que foram separados são removidos, retornando a lista com os
     * restantes.
     * <p>
     *
     * @param <T>    method type definer
     * @param source &lt T &gt
     * @param dest   &lt T &gt
     * @return {@link List}&lt {@link Field} &gt
     */
    private static <T, S> List<Field> getFieldsToCopy(S source, T dest) {
        List<Field> sourceFields = new ArrayList<>(ReflectionUtil.getFieldsAsCollection(source));
        List<Field> fieldsToRemove = sourceFields.stream()
                .filter(PREDICATE_MODIFIERS).collect(Collectors.toCollection(LinkedList::new));
        String[] exclude = getExcludeFromAnnotation(dest);
        if (exclude != null && Array.getLength(exclude) > 0) {
            Arrays.stream(exclude).forEach(excludeField -> {
                Optional<Field> opField = sourceFields.stream()
                        .filter(sourceField -> sourceField.getName().equalsIgnoreCase(excludeField))
                        .findAny();
                if (opField.isPresent() && !fieldsToRemove.contains(opField.get())) {
                    fieldsToRemove.add(opField.get());
                }
            });
        }
        if (CollectionUtils.isNotEmpty(fieldsToRemove)) {
            sourceFields.removeAll(fieldsToRemove);
        }
        return sourceFields;
    }

    /**
     * <strong>Método que verifica o Objeto de destino. Se houver a annotation
     * {@link ObjectConstructor} na classe, retorna o exclude. Caso contrário,
     * retorna um array vazio.</strong>
     *
     * @param <T>  method type definer
     * @param dest &lt T &gt
     * @return {@link String}[]
     */
    private static <T> String[] getExcludeFromAnnotation(T dest) {
        if (dest.getClass().isAnnotationPresent(ObjectConstructor.class)) {
            return dest.getClass().getAnnotation(ObjectConstructor.class).exclude();
        }
        return new String[0];
    }

    /**
     * <strong>Método para verificar os casos especiais em que os tipos do
     * objeto de origem e destino são diferentes e é necessário um tratamento
     * específico para retornar o valor correto.</strong>
     *
     * <p>
     * Faz tratamento específicos entre Wrappers e tipos primitivos, tanto do
     * atributo copiado, quanto do destino. Também possui um tratamento
     * específico no caso do atributo do objeto copiado ser uma
     * {@linkplain String} e o atributo do destino ser um {@linkplain Enum}. No
     * caso de {@linkplain Collection} ou {@linkplain Map}, apenas retorna null,
     * pois é um tratamento mais específico de implementação.
     * <p>
     *
     * @param <S>         - Tipo do objeto copiado
     * @param sourceField - {@link Field}
     * @param destField   - {@link Field}
     * @param source      - S
     * @return {@link Object}
     */
    private static <S> Object verifyValue(Field sourceField, Field destField, S source) throws ObjectFactoryUtilException {
        Object sourceValue = FieldUtil.getProtectedFieldValue(sourceField.getName(), source);
        Class<?> sourceFieldType = sourceField.getType();
        Class<?> destFieldType = destField.getType();

        if (sourceFieldType == destFieldType) {
            return copyValue(sourceField, destField, sourceValue);
        }

        if (ReflectionUtil.isWrapperType(sourceFieldType) && destFieldType.isPrimitive() && sourceValue == null) {
            return ReflectionUtil.defaultValueFor(destFieldType);
        }

        if (ReflectionUtil.isWrapperType(destFieldType) && sourceFieldType.isPrimitive()
                && Objects.equals(sourceValue, ReflectionUtil.defaultValueFor(sourceFieldType))) {
            return null;
        }

        if (sourceFieldType.isEnum() || destFieldType.isEnum()) {
            return validateEnums(sourceField, destField, sourceValue);
        }

        if (isClassMapCollection(destFieldType) || isClassMapCollection(sourceFieldType)) {
            return null;
        }

        return copyValue(sourceField, destField, sourceValue);
    }

    /**
     * <strong>Método validação de enum, para o caso de algum dos valores dos atributos envolvidos
     * na cópia seja do tipo enum</strong>
     *
     * @param sourceField - {@linkplain Field}
     * @param destField   - {@linkplain Field}
     * @param sourceValue - {@linkplain Object}
     * @return {@linkplain Object}
     */
    private static Object validateEnums(Field sourceField, Field destField, Object sourceValue) {
        Class<?> sourceFieldType = sourceField.getType();
        Class<?> destFieldType = destField.getType();
        if (destFieldType.isEnum()) {
            if (sourceFieldType.equals(String.class)) {
                return findEnumConstantEquivalent(destFieldType, sourceValue);
            } else if (sourceFieldType.isEnum() && sourceValue != null) {
                return findEnumConstantEquivalent(destFieldType, sourceValue.toString());
            }
        }
        if (sourceFieldType.isEnum() && (sourceValue != null && destFieldType.equals(String.class))) {
            return sourceValue.toString();
        }
        return null;
    }

    /**
     * <strong> Método para encontrar a constante enum equivalente à String que
     * está sendo copiada.</strong>
     *
     * @param type        - {@link Class}&lt?&gt
     * @param sourceValue {@link Object}
     * @return {@linkplain Object}
     */
    private static Object findEnumConstantEquivalent(Class<?> type, Object sourceValue) {
        Object[] returnValue = {null};
        Stream.of(type.getEnumConstants()).forEach(enumConstant -> {
            if (enumConstant.toString().equals(sourceValue)) {
                returnValue[0] = enumConstant;
            }
        });
        return returnValue[0];
    }

    /**
     * <strong>Método para verificar o tipo do valor copiado, com o intuito de
     * definir a melhor forma para copiá-lo.</strong>
     *
     * <p>
     * Verifica se o {@link Field} é um tipo primitivo ou {@link Enum} e, caso
     * seja, apenas obtem o valor do campo de nome correspondente.
     * Posteriormente, verifica se é um Wrapper, que será copiado apenas via
     * serialização. Caso o valor seja uma {@link Collection} ou um {@link Map},
     * também possui um fluxo para validação dos tipos e devida cópia dos
     * valores. Se não for nenhum desses tipos, é necessário utilizar o método
     * {@link ObjectFactoryUtil#serializingCloneObjects(Object, Class) objectCopy}, que cria uma
     * nova instância do objeto e faz a cópia via serialização, para garantir
     * que seja feita a cópia por valor, não por referência.
     * <p>
     *
     * @param sourceField - {@link Field}
     * @param sourceValue - {@link Object}
     * @return {@link Object}
     * @throws ObjectFactoryUtilException - Exceção interna lançada em erros de serialização
     */
    private static Object copyValue(Field sourceField, Field destField, Object sourceValue) throws ObjectFactoryUtilException {
        if (isPrimitiveOrEnum(sourceField.getType())) {
            return sourceValue;
        }
        if (ReflectionUtil.isWrapperType(sourceField.getType())) {
            try {
                return serializingClone(sourceValue, destField.getType());
            } catch (IOException | ObjectFactoryUtilException ex) {
                throw new ObjectFactoryUtilException("Erro ao serializar objeto para copiar o valor.", ex);
            }
        }
        if (isClassMapCollection(sourceField.getType())) {
            return serializingCloneCollectionMap(sourceValue, destField.getGenericType());
        }
        try {
            return serializingCloneObjects(sourceValue, destField.getType());
        } catch (IOException ex) {
            throw new ObjectFactoryUtilException("Erro ao serializar objeto para copiar o valor.", ex);
        }
    }

    /**
     * <strong>Cria uma nova instância do tipo da classe especificada.</strong>
     *
     * @param <T>    - Tipo da classe a ser instanciada
     * @param aClass {@link Class} &lt T &gt
     * @return {@link Object}
     * @throws NoSuchMethodException     - Exception lançada por problemas ao instanciar a classe
     * @throws IllegalAccessException    - Exception lançada por problemas ao instanciar a classe
     * @throws InstantiationException    - Exception lançada por problemas ao instanciar a classe
     * @throws IllegalArgumentException  - Exception lançada por problemas ao instanciar a classe
     * @throws InvocationTargetException - Exception lançada por problemas ao instanciar a classe
     */
    private static <T> Object instanciateClass(Class<T> aClass) throws NoSuchMethodException, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException {
        Constructor<T> ctor = aClass.getDeclaredConstructor();
        return ctor.newInstance();
    }

    /**
     * <strong>Método para copiar o valor de objetos do tipo
     * <i>Wrappers</i>.</strong>
     *
     * @param source - {@link Object}
     * @param aClass - {@link Class}&lt ?&gt
     * @return {@link Object}
     */
    private static Object serializingClone(Object source, Class<?> aClass) throws ObjectFactoryUtilException, IOException {
        if (source != null) {
            return serializingCloneObjects(source, aClass);
        }
        return null;
    }

    /**
     * <strong>Método que efetivamente faz a cópia dos valores via serialização,
     * nos casos de <i>Wrappers</i> e objetos.</strong>
     *
     * @param source - {@link Object}
     * @param aClass - {@link Class}&lt ?&gt
     * @return {@link Object}
     */
    private static Object serializingCloneObjects(Object source, Class<?> aClass) throws ObjectFactoryUtilException, IOException {
        Object clone;
        byte[] byteClone;
        if (aClass.isPrimitive() || ReflectionUtil.isWrapperType(aClass)) {
            byteClone = IOUtils.serialize(source);
            clone = IOUtils.deserialize(byteClone);
        } else {
            byteClone = SerializationUtil.serializaObjetoGetAsByte(source);
            clone = GSON.fromJson(SerializationUtil.getDesserealizedObjectAsString(byteClone), aClass);
        }
        return clone;
    }

    /**
     * <strong>Método que efetivamente faz a cópia do valor via serialização
     * para {@link Collection} e {@link Map}.</strong>
     *
     * @param source      - {@link Object}
     * @param genericType - {@link Type}
     * @return {@link Object}
     */
    private static Object serializingCloneCollectionMap(Object source, Type genericType) throws ObjectFactoryUtilException {
        Object clone = null;
        if (source != null) {
            try {
                byte[] byteClone = SerializationUtil.serializaObjetoGetAsByte(source);
                if (isCollection(source.getClass())) {
                    clone = verifyList(source, genericType, byteClone);
                } else {
                    clone = GSON.fromJson(SerializationUtil.getDesserealizedObjectAsString(byteClone), genericType);
                }
            } catch (ReflectiveOperationException ex) {
                throw new ObjectFactoryUtilException("Erro ao deserializar collection na cópia de objeto.", ex);
            }
        }
        return clone;
    }

    /**
     * <strong>Método que executa a verificação da lista, para poder definir os
     * tipos corretamente para a cópia do valor via serialização usando o
     * {@link Gson}, configurado na classe {@link GsonUtil}.</strong>
     *
     * <p>
     * Primeiramente passa por uma verificação do tipo e, caso passe, executa a
     * desserialização usando o {@link Type genericType} normalmente. Caso
     * capture alguma exception no processo, significa que possui algum tipo
     * genérico. Nesse caso, passará por um processo para identificar o tipo
     * utilizado em <i>Runtime</i> através do valor, para que possa ser feita a
     * desserialização da forma devida.
     * <p>
     *
     * @param sourceValue - {@link Object}
     * @param genericType - {@link Type}
     * @param byteClone   - byte[]
     * @return {@link Object}
     * @throws ObjectFactoryUtilException   - Exception interna lançada quando ocorre algum erro
     * @throws ReflectiveOperationException - Exception lançada se não for possível criar o Parameterized Type
     */
    @SuppressWarnings("unchecked")
    private static Object verifyList(Object sourceValue, Type genericType, byte[] byteClone) throws ObjectFactoryUtilException, ReflectiveOperationException {
        Object clone = null;
        try {
            verifyType(genericType);
            clone = desserializeCollection(byteClone, genericType);
        } catch (ReflectiveOperationException ex) {
            List<Object> aux = new ArrayList<>(Collections.checkedCollection((Collection<Object>) sourceValue, Object.class));
            if (CollectionUtils.isNotEmpty(aux)) {
                Class<?> objectType = aux.get(0).getClass();
                clone = desserializeCollection(byteClone, GsonUtil.getType(getRawType(genericType), objectType));
            }
        }
        return clone;
    }

    /**
     * <strong>Método para verificar o tipo dos parâmetros da
     * {@link Collection}, de modo a conseguir definir os tipos utilizados na
     * desserialização.</strong>
     *
     * <p>
     * Primeiramente passa por uma verificação dos parâmetros da
     * {@link Collection}, que identifica se o valor é um tipo primitivo,
     * {@link Enum} ou <i>Wrapper</i>. Caso não seja, tenta instanciar através
     * do seu tipo.
     * <p>
     *
     * @param genericType - {@link Type}
     * @throws ReflectiveOperationException - Exception que pode ser lançada ao tentar instanciar
     */
    private static void verifyType(Type genericType) throws ReflectiveOperationException {
        ParameterizedType typeTest = (ParameterizedType) genericType;
        for (Type type : typeTest.getActualTypeArguments()) {
            Class<?> clazz = Class.forName(type.getTypeName());
            if (!isPrimitiveOrEnum(clazz) && !ReflectionUtil.isWrapperType(clazz)) {
                clazz.getDeclaredConstructor().newInstance();
            }
        }
    }

    private static Object desserializeCollection(byte[] byteClone, Type genericType) throws ObjectFactoryUtilException {
        return GSON.fromJson(SerializationUtil.getDesserealizedObjectAsString(byteClone), genericType);
    }

    private static Class<?> getRawType(Type genericType) throws ClassNotFoundException {
        return Class.forName(((ParameterizedType) genericType).getRawType().getTypeName());
    }

    /**
     * <strong>Método para verificar se é um tipo primitivo ou
     * {@link Enum}.</strong>
     *
     * @param type - {@link Class}&lt ?&gt
     * @return boolean
     */
    private static boolean isPrimitiveOrEnum(Class<?> type) {
        return type.isPrimitive() || type.isEnum();
    }

    /**
     * <strong>Método para verificar se é uma {@link Collection} ou um
     * {@link Map}.</strong>
     *
     * @param clazz - {@link Class}&lt ?&gt
     * @return boolean
     */
    private static boolean isClassMapCollection(Class<?> clazz) {
        return isCollection(clazz) || isMap(clazz);
    }

    /**
     * <strong>Método para verificar se é uma {@link Collection}.</strong>
     *
     * @param clazz - {@link Class}&lt ?&gt
     * @return boolean
     */
    private static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    /**
     * <strong>Método para verificar se é um {@link Map}.</strong>
     *
     * @param clazz - {@link Class}&lt ?&gt
     * @return boolean
     */
    private static boolean isMap(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    /**
     * <strong>Método responsável por criar o predicate que verifica se o campo
     * testado possui os modificadores <i>static</i> e <i>final</i>, que,
     * normalmente, caracteriza uma constante, cujo valor não precisa ser
     * copiado.</strong>
     *
     * @return {@link Predicate}&lt{@link Field}&gt
     */
    private static Predicate<Field> criaPredicateModifiers() {
        return p -> Modifier.isStatic(p.getModifiers()) && Modifier.isFinal(p.getModifiers());
    }
}