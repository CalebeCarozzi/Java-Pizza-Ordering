package controller;

import model.EstadoPedido;
import model.Pedido;
import service.PedidoService;

import java.math.BigDecimal;
import java.util.List;

public class StatusPedidoController {

    private final PedidoService pedidoService;

    public StatusPedidoController(PedidoService pedidoService) {

        this.pedidoService = pedidoService;
    }

    public List<Pedido> listarPedidosPorSobrenomeCliente(
            String sobrenome) {

        return pedidoService.listarPedidosPorSobrenomeCliente(
                sobrenome
        );
    }

    public Pedido buscaPedidoPorId(int id) {

        return pedidoService.buscaPedidoPorId(id);
    }

    public void alterarEstadoPedido(
            Pedido pedido, EstadoPedido estado) {

        pedidoService.alterarEstadoPedido(pedido, estado);
    }

    public void excluirPedido(int id) {

        pedidoService.excluirPedido(id);
    }

    public BigDecimal calcularTotalPedido(Pedido pedido) {

        return pedidoService.calcularTotalPedido(pedido);
    }
}