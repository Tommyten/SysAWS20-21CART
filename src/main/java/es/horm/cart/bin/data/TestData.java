package es.horm.cart.bin.data;

import es.horm.cart.lib.data.annotation.OutputField;

public class TestData {


    private float a;
    private float b;
    @OutputField
    private float c;
    private Richtung richtung;

    public TestData() {
    }

    public Richtung getRichtung() {
        return richtung;
    }

    public void setRichtung(Richtung richtung) {
        this.richtung = richtung;
    }

    public float getA() {
        return a;
    }

    public void setA(float a) {
        this.a = a;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getC() {
        return c;
    }

    public void setC(float c) {
        this.c = c;
    }
}