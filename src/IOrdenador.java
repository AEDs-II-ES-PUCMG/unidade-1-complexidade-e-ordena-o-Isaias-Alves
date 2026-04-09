import java.util.Comparator;

public interface IOrdenador<T extends Comparable<T>>{

    public T[] ordenar(T[] dados);
    public T[] ordenar(T[] dados, Comparator<T> comparador);
    public long getComparacoes();
	public long getMovimentacoes();
    public double getTempoOrdenacao();
}
