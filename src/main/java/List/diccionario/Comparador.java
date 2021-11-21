package List.diccionario;

public interface Comparador<TKEY> {

    boolean esIgual(TKEY key1, TKEY key2);

    int getHashCode(TKEY key);
}
