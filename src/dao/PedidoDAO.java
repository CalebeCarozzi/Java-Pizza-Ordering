package dao;

import model.EstadoPedido;
import model.ItemPedido;
import model.Pedido;

import java.util.List;

public interface PedidoDAO {

    void salvar(Pedido pedido);

    Pedido buscarPorId(int idPedido);

    Pedido buscarAbertoPorCliente(int idCliente);

    List<Pedido> listarPorCliente(int idCliente);

    List<Pedido> listarPorSobrenome(String sobrenome);

    void adicionarItem(int idPedido, ItemPedido item);

    void atualizarItem(int idPedido, int idItem, ItemPedido item);

    void removerItem(int idPedido, int idItem);

    void alterarEstado(int idPedido, EstadoPedido estado);

    void excluir(int idPedido);
}