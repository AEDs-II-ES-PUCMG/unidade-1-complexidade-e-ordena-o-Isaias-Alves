import java.util.Comparator;

/**
 * Critério C - Índice de Economia (decrescente).
 * O índice de economia é a diferença entre o valor de catálogo atual e o valor efetivamente pago.
 * Desempate 1: Valor Final do Pedido (crescente).
 * Desempate 2: Código Identificador do pedido (crescente).
 */
public class ComparadorCriterioC implements Comparator<Pedido> {

    @Override
    public int compare(Pedido o1, Pedido o2) {
        int res = Double.compare(o1.getTicketMedioPorVariedade(), o2.getTicketMedioPorVariedade());
            if ( res == 0 ){
                res = Double.compare(o1.getValorFinal(), o2.getValorFinal());
                    if ( res == 0 ){
                        res = o1.compareTo(o2);
                        return res;
                    }
                return res;
            }
        return res;
    }
}
