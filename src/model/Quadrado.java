package model;

public class Quadrado extends Forma{
    private double lado;

    public Quadrado(double lado){
        this.lado = lado;
    }

    public double getLado() {
        return lado;
    }

    public void setLado(double lado) {
        this.lado = lado;
    }

    @Override
    public double calculaArea(){
        return lado*lado;
    }

    @Override
    public String descricao() {

        return String.format(
                "Quadrado - Lado de: %.2f", lado
        );
    }
}
