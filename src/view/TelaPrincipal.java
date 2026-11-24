package view;

import controller.ClienteController;
import controller.SaborController;
import service.ClienteService;
import service.PedidoService;
import service.PrecoService;
import service.SaborService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;

public class TelaPrincipal extends JFrame {

    private final ClienteController clienteController;
    private final SaborController saborController;

    // Dependências temporárias dos módulos antigos.
    private final ClienteService clienteService;
    private final SaborService saborService;
    private final PrecoService precoService;
    private final PedidoService pedidoService;

    private JButton botaoPedidos;
    private JButton botaoClientes;
    private JButton botaoSabores;
    private JButton botaoPrecos;
    private JButton botaoStatusPedidos;
    private JButton botaoSair;

    public TelaPrincipal(
            ClienteController clienteController,
            SaborController saborController,
            ClienteService clienteService,
            SaborService saborService,
            PrecoService precoService,
            PedidoService pedidoService) {

        this.clienteController = clienteController;
        this.saborController = saborController;

        this.clienteService = clienteService;
        this.saborService = saborService;
        this.precoService = precoService;
        this.pedidoService = pedidoService;

        configurarJanela();
        inicializarComponentes();
        montarLayout();
        configurarEventos();
    }

    // ================= JANELA =================

    private void configurarJanela() {

        setTitle("Sistema de Pedidos de Pizza");
        setSize(420, 420);
        setMinimumSize(new Dimension(420, 420));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // ================= COMPONENTES =================

    private void inicializarComponentes() {

        botaoPedidos = new JButton("Pedidos");
        botaoClientes = new JButton("Clientes");
        botaoSabores = new JButton("Sabores");
        botaoPrecos = new JButton("Preços");

        botaoStatusPedidos =
                new JButton("Status dos Pedidos");

        botaoSair = new JButton("Sair");
    }

    // ================= LAYOUT =================

    private void montarLayout() {

        JPanel painelPrincipal =
                new JPanel(new BorderLayout());

        painelPrincipal.setBorder(
                new EmptyBorder(20, 20, 20, 20)
        );

        JLabel titulo =
                new JLabel("Sistema de Pedidos de Pizza");

        titulo.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        titulo.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        JPanel painelBotoes =
                new JPanel(new GridLayout(6, 1, 0, 10));

        painelBotoes.setPreferredSize(
                new Dimension(320, 300)
        );

        painelBotoes.add(botaoPedidos);
        painelBotoes.add(botaoClientes);
        painelBotoes.add(botaoSabores);
        painelBotoes.add(botaoPrecos);
        painelBotoes.add(botaoStatusPedidos);
        painelBotoes.add(botaoSair);

        JPanel painelCentro =
                new JPanel(new GridBagLayout());

        painelCentro.add(painelBotoes);

        painelPrincipal.add(
                titulo, BorderLayout.NORTH
        );

        painelPrincipal.add(
                painelCentro, BorderLayout.CENTER
        );

        setContentPane(painelPrincipal);
    }

    // ================= EVENTOS =================

    private void configurarEventos() {

        botaoPedidos.addActionListener(
                e -> abrirTelaPedidos()
        );

        botaoClientes.addActionListener(
                e -> abrirTelaClientes()
        );

        botaoSabores.addActionListener(
                e -> abrirTelaSabores()
        );

        botaoPrecos.addActionListener(
                e -> abrirTelaPrecos()
        );

        botaoStatusPedidos.addActionListener(
                e -> abrirTelaStatusPedidos()
        );

        botaoSair.addActionListener(
                e -> sair()
        );
    }

    // ================= TELAS =================

    private void abrirTelaClientes() {

        TelaCliente telaCliente =
                new TelaCliente(clienteController);

        telaCliente.setVisible(true);
    }

    private void abrirTelaSabores() {

        TelaSabor telaSabor =
                new TelaSabor(saborController);

        telaSabor.setVisible(true);
    }

    private void abrirTelaPedidos() {

        TelaPedido telaPedido =
                new TelaPedido(
                        clienteService,
                        saborService,
                        pedidoService
                );

        telaPedido.setVisible(true);
    }

    private void abrirTelaPrecos() {

        TelaPreco telaPreco =
                new TelaPreco(precoService);

        telaPreco.setVisible(true);
    }

    private void abrirTelaStatusPedidos() {

        TelaStatusPedido telaStatusPedido =
                new TelaStatusPedido(pedidoService);

        telaStatusPedido.setVisible(true);
    }

    // ================= SAIR =================

    private void sair() {

        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Deseja sair do sistema?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (opcao == JOptionPane.YES_OPTION) {

            System.exit(0);
        }
    }
}