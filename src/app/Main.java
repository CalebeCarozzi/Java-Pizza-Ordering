package app;

import controller.ClienteController;
import controller.SaborController;
import controller.PrecoController;

import dao.ClienteDAO;
import dao.SaborDAO;
import dao.PrecoDAO;
import dao.DaoFactory;

import repository.RepositoryMemoria;

import service.ClienteService;
import service.SaborService;
import service.PrecoService;
import service.PedidoService;

import view.TelaPrincipal;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // Armazenamento temporário
            RepositoryMemoria repositoryMemoria =
                    new RepositoryMemoria();

            // DAOs
            ClienteDAO clienteDAO =
                    DaoFactory.getClienteDAO();

            SaborDAO saborDAO =
                    DaoFactory.getSaborDAO();

            PrecoDAO precoDAO =
                    DaoFactory.getPrecoDAO();

            // Services
            ClienteService clienteService =
                    new ClienteService(
                            clienteDAO,
                            repositoryMemoria
                    );

            SaborService saborService =
                    new SaborService(saborDAO);

            PrecoService precoService =
                    new PrecoService(precoDAO);

            PedidoService pedidoService =
                    new PedidoService(
                            repositoryMemoria,
                            precoService
                    );

            // Controllers
            ClienteController clienteController =
                    new ClienteController(clienteService);

            SaborController saborController =
                    new SaborController(saborService);

            PrecoController precoController =
                    new PrecoController(precoService);

            // Interface principal
            TelaPrincipal telaPrincipal =
                    new TelaPrincipal(
                            clienteController,
                            saborController,
                            precoController,
                            clienteService,
                            saborService,
                            pedidoService
                    );

            telaPrincipal.setVisible(true);
        });
    }
}