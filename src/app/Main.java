package app;

import controller.ClienteController;
import controller.SaborController;
import dao.ClienteDAO;
import dao.SaborDAO;
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

            // Services
            ClienteService clienteService =
                    new ClienteService(
                            clienteDAO,
                            repositoryMemoria
                    );

            SaborService saborService =
                    new SaborService(saborDAO);

            PrecoService precoService =
                    new PrecoService(repositoryMemoria);

            PedidoService pedidoService =
                    new PedidoService(repositoryMemoria);

            // Controllers
            ClienteController clienteController =
                    new ClienteController(clienteService);

            SaborController saborController =
                    new SaborController(saborService);

            // Interface principal
            TelaPrincipal telaPrincipal =
                    new TelaPrincipal(
                            clienteController,
                            saborController,
                            clienteService,
                            saborService,
                            precoService,
                            pedidoService
                    );

            telaPrincipal.setVisible(true);
        });
    }
}