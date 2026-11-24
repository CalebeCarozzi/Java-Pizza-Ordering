package service;

import dao.PedidoDAO;
import dao.SaborDAO;
import exception.CampoInvalidoException;
import model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public class PedidoService {

    private final PedidoDAO pedidoDAO;
    private final SaborDAO saborDAO;
    private final PrecoService precoService;
    private final FormaFactory formaFactory;

    public PedidoService(
            PedidoDAO pedidoDAO,
            SaborDAO saborDAO,
            PrecoService precoService,
            FormaFactory formaFactory) {

        this.pedidoDAO = pedidoDAO;
        this.saborDAO = saborDAO;
        this.precoService = precoService;
        this.formaFactory = formaFactory;
    }

    // ================= CRIAR PEDIDO =================

    public Pedido buscarOuCriarPedidoAberto(Cliente cliente) {

        validarCliente(cliente);

        Pedido pedido = pedidoDAO.buscarAbertoPorCliente(
                cliente.getId()
        );

        if (pedido != null) {
            return pedido;
        }

        return criarNovoPedido(cliente);
    }

    public Pedido criarNovoPedido(Cliente cliente) {

        validarCliente(cliente);

        Pedido pedido = new Pedido(cliente);

        pedidoDAO.salvar(pedido);

        return pedido;
    }

    // ================= CONSULTAS =================

    public Pedido buscaPedidoPorId(int idPedido) {

        validarId(idPedido);

        return pedidoDAO.buscarPorId(idPedido);
    }

    public List<Pedido> listarPedidosPorCliente(Cliente cliente) {

        validarCliente(cliente);

        return pedidoDAO.listarPorCliente(cliente.getId());
    }

    public List<Pedido> listarPedidosPorSobrenomeCliente(
            String sobrenome) {

        return pedidoDAO.listarPorSobrenome(sobrenome);
    }

    // ================= ADICIONAR PIZZA =================

    public void adicionarPizzaPorDimensao(
            Pedido pedido,
            String tipoForma,
            double dimensao,
            Sabor sabor1,
            Sabor sabor2,
            int quantidade) {

        adicionarPizza(
                pedido, tipoForma, dimensao, false,
                sabor1, sabor2, quantidade
        );
    }

    public void adicionarPizzaPorArea(
            Pedido pedido,
            String tipoForma,
            double area,
            Sabor sabor1,
            Sabor sabor2,
            int quantidade) {

        adicionarPizza(
                pedido, tipoForma, area, true,
                sabor1, sabor2, quantidade
        );
    }

    private void adicionarPizza(
            Pedido pedido,
            String tipoForma,
            double valor,
            boolean porArea,
            Sabor sabor1,
            Sabor sabor2,
            int quantidade) {

        validarPedidoAberto(pedido);

        ItemPedido item = criarItem(
                tipoForma, valor, porArea,
                sabor1, sabor2, quantidade
        );

        pedidoDAO.adicionarItem(
                pedido.getIdPedido(), item
        );

        recarregarPedido(pedido);
    }

    // ================= CRIAR ITEM =================

    private ItemPedido criarItem(
            String tipoForma,
            double valor,
            boolean porArea,
            Sabor sabor1,
            Sabor sabor2,
            int quantidade) {

        if (quantidade <= 0) {

            throw new CampoInvalidoException(
                    "Quantidade deve ser maior que zero."
            );
        }

        validarSabores(sabor1, sabor2);

        Forma forma = criarForma(
                tipoForma, valor, porArea
        );

        Pizza pizza = new Pizza(forma);

        pizza.adicionaSabor(
                saborDAO.buscarPorId(sabor1.getIdSabor())
        );

        if (sabor2 != null) {

            pizza.adicionaSabor(
                    saborDAO.buscarPorId(sabor2.getIdSabor())
            );
        }

        TabelaPreco tabela = precoService.getTabelaPreco();

        BigDecimal preco = BigDecimal
                .valueOf(pizza.calculaPreco(tabela))
                .setScale(2, RoundingMode.HALF_UP);

        if (preco.signum() <= 0) {

            throw new CampoInvalidoException(
                    "Preço da pizza inválido."
            );
        }

        ItemPedido item = new ItemPedido(
                pizza, quantidade, preco
        );

        if (item.calculaSubTotal().compareTo(
                new BigDecimal("9999999999.99")) > 0) {

            throw new CampoInvalidoException(
                    "Valor do item acima do limite permitido."
            );
        }

        return item;
    }

    // ================= ATUALIZAR ITEM =================

    public void atualizarItem(
            Pedido pedido,
            int indice,
            String tipoForma,
            double valor,
            boolean porArea,
            Sabor sabor1,
            Sabor sabor2,
            int quantidade) {

        validarPedidoAberto(pedido);

        ItemPedido antigo = obterItem(pedido, indice);

        ItemPedido novo = criarItem(
                tipoForma, valor, porArea,
                sabor1, sabor2, quantidade
        );

        pedidoDAO.atualizarItem(
                pedido.getIdPedido(),
                antigo.getIdItemPedido(),
                novo
        );

        recarregarPedido(pedido);
    }

    // ================= REMOVER ITEM =================

    public void removerItem(Pedido pedido, int indice) {

        validarPedidoAberto(pedido);

        ItemPedido item = obterItem(pedido, indice);

        pedidoDAO.removerItem(
                pedido.getIdPedido(),
                item.getIdItemPedido()
        );

        recarregarPedido(pedido);
    }

    private ItemPedido obterItem(Pedido pedido, int indice) {

        if (indice < 0 || indice >= pedido.getItens().size()) {

            throw new CampoInvalidoException(
                    "Selecione um item válido."
            );
        }

        return pedido.getItens().get(indice);
    }

    // ================= TOTAL =================

    public BigDecimal calcularTotalPedido(Pedido pedido) {

        validarPedido(pedido);

        return pedido.getPrecoTotal();
    }

    // ================= FINALIZAR PEDIDO =================

    public void finalizarPedido(Pedido pedido) {

        validarPedido(pedido);

        recarregarPedido(pedido);

        validarPedidoAberto(pedido);

        if (pedido.getItens().isEmpty()) {

            throw new CampoInvalidoException(
                    "Não é possível finalizar um pedido vazio."
            );
        }

        // Os itens já foram salvos no banco.
        // O estado será alterado pela tela de status.
    }

    // ================= ALTERAR ESTADO =================

    public void alterarEstadoPedido(
            Pedido pedido, EstadoPedido estado) {

        validarPedido(pedido);

        if (estado == null) {

            throw new CampoInvalidoException(
                    "Selecione um estado."
            );
        }

        pedidoDAO.alterarEstado(
                pedido.getIdPedido(), estado
        );

        recarregarPedido(pedido);
    }

    // ================= EXCLUIR PEDIDO =================

    public void excluirPedido(int idPedido) {

        validarId(idPedido);

        pedidoDAO.excluir(idPedido);
    }

    // ================= RECARREGAR PEDIDO =================

    private void recarregarPedido(Pedido pedido) {

        Pedido atualizado = pedidoDAO.buscarPorId(
                pedido.getIdPedido()
        );

        pedido.setCliente(atualizado.getCliente());

        pedido.setItens(atualizado.getItens());

        pedido.setEstadoPedido(
                atualizado.getEstadoPedido()
        );

        pedido.setPrecoTotal(
                atualizado.getPrecoTotal()
        );
    }

    // ================= FORMAS =================

    private Forma criarForma(
            String tipoForma,
            double valor,
            boolean porArea) {

        if (!Double.isFinite(valor) || valor <= 0) {

            throw new CampoInvalidoException(
                    "Informe uma medida válida."
            );
        }

        if (tipoForma == null) {

            throw new CampoInvalidoException(
                    "Selecione uma forma."
            );
        }

        String tipo = tipoForma.trim().toUpperCase(Locale.ROOT);

        return switch (tipo) {

            case "QUADRADO" -> porArea
                    ? formaFactory.criarQuadradoPorArea(valor)
                    : formaFactory.criarQuadradoPorLado(valor);

            case "CIRCULO" -> porArea
                    ? formaFactory.criarCirculoPorArea(valor)
                    : formaFactory.criarCirculoPorRaio(valor);

            case "TRIANGULO" -> porArea
                    ? formaFactory.criarTrianguloPorArea(valor)
                    : formaFactory.criarTrianguloPorLado(valor);

            default -> throw new CampoInvalidoException(
                    "Forma inválida."
            );
        };
    }

    public String calcularResultadoMedida(
            String tipoForma,
            double valor,
            boolean porArea) {

        Forma forma = criarForma(
                tipoForma, valor, porArea
        );

        if (!porArea) {

            return String.format(
                    "Resultado: área calculada: %.2f cm²",
                    forma.calculaArea()
            );
        }

        if (forma instanceof Circulo circulo) {

            return String.format(
                    "Resultado: raio calculado: %.2f cm",
                    circulo.getRaio()
            );
        }

        if (forma instanceof Quadrado quadrado) {

            return String.format(
                    "Resultado: lado calculado: %.2f cm",
                    quadrado.getLado()
            );
        }

        if (forma instanceof Triangulo triangulo) {

            return String.format(
                    "Resultado: lado calculado: %.2f cm",
                    triangulo.getLado()
            );
        }

        throw new CampoInvalidoException("Forma inválida.");
    }

    // ================= VALIDAÇÕES =================

    private void validarCliente(Cliente cliente) {

        if (cliente == null || cliente.getId() <= 0) {

            throw new CampoInvalidoException(
                    "Selecione um cliente cadastrado."
            );
        }
    }

    private void validarPedido(Pedido pedido) {

        if (pedido == null) {

            throw new CampoInvalidoException(
                    "Selecione um pedido."
            );
        }

        validarId(pedido.getIdPedido());
    }

    private void validarId(int idPedido) {

        if (idPedido <= 0) {

            throw new CampoInvalidoException(
                    "Pedido inválido."
            );
        }
    }

    private void validarPedidoAberto(Pedido pedido) {

        validarPedido(pedido);

        if (pedido.getEstadoPedido() != EstadoPedido.ABERTO) {

            throw new CampoInvalidoException(
                    "Somente pedidos abertos podem ser editados."
            );
        }
    }

    private void validarSabores(Sabor sabor1, Sabor sabor2) {

        if (sabor1 == null || sabor1.getIdSabor() <= 0) {

            throw new CampoInvalidoException(
                    "Selecione o primeiro sabor."
            );
        }

        if (sabor2 != null
                && (sabor2.getIdSabor() <= 0
                || sabor1.getIdSabor().equals(sabor2.getIdSabor()))) {

            throw new CampoInvalidoException(
                    "Selecione dois sabores diferentes."
            );
        }
    }
}