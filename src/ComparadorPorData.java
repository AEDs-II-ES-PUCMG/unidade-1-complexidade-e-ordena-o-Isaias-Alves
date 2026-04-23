import java.util.Comparator;

public class ComparadorPorData implements Comparator<Pedido>{

    @Override
    public int compare(Pedido o1, Pedido o2) {
    	
    	if (o1.getDataPedido().equals(o2.getDataPedido())) {
    		return (o1.getIdPedido() - o2.getIdPedido());
    	} else if (o1.getDataPedido().isBefore(o2.getDataPedido())) {
        	return -1;
        } else {
        	return 1;
        }
    }
}