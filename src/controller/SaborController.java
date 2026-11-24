package controller;

import model.Sabor;
import model.TipoPizza;
import service.SaborService;

import java.util.List;

public class SaborController {

    private final SaborService saborService;

    public SaborController(SaborService saborService) {

        this.saborService = saborService;
    }

    public void cadastrarSabor(String nome, TipoPizza tipo) {

        saborService.cadastrarSabor(nome, tipo);
    }

    public void atualizarSabor(
            int idSabor, String nome, TipoPizza tipo) {

        saborService.editarSabor(idSabor, nome, tipo);
    }

    public void excluirSabor(int idSabor) {

        saborService.excluirSabor(idSabor);
    }

    public List<Sabor> listarSabores() {

        return saborService.listarSabores();
    }

    public List<Sabor> filtrarSabores(String nome) {

        return saborService.listarPorSabor(nome);
    }
}