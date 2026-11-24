package service;

import dao.PrecoDAO;
import exception.CampoInvalidoException;
import exception.PrecoInvalidoException;
import model.TabelaPreco;
import model.TipoPizza;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public class PrecoService {

    private final PrecoDAO precoDAO;

    public PrecoService(PrecoDAO precoDAO) {
        this.precoDAO = precoDAO;
    }

    public TabelaPreco getTabelaPreco() {
        return precoDAO.consultarPrecos();
    }

    public double getPrecoSimples() {
        return getTabelaPreco().getPrecoSimples();
    }

    public double getPrecoEspecial() {
        return getTabelaPreco().getPrecoEspecial();
    }

    public double getPrecoPremium() {
        return getTabelaPreco().getPrecoPremium();
    }

    public double getPrecoPorTipo(TipoPizza tipo) {

        validarTipo(tipo);

        return getTabelaPreco().getPrecoPorTipo(tipo);
    }

    public void alterarPreco(TipoPizza tipo, double valor) {

        validarTipo(tipo);
        validarPreco(valor);

        precoDAO.atualizarPrecos(
                Map.of(tipo, BigDecimal.valueOf(valor))
        );
    }

    public void atualizarPrecos(
            String simples,
            String especial,
            String premium) {

        Map<TipoPizza, BigDecimal> precos =
                new EnumMap<>(TipoPizza.class);

        adicionarPreco(precos, TipoPizza.SIMPLES, simples);
        adicionarPreco(precos, TipoPizza.ESPECIAL, especial);
        adicionarPreco(precos, TipoPizza.PREMIUM, premium);

        if (precos.isEmpty()) {

            throw new CampoInvalidoException(
                    "Informe pelo menos um preço."
            );
        }

        precoDAO.atualizarPrecos(precos);
    }

    private void adicionarPreco(
            Map<TipoPizza, BigDecimal> precos,
            TipoPizza tipo,
            String texto) {

        if (texto == null || texto.isBlank()) {
            return;
        }

        BigDecimal valor = converterPreco(texto);

        validarPrecoDecimal(valor);

        precos.put(tipo, valor);
    }

    private BigDecimal converterPreco(String texto) {

        try {

            return new BigDecimal(
                    texto.trim().replace(",", ".")
            );

        } catch (NumberFormatException e) {

            throw new PrecoInvalidoException(
                    "Digite um preço válido. Exemplo: 0,05."
            );
        }
    }

    public void validarTipo(TipoPizza tipo) {

        if (tipo == null) {

            throw new CampoInvalidoException(
                    "Tipo de pizza é obrigatório."
            );
        }
    }

    public void validarPreco(double valor) {

        if (!Double.isFinite(valor)) {

            throw new PrecoInvalidoException(
                    "Informe um preço válido."
            );
        }

        validarPrecoDecimal(BigDecimal.valueOf(valor));
    }

    private void validarPrecoDecimal(BigDecimal valor) {

        if (valor.signum() <= 0) {

            throw new PrecoInvalidoException(
                    "O preço deve ser maior que zero."
            );
        }

        if (valor.stripTrailingZeros().scale() > 4) {

            throw new PrecoInvalidoException(
                    "O preço deve ter no máximo 4 casas decimais."
            );
        }

        if (valor.compareTo(
                new BigDecimal("999999.9999")) > 0) {

            throw new PrecoInvalidoException(
                    "Preço acima do limite permitido."
            );
        }
    }
}