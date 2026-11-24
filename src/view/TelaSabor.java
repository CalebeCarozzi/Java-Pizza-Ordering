package view;

import controller.SaborController;
import exception.CampoInvalidoException;
import exception.NenhumaLinhaSelecionadaException;
import exception.PersistenciaException;
import exception.SaborInvalidoException;
import model.Sabor;
import model.TipoPizza;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TelaSabor extends JFrame {

    private final SaborController controller;

    private JTextField campoNomeSabor;
    private JTextField campoBuscaSabor;
    private JComboBox<TipoPizza> comboTipoPizza;

    private JButton botaoCadastrar;
    private JButton botaoAtualizar;
    private JButton botaoExcluir;
    private JButton botaoLimparCampos;
    private JButton botaoVoltar;

    private JTable tabelaSabores;
    private DefaultTableModel modeloTabela;

    private JLabel labelTotalSabores;

    public TelaSabor(SaborController controller) {

        this.controller = controller;

        configurarJanela();
        inicializarComponentes();
        montarLayout();
        configurarEventos();
        carregarTabela();
    }

    // ================= JANELA =================

    private void configurarJanela() {

        setTitle("Cadastro de Sabores");
        setSize(900, 550);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // ================= COMPONENTES =================

    private void inicializarComponentes() {

        campoNomeSabor = new JTextField();
        campoBuscaSabor = new JTextField();

        comboTipoPizza = new JComboBox<>(
                TipoPizza.values()
        );

        comboTipoPizza.setSelectedIndex(0);

        botaoCadastrar = new JButton("Cadastrar");
        botaoAtualizar = new JButton("Atualizar");
        botaoExcluir = new JButton("Excluir");
        botaoLimparCampos = new JButton("Limpar campos");
        botaoVoltar = new JButton("Voltar");

        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Sabor", "Tipo"},
                0
        ) {
            @Override
            public boolean isCellEditable(
                    int row, int column) {

                return false;
            }
        };

        tabelaSabores = new JTable(modeloTabela);

        tabelaSabores.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabelaSabores.getTableHeader()
                .setReorderingAllowed(false);

        tabelaSabores.setRowHeight(24);

        tabelaSabores.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        tabelaSabores.getColumnModel()
                .getColumn(0).setPreferredWidth(60);

        tabelaSabores.getColumnModel()
                .getColumn(1).setPreferredWidth(300);

        tabelaSabores.getColumnModel()
                .getColumn(2).setPreferredWidth(160);

        labelTotalSabores =
                new JLabel("Total de sabores: 0");
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
                new JLabel("Cadastro de Sabores");

        titulo.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        JLabel subtitulo = new JLabel(
                "Cadastre os sabores e relacione cada um ao tipo da pizza"
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
                criarPainelFormulario(), BorderLayout.WEST
        );

        painelCentral.add(
                criarPainelTabela(), BorderLayout.CENTER
        );

        return painelCentral;
    }

    private JPanel criarPainelFormulario() {

        JPanel painelFormulario = new JPanel();

        painelFormulario.setLayout(
                new BoxLayout(
                        painelFormulario,
                        BoxLayout.Y_AXIS
                )
        );

        painelFormulario.setPreferredSize(
                new Dimension(300, 0)
        );

        JPanel painelDados = criarPainelDadosSabor();
        JPanel painelAcoes = criarPainelAcoes();

        painelDados.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 150)
        );

        painelAcoes.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 190)
        );

        painelFormulario.add(painelDados);
        painelFormulario.add(Box.createVerticalStrut(12));

        painelFormulario.add(painelAcoes);
        painelFormulario.add(Box.createVerticalGlue());

        return painelFormulario;
    }

    private JPanel criarPainelDadosSabor() {

        JPanel painel =
                new JPanel(new GridLayout(4, 1, 8, 8));

        painel.setBorder(
                BorderFactory.createTitledBorder(
                        "Dados do sabor"
                )
        );

        painel.add(new JLabel("Nome do sabor:"));
        painel.add(campoNomeSabor);

        painel.add(new JLabel("Tipo da pizza:"));
        painel.add(comboTipoPizza);

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

    private JPanel criarPainelBusca() {

        JPanel painelBusca =
                new JPanel(new BorderLayout(8, 8));

        painelBusca.add(
                new JLabel("Buscar sabor:"),
                BorderLayout.WEST
        );

        painelBusca.add(
                campoBuscaSabor, BorderLayout.CENTER
        );

        return painelBusca;
    }

    private JPanel criarPainelTabela() {

        JPanel painelTabela =
                new JPanel(new BorderLayout(10, 10));

        painelTabela.setBorder(
                BorderFactory.createTitledBorder(
                        "Sabores cadastrados"
                )
        );

        JScrollPane scrollTabela = new JScrollPane(
                tabelaSabores,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        JPanel painelInferior =
                new JPanel(new BorderLayout());

        painelInferior.add(
                labelTotalSabores, BorderLayout.WEST
        );

        painelTabela.add(
                criarPainelBusca(), BorderLayout.NORTH
        );

        painelTabela.add(
                scrollTabela, BorderLayout.CENTER
        );

        painelTabela.add(
                painelInferior, BorderLayout.SOUTH
        );

        return painelTabela;
    }

    private JPanel criarPainelRodape() {

        JPanel painelRodape =
                new JPanel(new BorderLayout());

        JLabel mensagem = new JLabel(
                "Selecione um sabor na tabela para atualizar ou excluir."
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

    // ================= EVENTOS =================

    private void configurarEventos() {

        botaoCadastrar.addActionListener(
                e -> cadastrarSabor()
        );

        botaoAtualizar.addActionListener(
                e -> atualizarSabor()
        );

        botaoExcluir.addActionListener(
                e -> excluirSabor()
        );

        botaoLimparCampos.addActionListener(
                e -> limparCampos()
        );

        botaoVoltar.addActionListener(
                e -> dispose()
        );

        campoNomeSabor.addActionListener(
                e -> cadastrarSabor()
        );

        tabelaSabores.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if (e.getClickCount() == 2
                                && tabelaSabores.rowAtPoint(
                                e.getPoint()) != -1
                                && tabelaSabores.getSelectedRow() != -1) {

                            obterCamposSelecionados();
                        }
                    }
                }
        );

        campoBuscaSabor.addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyReleased(KeyEvent e) {

                        filtrarSaboresPorNome();
                    }
                }
        );
    }

    // ================= CADASTRAR =================

    private void cadastrarSabor() {

        try {

            String nome = campoNomeSabor.getText();

            TipoPizza tipo = (TipoPizza)
                    comboTipoPizza.getSelectedItem();

            controller.cadastrarSabor(nome, tipo);

            atualizarTela();

            exibirMensagem(
                    "Sabor cadastrado com sucesso."
            );

        } catch (CampoInvalidoException |
                 SaborInvalidoException e) {

            exibirErro(e.getMessage());

        } catch (PersistenciaException e) {

            tratarErroPersistencia(e);
        }
    }

    // ================= ATUALIZAR =================

    private void atualizarSabor() {

        try {

            int id = obterIdSelecionado();

            String nome = campoNomeSabor.getText();

            TipoPizza tipo = (TipoPizza)
                    comboTipoPizza.getSelectedItem();

            controller.atualizarSabor(
                    id, nome, tipo
            );

            atualizarTela();

            exibirMensagem(
                    "Sabor atualizado com sucesso."
            );

        } catch (CampoInvalidoException |
                 SaborInvalidoException |
                 NenhumaLinhaSelecionadaException e) {

            exibirErro(e.getMessage());

        } catch (PersistenciaException e) {

            tratarErroPersistencia(e);
        }
    }

    // ================= EXCLUIR =================

    private void excluirSabor() {

        try {

            int id = obterIdSelecionado();

            int opcao = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja realmente excluir esse sabor?",
                    "Confirmar exclusão",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcao != JOptionPane.YES_OPTION) {
                return;
            }

            controller.excluirSabor(id);

            atualizarTela();

            exibirMensagem(
                    "Sabor excluído com sucesso."
            );

        } catch (SaborInvalidoException |
                 NenhumaLinhaSelecionadaException e) {

            exibirErro(e.getMessage());

        } catch (PersistenciaException e) {

            tratarErroPersistencia(e);
        }
    }

    // ================= FILTRAR =================

    private void filtrarSaboresPorNome() {

        try {

            List<Sabor> sabores =
                    controller.filtrarSabores(
                            campoBuscaSabor.getText()
                    );

            limparCampos();

            exibirSabores(sabores);

        } catch (PersistenciaException e) {

            tratarErroPersistencia(e);
        }
    }

    // ================= TABELA =================

    private void carregarTabela() {

        try {

            List<Sabor> sabores =
                    controller.listarSabores();

            exibirSabores(sabores);

        } catch (PersistenciaException e) {

            tratarErroPersistencia(e);
        }
    }

    private void exibirSabores(List<Sabor> sabores) {

        modeloTabela.setRowCount(0);

        for (Sabor sabor : sabores) {

            modeloTabela.addRow(new Object[]{
                    sabor.getIdSabor(),
                    sabor.getNome(),
                    sabor.getTipoPizza()
            });
        }

        labelTotalSabores.setText(
                "Total de sabores: " + sabores.size()
        );
    }

    private int obterLinhaSelecionada() {

        int linha = tabelaSabores.getSelectedRow();

        if (linha == -1) {

            throw new NenhumaLinhaSelecionadaException(
                    "Nenhuma linha foi selecionada."
            );
        }

        return tabelaSabores.convertRowIndexToModel(linha);
    }

    private int obterIdSelecionado() {

        int linha = obterLinhaSelecionada();

        return Integer.parseInt(
                modeloTabela.getValueAt(linha, 0).toString()
        );
    }

    private void obterCamposSelecionados() {

        int linha = obterLinhaSelecionada();

        String nome = modeloTabela.getValueAt(
                linha, 1
        ).toString();

        TipoPizza tipo = TipoPizza.valueOf(
                modeloTabela.getValueAt(linha, 2).toString()
        );

        campoNomeSabor.setText(nome);

        comboTipoPizza.setSelectedItem(tipo);
    }

    // ================= LIMPEZA =================

    private void limparCampos() {

        campoNomeSabor.setText("");
        comboTipoPizza.setSelectedIndex(0);
        tabelaSabores.clearSelection();
    }

    private void atualizarTela() {

        limparCampos();

        campoBuscaSabor.setText("");

        carregarTabela();
    }

    // ================= MENSAGENS =================

    private void exibirMensagem(String mensagem) {

        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Informação",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void exibirErro(String mensagem) {

        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void tratarErroPersistencia(
            PersistenciaException e) {

        e.printStackTrace();

        exibirErro(e.getMessage());
    }
}