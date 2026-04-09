import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * MIT License
 *
 * Copyright(c) 2025 João Caram <caram@pucminas.br>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public abstract class Produto implements Comparable<Produto> {

    private static final double MARGEM_PADRAO = 0.2;
    private static int ultimoID = 10_000;

    protected int idProduto;
    protected String descricao;
    protected double precoCusto;
    protected double margemLucro;

    /**
     * Inicializador privado. Os valores default em caso de erro são:
     * "Produto sem descrição", R$0.01, 1 unidade, 0 unidades
     * @param desc Descrição do produto (mínimo 3 caracteres)
     * @param precoCusto Preço do produto (mínimo 0.01)
     * @param quant Quantidade atual no estoque (mínimo 0)
     * @param estoqueMinimo Estoque mínimo (mínimo 0)
     * @param validade Data de validade passada como parâmetro
     */
    private void init(String desc, double precoCusto, double margemLucro) {
        if (
            desc.length() < 3 || precoCusto <= 0 || margemLucro <= 0
        ) throw new IllegalArgumentException(
            "Valores inválidos para o produto"
        );
        descricao = desc;
        this.precoCusto = precoCusto;
        this.margemLucro = margemLucro;
        idProduto = ultimoID++;
    }

    /**
     * Construtor completo. Os valores default em caso de erro são:
     * "Produto sem descrição", R$0.01, 1 unidade, 0 unidades
     * @param desc Descrição do produto (mínimo 3 caracteres)
     * @param preco Preço do produto (mínimo 0.01)
     * @param quant Quantidade atual no estoque (mínimo 0)
     * @param estoqueMinimo Estoque mínimo (mínimo 0)
     * @param validade Data de validade passada como parâmetro
     */
    protected Produto(String desc, double precoCusto, double margemLucro) {
        init(desc, precoCusto, margemLucro);
    }

    /**
     * Construtor sem estoque mínimo - fica considerado como 0.
     * Os valores default em caso de erro são:
     * "Produto sem descrição", R$0.01, 1 unidade, 0 unidades
     * @param desc Descrição do produto (mínimo 3 caracteres)
     * @param preco Preço do produto (mínimo 0.01)
     * @param quant Quantidade atual no estoque (mínimo 0)
     * @param validade Data de validade passada como parâmetro
     */
    protected Produto(String desc, double precoCusto) {
        init(desc, precoCusto, MARGEM_PADRAO);
    }

    /**
     * Retorna o valor de venda do produto, considerando seu preço de custo e margem de lucro
     * @return Valor de venda do produto (double, positivo)
     */
    public abstract double valorDeVenda();

    @Override
    /**
     * Retorna o código identificador do produto. É um valor único para cada produto (==chave)
     * @return Inteiro positivo com o identificador do produto
     */
    public int hashCode() {
        return idProduto;
    }

    /**
     * Descrição em string do produto, contendo sua descrição e o valor de venda.
     *  @return String com o formato:
     * [NOME]: R$ [VALOR DE VENDA]
     */
    @Override
    public String toString() {
        NumberFormat moeda = NumberFormat.getCurrencyInstance();

        return String.format(
            "%04d - %s: %s",
            idProduto,
            descricao,
            moeda.format(valorDeVenda())
        );
    }

    @Override
    /**
     * Comparação padrão do produto: identificador/hash code.
     * Retorna um valor negativo se este produto tem um identificador anterior ao outro,
     * valor positivo se o identificador é posterior ao outro. Para o mesmo produto, o
     * retorno é 0.
     * @param outro Produto a ser comparado
     * @return Int de acordo com a regra padrão de Comparable (descrita acima)
     */
    public int compareTo(Produto outro) {
        return this.descricao.compareTo(outro.descricao);
    }

    /**
     * Igualdade de produtos: caso possuam o mesmo código.
     * @param obj Outro produto a ser comparado
     * @return booleano true/false conforme o parâmetro possua o mesmo código identificador deste objeto
     */
    @Override
    public boolean equals(Object obj) {
        try {
            Produto outro = (Produto) obj;
            return this.hashCode() == outro.hashCode();
        } catch (ClassCastException ex) {
            return false;
        }
    }

    /**
     * Cria um produto a partir de uma linha de dados em formato texto. A linha de dados deve estar de acordo com a formatação
     * "tipo; descrição;preçoDeCusto;margemDeLucro;[dataDeValidade]"
     * ou o funcionamento não será garantido. Os tipos são 1 para produto não perecível e 2 para perecível.
     * @param linha Linha com os dados do produto a ser criado.
     * @return Um produto com os dados recebidos
     */
    static Produto criarDoTexto(String linha) {
        //2;descrição;preçoDeCusto;margemDeLucro;dataDeValidade
        Produto novoProduto = null;
        String[] detalhes = linha.split(";");
        String descr = detalhes[1];
        double precoCusto = Double.parseDouble(detalhes[2].replace(",", "."));
        double margem = Double.parseDouble(detalhes[3].replace(",", "."));
        if (detalhes[0].equals("1")) {
            novoProduto = new ProdutoNaoPerecivel(descr, precoCusto, margem);
        } else {
            LocalDate dataValidade = LocalDate.parse(
                detalhes[4],
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            );
            novoProduto = new ProdutoPerecivel(
                descr,
                precoCusto,
                margem,
                dataValidade
            );
        }
        return novoProduto;
    }

    /**
     * Gera uma linha de texto a partir dos dados do produto
     * @return Uma string no formato "tipo; descrição;preçoDeCusto;margemDeLucro;[dataDeValidade]"
     */
    public abstract String gerarDadosTexto();

    public double getPrecoVenda() {
        return precoCusto * (1 + margemLucro);
    }
}
