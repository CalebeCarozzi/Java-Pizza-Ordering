package view;

import controller.ClienteController;
import exception.NenhumaLinhaSelecionadaException;
import model.Cliente;
import service.ClienteService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TelaCliente extends JFrame {

    private final ClienteController controller;

    private JTextField campoNome;
    private JTextField campoSobrenome;
    private JTextField campoTelefone;

    private JTextField campoFiltroSobrenome;
    private JTextField campoFiltroTelefone;

    private JButton botaoCadastrar;
    private JButton botaoAtualizar;
    private JButton botaoExcluir;
    private JButton botaoLimparCampos;

    private JButton botaoFiltrar;
    private JButton botaoLimparFiltros;
    private JButton botaoVoltar;

    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;

    private JLabel labelTotalClientes;

    public TelaCliente(ClienteService clienteService) {

        this.controller = new ClienteController(
                clienteService, this
        );

        configurarJanela();
        inicializarComponentes();
        montarLayout();
        configurarEventos();

        controller.carregarClientes();
    }

    // ================= JANELA =================

    private void configurarJanela() {

        setTitle("Cadastro de Clientes");
        setSize(1050, 650);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // ================= COMPONENTES =================

    private void inicializarComponentes() {

        campoNome = new JTextField();
        campoSobrenome = new JTextField();
        campoTelefone = new JTextField();

        campoFiltroSobrenome = new JTextField();
        campoFiltroTelefone = new JTextField();

        botaoCadastrar = new JButton("Cadastrar");
        botaoAtualizar = new JButton("Atualizar");
        botaoExcluir = new JButton("Excluir");
        botaoLimparCampos = new JButton("Limpar campos");

        botaoFiltrar = new JButton("Filtrar");
        botaoLimparFiltros = new JButton("Limpar filtros");

        botaoVoltar = new JButton("Voltar");

        modeloTabela = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Nome",
                        "Sobrenome",
                        "Telefone"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(
                    int row, int column) {

                return false;
            }
        };

        tabelaClientes = new JTable(modeloTabela);

        tabelaClientes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabelaClientes.getTableHeader()
                .setReorderingAllowed(false);

        tabelaClientes.setRowHeight(24);

        tabelaClientes.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        tabelaClientes.getColumnModel()
                .getColumn(0).setPreferredWidth(60);

        tabelaClientes.getColumnModel()
                .getColumn(1).setPreferredWidth(220);

        tabelaClientes.getColumnModel()
                .getColumn(2).setPreferredWidth(260);

        tabelaClientes.getColumnModel()
                .getColumn(3).setPreferredWidth(180);

        labelTotalClientes =
                new JLabel("Total de clientes: 0");
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

        JPanel painelTopo =
                new JPanel(new BorderLayout());

        JLabel titulo =
                new JLabel("Cadastro de Clientes");

        titulo.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        JLabel subtitulo = new JLabel(
                "Cadastre, atualize, exclua e filtre clientes da pizzaria"
        );

        subtitulo.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        JPanel painelTextos = new JPanel();

        painelTextos.setLayout(
                new BoxLayout(painelTextos, BoxLayout.Y_AXIS)
        );

        painelTextos.add(titulo);
        painelTextos.add(Box.createVerticalStrut(6));
        painelTextos.add(subtitulo);

        painelTopo.add(
                painelTextos, BorderLayout.WEST
        );

        return painelTopo;
    }

    private JPanel criarPainelCentral() {

        JPanel painelCentral =
                new JPanel(new BorderLayout(15, 0));

        painelCentral.add(
                criarPainelLateral(), BorderLayout.WEST
        );

        painelCentral.add(
                criarPainelTabela(), BorderLayout.CENTER
        );

        return painelCentral;
    }

    private JPanel criarPainelLateral() {

        JPanel painelLateral = new JPanel();

        painelLateral.setLayout(
                new BoxLayout(
                        painelLateral,
                        BoxLayout.Y_AXIS
                )
        );

        painelLateral.setPreferredSize(
                new Dimension(340, 0)
        );

        JPanel painelDados = criarPainelDadosCliente();
        JPanel painelFiltros = criarPainelFiltros();
        JPanel painelAcoes = criarPainelAcoes();

        painelDados.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 165)
        );

        painelFiltros.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 155)
        );

        painelAcoes.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 190)
        );

        painelLateral.add(painelDados);
        painelLateral.add(Box.createVerticalStrut(12));

        painelLateral.add(painelFiltros);
        painelLateral.add(Box.createVerticalStrut(12));

        painelLateral.add(painelAcoes);
        painelLateral.add(Box.createVerticalGlue());

        return painelLateral;
    }

    private JPanel criarPainelDadosCliente() {

        JPanel painel =
                new JPanel(new GridBagLayout());

        painel.setBorder(
                BorderFactory.createTitledBorder(
                        "Dados do cliente"
                )
        );

        GridBagConstraints gbc =
                criarGridBagConstraints();

        adicionarLinhaFormulario(
                painel, gbc, "Nome:", campoNome, 0
        );

        adicionarLinhaFormulario(
                painel, gbc, "Sobrenome:", campoSobrenome, 1
        );

        adicionarLinhaFormulario(
                painel, gbc, "Telefone:", campoTelefone, 2
        );

        return painel;
    }

    private JPanel criarPainelFiltros() {

        JPanel painel =
                new JPanel(new GridBagLayout());

        painel.setBorder(
                BorderFactory.createTitledBorder(
                        "Filtros de clientes"
                )
        );

        GridBagConstraints gbc =
                criarGridBagConstraints();

        adicionarLinhaFormulario(
                painel, gbc, "Sobrenome:",
                campoFiltroSobrenome, 0
        );

        adicionarLinhaFormulario(
                painel, gbc, "Telefone:",
                campoFiltroTelefone, 1
        );

        JPanel painelBotoesFiltro =
                new JPanel(new GridLayout(1, 2, 8, 0));

        painelBotoesFiltro.add(botaoFiltrar);
        painelBotoesFiltro.add(botaoLimparFiltros);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.insets = new Insets(10, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        painel.add(painelBotoesFiltro, gbc);

        return painel;
    }

    private JPanel criarPainelAcoes() {

        JPanel painel =
                new JPanel(new GridLayout(4, 1, 0, 10));

        painel.setBorder(
                BorderFactory.createTitledBorder("Ações")
        );

        painel.add(botaoCadastrar);
        painel.add(botaoAtualizar);
        painel.add(botaoExcluir);
        painel.add(botaoLimparCampos);

        return painel;
    }

    private JPanel criarPainelTabela() {

        JPanel painelTabela =
                new JPanel(new BorderLayout(10, 10));

        painelTabela.setBorder(
                BorderFactory.createTitledBorder(
                        "Clientes cadastrados"
                )
        );

        JScrollPane scrollTabela = new JScrollPane(
                tabelaClientes,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        JPanel painelInferiorTabela =
                new JPanel(new BorderLayout());

        painelInferiorTabela.add(
                labelTotalClientes, BorderLayout.WEST
        );

        painelTabela.add(
                scrollTabela, BorderLayout.CENTER
        );

        painelTabela.add(
                painelInferiorTabela, BorderLayout.SOUTH
        );

        return painelTabela;
    }

    private JPanel criarPainelRodape() {

        JPanel painelRodape =
                new JPanel(new BorderLayout());

        JLabel mensagem = new JLabel(
                "Selecione um cliente na tabela para atualizar ou excluir."
        );

        JPanel painelBotaoVoltar =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT, 0, 0
                ));

        painelBotaoVoltar.add(botaoVoltar);

        painelRodape.add(
                mensagem, BorderLayout.WEST
        );

        painelRodape.add(
                painelBotaoVoltar, BorderLayout.EAST
        );

        return painelRodape;
    }

    private GridBagConstraints criarGridBagConstraints() {

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        return gbc;
    }

    private void adicionarLinhaFormulario(
            JPanel painel,
            GridBagConstraints gbc,
            String textoLabel,
            JTextField campo,
            int linha) {

        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 8, 8, 8);

        painel.add(new JLabel(textoLabel), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        painel.add(campo, gbc);
    }

    // ================= EVENTOS =================

    private void configurarEventos() {

        botaoCadastrar.addActionListener(
                e -> controller.cadastrarCliente()
        );

        botaoAtualizar.addActionListener(
                e -> controller.atualizarCliente()
        );

        botaoExcluir.addActionListener(
                e -> controller.excluirCliente()
        );

        botaoFiltrar.addActionListener(
                e -> controller.filtrarClientes()
        );

        botaoLimparFiltros.addActionListener(
                e -> controller.limparFiltros()
        );

        botaoLimparCampos.addActionListener(
                e -> limparCampos()
        );

        botaoVoltar.addActionListener(
                e -> dispose()
        );

        campoNome.addActionListener(
                e -> controller.cadastrarCliente()
        );

        campoSobrenome.addActionListener(
                e -> controller.cadastrarCliente()
        );

        campoTelefone.addActionListener(
                e -> controller.cadastrarCliente()
        );

        campoFiltroSobrenome.addActionListener(
                e -> controller.filtrarClientes()
        );

        campoFiltroTelefone.addActionListener(
                e -> controller.filtrarClientes()
        );

        tabelaClientes.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if (e.getClickCount() == 2
                                && tabelaClientes.getSelectedRow() != -1) {

                            obterCamposSelecionados();
                        }
                    }
                }
        );
    }

    // ================= GETTERS =================

    public String getNome() {
        return campoNome.getText();
    }

    public String getSobrenome() {
        return campoSobrenome.getText();
    }

    public String getTelefone() {
        return campoTelefone.getText();
    }

    public String getFiltroSobrenome() {
        return campoFiltroSobrenome.getText();
    }

    public String getFiltroTelefone() {
        return campoFiltroTelefone.getText();
    }

    // ================= TABELA =================

    public void exibirClientes(List<Cliente> clientes) {

        modeloTabela.setRowCount(0);

        for (Cliente cliente : clientes) {

            modeloTabela.addRow(new Object[]{
                    cliente.getId(),
                    cliente.getNome(),
                    cliente.getSobrenome(),
                    cliente.getTelefone()
            });
        }

        labelTotalClientes.setText(
                "Total de clientes: " + clientes.size()
        );
    }

    public int obterLinhaSelecionada() {

        int linha = tabelaClientes.getSelectedRow();

        if (linha == -1) {

            throw new NenhumaLinhaSelecionadaException(
                    "Nenhuma linha foi selecionada."
            );
        }

        return tabelaClientes.convertRowIndexToModel(linha);
    }

    public int obterIdSelecionado() {

        int linha = obterLinhaSelecionada();

        return Integer.parseInt(
                modeloTabela.getValueAt(linha, 0).toString()
        );
    }

    public void obterCamposSelecionados() {

        int linha = obterLinhaSelecionada();

        campoNome.setText(
                modeloTabela.getValueAt(linha, 1).toString()
        );

        campoSobrenome.setText(
                modeloTabela.getValueAt(linha, 2).toString()
        );

        campoTelefone.setText(
                modeloTabela.getValueAt(linha, 3).toString()
        );
    }

    // ================= LIMPEZA =================

    public void limparCampos() {

        campoNome.setText("");
        campoSobrenome.setText("");
        campoTelefone.setText("");

        tabelaClientes.clearSelection();
    }

    public void limparCamposFiltro() {

        campoFiltroSobrenome.setText("");
        campoFiltroTelefone.setText("");
    }

    // ================= MENSAGENS =================

    public void exibirMensagem(String mensagem) {

        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Informação",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void exibirErro(String mensagem) {

        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public boolean confirmarExclusao() {

        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir esse cliente e seus pedidos?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        return opcao == JOptionPane.YES_OPTION;
    }
}