package dao;

import model.TabelaPreco;
import model.TipoPizza;

import java.math.BigDecimal;
import java.util.Map;

public interface PrecoDAO {

    TabelaPreco consultarPrecos();

    void atualizarPrecos(Map<TipoPizza, BigDecimal> precos);
}