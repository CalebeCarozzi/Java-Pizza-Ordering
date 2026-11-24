package dao;

import model.Cliente;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TesteClienteDAO {

    public static void main(String[] args) {

        ClienteDAO clienteDAO = DaoFactory.getClienteDAO();

        // Gera um telefone para reduzir a chance de duplicidade.
        String telefone = String.valueOf(
                ThreadLocalRandom.current().nextLong(
                        10000000000L,
                        100000000000L
                )
        );

        Cliente cliente = new Cliente(
                "João",
                "Silva",
                telefone
        );

        // 1. Cadastrar
        clienteDAO.salvar(cliente);

        System.out.println(
                "Cliente cadastrado. ID: " + cliente.getId()
        );

        try {

            // 2. Listar
            List<Cliente> clientes = clienteDAO.listarTodos();

            System.out.println("\nClientes cadastrados:");

            for (Cliente atual : clientes) {

                System.out.println(
                        atual.getId() + " - "
                                + atual.getNome() + " "
                                + atual.getSobrenome() + " - "
                                + atual.getTelefone()
                );
            }

            // 3. Buscar por ID
            Cliente encontrado =
                    clienteDAO.buscarPorId(cliente.getId());

            System.out.println(
                    "\nCliente encontrado: " + encontrado.getNome()
            );

            // 4. Atualizar
            encontrado.setNome("Pedro");

            clienteDAO.atualizar(encontrado);

            Cliente atualizado =
                    clienteDAO.buscarPorId(cliente.getId());

            if (!"Pedro".equals(atualizado.getNome())) {
                throw new IllegalStateException(
                        "Falha na atualização do cliente."
                );
            }

            System.out.println(
                    "\nNome atualizado: " + atualizado.getNome()
            );

            // 5. Filtrar
            List<Cliente> filtrados =
                    clienteDAO.filtrar("Silva", telefone);

            if (filtrados.size() != 1) {
                throw new IllegalStateException(
                        "Falha no filtro de clientes."
                );
            }

            System.out.println(
                    "\nClientes encontrados no filtro: "
                            + filtrados.size()
            );

        } finally {

            // Remove somente o cliente criado pelo teste.
            clienteDAO.excluir(cliente.getId());

            System.out.println(
                    "\nCliente excluído com sucesso!"
            );
        }

        System.out.println(
                "\nTeste finalizado com sucesso!"
        );
    }
}