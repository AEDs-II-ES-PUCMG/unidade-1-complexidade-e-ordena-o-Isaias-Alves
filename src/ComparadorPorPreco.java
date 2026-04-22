import java.util.Comparator;

public class ComparadorPorPreco implements Comparator<Produto> {

    @Override
    public int compare(Produto o1, Produto o2) {
        return Double.compare(o1.valorDeVenda(), o2.valorDeVenda());
    }
}
