package dao.sql;

import connection.ConnectionFactory;
import dao.PedidoDAO;
import exception.CampoInvalidoException;
import exception.PersistenciaException;
import model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOSql implements PedidoDAO {

    private static final String SELECT_PEDIDOS = """
            SELECT p.id AS pedido_id, p.estado, p.preco_total,
                   c.id AS cliente_id, c.nome AS cliente_nome,
                   c.sobrenome, c.telefone,
                   i.id AS item_id, i.forma, i.medida,
                   i.quantidade, i.preco_unitario,
                   s1.id AS sabor1_id,
                   s1.nome AS sabor1_nome,
                   s1.tipo AS sabor1_tipo,
                   s2.id AS sabor2_id,
                   s2.nome AS sabor2_nome,
                   s2.tipo AS sabor2_tipo
            FROM pedido p
            JOIN cliente c ON c.id = p.cliente_id
            LEFT JOIN item_pedido i ON i.pedido_id = p.id
            LEFT JOIN sabor s1 ON s1.id = i.sabor1_id
            LEFT JOIN sabor s2 ON s2.id = i.sabor2_id
            """;

    // ================= SALVAR PEDIDO =================

    @Override
    public void salvar(Pedido pedido) {

        if (!pedido.getItens().isEmpty()
                || pedido.getEstadoPedido() != EstadoPedido.ABERTO) {

            throw new CampoInvalidoException(
                    "Um novo pedido deve estar aberto e vazio."
            );
        }

        String sql = """
                INSERT INTO pedido
                (cliente_id, estado, preco_total)
                VALUES (?, 'ABERTO', 0)
                """;

        int id = emTransacao(conexao -> {

            int idCliente = pedido.getCliente().getId();

            bloquearCliente(conexao, idCliente);
            verificarOutroAberto(conexao, idCliente, 0);

            try (PreparedStatement stmt = conexao.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setInt(1, idCliente);
                stmt.executeUpdate();

                return obterIdGerado(stmt);
            }
        });

        pedido.setIdPedido(id);
        pedido.setPrecoTotal(BigDecimal.ZERO);
    }

    // ================= CONSULTAS =================

    @Override
    public Pedido buscarPorId(int idPedido) {

        List<Pedido> pedidos = consultar(
                "WHERE p.id = ?", idPedido
        );

        if (pedidos.isEmpty()) {
            throw new CampoInvalidoException("Pedido não encontrado.");
        }

        return pedidos.get(0);
    }

    @Override
    public Pedido buscarAbertoPorCliente(int idCliente) {

        List<Pedido> pedidos = consultar(
                "WHERE p.cliente_id = ? AND p.estado = 'ABERTO'",
                idCliente
        );

        return pedidos.isEmpty() ? null : pedidos.get(0);
    }

    @Override
    public List<Pedido> listarPorCliente(int idCliente) {

        return consultar(
                "WHERE p.cliente_id = ?", idCliente
        );
    }

    @Override
    public List<Pedido> listarPorSobrenome(String sobrenome) {

        sobrenome = sobrenome == null ? "" : sobrenome.trim();

        return consultar(
                "WHERE LOCATE(LOWER(?), LOWER(c.sobrenome)) > 0",
                sobrenome
        );
    }

    private List<Pedido> consultar(String filtro, Object parametro) {

        String sql = SELECT_PEDIDOS + "\n"
                + filtro + " ORDER BY p.id, i.id";

        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setObject(1, parametro);

            try (ResultSet rs = stmt.executeQuery()) {

                Pedido pedidoAtual = null;

                while (rs.next()) {

                    int id = rs.getInt("pedido_id");

                    if (pedidoAtual == null
                            || pedidoAtual.getIdPedido() != id) {

                        pedidoAtual = mapearPedido(rs);
                        pedidos.add(pedidoAtual);
                    }

                    if (rs.getObject("item_id") != null) {

                        pedidoAtual.adicionaItem(
                                mapearItem(rs)
                        );
                    }
                }
            }

        } catch (SQLException e) {

            throw new PersistenciaException(
                    "Erro ao consultar pedidos.", e
            );
        }

        return pedidos;
    }

    // ================= MAPEAMENTO =================

    private Pedido mapearPedido(ResultSet rs)
            throws SQLException {

        Cliente cliente = new Cliente(
                rs.getString("cliente_nome"),
                rs.getString("sobrenome"),
                rs.getString("telefone")
        );

        cliente.setId(rs.getInt("cliente_id"));

        Pedido pedido = new Pedido(cliente);

        pedido.setIdPedido(rs.getInt("pedido_id"));

        pedido.setEstadoPedido(
                EstadoPedido.valueOf(rs.getString("estado"))
        );

        pedido.setPrecoTotal(
                rs.getBigDecimal("preco_total")
        );

        return pedido;
    }

    private ItemPedido mapearItem(ResultSet rs)
            throws SQLException {

        double medida = rs.getDouble("medida");

        Forma forma = switch (rs.getString("forma")) {

            case "QUADRADO" -> new Quadrado(medida);

            case "CIRCULO" -> new Circulo(medida);

            case "TRIANGULO" -> new Triangulo(medida);

            default -> throw new PersistenciaException(
                    "Forma inválida no banco."
            );
        };

        Pizza pizza = new Pizza(forma);

        pizza.adicionaSabor(
                mapearSabor(rs, "sabor1")
        );

        if (rs.getObject("sabor2_id") != null) {

            pizza.adicionaSabor(
                    mapearSabor(rs, "sabor2")
            );
        }

        ItemPedido item = new ItemPedido(
                pizza,
                rs.getInt("quantidade"),
                rs.getBigDecimal("preco_unitario")
        );

        item.setIdItemPedido(rs.getInt("item_id"));

        return item;
    }

    private Sabor mapearSabor(ResultSet rs, String prefixo)
            throws SQLException {

        Sabor sabor = new Sabor(
                rs.getString(prefixo + "_nome"),
                TipoPizza.valueOf(
                        rs.getString(prefixo + "_tipo")
                )
        );

        sabor.setIdSabor(
                rs.getInt(prefixo + "_id")
        );

        return sabor;
    }

    // ================= ADICIONAR ITEM =================

    @Override
    public void adicionarItem(int idPedido, ItemPedido item) {

        String sql = """
                INSERT INTO item_pedido
                (forma, medida, sabor1_id, sabor2_id,
                 quantidade, preco_unitario, pedido_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        int id = emTransacao(conexao -> {

            verificarPedidoAberto(conexao, idPedido);

            int idItem;

            try (PreparedStatement stmt = conexao.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

                preencherItem(stmt, item);

                stmt.setInt(7, idPedido);
                stmt.executeUpdate();

                idItem = obterIdGerado(stmt);
            }

            atualizarTotal(conexao, idPedido);

            return idItem;
        });

        item.setIdItemPedido(id);
    }

    // ================= ATUALIZAR ITEM =================

    @Override
    public void atualizarItem(
            int idPedido, int idItem, ItemPedido item) {

        String sql = """
                UPDATE item_pedido
                SET forma = ?, medida = ?,
                    sabor1_id = ?, sabor2_id = ?,
                    quantidade = ?, preco_unitario = ?
                WHERE id = ? AND pedido_id = ?
                """;

        emTransacao(conexao -> {

            verificarPedidoAberto(conexao, idPedido);
            verificarItem(conexao, idPedido, idItem);

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sql)) {

                preencherItem(stmt, item);

                stmt.setInt(7, idItem);
                stmt.setInt(8, idPedido);

                stmt.executeUpdate();
            }

            atualizarTotal(conexao, idPedido);

            return null;
        });

        item.setIdItemPedido(idItem);
    }

    // ================= REMOVER ITEM =================

    @Override
    public void removerItem(int idPedido, int idItem) {

        String sql = """
                DELETE FROM item_pedido
                WHERE id = ? AND pedido_id = ?
                """;

        emTransacao(conexao -> {

            verificarPedidoAberto(conexao, idPedido);

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sql)) {

                stmt.setInt(1, idItem);
                stmt.setInt(2, idPedido);

                if (stmt.executeUpdate() == 0) {
                    throw new CampoInvalidoException(
                            "Item não encontrado."
                    );
                }
            }

            atualizarTotal(conexao, idPedido);

            return null;
        });
    }

    // ================= DADOS DO ITEM =================

    private void preencherItem(
            PreparedStatement stmt, ItemPedido item)
            throws SQLException {

        Forma forma = item.getPizza().getForma();

        String tipo;
        double medida;

        if (forma instanceof Quadrado quadrado) {

            tipo = "QUADRADO";
            medida = quadrado.getLado();

        } else if (forma instanceof Circulo circulo) {

            tipo = "CIRCULO";
            medida = circulo.getRaio();

        } else if (forma instanceof Triangulo triangulo) {

            tipo = "TRIANGULO";
            medida = triangulo.getLado();

        } else {

            throw new CampoInvalidoException("Forma inválida.");
        }

        List<Sabor> sabores = item.getPizza().getSabor();

        if (sabores.isEmpty() || sabores.size() > 2) {

            throw new CampoInvalidoException(
                    "Informe um ou dois sabores."
            );
        }

        stmt.setString(1, tipo);

        stmt.setBigDecimal(
                2,
                BigDecimal.valueOf(medida)
                        .setScale(10, RoundingMode.HALF_UP)
        );

        stmt.setInt(3, sabores.get(0).getIdSabor());

        if (sabores.size() == 2) {

            stmt.setInt(4, sabores.get(1).getIdSabor());

        } else {

            stmt.setNull(4, Types.INTEGER);
        }

        stmt.setInt(5, item.getQuantidade());
        stmt.setBigDecimal(6, item.getPrecoUnitario());
    }

    // ================= ATUALIZAR TOTAL =================

    private void atualizarTotal(
            Connection conexao, int idPedido)
            throws SQLException {

        String sql = """
                UPDATE pedido
                SET preco_total = (
                    SELECT COALESCE(
                        SUM(preco_unitario * quantidade), 0
                    )
                    FROM item_pedido
                    WHERE pedido_id = ?
                )
                WHERE id = ?
                """;

        try (PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

            stmt.setInt(1, idPedido);
            stmt.setInt(2, idPedido);

            stmt.executeUpdate();
        }
    }

    // ================= ALTERAR ESTADO =================

    @Override
    public void alterarEstado(int idPedido, EstadoPedido estado) {

        if (estado == null) {

            throw new CampoInvalidoException("Estado obrigatório.");
        }

        emTransacao(conexao -> {

            int idCliente;

            String sqlCliente = """
                    SELECT cliente_id
                    FROM pedido WHERE id = ?
                    """;

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sqlCliente)) {

                stmt.setInt(1, idPedido);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (!rs.next()) {

                        throw new CampoInvalidoException(
                                "Pedido não encontrado."
                        );
                    }

                    idCliente = rs.getInt("cliente_id");
                }
            }

            bloquearCliente(conexao, idCliente);

            String sqlTotal = """
                    SELECT preco_total
                    FROM pedido WHERE id = ?
                    FOR UPDATE
                    """;

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sqlTotal)) {

                stmt.setInt(1, idPedido);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (!rs.next()) {

                        throw new CampoInvalidoException(
                                "Pedido não encontrado."
                        );
                    }

                    if (estado != EstadoPedido.ABERTO
                            && rs.getBigDecimal(1).signum() <= 0) {

                        throw new CampoInvalidoException(
                                "O pedido está vazio."
                        );
                    }
                }
            }

            if (estado == EstadoPedido.ABERTO) {

                verificarOutroAberto(
                        conexao, idCliente, idPedido
                );
            }

            String sql = """
                    UPDATE pedido
                    SET estado = ?
                    WHERE id = ?
                    """;

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sql)) {

                stmt.setString(1, estado.name());
                stmt.setInt(2, idPedido);

                stmt.executeUpdate();
            }

            return null;
        });
    }

    // ================= EXCLUIR PEDIDO =================

    @Override
    public void excluir(int idPedido) {

        String sql = "DELETE FROM pedido WHERE id = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idPedido);

            if (stmt.executeUpdate() == 0) {

                throw new CampoInvalidoException(
                        "Pedido não encontrado."
                );
            }

        } catch (SQLException e) {

            throw new PersistenciaException(
                    "Erro ao excluir pedido.", e
            );
        }
    }

    // ================= VERIFICAÇÕES =================

    private void bloquearCliente(
            Connection conexao, int idCliente)
            throws SQLException {

        String sql = """
                SELECT id FROM cliente
                WHERE id = ? FOR UPDATE
                """;

        try (PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {

                    throw new CampoInvalidoException(
                            "Cliente não encontrado."
                    );
                }
            }
        }
    }

    private void verificarOutroAberto(
            Connection conexao,
            int idCliente,
            int ignorarId) throws SQLException {

        String sql = """
                SELECT id FROM pedido
                WHERE cliente_id = ?
                  AND estado = 'ABERTO'
                  AND id <> ?
                FOR UPDATE
                """;

        try (PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            stmt.setInt(2, ignorarId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    throw new CampoInvalidoException(
                            "O cliente já possui um pedido aberto."
                    );
                }
            }
        }
    }

    private void verificarPedidoAberto(
            Connection conexao, int idPedido)
            throws SQLException {

        String sql = """
                SELECT estado FROM pedido
                WHERE id = ? FOR UPDATE
                """;

        try (PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

            stmt.setInt(1, idPedido);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {

                    throw new CampoInvalidoException(
                            "Pedido não encontrado."
                    );
                }

                if (!EstadoPedido.ABERTO.name().equals(
                        rs.getString("estado"))) {

                    throw new CampoInvalidoException(
                            "Somente pedidos abertos podem ser editados."
                    );
                }
            }
        }
    }

    private void verificarItem(
            Connection conexao, int idPedido, int idItem)
            throws SQLException {

        String sql = """
                SELECT id FROM item_pedido
                WHERE id = ? AND pedido_id = ?
                FOR UPDATE
                """;

        try (PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

            stmt.setInt(1, idItem);
            stmt.setInt(2, idPedido);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {

                    throw new CampoInvalidoException(
                            "Item não encontrado."
                    );
                }
            }
        }
    }

    private int obterIdGerado(PreparedStatement stmt)
            throws SQLException {

        try (ResultSet rs = stmt.getGeneratedKeys()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        throw new PersistenciaException(
                "ID não retornado pelo banco."
        );
    }

    // ================= TRANSAÇÕES =================

    @FunctionalInterface
    private interface Operacao<T> {

        T executar(Connection conexao) throws SQLException;
    }

    private <T> T emTransacao(Operacao<T> operacao) {

        try (Connection conexao = ConnectionFactory.getConnection()) {

            conexao.setAutoCommit(false);

            try {

                T resultado = operacao.executar(conexao);

                conexao.commit();

                return resultado;

            } catch (SQLException | RuntimeException e) {

                try {

                    conexao.rollback();

                } catch (SQLException erro) {

                    e.addSuppressed(erro);
                }

                throw e;
            }

        } catch (SQLException e) {

            throw new PersistenciaException(
                    "Erro ao gravar pedido.", e
            );
        }
    }
}