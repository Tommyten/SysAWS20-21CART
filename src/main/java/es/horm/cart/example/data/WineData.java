package es.horm.cart.example.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.horm.cart.lib.annotation.InputField;
import es.horm.cart.lib.annotation.OutputField;

public class WineData {

    @JsonProperty("fixed acidity")
    @InputField
    float fixedAcidity;
    @JsonProperty("volatile acidity")
    @InputField
    float volatileAcidity;
    @JsonProperty("citric acid")
    @InputField
    float citricAcid;
    @JsonProperty("residual sugar")
    @InputField
    float residualSugar;
    @InputField
    float chlorides;
    @JsonProperty("free sulfur dioxide")
    @InputField
    float freeSulfurDioxide;
    @JsonProperty("total sulfur dioxide")
    @InputField
    float totalSulfurDioxide;
    @InputField
    float density;
    @InputField
    float pH;
    @InputField
    float sulphates;
    @InputField
    float alcohol;

    @OutputField
    float quality;

    public WineData() {
    }

    public WineData(float fixedAcidity, float volatileAcidity, float citricAcid, float residualSugar, float chlorides, float freeSulfurDioxide, float totalSulfurDioxide, float density, float pH, float sulphates, float alcohol, float quality) {
        this.fixedAcidity = fixedAcidity;
        this.volatileAcidity = volatileAcidity;
        this.citricAcid = citricAcid;
        this.residualSugar = residualSugar;
        this.chlorides = chlorides;
        this.freeSulfurDioxide = freeSulfurDioxide;
        this.totalSulfurDioxide = totalSulfurDioxide;
        this.density = density;
        this.pH = pH;
        this.sulphates = sulphates;
        this.alcohol = alcohol;
        this.quality = quality;
    }

    public float getFixedAcidity() {
        return fixedAcidity;
    }

    public void setFixedAcidity(float fixedAcidity) {
        this.fixedAcidity = fixedAcidity;
    }

    public float getVolatileAcidity() {
        return volatileAcidity;
    }

    public void setVolatileAcidity(float volatileAcidity) {
        this.volatileAcidity = volatileAcidity;
    }

    public float getCitricAcid() {
        return citricAcid;
    }

    public void setCitricAcid(float citricAcid) {
        this.citricAcid = citricAcid;
    }

    public float getResidualSugar() {
        return residualSugar;
    }

    public void setResidualSugar(float residualSugar) {
        this.residualSugar = residualSugar;
    }

    public float getChlorides() {
        return chlorides;
    }

    public void setChlorides(float chlorides) {
        this.chlorides = chlorides;
    }

    public float getFreeSulfurDioxide() {
        return freeSulfurDioxide;
    }

    public void setFreeSulfurDioxide(float freeSulfurDioxide) {
        this.freeSulfurDioxide = freeSulfurDioxide;
    }

    public float getTotalSulfurDioxide() {
        return totalSulfurDioxide;
    }

    public void setTotalSulfurDioxide(float totalSulfurDioxide) {
        this.totalSulfurDioxide = totalSulfurDioxide;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getpH() {
        return pH;
    }

    public void setpH(float pH) {
        this.pH = pH;
    }

    public float getSulphates() {
        return sulphates;
    }

    public void setSulphates(float sulphates) {
        this.sulphates = sulphates;
    }

    public float getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(float alcohol) {
        this.alcohol = alcohol;
    }

    public float getQuality() {
        return quality;
    }

    public void setQuality(float quality) {
        this.quality = quality;
    }
}
