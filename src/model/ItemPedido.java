package model;

import java.math.BigDecimal;

public class ItemPedido {

    private int idItemPedido;
    private Pizza pizza;
    private int quantidade;
    private BigDecimal precoUnitario;

    public ItemPedido(
            Pizza pizza,
            int quantidade,
            BigDecimal precoUnitario) {

        this.pizza = pizza;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public int getIdItemPedido() {
        return idItemPedido;
    }

    public void setIdItemPedido(int idItemPedido) {
        this.idItemPedido = idItemPedido;
    }

    public Pizza getPizza() {
        return pizza;
    }

    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal calculaSubTotal() {

        return precoUnitario.multiply(
                BigDecimal.valueOf(quantidade)
        );
    }
}