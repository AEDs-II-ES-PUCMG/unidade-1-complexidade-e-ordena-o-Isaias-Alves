import java.util.Comparator;

public class ComparadorPorValor implements Comparator<Pedido>{

	@Override
	public int compare(Pedido o1, Pedido o2) {
	  int res = Double.compare(o1.getValorFinal(), o2.getValorFinal());
	  if (res == 0){
		res = o1.getTotalItens() - o2.getTotalItens();
		if (res == 0){
				res = o1.getIdPrimeiroProduto() - o2.getIdPrimeiroProduto();
				return res;
			}
			return res;
		}
		return res;
	} 
}
