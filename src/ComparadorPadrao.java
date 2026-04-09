import java.util.Comparator;

public class ComparadorPadrao implements Comparator<Produto> {

    @Override
    public int compare(Produto o1, Produto o2) {
        return Double.compare(o1.getPrecoVenda(), o2.getPrecoVenda());
    }
}
