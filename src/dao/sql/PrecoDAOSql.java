package dao.sql;

import connection.ConnectionFactory;
import dao.PrecoDAO;
import exception.PersistenciaException;
import model.TabelaPreco;
import model.TipoPizza;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class PrecoDAOSql implements PrecoDAO {

    @Override
    public TabelaPreco consultarPrecos() {

        String sql = """
                SELECT tipo, preco_cm2
                FROM tipo_pizza
                """;

        TabelaPreco tabelaPreco = new TabelaPreco();

        Set<TipoPizza> tiposEncontrados =
                EnumSet.noneOf(TipoPizza.class);

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {

                TipoPizza tipo = TipoPizza.valueOf(
                        resultado.getString("tipo")
                );

                double preco = resultado.getDouble("preco_cm2");

                tabelaPreco.alterarPreco(tipo, preco);
                tiposEncontrados.add(tipo);
            }

            if (tiposEncontrados.size() != TipoPizza.values().length) {

                throw new PersistenciaException(
                        "A tabela de preços está incompleta."
                );
            }

        } catch (SQLException e) {

            throw new PersistenciaException(
                    "Erro ao consultar preços.", e
            );
        }

        return tabelaPreco;
    }

    @Override
    public void atualizarPrecos(
            Map<TipoPizza, BigDecimal> precos) {

        String sql = """
                UPDATE tipo_pizza
                SET preco_cm2 = ?
                WHERE tipo = ?
                """;

        try (Connection conexao = ConnectionFactory.getConnection()) {

            conexao.setAutoCommit(false);

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sql)) {

                for (Map.Entry<TipoPizza, BigDecimal> entrada
                        : precos.entrySet()) {

                    stmt.setBigDecimal(1, entrada.getValue());
                    stmt.setString(2, entrada.getKey().name());

                    int linhasAlteradas = stmt.executeUpdate();

                    if (linhasAlteradas != 1) {

                        throw new PersistenciaException(
                                "Tipo de pizza não encontrado: "
                                        + entrada.getKey()
                        );
                    }
                }

                conexao.commit();

            } catch (SQLException | RuntimeException e) {

                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    e.addSuppressed(erroRollback);
                }

                throw e;
            }

        } catch (SQLException e) {

            throw new PersistenciaException(
                    "Erro ao atualizar preços.", e
            );
        }
    }
}