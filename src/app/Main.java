package app;

import controller.ClienteController;
import controller.SaborController;
import controller.PrecoController;
import controller.PedidoController;
import controller.StatusPedidoController;

import dao.ClienteDAO;
import dao.SaborDAO;
import dao.PrecoDAO;
import dao.PedidoDAO;
import dao.DaoFactory;

import service.ClienteService;
import service.SaborService;
import service.PrecoService;
import service.PedidoService;
import service.FormaFactory;

import view.TelaPrincipal;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // DAOs
            ClienteDAO clienteDAO =
                    DaoFactory.getClienteDAO();

            SaborDAO saborDAO =
                    DaoFactory.getSaborDAO();

            PrecoDAO precoDAO =
                    DaoFactory.getPrecoDAO();

            PedidoDAO pedidoDAO =
                    DaoFactory.getPedidoDAO();

            // Factory
            FormaFactory formaFactory =
                    new FormaFactory();

            // Services
            ClienteService clienteService =
                    new ClienteService(clienteDAO);

            SaborService saborService =
                    new SaborService(saborDAO);

            PrecoService precoService =
                    new PrecoService(precoDAO);

            PedidoService pedidoService =
                    new PedidoService(
                            pedidoDAO,
                            saborDAO,
                            precoService,
                            formaFactory
                    );

            // Controllers
            ClienteController clienteController =
                    new ClienteController(clienteService);

            SaborController saborController =
                    new SaborController(saborService);

            PrecoController precoController =
                    new PrecoController(precoService);

            PedidoController pedidoController =
                    new PedidoController(
                            clienteService,
                            saborService,
                            pedidoService
                    );

            StatusPedidoController statusPedidoController =
                    new StatusPedidoController(pedidoService);

            // Interface principal
            TelaPrincipal telaPrincipal =
                    new TelaPrincipal(
                            clienteController,
                            saborController,
                            precoController,
                            pedidoController,
                            statusPedidoController
                    );

            telaPrincipal.setVisible(true);
        });
    }
}