package app;

import controller.ClienteController;
import dao.ClienteDAO;
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

            // Armazenamento temporário dos módulos
            // que ainda não foram migrados.
            RepositoryMemoria repositoryMemoria =
                    new RepositoryMemoria();

            // DAOs
            ClienteDAO clienteDAO =
                    DaoFactory.getClienteDAO();

            // Services
            ClienteService clienteService =
                    new ClienteService(
                            clienteDAO,
                            repositoryMemoria
                    );

            SaborService saborService =
                    new SaborService(repositoryMemoria);

            PrecoService precoService =
                    new PrecoService(repositoryMemoria);

            PedidoService pedidoService =
                    new PedidoService(repositoryMemoria);

            // Controllers
            ClienteController clienteController =
                    new ClienteController(clienteService);

            // Interface principal
            TelaPrincipal telaPrincipal =
                    new TelaPrincipal(
                            clienteController,
                            clienteService,
                            saborService,
                            precoService,
                            pedidoService
                    );

            telaPrincipal.setVisible(true);
        });
    }
}