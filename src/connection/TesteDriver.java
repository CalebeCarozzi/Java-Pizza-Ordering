package connection;

public class TesteDriver {

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Driver JDBC carregado com sucesso!");

        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC não encontrado.");
            e.printStackTrace();
        }
    }
}