package dao;

import model.Cliente;
import java.util.List;

public interface ClienteDAO {

    void salvar(Cliente cliente);

    void atualizar(Cliente cliente);

    void excluir(int id);

    Cliente buscarPorId(int id);

    List<Cliente> listarTodos();

    List<Cliente> filtrar(String sobrenome, String telefone);
}