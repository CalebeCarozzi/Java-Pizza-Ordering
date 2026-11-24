package dao;

import model.*;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TestePedidoDAO {

    public static void main(String[] args) {

        ClienteDAO clienteDAO = DaoFactory.getClienteDAO();
        SaborDAO saborDAO = DaoFactory.getSaborDAO();
        PedidoDAO pedidoDAO = DaoFactory.getPedidoDAO();

        String telefone = String.valueOf(
                ThreadLocalRandom.current().nextLong(
                        10000000000L,
                        100000000000L
                )
        );

        Cliente cliente = new Cliente(
                "Cliente",
                "Teste",
                telefone
        );

        Sabor sabor = new Sabor(
                "Teste " + UUID.randomUUID(),
                TipoPizza.SIMPLES
        );

        clienteDAO.salvar(cliente);

        try {

            saborDAO.salvar(sabor);

            Pedido pedido = new Pedido(cliente);

            pedidoDAO.salvar(pedido);

            int idPedido = pedido.getIdPedido();

            System.out.println(
                    "Pedido criado. ID: " + idPedido
            );

            // Criar pizza quadrada de 400 cm².
            Pizza pizza = new Pizza(
                    new Quadrado(20)
            );

            pizza.adicionaSabor(sabor);

            ItemPedido item = new ItemPedido(
                    pizza,
                    2,
                    new BigDecimal("20.00")
            );

            pedidoDAO.adicionarItem(
                    idPedido, item
            );

            Pedido encontrado =
                    pedidoDAO.buscarPorId(idPedido);

            verificarTotal(encontrado, "40.00");

            System.out.println(
                    "Item adicionado. Total: "
                            + encontrado.getPrecoTotal()
            );

            // Atualizar quantidade de 2 para 3.
            item.setQuantidade(3);

            pedidoDAO.atualizarItem(
                    idPedido,
                    item.getIdItemPedido(),
                    item
            );

            encontrado = pedidoDAO.buscarPorId(idPedido);

            verificarTotal(encontrado, "60.00");

            System.out.println(
                    "Item atualizado. Total: "
                            + encontrado.getPrecoTotal()
            );

            // Remover item.
            pedidoDAO.removerItem(
                    idPedido,
                    item.getIdItemPedido()
            );

            encontrado = pedidoDAO.buscarPorId(idPedido);

            verificarTotal(encontrado, "0.00");

            if (!encontrado.getItens().isEmpty()) {

                throw new IllegalStateException(
                        "Falha ao remover item."
                );
            }

            System.out.println(
                    "Item removido. Total: "
                            + encontrado.getPrecoTotal()
            );

            // Adicionar novamente para testar o estado.
            pedidoDAO.adicionarItem(idPedido, item);

            pedidoDAO.alterarEstado(
                    idPedido,
                    EstadoPedido.A_CAMINHO
            );

            encontrado = pedidoDAO.buscarPorId(idPedido);

            verificarTotal(encontrado, "60.00");

            if (encontrado.getEstadoPedido()
                    != EstadoPedido.A_CAMINHO) {

                throw new IllegalStateException(
                        "Falha ao alterar estado."
                );
            }

            System.out.println(
                    "Estado atualizado: "
                            + encontrado.getEstadoPedido()
            );

            // Excluir pedido.
            pedidoDAO.excluir(idPedido);

            if (!pedidoDAO.listarPorCliente(
                    cliente.getId()).isEmpty()) {

                throw new IllegalStateException(
                        "Falha ao excluir pedido."
                );
            }

            System.out.println(
                    "Pedido excluído com sucesso."
            );

        } finally {

            // A exclusão do cliente remove seus pedidos
            // e itens por ON DELETE CASCADE.
            try {

                clienteDAO.excluir(cliente.getId());

            } finally {

                if (sabor.getIdSabor() > 0) {

                    saborDAO.excluir(
                            sabor.getIdSabor()
                    );
                }
            }
        }

        System.out.println(
                "\nTeste finalizado com sucesso!"
        );
    }

    private static void verificarTotal(
            Pedido pedido,
            String valorEsperado) {

        BigDecimal esperado =
                new BigDecimal(valorEsperado);

        if (pedido.getPrecoTotal().compareTo(esperado) != 0
                || pedido.calculaTotal().compareTo(esperado) != 0) {

            throw new IllegalStateException(
                    "Total incorreto. Esperado: "
                            + esperado
                            + ", encontrado: "
                            + pedido.getPrecoTotal()
            );
        }
    }
}