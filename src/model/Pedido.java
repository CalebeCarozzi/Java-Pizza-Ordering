package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private int idPedido;
    private Cliente cliente;
    private List<ItemPedido> itens;
    private EstadoPedido estadoPedido;
    private BigDecimal precoTotal;

    public Pedido(Cliente cliente) {

        this.cliente = cliente;
        this.itens = new ArrayList<>();
        this.estadoPedido = EstadoPedido.ABERTO;
        this.precoTotal = BigDecimal.ZERO;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public EstadoPedido getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(EstadoPedido estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public BigDecimal getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(BigDecimal precoTotal) {
        this.precoTotal = precoTotal;
    }

    public void adicionaItem(ItemPedido itemPedido) {
        itens.add(itemPedido);
    }

    public void removeItem(ItemPedido itemPedido) {
        itens.remove(itemPedido);
    }

    public BigDecimal calculaTotal() {

        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedido item : itens) {
            total = total.add(item.calculaSubTotal());
        }

        return total;
    }

    public void atualizarPrecoTotal() {
        this.precoTotal = calculaTotal();
    }
}