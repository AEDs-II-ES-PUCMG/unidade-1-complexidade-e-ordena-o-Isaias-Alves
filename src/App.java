import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    /** Vetor de pedidos cadastrados */
    static Pedido[] pedidosCadastrados;
    
    /** Vetor de pedidos ordenados pela data do pedido */
    static Pedido[] pedidosOrdenadosPorData;

    /** Vetor de pedidos ordenados pelo valor final (crescente) — usado para busca binária em localizarPedidosPremium */
    static Pedido[] pedidosOrdenadosPorValor;
    
    /** Quantidade de pedidos cadastrados atualmente no vetor */
    static int quantPedidos = 0;
    
    static IOrdenator<Pedido> ordenador;
    
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
    
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de pedidos. Arquivo-texto no formato
     * N  (quantidade de pedidos) <br/>
     * dataDoPedido;formaDePagamento;descrições dos produtos do pedido <br/>
     * Deve haver uma linha para cada um dos pedidos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os pedidos carregados, ou vazio em caso de problemas de leitura.
     */
    static Pedido[] lerPedidos(String nomeArquivoDados) {
    	
    	Pedido[] pedidosCadastrados;
    	Scanner arquivo = null;
    	int numPedidos;
    	String linha;
    	Pedido pedido;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numPedidos = Integer.parseInt(arquivo.nextLine());
    		pedidosCadastrados = new Pedido[numPedidos];
    		
    		for (int i = 0; i < numPedidos; i++) {
    			linha = arquivo.nextLine();
    			pedido = criarPedido(linha);
    			pedidosCadastrados[i] = pedido;
    		}
    		quantPedidos = numPedidos;
    		
    	} catch (IOException excecaoArquivo) {
    		pedidosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return pedidosCadastrados;
    }
    
    private static Pedido criarPedido(String dados) {

    	String[] dadosPedido;
    	DateTimeFormatter formatoData;
    	LocalDate dataDoPedido;
    	int formaDePagamento;
    	Pedido pedido;
    	Produto produto;

    	dadosPedido = dados.split(";");

    	formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    	dataDoPedido = LocalDate.parse(dadosPedido[0], formatoData);

    	formaDePagamento = Integer.parseInt(dadosPedido[1]);

    	pedido = new Pedido(dataDoPedido, formaDePagamento);

    	for (int i = 2; i < dadosPedido.length; i++) {
    		String campo = dadosPedido[i];
    		String nomeProduto;
    		int quantidade;

    		int separador = campo.lastIndexOf(':');
    		if (separador >= 0) {
    			nomeProduto = campo.substring(0, separador);
    			try {
    				quantidade = Integer.parseInt(campo.substring(separador + 1));
    			} catch (NumberFormatException e) {
    				nomeProduto = campo;
    				quantidade = 1;
    			}
    		} else {
    			nomeProduto = campo;
    			quantidade = 1;
    		}

    		produto = pesquisarProduto(nomeProduto);
    		pedido.incluirProduto(produto, quantidade);
    	}
    	return pedido;
    }

    /** Localiza um produto no vetor de produtos cadastrados, a partir do nome de produto passado como parâmetro para esse método. 
     *  A busca não é sensível ao caso.
     *  @param pesquisado Nome do produto a ser pesquisado no vetor de produtos cadastrados. 
     *  @return O produto encontrado ou null, caso o produto não tenha sido localizado no vetor de produtos cadastrados.
     */
    static Produto pesquisarProduto(String pesquisado) {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	
    	for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].descricao.equals(pesquisado)) {
        		produto = produtosCadastrados[i];
        		localizado = true;
        	}
        }
        
        if (!localizado) {
        	return null;
        } else {
        	return(produto);
        }     
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
    */
    static int menu() {
        cabecalho();
        System.out.println("1 - Ordenar pedidos");
        System.out.println("2 - Embaralhar pedidos");
        System.out.println("3 - Listar todos os pedidos");
        System.out.println("4 - Localizar pedidos premium (por valor de corte)");
        System.out.println("0 - Finalizar");
        
        return lerOpcao("Digite sua opção: ", Integer.class);
    }
    
    
    /**
     * Localiza e exibe todos os pedidos cujo valor final seja maior ou igual a um valor de corte
     * informado pelo usuário.
     *
     * Estratégia otimizada: o vetor pedidosOrdenadosPorValor é mantido ordenado de forma crescente
     * pelo valor final (ComparadorCriterioA). Uma busca binária identifica o primeiro índice cujo
     * valorFinal() >= valorCorte em O(log n), evitando varredura completa do vetor. Os pedidos
     * elegíveis — todos a partir desse índice — são impressos em O(k), onde k é o número de
     * resultados encontrados, sem recalcular valores abaixo do corte.
     */
    static void localizarPedidosPremium() {

        cabecalho();
        Double valorCorte = lerOpcao("Informe o valor de corte (R$): ", Double.class);

        if (valorCorte == null) {
            System.out.println("Valor inválido!");
            return;
        }
        for ( int index = pedidosOrdenadosPorValor.length - 1; pedidosOrdenadosPorValor[index].valorFinal() > valorCorte ; index --){
            System.out.println(String.format("%02d - %s\n", (index + 1), pedidosCadastrados[index].toString()));
        
        if ( pedidosOrdenadosPorValor[index].valorFinal() <= valorCorte ){
        System.out.println("Its over bro");
        return;
        }
        }
    } 

    static int exibirMenuOrdenadores() {
        cabecalho();
        System.out.println("1 - Bolha");
        System.out.println("2 - Inserção"); 
        System.out.println("3 - Seleção"); 
        System.out.println("4 - Mergesort"); 
        System.out.println("5 - Heapsort"); 
        System.out.println("0 - Finalizar");
       
        return lerOpcao("Digite sua opção: ", Integer.class);
    }
    
    static int exibirMenuComparadores() {
        cabecalho();
        System.out.println("1 - Critério A: Valor Final do Pedido");
        System.out.println("2 - Critério B: Volume Total de Itens");
        System.out.println("3 - Critério C: Índice de Economia (Decrescente)");

        return lerOpcao("Digite sua opção: ", Integer.class);
    }
    
    /** Ordena o vetor de pedidos cadastrados empregando um método de ordenação selecionado pelo
     *  usuário, dentre os seguintes: bolha, seleção, inserção, mergesort e heapsort.
     *  O usuário também escolhe um critério de ordenação: A (Valor Final), B (Volume de Itens)
     *  ou C (Índice de Economia, decrescente).
     *  O método interage com o usuário por meio de menus e aplica
     *  a ordenação escolhida. Ao final, exibe o tempo total gasto no processo de ordenação, em ms.
    */
    static void ordenarPedidos(){

        int opcao = exibirMenuOrdenadores();
        switch (opcao) {
            case 1 -> ordenador = new Bubblesort<>();
            case 2 -> ordenador = new InsertionSort<>();
            case 3 -> ordenador = new SelectionSort<>();
            case 4 -> ordenador = new Mergesort<>();
            case 5 -> ordenador = new Heapsort<>();
        }

        if (ordenador != null) {
            opcao = exibirMenuComparadores();
            switch (opcao) {
                case 1:
                    ordenador.setComparador(new ComparadorCriterioA());
                    pedidosCadastrados = ordenador.ordenar(pedidosCadastrados);
                    break;
                case 2:
                    ordenador.setComparador(new ComparadorCriterioB());
                    pedidosCadastrados = ordenador.ordenar(pedidosCadastrados);
                    break;
                case 3:
                    ordenador.setComparador(new ComparadorCriterioC());
                    pedidosCadastrados = ordenador.ordenar(pedidosCadastrados);
                    break;
                default:
                    pedidosCadastrados = ordenador.ordenar(pedidosCadastrados);
            }

            System.out.println("Tempo gasto com a ordenação dos pedidos: " + ordenador.getTempoOrdenacao() + " ms.");
        }
        ordenador = null;
    }

    static void embaralharPedidos(){
        Collections.shuffle(Arrays.asList(pedidosCadastrados));
    }

    /** Lista todos os pedidos cadastrados, numerados, um por linha */
    static void listarTodosOsPedidos() {
    	
        cabecalho();
        System.out.println("\nPedidos cadastrados: ");
        for (int i = 0; i < quantPedidos; i++) {
        	System.out.println(String.format("%02d - %s\n", (i + 1), pedidosCadastrados[i].toString()));
        }
    }
    
    public static void main(String[] args) {
		
    	teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        
    	nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
       
        String nomeArquivoPedidos = "pedidos.txt";
        pedidosCadastrados = lerPedidos(nomeArquivoPedidos);
        
        ComparadorPorData comparadorPorData = new ComparadorPorData();
        ordenador = new Heapsort<>();
        ordenador.setComparador(comparadorPorData);
        pedidosOrdenadosPorData = ordenador.ordenar(pedidosCadastrados);

        // Cria cópia independente ordenada por valorFinal() (crescente) para busca binária em localizarPedidosPremium
        pedidosOrdenadosPorValor = Arrays.copyOf(pedidosCadastrados, quantPedidos);
        IOrdenator<Pedido> ordenadorValor = new Heapsort<>();
        ordenadorValor.setComparador(new ComparadorCriterioA());
        pedidosOrdenadosPorValor = ordenadorValor.ordenar(pedidosOrdenadosPorValor);

        int opcao = -1;
      
        do{
        	opcao = menu();
            switch (opcao) {
                case 1 -> ordenarPedidos();
                case 2 -> embaralharPedidos();
                case 3 -> listarTodosOsPedidos();
                case 4 -> localizarPedidosPremium();
                case 0 -> System.out.println("FLW VLW OBG VLT SMP.");
            }
            pausa();
        } while (opcao != 0);       

        teclado.close();    
    }
}
