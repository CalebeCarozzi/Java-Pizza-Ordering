package view;

import controller.PrecoController;
import exception.CampoInvalidoException;
import exception.PersistenciaException;
import exception.PrecoInvalidoException;
import model.TabelaPreco;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.util.Locale;

public class TelaPreco extends JFrame {

    private final PrecoController controller;

    private JTextField campoPrecoSimples;
    private JTextField campoPrecoEspecial;
    private JTextField campoPrecoPremium;

    private JLabel labelPrecoSimplesAtual;
    private JLabel labelPrecoEspecialAtual;
    private JLabel labelPrecoPremiumAtual;

    private JButton botaoSalvarPrecos;
    private JButton botaoLimparCampos;
    private JButton botaoVoltar;

    public TelaPreco(PrecoController controller) {

        this.controller = controller;

        configurarJanela();
        inicializarComponentes();
        montarLayout();
        configurarEventos();
        carregarPrecos();
    }

    // ================= JANELA =================

    private void configurarJanela() {

        setTitle("Atualização de Preços");
        setSize(1050, 650);
        setMinimumSize(new Dimension(850, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // ================= COMPONENTES =================

    private void inicializarComponentes() {

        campoPrecoSimples = new JTextField();
        campoPrecoEspecial = new JTextField();
        campoPrecoPremium = new JTextField();

        labelPrecoSimplesAtual = new JLabel();
        labelPrecoEspecialAtual = new JLabel();
        labelPrecoPremiumAtual = new JLabel();

        botaoSalvarPrecos = new JButton("Salvar preços");
        botaoLimparCampos = new JButton("Limpar campos");
        botaoVoltar = new JButton("Voltar");

        Font fonte = new Font("Arial", Font.BOLD, 14);

        labelPrecoSimplesAtual.setFont(fonte);
        labelPrecoEspecialAtual.setFont(fonte);
        labelPrecoPremiumAtual.setFont(fonte);
    }

    // ================= LAYOUT =================

    private void montarLayout() {

        JPanel painelPrincipal =
                new JPanel(new GridBagLayout());

        painelPrincipal.setBorder(
                new EmptyBorder(20, 20, 20, 20)
        );

        JPanel painelConteudo =
                new JPanel(new BorderLayout(15, 15));

        painelConteudo.setPreferredSize(
                new Dimension(1050, 560)
        );

        painelConteudo.setBorder(
                new EmptyBorder(20, 20, 20, 20)
        );

        painelConteudo.add(
                criarPainelTopo(), BorderLayout.NORTH
        );

        painelConteudo.add(
                criarPainelCentral(), BorderLayout.CENTER
        );

        painelConteudo.add(
                criarPainelRodape(), BorderLayout.SOUTH
        );

        painelPrincipal.add(painelConteudo);

        setContentPane(painelPrincipal);
    }

    private JPanel criarPainelTopo() {

        JPanel painel = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel("Atualização de Preços");

        titulo.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        JLabel subtitulo = new JLabel(
                "Defina o preço por centímetro quadrado para cada tipo de pizza"
        );

        JPanel painelTextos = new JPanel();

        painelTextos.setLayout(
                new BoxLayout(painelTextos, BoxLayout.Y_AXIS)
        );

        painelTextos.add(titulo);
        painelTextos.add(Box.createVerticalStrut(6));
        painelTextos.add(subtitulo);

        painel.add(painelTextos, BorderLayout.WEST);

        return painel;
    }

    private JPanel criarPainelCentral() {

        JPanel painel =
                new JPanel(new BorderLayout(15, 0));

        painel.add(
                criarPainelFormulario(), BorderLayout.CENTER
        );

        painel.add(
                criarPainelAcoes(), BorderLayout.EAST
        );

        return painel;
    }

    private JPanel criarPainelFormulario() {

        JPanel painel =
                new JPanel(new GridLayout(3, 1, 0, 15));

        painel.add(
                criarPainelPreco(
                        "Pizza Simples",
                        campoPrecoSimples,
                        labelPrecoSimplesAtual,
                        "0,05"
                )
        );

        painel.add(
                criarPainelPreco(
                        "Pizza Especial",
                        campoPrecoEspecial,
                        labelPrecoEspecialAtual,
                        "0,07"
                )
        );

        painel.add(
                criarPainelPreco(
                        "Pizza Premium",
                        campoPrecoPremium,
                        labelPrecoPremiumAtual,
                        "0,10"
                )
        );

        return painel;
    }

    private JPanel criarPainelPreco(
            String titulo,
            JTextField campo,
            JLabel labelPrecoAtual,
            String exemplo) {

        JPanel painel =
                new JPanel(new BorderLayout(10, 10));

        painel.setBorder(
                BorderFactory.createTitledBorder(titulo)
        );

        JPanel painelCampo =
                new JPanel(new BorderLayout(8, 8));

        painelCampo.add(
                new JLabel("Preço por cm²:"),
                BorderLayout.WEST
        );

        painelCampo.add(
                campo, BorderLayout.CENTER
        );

        JPanel painelInfo = new JPanel();

        painelInfo.setLayout(
                new BoxLayout(painelInfo, BoxLayout.Y_AXIS)
        );

        painelInfo.add(labelPrecoAtual);
        painelInfo.add(Box.createVerticalStrut(4));
        painelInfo.add(new JLabel("Exemplo: " + exemplo));

        painel.add(painelCampo, BorderLayout.NORTH);
        painel.add(painelInfo, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelAcoes() {

        JPanel painel =
                new JPanel(new BorderLayout());

        painel.setBorder(
                BorderFactory.createTitledBorder("Ações")
        );

        painel.setPreferredSize(
                new Dimension(180, 0)
        );

        JPanel painelBotoes =
                new JPanel(new GridLayout(3, 1, 0, 10));

        painelBotoes.setPreferredSize(
                new Dimension(160, 150)
        );

        painelBotoes.add(botaoSalvarPrecos);
        painelBotoes.add(botaoLimparCampos);
        painelBotoes.add(botaoVoltar);

        painel.add(painelBotoes, BorderLayout.NORTH);

        return painel;
    }

    private JPanel criarPainelRodape() {

        JPanel painel = new JPanel(new BorderLayout());

        JLabel mensagem = new JLabel(
                "Use ponto ou vírgula. "
                        + "Deixe os campos que não deseja alterar vazios."
        );

        painel.add(mensagem, BorderLayout.WEST);

        return painel;
    }

    // ================= EVENTOS =================

    private void configurarEventos() {

        botaoSalvarPrecos.addActionListener(
                e -> salvarPrecos()
        );

        botaoLimparCampos.addActionListener(
                e -> limparCampos()
        );

        botaoVoltar.addActionListener(
                e -> dispose()
        );

        campoPrecoSimples.addActionListener(
                e -> salvarPrecos()
        );

        campoPrecoEspecial.addActionListener(
                e -> salvarPrecos()
        );

        campoPrecoPremium.addActionListener(
                e -> salvarPrecos()
        );
    }

    // ================= CONSULTAR =================

    private void carregarPrecos() {

        try {

            TabelaPreco tabela =
                    controller.consultarPrecos();

            labelPrecoSimplesAtual.setText(
                    formatarPreco(tabela.getPrecoSimples())
            );

            labelPrecoEspecialAtual.setText(
                    formatarPreco(tabela.getPrecoEspecial())
            );

            labelPrecoPremiumAtual.setText(
                    formatarPreco(tabela.getPrecoPremium())
            );

        } catch (PersistenciaException e) {

            tratarErroPersistencia(e);
        }
    }

    // ================= SALVAR =================

    private void salvarPrecos() {

        try {

            controller.atualizarPrecos(
                    campoPrecoSimples.getText(),
                    campoPrecoEspecial.getText(),
                    campoPrecoPremium.getText()
            );

        } catch (CampoInvalidoException |
                 PrecoInvalidoException e) {

            exibirErro(e.getMessage());
            return;

        } catch (PersistenciaException e) {

            tratarErroPersistencia(e);
            return;
        }

        limparCampos();
        carregarPrecos();

        JOptionPane.showMessageDialog(
                this,
                "Preços atualizados com sucesso."
        );
    }

    // ================= AUXILIARES =================

    private String formatarPreco(double preco) {

        return String.format(
                Locale.forLanguageTag("pt-BR"),
                "Preço atual: R$ %.4f por cm²",
                preco
        );
    }

    private void limparCampos() {

        campoPrecoSimples.setText("");
        campoPrecoEspecial.setText("");
        campoPrecoPremium.setText("");
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