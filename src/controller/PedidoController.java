package controller;

import model.*;
import service.ClienteService;
import service.SaborService;
import service.PedidoService;

import java.math.BigDecimal;
import java.util.List;

public class PedidoController {

    private final ClienteService clienteService;
    private final SaborService saborService;
    private final PedidoService pedidoService;

    public PedidoController(
            ClienteService clienteService,
            SaborService saborService,
            PedidoService pedidoService) {

        this.clienteService = clienteService;
        this.saborService = saborService;
        this.pedidoService = pedidoService;
    }

    // ================= CLIENTES =================

    public List<Cliente> mostrarClientes() {

        return clienteService.mostrarClientes();
    }

    public List<Cliente> buscarPorTelefone(String telefone) {

        return clienteService.buscarPorTelefone(telefone);
    }

    // ================= SABORES =================

    public List<Sabor> listarSabores() {

        return saborService.listarSabores();
    }

    // ================= PEDIDOS =================

    public Pedido buscarOuCriarPedidoAberto(Cliente cliente) {

        return pedidoService.buscarOuCriarPedidoAberto(cliente);
    }

    public Pedido criarNovoPedido(Cliente cliente) {

        return pedidoService.criarNovoPedido(cliente);
    }

    public Pedido buscaPedidoPorId(int id) {

        return pedidoService.buscaPedidoPorId(id);
    }

    public List<Pedido> listarPedidosPorCliente(Cliente cliente) {

        return pedidoService.listarPedidosPorCliente(cliente);
    }

    public void excluirPedido(int id) {

        pedidoService.excluirPedido(id);
    }

    public void finalizarPedido(Pedido pedido) {

        pedidoService.finalizarPedido(pedido);
    }

    // ================= PIZZAS =================

    public void adicionarPizzaPorDimensao(
            Pedido pedido,
            String forma,
            double valor,
            Sabor sabor1,
            Sabor sabor2,
            int quantidade) {

        pedidoService.adicionarPizzaPorDimensao(
                pedido, forma, valor,
                sabor1, sabor2, quantidade
        );
    }

    public void adicionarPizzaPorArea(
            Pedido pedido,
            String forma,
            double valor,
            Sabor sabor1,
            Sabor sabor2,
            int quantidade) {

        pedidoService.adicionarPizzaPorArea(
                pedido, forma, valor,
                sabor1, sabor2, quantidade
        );
    }

    public void atualizarItem(
            Pedido pedido,
            int indice,
            String forma,
            double valor,
            boolean porArea,
            Sabor sabor1,
            Sabor sabor2,
            int quantidade) {

        pedidoService.atualizarItem(
                pedido, indice, forma, valor, porArea,
                sabor1, sabor2, quantidade
        );
    }

    public void removerItem(Pedido pedido, int indice) {

        pedidoService.removerItem(pedido, indice);
    }

    // ================= CÁLCULOS =================

    public String calcularResultadoMedida(
            String forma, double valor, boolean porArea) {

        return pedidoService.calcularResultadoMedida(
                forma, valor, porArea
        );
    }

    public BigDecimal calcularTotalPedido(Pedido pedido) {

        return pedidoService.calcularTotalPedido(pedido);
    }
}