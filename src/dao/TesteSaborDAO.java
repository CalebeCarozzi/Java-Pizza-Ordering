package dao;

import model.Sabor;
import model.TipoPizza;

import java.util.List;
import java.util.UUID;

public class TesteSaborDAO {

    public static void main(String[] args) {

        SaborDAO saborDAO = DaoFactory.getSaborDAO();

        String nome = "Teste " + UUID.randomUUID();

        Sabor sabor = new Sabor(
                nome,
                TipoPizza.SIMPLES
        );

        // Cadastrar sabor
        saborDAO.salvar(sabor);

        System.out.println(
                "Sabor cadastrado. ID: " + sabor.getIdSabor()
        );

        try {

            // Listar sabores
            List<Sabor> sabores = saborDAO.listarTodos();

            System.out.println("\nSabores cadastrados:");

            for (Sabor atual : sabores) {

                System.out.println(
                        atual.getIdSabor() + " - "
                                + atual.getNome() + " - "
                                + atual.getTipoPizza()
                );
            }

            // Buscar por ID
            Sabor encontrado = saborDAO.buscarPorId(
                    sabor.getIdSabor()
            );

            System.out.println(
                    "\nSabor encontrado: " + encontrado.getNome()
            );

            // Atualizar sabor
            encontrado.setNome(nome + " Atualizado");
            encontrado.setTipoPizza(TipoPizza.PREMIUM);

            saborDAO.atualizar(encontrado);

            Sabor atualizado = saborDAO.buscarPorId(
                    sabor.getIdSabor()
            );

            if (atualizado.getTipoPizza() != TipoPizza.PREMIUM
                    || !atualizado.getNome().equals(nome + " Atualizado")) {

                throw new IllegalStateException(
                        "Falha na atualização do sabor."
                );
            }

            System.out.println(
                    "\nTipo atualizado: " + atualizado.getTipoPizza()
            );

            // Filtrar sabores
            List<Sabor> filtrados =
                    saborDAO.filtrarPorNome(nome);

            if (filtrados.size() != 1) {

                throw new IllegalStateException(
                        "Falha no filtro de sabores."
                );
            }

            System.out.println(
                    "\nSabores encontrados: " + filtrados.size()
            );

        } finally {

            saborDAO.excluir(sabor.getIdSabor());

            System.out.println(
                    "\nSabor excluído com sucesso!"
            );
        }

        System.out.println(
                "\nTeste finalizado com sucesso!"
        );
    }
}