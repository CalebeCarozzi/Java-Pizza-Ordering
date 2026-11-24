package view;

import controller.StatusPedidoController;

import exception.CampoInvalidoException;
import exception.PersistenciaException;

import model.Cliente;
import model.EstadoPedido;
import model.Pedido;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.List;
import java.util.Locale;

public class TelaStatusPedido extends JFrame {

    private final StatusPedidoController controller;

    private Pedido pedidoSelecionado;

    private JTable tabelaPedidos;
    private DefaultTableModel modeloTabela;

    private JTextField campoBuscaSobrenome;

    private JComboBox<EstadoPedido> comboEstadoPedido;

    private JButton botaoAlterarEstado;
    private JButton botaoExcluirPedido;
    private JButton botaoVoltar;

    private JLabel labelPedidoSelecionado;
    private JLabel labelTotalPedidos;

    public TelaStatusPedido(
            StatusPedidoController controller) {

        this.controller = controller;

        configurarJanela();
        inicializarComponentes();
        montarLayout();
        configurarEventos();

        executarAcao(this::carregarTabelaPedidos);
    }

    // ================= JANELA =================

    private void configurarJanela() {

        setTitle("Status dos Pedidos");
        setSize(1100, 650);
        setMinimumSize(new Dimension(900, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // ================= COMPONENTES =================

    private void inicializarComponentes() {

        modeloTabela = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Cliente",
                        "Telefone",
                        "Estado",
                        "Itens",
                        "Total"
                },
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int linha, int coluna) {

                return false;
            }
        };

        tabelaPedidos = new JTable(modeloTabela);

        tabelaPedidos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabelaPedidos.getTableHeader()
                .setReorderingAllowed(false);

        tabelaPedidos.setRowHeight(24);

        campoBuscaSobrenome = new JTextField();

        comboEstadoPedido = new JComboBox<>(
                EstadoPedido.values()
        );

        botaoAlterarEstado =
                new JButton("Alterar estado");

        botaoExcluirPedido =
                new JButton("Excluir pedido");

        botaoVoltar =
                new JButton("Voltar");

        labelPedidoSelecionado =
                new JLabel("Pedido selecionado: nenhum");

        labelTotalPedidos =
                new JLabel("Total de pedidos: 0");
    }

    // ================= LAYOUT =================

    private void montarLayout() {

        JPanel painelPrincipal =
                new JPanel(new BorderLayout(15, 15));

        painelPrincipal.setBorder(
                new EmptyBorder(20, 20, 20, 20)
        );

        painelPrincipal.add(
                criarPainelTopo(), BorderLayout.NORTH
        );

        painelPrincipal.add(
                criarPainelCentral(), BorderLayout.CENTER
        );

        painelPrincipal.add(
                criarPainelRodape(), BorderLayout.SOUTH
        );

        setContentPane(painelPrincipal);
    }

    private JPanel criarPainelTopo() {

        JPanel painel = new JPanel();

        painel.setLayout(
                new BoxLayout(painel, BoxLayout.Y_AXIS)
        );

        JLabel titulo =
                new JLabel("Status dos Pedidos");

        titulo.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        JLabel subtitulo = new JLabel(
                "Visualize os pedidos e altere seus estados."
        );

        painel.add(titulo);
        painel.add(Box.createVerticalStrut(6));
        painel.add(subtitulo);

        return painel;
    }

    private JPanel criarPainelCentral() {

        JPanel painel =
                new JPanel(new BorderLayout(15, 15));

        painel.add(
                criarPainelTabela(), BorderLayout.CENTER
        );

        painel.add(
                criarPainelAcoes(), BorderLayout.EAST
        );

        return painel;
    }

    private JPanel criarPainelTabela() {

        JPanel painel =
                new JPanel(new BorderLayout(10, 10));

        painel.setBorder(
                BorderFactory.createTitledBorder(
                        "Pedidos cadastrados"
                )
        );

        JPanel painelBusca =
                new JPanel(new BorderLayout(8, 8));

        painelBusca.add(
                new JLabel("Buscar por sobrenome:"),
                BorderLayout.WEST
        );

        painelBusca.add(
                campoBuscaSobrenome,
                BorderLayout.CENTER
        );

        JScrollPane scroll =
                new JScrollPane(tabelaPedidos);

        painel.add(
                painelBusca, BorderLayout.NORTH
        );

        painel.add(
                scroll, BorderLayout.CENTER
        );

        painel.add(
                labelTotalPedidos, BorderLayout.SOUTH
        );

        return painel;
    }

    private JPanel criarPainelAcoes() {

        JPanel painel =
                new JPanel(new BorderLayout(10, 10));

        painel.setBorder(
                BorderFactory.createTitledBorder("Ações")
        );

        painel.setPreferredSize(
                new Dimension(250, 0)
        );

        JPanel painelCampos = new JPanel();

        painelCampos.setLayout(
                new BoxLayout(painelCampos, BoxLayout.Y_AXIS)
        );

        JLabel labelEstado =
                new JLabel("Novo estado:");

        labelPedidoSelecionado.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        labelEstado.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        comboEstadoPedido.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        comboEstadoPedido.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 30)
        );

        JPanel painelBotoes =
                new JPanel(new GridLayout(3, 1, 0, 10));

        painelBotoes.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        painelBotoes.add(botaoAlterarEstado);
        painelBotoes.add(botaoExcluirPedido);
        painelBotoes.add(botaoVoltar);

        painelCampos.add(labelPedidoSelecionado);

        painelCampos.add(
                Box.createVerticalStrut(15)
        );

        painelCampos.add(labelEstado);

        painelCampos.add(
                Box.createVerticalStrut(5)
        );

        painelCampos.add(comboEstadoPedido);

        painelCampos.add(
                Box.createVerticalStrut(20)
        );

        painelCampos.add(painelBotoes);

        painel.add(
                painelCampos, BorderLayout.NORTH
        );

        return painel;
    }

    private JPanel criarPainelRodape() {

        JPanel painel =
                new JPanel(new BorderLayout());

        JLabel mensagem = new JLabel(
                "Selecione um pedido para alterar "
                        + "seu estado ou excluí-lo."
        );

        painel.add(
                mensagem, BorderLayout.WEST
        );

        return painel;
    }

    // ================= EVENTOS =================

    private void configurarEventos() {

        tabelaPedidos.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        executarAcao(
                                TelaStatusPedido.this::selecionarPedido
                        );
                    }
                }
        );

        campoBuscaSobrenome.addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyReleased(KeyEvent e) {

                        limparSelecao();

                        executarAcao(
                                TelaStatusPedido.this::carregarTabelaPedidos
                        );
                    }
                }
        );

        botaoAlterarEstado.addActionListener(
                e -> executarAcao(this::alterarEstadoPedido)
        );

        botaoExcluirPedido.addActionListener(
                e -> executarAcao(this::excluirPedido)
        );

        botaoVoltar.addActionListener(
                e -> dispose()
        );
    }

    // ================= CONSULTAR PEDIDOS =================

    private void carregarTabelaPedidos() {

        modeloTabela.setRowCount(0);

        String sobrenome =
                campoBuscaSobrenome.getText();

        List<Pedido> pedidos =
                controller.listarPedidosPorSobrenomeCliente(
                        sobrenome
                );

        for (Pedido pedido : pedidos) {

            Cliente cliente = pedido.getCliente();

            modeloTabela.addRow(new Object[]{
                    pedido.getIdPedido(),

                    cliente.getNome()
                            + " "
                            + cliente.getSobrenome(),

                    cliente.getTelefone(),

                    pedido.getEstadoPedido(),

                    pedido.getItens().size(),

                    String.format(
                            Locale.forLanguageTag("pt-BR"),
                            "R$ %.2f",
                            controller.calcularTotalPedido(pedido)
                    )
            });
        }

        labelTotalPedidos.setText(
                "Total de pedidos: " + pedidos.size()
        );
    }

    // ================= SELECIONAR PEDIDO =================

    private void selecionarPedido() {

        int linhaSelecionada =
                tabelaPedidos.getSelectedRow();

        if (linhaSelecionada == -1) {
            return;
        }

        linhaSelecionada =
                tabelaPedidos.convertRowIndexToModel(
                        linhaSelecionada
                );

        int idPedido = Integer.parseInt(
                modeloTabela.getValueAt(
                        linhaSelecionada, 0
                ).toString()
        );

        pedidoSelecionado = null;

        labelPedidoSelecionado.setText(
                "Pedido selecionado: nenhum"
        );

        pedidoSelecionado =
                controller.buscaPedidoPorId(idPedido);

        comboEstadoPedido.setSelectedItem(
                pedidoSelecionado.getEstadoPedido()
        );

        labelPedidoSelecionado.setText(
                "Pedido selecionado: #"
                        + pedidoSelecionado.getIdPedido()
        );
    }

    // ================= ALTERAR ESTADO =================

    private void alterarEstadoPedido() {

        validarPedidoSelecionado();

        EstadoPedido novoEstado =
                (EstadoPedido) comboEstadoPedido.getSelectedItem();

        controller.alterarEstadoPedido(
                pedidoSelecionado,
                novoEstado
        );

        int idPedido =
                pedidoSelecionado.getIdPedido();

        carregarTabelaPedidos();

        selecionarPedidoNaTabela(idPedido);

        JOptionPane.showMessageDialog(
                this,
                "Estado do pedido alterado com sucesso."
        );
    }

    // ================= EXCLUIR PEDIDO =================

    private void excluirPedido() {

        validarPedidoSelecionado();

        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o pedido selecionado?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (opcao == JOptionPane.YES_OPTION) {

            controller.excluirPedido(
                    pedidoSelecionado.getIdPedido()
            );

            limparSelecao();

            carregarTabelaPedidos();

            JOptionPane.showMessageDialog(
                    this,
                    "Pedido excluído com sucesso."
            );
        }
    }

    // ================= MÉTODOS AUXILIARES =================

    private void selecionarPedidoNaTabela(int idPedido) {

        for (int i = 0; i < modeloTabela.getRowCount(); i++) {

            int idAtual = Integer.parseInt(
                    modeloTabela.getValueAt(i, 0).toString()
            );

            if (idAtual == idPedido) {

                int linha =
                        tabelaPedidos.convertRowIndexToView(i);

                tabelaPedidos.setRowSelectionInterval(
                        linha, linha
                );

                selecionarPedido();

                return;
            }
        }

        limparSelecao();
    }

    private void limparSelecao() {

        pedidoSelecionado = null;

        tabelaPedidos.clearSelection();

        comboEstadoPedido.setSelectedIndex(0);

        labelPedidoSelecionado.setText(
                "Pedido selecionado: nenhum"
        );
    }

    private void validarPedidoSelecionado() {

        if (pedidoSelecionado == null) {

            throw new CampoInvalidoException(
                    "Selecione um pedido primeiro."
            );
        }
    }

    // ================= TRATAMENTO DE ERROS =================

    private void executarAcao(Runnable acao) {

        try {

            acao.run();

        } catch (CampoInvalidoException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (PersistenciaException e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro de persistência",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (RuntimeException e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Ocorreu um erro inesperado.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}