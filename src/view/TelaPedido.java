package view;

import controller.PedidoController;

import exception.CampoInvalidoException;
import exception.ClienteNaoEncontradoException;
import exception.SaborInvalidoException;
import exception.PersistenciaException;

import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TelaPedido extends JFrame {

    private final PedidoController controller;

    private Cliente clienteSelecionado;
    private Pedido pedidoSelecionado;

    private List<Cliente> clientesEncontrados = new ArrayList<>();

    private JTextField campoTelefoneCliente;
    private JTextField campoValorMedida;
    private JTextField campoQuantidade;

    private JLabel labelClienteNome;
    private JLabel labelClienteTelefone;
    private JLabel labelPedidoAtual;
    private JLabel labelAjudaMedida;
    private JLabel labelResultadoCalculo;
    private JLabel labelTotalPedido;

    private JComboBox<String> comboForma;
    private JComboBox<String> comboModoEntrada;
    private JComboBox<Object> comboSabor1;
    private JComboBox<Object> comboSabor2;

    private JButton botaoBuscarCliente;
    private JButton botaoNovoPedido;
    private JButton botaoCalcularMedida;
    private JButton botaoAdicionarPizza;
    private JButton botaoAtualizarItem;
    private JButton botaoRemoverItem;
    private JButton botaoCancelarPedido;
    private JButton botaoLimparCampos;
    private JButton botaoFinalizarPedido;
    private JButton botaoVoltar;

    private JTable tabelaClientes;
    private JTable tabelaPedidos;
    private JTable tabelaItens;

    private DefaultTableModel modeloClientes;
    private DefaultTableModel modeloPedidos;
    private DefaultTableModel modeloItens;

    public TelaPedido(PedidoController controller) {

        this.controller = controller;

        configurarJanela();
        inicializarComponentes();
        montarLayout();
        configurarEventos();

        executarAcao(this::carregarSabores);
        executarAcao(this::carregarTodosClientes);

        atualizarTextoAjuda();
    }

    // ================= JANELA =================

    private void configurarJanela() {

        setTitle("Cadastro de Pedido");
        setSize(1250, 760);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // ================= COMPONENTES =================

    private void inicializarComponentes() {

        campoTelefoneCliente = new JTextField();
        campoValorMedida = new JTextField();
        campoQuantidade = new JTextField("1");

        labelClienteNome =
                new JLabel("Cliente selecionado: nenhum");

        labelClienteTelefone =
                new JLabel("Telefone: -");

        labelPedidoAtual =
                new JLabel("Pedido atual: nenhum");

        labelAjudaMedida = new JLabel();

        labelResultadoCalculo =
                new JLabel("Resultado: -");

        labelTotalPedido =
                new JLabel("Total do pedido: R$ 0,00");

        labelTotalPedido.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        comboForma = new JComboBox<>(new String[]{
                "QUADRADO", "CIRCULO", "TRIANGULO"
        });

        comboModoEntrada = new JComboBox<>(new String[]{
                "Por dimensão", "Por área"
        });

        comboSabor1 = new JComboBox<>();
        comboSabor2 = new JComboBox<>();

        comboSabor1.addItem("Selecione o primeiro sabor");
        comboSabor2.addItem("Sem segundo sabor");

        configurarRenderizador(comboSabor1);
        configurarRenderizador(comboSabor2);

        botaoBuscarCliente = new JButton("Buscar cliente");
        botaoNovoPedido = new JButton("Novo pedido");
        botaoCalcularMedida = new JButton("Calcular");
        botaoAdicionarPizza = new JButton("Adicionar pizza");
        botaoAtualizarItem = new JButton("Atualizar item");
        botaoRemoverItem = new JButton("Remover item");
        botaoCancelarPedido = new JButton("Cancelar pedido");
        botaoLimparCampos = new JButton("Limpar campos");
        botaoFinalizarPedido = new JButton("Finalizar pedido");
        botaoVoltar = new JButton("Voltar");

        modeloClientes = criarModelo(
                "ID", "Nome", "Sobrenome", "Telefone"
        );

        modeloPedidos = criarModelo(
                "ID", "Estado", "Itens", "Total"
        );

        modeloItens = criarModelo(
                "Item", "Forma", "Sabores",
                "Área cm²", "Qtd.",
                "Preço unitário", "Subtotal"
        );

        tabelaClientes = new JTable(modeloClientes);
        tabelaPedidos = new JTable(modeloPedidos);
        tabelaItens = new JTable(modeloItens);

        tabelaClientes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabelaPedidos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabelaItens.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
    }

    private DefaultTableModel criarModelo(String... colunas) {

        return new DefaultTableModel(colunas, 0) {

            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    // ================= LAYOUT =================

    private void montarLayout() {

        JPanel painelPrincipal =
                new JPanel(new BorderLayout(12, 12));

        painelPrincipal.setBorder(
                new EmptyBorder(12, 12, 12, 12)
        );

        JLabel titulo = new JLabel("Cadastro de Pedido");

        titulo.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        JPanel painelCentral =
                new JPanel(new BorderLayout(12, 12));

        painelCentral.add(
                criarPainelCliente(), BorderLayout.NORTH
        );

        JPanel painelMeio =
                new JPanel(new BorderLayout(12, 12));

        painelMeio.add(
                criarPainelPizza(), BorderLayout.WEST
        );

        painelMeio.add(
                criarPainelPedidos(), BorderLayout.CENTER
        );

        painelCentral.add(
                painelMeio, BorderLayout.CENTER
        );

        painelPrincipal.add(
                titulo, BorderLayout.NORTH
        );

        painelPrincipal.add(
                painelCentral, BorderLayout.CENTER
        );

        painelPrincipal.add(
                criarPainelRodape(), BorderLayout.SOUTH
        );

        setContentPane(painelPrincipal);
    }

    private JPanel criarPainelCliente() {

        JPanel painel =
                new JPanel(new BorderLayout(8, 8));

        painel.setBorder(
                BorderFactory.createTitledBorder("Cliente")
        );

        painel.setPreferredSize(
                new Dimension(0, 220)
        );

        JPanel painelBusca =
                new JPanel(new BorderLayout(8, 8));

        painelBusca.add(
                new JLabel("Telefone:"), BorderLayout.WEST
        );

        painelBusca.add(
                campoTelefoneCliente, BorderLayout.CENTER
        );

        painelBusca.add(
                botaoBuscarCliente, BorderLayout.EAST
        );

        JPanel painelDados =
                new JPanel(new GridLayout(3, 1, 4, 4));

        painelDados.add(labelClienteNome);
        painelDados.add(labelClienteTelefone);
        painelDados.add(labelPedidoAtual);

        painel.add(
                painelBusca, BorderLayout.NORTH
        );

        painel.add(
                new JScrollPane(tabelaClientes),
                BorderLayout.CENTER
        );

        painel.add(
                painelDados, BorderLayout.SOUTH
        );

        return painel;
    }

    private JPanel criarPainelPizza() {

        JPanel painel =
                new JPanel(new BorderLayout(10, 10));

        painel.setBorder(
                BorderFactory.createTitledBorder("Dados da pizza")
        );

        painel.setPreferredSize(
                new Dimension(370, 0)
        );

        JPanel painelCampos =
                new JPanel(new GridLayout(0, 2, 6, 6));

        painelCampos.add(new JLabel("Forma:"));
        painelCampos.add(comboForma);

        painelCampos.add(new JLabel("Modo de entrada:"));
        painelCampos.add(comboModoEntrada);

        JPanel painelValor =
                new JPanel(new BorderLayout(5, 0));

        painelValor.add(
                campoValorMedida, BorderLayout.CENTER
        );

        painelValor.add(
                botaoCalcularMedida, BorderLayout.EAST
        );

        painelCampos.add(new JLabel("Valor:"));
        painelCampos.add(painelValor);

        painelCampos.add(new JLabel("Sabor 1:"));
        painelCampos.add(comboSabor1);

        painelCampos.add(new JLabel("Sabor 2:"));
        painelCampos.add(comboSabor2);

        painelCampos.add(new JLabel("Quantidade:"));
        painelCampos.add(campoQuantidade);

        JPanel painelFormulario = new JPanel();

        painelFormulario.setLayout(
                new BoxLayout(painelFormulario, BoxLayout.Y_AXIS)
        );

        painelFormulario.add(painelCampos);
        painelFormulario.add(Box.createVerticalStrut(8));
        painelFormulario.add(labelAjudaMedida);
        painelFormulario.add(Box.createVerticalStrut(8));
        painelFormulario.add(labelResultadoCalculo);

        JPanel painelBotoes =
                new JPanel(new GridLayout(5, 1, 0, 6));

        painelBotoes.add(botaoAdicionarPizza);
        painelBotoes.add(botaoAtualizarItem);
        painelBotoes.add(botaoRemoverItem);
        painelBotoes.add(botaoCancelarPedido);
        painelBotoes.add(botaoLimparCampos);

        JPanel painelSuperior =
                new JPanel(new BorderLayout(10, 10));

        painelSuperior.add(
                painelFormulario, BorderLayout.NORTH
        );

        painelSuperior.add(
                painelBotoes, BorderLayout.CENTER
        );

        painel.add(
                painelSuperior, BorderLayout.NORTH
        );

        painel.add(
                criarPainelRegras(), BorderLayout.CENTER
        );

        return painel;
    }

    private JPanel criarPainelRegras() {

        JPanel painel = new JPanel(new BorderLayout());

        painel.setBorder(
                BorderFactory.createTitledBorder("Regras de medida")
        );

        JTextArea texto = new JTextArea(
                "Quadrado: lado entre 10 e 40 cm\n" +
                        "Círculo: raio entre 7 e 23 cm\n" +
                        "Triângulo: lado entre 20 e 60 cm\n" +
                        "Área: entre 100 e 1600 cm²\n" +
                        "Cada pizza pode ter até 2 sabores"
        );

        texto.setEditable(false);
        texto.setFocusable(false);
        texto.setBackground(painel.getBackground());
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);

        painel.add(texto, BorderLayout.NORTH);

        return painel;
    }

    private JPanel criarPainelPedidos() {

        JPanel painel =
                new JPanel(new BorderLayout(10, 10));

        JPanel painelPedidos =
                new JPanel(new BorderLayout(8, 8));

        painelPedidos.setBorder(
                BorderFactory.createTitledBorder(
                        "Pedidos do cliente"
                )
        );

        painelPedidos.setPreferredSize(
                new Dimension(0, 190)
        );

        painelPedidos.add(
                new JScrollPane(tabelaPedidos),
                BorderLayout.CENTER
        );

        JPanel painelNovoPedido =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        painelNovoPedido.add(botaoNovoPedido);

        painelPedidos.add(
                painelNovoPedido, BorderLayout.SOUTH
        );

        JPanel painelItens =
                new JPanel(new BorderLayout(8, 8));

        painelItens.setBorder(
                BorderFactory.createTitledBorder(
                        "Itens do pedido selecionado"
                )
        );

        painelItens.add(
                new JScrollPane(tabelaItens),
                BorderLayout.CENTER
        );

        JPanel painelTotal =
                new JPanel(new FlowLayout(FlowLayout.RIGHT));

        painelTotal.add(labelTotalPedido);

        painelItens.add(
                painelTotal, BorderLayout.SOUTH
        );

        painel.add(
                painelPedidos, BorderLayout.NORTH
        );

        painel.add(
                painelItens, BorderLayout.CENTER
        );

        return painel;
    }

    private JPanel criarPainelRodape() {

        JPanel painel = new JPanel(new BorderLayout());

        JLabel mensagem = new JLabel(
                "Selecione um cliente, um pedido e adicione as pizzas."
        );

        JPanel painelBotoes =
                new JPanel(new FlowLayout(FlowLayout.RIGHT));

        painelBotoes.add(botaoFinalizarPedido);
        painelBotoes.add(botaoVoltar);

        painel.add(mensagem, BorderLayout.WEST);
        painel.add(painelBotoes, BorderLayout.EAST);

        return painel;
    }

    // ================= EVENTOS =================

    private void configurarEventos() {

        botaoBuscarCliente.addActionListener(
                e -> executarAcao(this::buscarClientes)
        );

        campoTelefoneCliente.addActionListener(
                e -> executarAcao(this::buscarClientes)
        );

        botaoNovoPedido.addActionListener(
                e -> executarAcao(this::criarNovoPedido)
        );

        botaoCalcularMedida.addActionListener(
                e -> executarAcao(this::calcularMedida)
        );

        botaoAdicionarPizza.addActionListener(
                e -> executarAcao(this::adicionarPizza)
        );

        botaoAtualizarItem.addActionListener(
                e -> executarAcao(this::atualizarItem)
        );

        botaoRemoverItem.addActionListener(
                e -> executarAcao(this::removerItem)
        );

        botaoCancelarPedido.addActionListener(
                e -> executarAcao(this::cancelarPedido)
        );

        botaoFinalizarPedido.addActionListener(
                e -> executarAcao(this::finalizarPedido)
        );

        botaoLimparCampos.addActionListener(
                e -> limparCamposPizza()
        );

        botaoVoltar.addActionListener(
                e -> dispose()
        );

        comboForma.addActionListener(
                e -> atualizarTextoAjuda()
        );

        comboModoEntrada.addActionListener(
                e -> atualizarTextoAjuda()
        );

        tabelaClientes.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                executarAcao(TelaPedido.this::selecionarCliente);
            }
        });

        tabelaPedidos.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                executarAcao(TelaPedido.this::selecionarPedido);
            }
        });

        tabelaItens.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {

                    executarAcao(
                            TelaPedido.this::carregarItemNosCampos
                    );
                }
            }
        });
    }

    // ================= SABORES =================

    private void carregarSabores() {

        List<Sabor> sabores = controller.listarSabores();

        comboSabor1.removeAllItems();
        comboSabor2.removeAllItems();

        comboSabor1.addItem("Selecione o primeiro sabor");
        comboSabor2.addItem("Sem segundo sabor");

        for (Sabor sabor : sabores) {

            comboSabor1.addItem(sabor);
            comboSabor2.addItem(sabor);
        }
    }

    private void configurarRenderizador(JComboBox<Object> combo) {

        combo.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista,
                    Object valor,
                    int indice,
                    boolean selecionado,
                    boolean foco) {

                super.getListCellRendererComponent(
                        lista, valor, indice, selecionado, foco
                );

                if (valor instanceof Sabor sabor) {

                    setText(
                            sabor.getNome()
                                    + " (" + sabor.getTipoPizza() + ")"
                    );
                }

                return this;
            }
        });
    }

    // ================= CLIENTES =================

    private void carregarTodosClientes() {

        clientesEncontrados = controller.mostrarClientes();

        carregarTabelaClientes();
    }

    private void buscarClientes() {

        limparClienteEPedidoSelecionado();
        limparCamposPizza();

        clientesEncontrados = new ArrayList<>();

        modeloClientes.setRowCount(0);

        String telefone = campoTelefoneCliente.getText().trim();

        if (telefone.isBlank()) {

            carregarTodosClientes();

        } else {

            clientesEncontrados =
                    controller.buscarPorTelefone(telefone);

            carregarTabelaClientes();
        }
    }

    private void carregarTabelaClientes() {

        modeloClientes.setRowCount(0);

        for (Cliente cliente : clientesEncontrados) {

            modeloClientes.addRow(new Object[]{
                    cliente.getId(),
                    cliente.getNome(),
                    cliente.getSobrenome(),
                    cliente.getTelefone()
            });
        }
    }

    private void selecionarCliente() {

        int linha = tabelaClientes.getSelectedRow();

        if (linha == -1) {
            return;
        }

        limparClienteEPedidoSelecionado();
        limparCamposPizza();

        clienteSelecionado =
                clientesEncontrados.get(
                        tabelaClientes.convertRowIndexToModel(linha)
                );

        labelClienteNome.setText(
                "Cliente selecionado: "
                        + clienteSelecionado.getNome()
                        + " "
                        + clienteSelecionado.getSobrenome()
        );

        labelClienteTelefone.setText(
                "Telefone: " + clienteSelecionado.getTelefone()
        );

        pedidoSelecionado =
                controller.buscarOuCriarPedidoAberto(
                        clienteSelecionado
                );

        atualizarTabelasDepoisDeAlterar();
    }

    // ================= PEDIDOS =================

    private void criarNovoPedido() {

        validarClienteSelecionado();

        pedidoSelecionado =
                controller.criarNovoPedido(clienteSelecionado);

        atualizarTabelasDepoisDeAlterar();
        limparCamposPizza();

        JOptionPane.showMessageDialog(
                this, "Novo pedido criado com sucesso."
        );
    }

    private void selecionarPedido() {

        int linha = tabelaPedidos.getSelectedRow();

        if (linha == -1) {
            return;
        }

        linha = tabelaPedidos.convertRowIndexToModel(linha);

        int idPedido = Integer.parseInt(
                modeloPedidos.getValueAt(linha, 0).toString()
        );

        pedidoSelecionado = null;

        carregarTabelaItens();
        atualizarPedidoAtual();
        limparCamposPizza();

        pedidoSelecionado =
                controller.buscaPedidoPorId(idPedido);

        carregarTabelaItens();
        atualizarPedidoAtual();
    }

    private void carregarTabelaPedidos() {

        modeloPedidos.setRowCount(0);

        if (clienteSelecionado == null) {
            return;
        }

        List<Pedido> pedidos =
                controller.listarPedidosPorCliente(
                        clienteSelecionado
                );

        for (Pedido pedido : pedidos) {

            modeloPedidos.addRow(new Object[]{
                    pedido.getIdPedido(),
                    pedido.getEstadoPedido(),
                    pedido.getItens().size(),
                    formatarPreco(
                            controller.calcularTotalPedido(pedido)
                    )
            });
        }
    }

    private void selecionarPedidoNaTabela(int idPedido) {

        for (int i = 0; i < modeloPedidos.getRowCount(); i++) {

            int idAtual = Integer.parseInt(
                    modeloPedidos.getValueAt(i, 0).toString()
            );

            if (idAtual == idPedido) {

                int linha = tabelaPedidos.convertRowIndexToView(i);

                tabelaPedidos.setRowSelectionInterval(linha, linha);

                return;
            }
        }
    }

    // ================= ITENS =================

    private void carregarTabelaItens() {

        modeloItens.setRowCount(0);

        if (pedidoSelecionado == null) {

            labelTotalPedido.setText(
                    "Total do pedido: R$ 0,00"
            );

            return;
        }

        int numero = 1;

        for (ItemPedido item : pedidoSelecionado.getItens()) {

            modeloItens.addRow(new Object[]{
                    numero,
                    item.getPizza().getForma().descricao(),
                    montarSabores(item),
                    String.format(
                            "%.2f", item.getPizza().calculaArea()
                    ),
                    item.getQuantidade(),
                    formatarPreco(item.getPrecoUnitario()),
                    formatarPreco(item.calculaSubTotal())
            });

            numero++;
        }

        labelTotalPedido.setText(
                "Total do pedido: "
                        + formatarPreco(
                        controller.calcularTotalPedido(
                                pedidoSelecionado
                        )
                )
        );
    }

    private void calcularMedida() {

        String resultado = controller.calcularResultadoMedida(
                obterForma(),
                converterDouble(campoValorMedida.getText()),
                entradaPorArea()
        );

        labelResultadoCalculo.setText(
                "<html>" + resultado + "</html>"
        );
    }

    // ================= ADICIONAR PIZZA =================

    private void adicionarPizza() {

        validarPedidoSelecionado();

        String forma = obterForma();

        double valor = converterDouble(
                campoValorMedida.getText()
        );

        Sabor sabor1 = obterSabor1();
        Sabor sabor2 = obterSabor2();

        int quantidade = converterInteiro(
                campoQuantidade.getText()
        );

        if (entradaPorArea()) {

            controller.adicionarPizzaPorArea(
                    pedidoSelecionado,
                    forma,
                    valor,
                    sabor1,
                    sabor2,
                    quantidade
            );

        } else {

            controller.adicionarPizzaPorDimensao(
                    pedidoSelecionado,
                    forma,
                    valor,
                    sabor1,
                    sabor2,
                    quantidade
            );
        }

        atualizarTabelasDepoisDeAlterar();
        limparCamposPizza();

        JOptionPane.showMessageDialog(
                this, "Pizza adicionada ao pedido."
        );
    }

    // ================= ATUALIZAR ITEM =================

    private void atualizarItem() {

        validarPedidoSelecionado();

        int linha = obterLinhaItemSelecionado();

        controller.atualizarItem(
                pedidoSelecionado,
                linha,
                obterForma(),
                converterDouble(campoValorMedida.getText()),
                entradaPorArea(),
                obterSabor1(),
                obterSabor2(),
                converterInteiro(campoQuantidade.getText())
        );

        atualizarTabelasDepoisDeAlterar();
        limparCamposPizza();

        JOptionPane.showMessageDialog(
                this, "Item atualizado com sucesso."
        );
    }

    // ================= REMOVER ITEM =================

    private void removerItem() {

        validarPedidoSelecionado();

        int linha = obterLinhaItemSelecionado();

        controller.removerItem(
                pedidoSelecionado, linha
        );

        atualizarTabelasDepoisDeAlterar();
        limparCamposPizza();

        JOptionPane.showMessageDialog(
                this, "Item removido com sucesso."
        );
    }

    private int obterLinhaItemSelecionado() {

        int linha = tabelaItens.getSelectedRow();

        if (linha == -1) {

            throw new CampoInvalidoException(
                    "Selecione um item na tabela."
            );
        }

        return tabelaItens.convertRowIndexToModel(linha);
    }

    // ================= CARREGAR ITEM =================

    private void carregarItemNosCampos() {

        if (pedidoSelecionado == null) {
            return;
        }

        int linha = obterLinhaItemSelecionado();

        ItemPedido item =
                pedidoSelecionado.getItens().get(linha);

        carregarSabores();

        campoQuantidade.setText(
                String.valueOf(item.getQuantidade())
        );

        Forma forma = item.getPizza().getForma();

        double medida;
        double minimo;
        double maximo;

        if (forma instanceof Quadrado quadrado) {

            comboForma.setSelectedItem("QUADRADO");

            medida = quadrado.getLado();

            minimo = 10;
            maximo = 40;

        } else if (forma instanceof Circulo circulo) {

            comboForma.setSelectedItem("CIRCULO");

            medida = circulo.getRaio();

            minimo = 7;
            maximo = 23;

        } else if (forma instanceof Triangulo triangulo) {

            comboForma.setSelectedItem("TRIANGULO");

            medida = triangulo.getLado();

            minimo = 20;
            maximo = 60;

        } else {

            throw new CampoInvalidoException(
                    "Forma inválida."
            );
        }

        if (medida >= minimo && medida <= maximo) {

            comboModoEntrada.setSelectedItem("Por dimensão");

            campoValorMedida.setText(
                    formatarMedida(medida)
            );

        } else {

            comboModoEntrada.setSelectedItem("Por área");

            double area = forma.calculaArea();

            // Corrige diferenças de precisão próximas aos limites.
            if (Math.abs(area - 100) < 0.000001) {
                area = 100;
            }

            if (Math.abs(area - 1600) < 0.000001) {
                area = 1600;
            }

            campoValorMedida.setText(
                    formatarMedida(area)
            );
        }

        List<Sabor> sabores = item.getPizza().getSabor();

        comboSabor1.setSelectedIndex(0);
        comboSabor2.setSelectedIndex(0);

        if (!sabores.isEmpty()) {

            selecionarSaborPorId(
                    comboSabor1,
                    sabores.get(0).getIdSabor()
            );
        }

        if (sabores.size() > 1) {

            selecionarSaborPorId(
                    comboSabor2,
                    sabores.get(1).getIdSabor()
            );
        }

        labelResultadoCalculo.setText(
                String.format(
                        "Área calculada: %.2f cm²",
                        forma.calculaArea()
                )
        );

        atualizarTextoAjuda();
    }

    private void selecionarSaborPorId(
            JComboBox<Object> combo, int idSabor) {

        for (int i = 0; i < combo.getItemCount(); i++) {

            Object objeto = combo.getItemAt(i);

            if (objeto instanceof Sabor sabor
                    && sabor.getIdSabor() == idSabor) {

                combo.setSelectedIndex(i);

                return;
            }
        }

        throw new SaborInvalidoException(
                "Sabor do item não encontrado."
        );
    }

    // ================= CANCELAR PEDIDO =================

    private void cancelarPedido() {

        validarPedidoSelecionado();

        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Deseja cancelar o pedido selecionado?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (opcao == JOptionPane.YES_OPTION) {

            controller.excluirPedido(
                    pedidoSelecionado.getIdPedido()
            );

            pedidoSelecionado = null;

            carregarTabelaPedidos();
            carregarTabelaItens();
            atualizarPedidoAtual();
            limparCamposPizza();

            JOptionPane.showMessageDialog(
                    this, "Pedido cancelado."
            );
        }
    }

    // ================= FINALIZAR PEDIDO =================

    private void finalizarPedido() {

        validarPedidoSelecionado();

        controller.finalizarPedido(pedidoSelecionado);

        JOptionPane.showMessageDialog(
                this,
                "Pedido finalizado. O estado poderá ser "
                        + "alterado na tela de status."
        );

        limparClienteEPedidoSelecionado();
        limparCamposPizza();

        campoTelefoneCliente.setText("");

        tabelaClientes.clearSelection();

        carregarTodosClientes();
    }

    // ================= ATUALIZAÇÕES =================

    private void atualizarTabelasDepoisDeAlterar() {

        carregarTabelaPedidos();

        if (pedidoSelecionado != null) {

            selecionarPedidoNaTabela(
                    pedidoSelecionado.getIdPedido()
            );
        }

        carregarTabelaItens();
        atualizarPedidoAtual();
    }

    private void atualizarPedidoAtual() {

        if (pedidoSelecionado == null) {

            labelPedidoAtual.setText(
                    "Pedido atual: nenhum"
            );

        } else {

            labelPedidoAtual.setText(
                    "Pedido atual: #"
                            + pedidoSelecionado.getIdPedido()
                            + " - "
                            + pedidoSelecionado.getEstadoPedido()
            );
        }
    }

    private void atualizarTextoAjuda() {

        if (entradaPorArea()) {

            labelAjudaMedida.setText(
                    "Informe a área em cm²."
            );

        } else if (obterForma().equals("CIRCULO")) {

            labelAjudaMedida.setText(
                    "Informe o raio em cm."
            );

        } else {

            labelAjudaMedida.setText(
                    "Informe o lado em cm."
            );
        }
    }

    // ================= LIMPEZA =================

    private void limparCamposPizza() {

        comboForma.setSelectedIndex(0);
        comboModoEntrada.setSelectedIndex(0);

        campoValorMedida.setText("");
        campoQuantidade.setText("1");

        comboSabor1.setSelectedIndex(0);
        comboSabor2.setSelectedIndex(0);

        labelResultadoCalculo.setText("Resultado: -");

        tabelaItens.clearSelection();

        atualizarTextoAjuda();
    }

    private void limparClienteEPedidoSelecionado() {

        clienteSelecionado = null;
        pedidoSelecionado = null;

        labelClienteNome.setText(
                "Cliente selecionado: nenhum"
        );

        labelClienteTelefone.setText("Telefone: -");

        modeloPedidos.setRowCount(0);
        modeloItens.setRowCount(0);

        labelTotalPedido.setText(
                "Total do pedido: R$ 0,00"
        );

        atualizarPedidoAtual();
    }

    // ================= MÉTODOS AUXILIARES =================

    private String montarSabores(ItemPedido item) {

        List<Sabor> sabores = item.getPizza().getSabor();

        String texto = "";

        for (int i = 0; i < sabores.size(); i++) {

            texto += sabores.get(i).getNome();

            if (i < sabores.size() - 1) {
                texto += " / ";
            }
        }

        return texto;
    }

    private String formatarPreco(java.math.BigDecimal valor) {

        return String.format(
                java.util.Locale.forLanguageTag("pt-BR"),
                "R$ %.2f",
                valor
        );
    }

    private String obterForma() {

        return comboForma.getSelectedItem().toString();
    }

    private boolean entradaPorArea() {

        return "Por área".equals(
                comboModoEntrada.getSelectedItem()
        );
    }

    private Sabor obterSabor1() {

        Object objeto = comboSabor1.getSelectedItem();

        if (!(objeto instanceof Sabor)) {

            throw new CampoInvalidoException(
                    "Selecione o primeiro sabor."
            );
        }

        return (Sabor) objeto;
    }

    private Sabor obterSabor2() {

        Object objeto = comboSabor2.getSelectedItem();

        if (objeto instanceof Sabor sabor) {
            return sabor;
        }

        return null;
    }

    private double converterDouble(String texto) {

        try {

            return Double.parseDouble(
                    texto.trim().replace(",", ".")
            );

        } catch (NumberFormatException e) {

            throw new CampoInvalidoException(
                    "Digite uma medida válida."
            );
        }
    }

    private int converterInteiro(String texto) {

        try {

            return Integer.parseInt(texto.trim());

        } catch (NumberFormatException e) {

            throw new CampoInvalidoException(
                    "Digite uma quantidade válida."
            );
        }
    }

    // ================= VALIDAÇÕES DA TELA =================

    private void validarClienteSelecionado() {

        if (clienteSelecionado == null) {

            throw new CampoInvalidoException(
                    "Selecione um cliente primeiro."
            );
        }
    }

    private void validarPedidoSelecionado() {

        if (pedidoSelecionado == null) {

            throw new CampoInvalidoException(
                    "Selecione ou crie um pedido primeiro."
            );
        }
    }

    // ================= TRATAMENTO DE ERROS =================

    private void executarAcao(Runnable acao) {

        try {

            acao.run();

        } catch (CampoInvalidoException |
                 ClienteNaoEncontradoException |
                 SaborInvalidoException e) {

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

    private String formatarMedida(double valor) {

        return BigDecimal.valueOf(valor)
                .setScale(8, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

}