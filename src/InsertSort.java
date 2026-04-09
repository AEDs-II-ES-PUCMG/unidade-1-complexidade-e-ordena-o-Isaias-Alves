import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;


public class InsertSort<T extends Comparable<T>> implements IOrdenador<T>{

	private long comparacoes;
	private long movimentacoes;
	private LocalDateTime inicio;
	private LocalDateTime termino;	
	
	public InsertSort() {
		comparacoes = 0;
		movimentacoes = 0;
	}
	
	@Override
	public T[] ordenar(T[] dados) {
		return ordenar(dados, T::compareTo);
	}

	@Override
	public T[] ordenar(T[] dados, Comparator<T> comparador) {
		T[] dadosOrdenados = Arrays.copyOf(dados, dados.length);
		int tamanho = dadosOrdenados.length;
		
		inicio = LocalDateTime.now();
		
		for (int posReferencia = 1; posReferencia <= tamanho -1; posReferencia++) {
			T valor = dadosOrdenados[posReferencia];
            int j = posReferencia-1;
            comparacoes++;
            while(j >=0 && comparador.compare(valor,dadosOrdenados[j]) <0){
                j--;
                comparacoes++;
            }
                
            copiarDados(j+1, posReferencia, dadosOrdenados);
            dadosOrdenados[j+1] = valor;
            
		}	
		termino = LocalDateTime.now();

		return dadosOrdenados;
	}
	
	private void copiarDados(int inicio, int fim, T[] vet) {
		for (int i = fim; i > inicio; i--) {
            movimentacoes++;
            vet[i] = vet[i-1];
        }
	}
	
	public long getComparacoes() {
		return comparacoes;
	}
	
	public long getMovimentacoes() {
		return movimentacoes;
	}
	
	public double getTempoOrdenacao() {
	    return  Duration.between(inicio, termino).toMillis();	    
	}

	

	
}