package dao.sql;

import connection.ConnectionFactory;
import dao.ClienteDAO;
import exception.ClienteNaoEncontradoException;
import exception.PersistenciaException;
import model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOSql implements ClienteDAO {

    @Override
    public void salvar(Cliente cliente) {

        String sql = """
                INSERT INTO cliente
                (nome, sobrenome, telefone)
                VALUES (?, ?, ?)
                """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getSobrenome());
            stmt.setString(3, cliente.getTelefone());

            stmt.executeUpdate();

            try (ResultSet resultado = stmt.getGeneratedKeys()) {

                if (resultado.next()) {
                    cliente.setId(resultado.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                    "Erro ao cadastrar cliente.", e
            );
        }
    }

    @Override
    public void atualizar(Cliente cliente) {

        String sql = """
                UPDATE cliente
                SET nome = ?, sobrenome = ?, telefone = ?
                WHERE id = ?
                """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getSobrenome());
            stmt.setString(3, cliente.getTelefone());
            stmt.setInt(4, cliente.getId());

            int linhasAlteradas = stmt.executeUpdate();

            if (linhasAlteradas == 0) {
                throw new ClienteNaoEncontradoException(
                        "Cliente não encontrado."
                );
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                    "Erro ao atualizar cliente.", e
            );
        }
    }

    @Override
    public void excluir(int id) {

        String sql = """
                DELETE FROM cliente
                WHERE id = ?
                """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int linhasAlteradas = stmt.executeUpdate();

            if (linhasAlteradas == 0) {
                throw new ClienteNaoEncontradoException(
                        "Cliente não encontrado."
                );
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                    "Erro ao excluir cliente.", e
            );
        }
    }

    @Override
    public Cliente buscarPorId(int id) {

        String sql = """
                SELECT id, nome, sobrenome, telefone
                FROM cliente
                WHERE id = ?
                """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet resultado = stmt.executeQuery()) {

                if (resultado.next()) {
                    return mapearCliente(resultado);
                }
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                    "Erro ao buscar cliente.", e
            );
        }

        throw new ClienteNaoEncontradoException(
                "Cliente não encontrado."
        );
    }

    @Override
    public List<Cliente> listarTodos() {

        String sql = """
                SELECT id, nome, sobrenome, telefone
                FROM cliente
                ORDER BY id
                """;

        List<Cliente> clientes = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                clientes.add(mapearCliente(resultado));
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                    "Erro ao listar clientes.", e
            );
        }

        return clientes;
    }

    @Override
    public List<Cliente> filtrar(
            String sobrenome, String telefone) {

        String sql = """
                SELECT id, nome, sobrenome, telefone
                FROM cliente
                WHERE LOCATE(LOWER(?), LOWER(sobrenome)) > 0
                AND LOCATE(?, telefone) > 0
                ORDER BY id
                """;

        List<Cliente> clientes = new ArrayList<>();

        sobrenome = sobrenome == null
                ? "" : sobrenome.trim();

        telefone = telefone == null
                ? "" : telefone.trim();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, sobrenome);
            stmt.setString(2, telefone);

            try (ResultSet resultado = stmt.executeQuery()) {

                while (resultado.next()) {
                    clientes.add(mapearCliente(resultado));
                }
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                    "Erro ao filtrar clientes.", e
            );
        }

        return clientes;
    }

    private Cliente mapearCliente(ResultSet resultado)
            throws SQLException {

        Cliente cliente = new Cliente(
                resultado.getString("nome"),
                resultado.getString("sobrenome"),
                resultado.getString("telefone")
        );

        cliente.setId(resultado.getInt("id"));

        return cliente;
    }
}