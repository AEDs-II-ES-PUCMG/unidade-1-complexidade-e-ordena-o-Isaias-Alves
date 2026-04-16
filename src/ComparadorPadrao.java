import java.util.Comparator;

public class ComparadorPadrao implements Comparator<Produto> {

    @Override
    public int compare(Produto o1, Produto o2) {
        return o1.descricao.compareTo(o2.descricao);
    }
}
