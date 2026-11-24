package controller;

import model.TabelaPreco;
import service.PrecoService;

public class PrecoController {

    private final PrecoService precoService;

    public PrecoController(PrecoService precoService) {
        this.precoService = precoService;
    }

    public TabelaPreco consultarPrecos() {
        return precoService.getTabelaPreco();
    }

    public void atualizarPrecos(
            String simples,
            String especial,
            String premium) {

        precoService.atualizarPrecos(
                simples,
                especial,
                premium
        );
    }
}