import java.util.Comparator;

/**
 * Critério A - Valor Final do Pedido (crescente).
 * Desempate 1: Volume Total de Itens (quantProdutos).
 * Desempate 2: Código Identificador do primeiro item do pedido.
 */
public class ComparadorCriterioA implements Comparator<Pedido> {

    @Override
	public int compare(Pedido o1, Pedido o2) {
	  int res = Double.compare(o1.getValorFinal(), o2.getValorFinal());
	  if (res == 0){
		res = o1.getTotalItens() - o2.getTotalItens();
		if (res == 0){
				res = o1.compareTo(o2);
				return res;
			}
			return res;
		}
		return res;
	} 
}
