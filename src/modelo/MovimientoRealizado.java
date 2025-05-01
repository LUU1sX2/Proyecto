package modelo;

public class MovimientoRealizado {
    private Ficha origen;
    private Ficha intermedia;
    private Ficha destino;

    public MovimientoRealizado(Ficha origen, Ficha intermedia, Ficha destino) {
        this.origen = clonarFicha(origen);
        this.intermedia = clonarFicha(intermedia);
        this.destino = clonarFicha(destino);
    }

    private Ficha clonarFicha(Ficha ficha) {
        return new Ficha(ficha.getxPosicion(), ficha.getyPosicion(), ficha.isExiste());
    }

    public Ficha getOrigen() {
        return origen;
    }

    public Ficha getIntermedia() {
        return intermedia;
    }

    public Ficha getDestino() {
        return destino;
    }
}
