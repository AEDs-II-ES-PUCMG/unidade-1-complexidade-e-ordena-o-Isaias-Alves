import java.util.Comparator;

/**
 * Critério B - Volume Total de Itens (crescente).
 * Desempate 1: Data do Pedido.
 * Desempate 2: Código Identificador do pedido.
 */
public class ComparadorCriterioB implements Comparator<Pedido> {

    @Override
    public int compare(Pedido o1, Pedido o2) {
        int res = o1.getFormaDePagamento() - o2.getFormaDePagamento();
        if (res == 0){
            res = Double.compare(o1.getValorFinal(), o2.getValorFinal());
            if (res == 0){
                res = o1.compareTo(o2);
                return res;
            }
            return res;
        }
        return res;
    }
}
