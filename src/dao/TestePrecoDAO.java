package dao;

import model.TabelaPreco;
import model.TipoPizza;

import java.math.BigDecimal;
import java.util.Map;

public class TestePrecoDAO {

    public static void main(String[] args) {

        PrecoDAO precoDAO = DaoFactory.getPrecoDAO();

        TabelaPreco original = precoDAO.consultarPrecos();

        System.out.println("Preços originais:");
        exibirPrecos(original);

        Map<TipoPizza, BigDecimal> precosTeste = Map.of(
                TipoPizza.SIMPLES, new BigDecimal("0.08"),
                TipoPizza.ESPECIAL, new BigDecimal("0.12"),
                TipoPizza.PREMIUM, new BigDecimal("0.15")
        );

        try {

            precoDAO.atualizarPrecos(precosTeste);

            TabelaPreco atualizada =
                    precoDAO.consultarPrecos();

            System.out.println("\nPreços atualizados:");
            exibirPrecos(atualizada);

            for (TipoPizza tipo : TipoPizza.values()) {

                double esperado =
                        precosTeste.get(tipo).doubleValue();

                double encontrado =
                        atualizada.getPrecoPorTipo(tipo);

                if (Math.abs(esperado - encontrado) > 0.000001) {

                    throw new IllegalStateException(
                            "Falha ao atualizar preço: " + tipo
                    );
                }
            }

        } finally {

            Map<TipoPizza, BigDecimal> precosOriginais = Map.of(
                    TipoPizza.SIMPLES,
                    BigDecimal.valueOf(original.getPrecoSimples()),

                    TipoPizza.ESPECIAL,
                    BigDecimal.valueOf(original.getPrecoEspecial()),

                    TipoPizza.PREMIUM,
                    BigDecimal.valueOf(original.getPrecoPremium())
            );

            precoDAO.atualizarPrecos(precosOriginais);

            System.out.println("\nPreços originais restaurados:");

            exibirPrecos(precoDAO.consultarPrecos());
        }

        System.out.println("\nTeste finalizado com sucesso!");
    }

    private static void exibirPrecos(TabelaPreco tabela) {

        for (TipoPizza tipo : TipoPizza.values()) {

            System.out.println(
                    tipo + ": R$ " + tabela.getPrecoPorTipo(tipo)
            );
        }
    }
}