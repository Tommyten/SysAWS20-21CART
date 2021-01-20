package es.horm.cart.bin.data;

import es.horm.cart.lib.data.annotation.OutputField;

public class GiniTestData {

    @OutputField
    private char letter;
    private int number;
    private int decimal;

    public GiniTestData() {
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }
}
