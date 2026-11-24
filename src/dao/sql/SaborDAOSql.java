package dao.sql;

import connection.ConnectionFactory;
import dao.SaborDAO;
import exception.PersistenciaException;
import exception.SaborInvalidoException;
import model.Sabor;
import model.TipoPizza;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SaborDAOSql implements SaborDAO {

    @Override
    public void salvar(Sabor sabor) {

        String sql = """
                INSERT INTO sabor (nome, tipo)
                VALUES (?, ?)
                """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, sabor.getNome());
            stmt.setString(2, sabor.getTipoPizza().name());

            stmt.executeUpdate();

            try (ResultSet resultado = stmt.getGeneratedKeys()) {

                if (resultado.next()) {
                    sabor.setIdSabor(resultado.getInt(1));
                }
            }

        } catch (SQLException e) {

            throw new PersistenciaException(
                    "Erro ao cadastrar sabor.", e
            );
        }
    }

    @Override
    public void atualizar(Sabor sabor) {

        String sql = """
                UPDATE sabor
                SET nome = ?, tipo = ?
                WHERE id = ?
                """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, sabor.getNome());
            stmt.setString(2, sabor.getTipoPizza().name());
            stmt.setInt(3, sabor.getIdSabor());

            int linhasAlteradas = stmt.executeUpdate();

            if (linhasAlteradas == 0) {

                throw new SaborInvalidoException(
                        "Sabor não encontrado."
                );
            }

        } catch (SQLException e) {

            throw new PersistenciaException(
                    "Erro ao atualizar sabor.", e
            );
        }
    }

    @Override
    public void excluir(int id) {

        String sql = """
                DELETE FROM sabor
                WHERE id = ?
                """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int linhasAlteradas = stmt.executeUpdate();

            if (linhasAlteradas == 0) {

                throw new SaborInvalidoException(
                        "Sabor não encontrado."
                );
            }

        } catch (SQLException e) {

            // Violação de chave estrangeira no MySQL.
            if (e.getErrorCode() == 1451) {

                throw new PersistenciaException(
                        "Não é possível excluir um sabor utilizado em pedidos.",
                        e
                );
            }

            throw new PersistenciaException(
                    "Erro ao excluir sabor.", e
            );
        }
    }

    @Override
    public Sabor buscarPorId(int id) {

        String sql = """
                SELECT id, nome, tipo
                FROM sabor
                WHERE id = ?
                """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet resultado = stmt.executeQuery()) {

                if (resultado.next()) {
                    return mapearSabor(resultado);
                }
            }

        } catch (SQLException e) {

            throw new PersistenciaException(
                    "Erro ao buscar sabor.", e
            );
        }

        throw new SaborInvalidoException(
                "Sabor não encontrado."
        );
    }

    @Override
    public List<Sabor> listarTodos() {

        return filtrarPorNome("");
    }

    @Override
    public List<Sabor> filtrarPorNome(String nome) {

        String sql = """
                SELECT id, nome, tipo
                FROM sabor
                WHERE LOCATE(LOWER(?), LOWER(nome)) > 0
                ORDER BY nome
                """;

        List<Sabor> sabores = new ArrayList<>();

        nome = nome == null ? "" : nome.trim();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, nome);

            try (ResultSet resultado = stmt.executeQuery()) {

                while (resultado.next()) {

                    sabores.add(mapearSabor(resultado));
                }
            }

        } catch (SQLException e) {

            throw new PersistenciaException(
                    "Erro ao consultar sabores.", e
            );
        }

        return sabores;
    }

    private Sabor mapearSabor(ResultSet resultado)
            throws SQLException {

        TipoPizza tipo = TipoPizza.valueOf(
                resultado.getString("tipo")
        );

        Sabor sabor = new Sabor(
                resultado.getString("nome"),
                tipo
        );

        sabor.setIdSabor(resultado.getInt("id"));

        return sabor;
    }
}