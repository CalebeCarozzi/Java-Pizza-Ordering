package connection;

import exception.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TesteConexao {

    public static void main(String[] args) {

        String sql = "SELECT DATABASE()";

        try (Connection conexao =
                     ConnectionFactory.getConnection();

             PreparedStatement statement =
                     conexao.prepareStatement(sql);

             ResultSet resultado =
                     statement.executeQuery()) {

            System.out.println(
                    "Conexão realizada com sucesso!"
            );

            if (resultado.next()) {

                System.out.println(
                        "Banco conectado: " + resultado.getString(1)
                );
            }

        } catch (PersistenciaException | SQLException e) {

            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}