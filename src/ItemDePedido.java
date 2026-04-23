import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

/**
 * Representa um item do carrinho de compras de um pedido.
 * Armazena a referência ao produto, a quantidade e o preço de venda
 * congelado no momento da transação.
 */
public class ItemDePedido {

    /** Referência ao produto vendido */
    private Produto produto;

    /** Quantidade deste produto no pedido */
    private int quantidade;

    /**
     * Preço unitário do produto congelado no momento da criação do item.
     * Não é afetado por alterações futuras no preço do catálogo.
     */
    private double precoVenda;

    /**
     * Cria um item de pedido, congelando o preço de venda atual do produto.
     * @param produto   Produto a ser incluído no item.
     * @param quantidade Quantidade deste produto.
     */
    public ItemDePedido(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoVenda = produto.valorDeVenda(); // congela o preço no momento da transação
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    /**
     * Representação em String do item, exibindo o nome do produto e o preço
     * congelado no momento da venda. Para produtos perecíveis, exibe também a
     * data de validade. A quantidade é exibida quando for maior que 1.
     */
    @Override
    public String toString() {

        NumberFormat moeda = NumberFormat.getCurrencyInstance();
        StringBuilder sb = new StringBuilder();

        sb.append("NOME: ").append(produto.descricao).append(": ").append(moeda.format(precoVenda));

        if (quantidade > 1) {
            sb.append(" (x").append(quantidade).append(")");
        }

        if (produto instanceof ProdutoPerecivel) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            sb.append("\nVálido até ").append(((ProdutoPerecivel) produto).getDataDeValidade().format(fmt));
        }

        return sb.toString();
    }
}
