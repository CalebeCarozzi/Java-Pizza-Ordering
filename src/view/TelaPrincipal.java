package view;

import controller.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TelaPrincipal extends JFrame {
    private final ClienteController clienteController;
    private final SaborController saborController;
    private final PrecoController precoController;
    private final PedidoController pedidoController;
    private final StatusPedidoController statusPedidoController;

    private final JButton botaoPedidos = new JButton("Pedidos");
    private final JButton botaoClientes = new JButton("Clientes");
    private final JButton botaoSabores = new JButton("Sabores");
    private final JButton botaoPrecos = new JButton("Preços");
    private final JButton botaoStatusPedidos = new JButton("Status dos Pedidos");
    private final JButton botaoSair = new JButton("Sair");

    public TelaPrincipal(ClienteController clienteController,
                         SaborController saborController,
                         PrecoController precoController,
                         PedidoController pedidoController,
                         StatusPedidoController statusPedidoController) {
        this.clienteController = clienteController;
        this.saborController = saborController;
        this.precoController = precoController;
        this.pedidoController = pedidoController;
        this.statusPedidoController = statusPedidoController;

        configurarJanela();
        montarLayout();
        configurarEventos();
    }

    private void configurarJanela() {
        setTitle("Sistema de Pedidos de Pizza");
        setSize(420, 420);
        setMinimumSize(new Dimension(420, 420));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void montarLayout() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Sistema de Pedidos de Pizza", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel painelBotoes = new JPanel(new GridLayout(6, 1, 0, 10));
        painelBotoes.setPreferredSize(new Dimension(320, 300));
        painelBotoes.add(botaoPedidos);
        painelBotoes.add(botaoClientes);
        painelBotoes.add(botaoSabores);
        painelBotoes.add(botaoPrecos);
        painelBotoes.add(botaoStatusPedidos);
        painelBotoes.add(botaoSair);

        JPanel painelCentro = new JPanel(new GridBagLayout());
        painelCentro.add(painelBotoes);
        painelPrincipal.add(titulo, BorderLayout.NORTH);
        painelPrincipal.add(painelCentro, BorderLayout.CENTER);
        setContentPane(painelPrincipal);
    }

    private void configurarEventos() {
        botaoPedidos.addActionListener(e -> new TelaPedido(pedidoController).setVisible(true));
        botaoClientes.addActionListener(e -> new TelaCliente(clienteController).setVisible(true));
        botaoSabores.addActionListener(e -> new TelaSabor(saborController).setVisible(true));
        botaoPrecos.addActionListener(e -> new TelaPreco(precoController).setVisible(true));
        botaoStatusPedidos.addActionListener(e -> new TelaStatusPedido(statusPedidoController).setVisible(true));
        botaoSair.addActionListener(e -> sair());
    }

    private void sair() {
        int opcao = JOptionPane.showConfirmDialog(this, "Deseja sair do sistema?",
                "Confirmação", JOptionPane.YES_NO_OPTION);
        if (opcao == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
