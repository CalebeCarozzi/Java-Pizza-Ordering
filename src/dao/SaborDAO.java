package dao;

import model.Sabor;

import java.util.List;

public interface SaborDAO {

    void salvar(Sabor sabor);

    void atualizar(Sabor sabor);

    void excluir(int id);

    Sabor buscarPorId(int id);

    List<Sabor> listarTodos();

    List<Sabor> filtrarPorNome(String nome);
}