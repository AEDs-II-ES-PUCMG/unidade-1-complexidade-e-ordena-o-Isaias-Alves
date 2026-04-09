import java.util.Comparator;

public class ComparadorPorCodigo implements Comparator<Produto>{

    @Override
    public int compare(Produto o1, Produto o2) {
        return o1.hashCode() - o2.hashCode();
    }
    
}
