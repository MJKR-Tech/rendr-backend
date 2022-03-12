package com.mjkrt.rendr.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class SimpleRow {

    @Id // if Id is numeric, then add generation type (not full defined for string cases)
    private String ticker;
    
    private String instrumentType;
    private Double coupon;
    private Double originalFace;
    private Double marketValue;
    private @JsonProperty("ISIN") String isin;
    private String portfolio;
    private String maturityDate;
    private Double price;
    private String positionDate;
    private Integer currentFace;
    private String currency;
    private String contractCode;
    
    public SimpleRow() {}

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Double getCoupon() {
        return coupon;
    }

    public void setCoupon(Double coupon) {
        this.coupon = coupon;
    }

    public Double getOriginalFace() {
        return originalFace;
    }

    public void setOriginalFace(Double originalFace) {
        this.originalFace = originalFace;
    }

    public Double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(Double marketValue) {
        this.marketValue = marketValue;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPositionDate() {
        return positionDate;
    }

    public void setPositionDate(String positionDate) {
        this.positionDate = positionDate;
    }

    public Integer getCurrentFace() {
        return currentFace;
    }

    public void setCurrentFace(Integer currentFace) {
        this.currentFace = currentFace;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }
    
    @JsonIgnore
    public static List<String> getFields() {
        return Arrays.asList(
                "Instrument Type",
                "Ticker",
                "Contract Code",
                "Coupon",
                "Maturity",
                "Currency",
                "ISIN",
                "Current Face",
                "Original Face",
                "Price",
                "Market Value"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleRow simpleRow = (SimpleRow) o;
        return Objects.equals(coupon, simpleRow.coupon)
                && Double.compare(simpleRow.originalFace, originalFace) == 0
                && Double.compare(simpleRow.marketValue, marketValue) == 0
                && Double.compare(simpleRow.price, price) == 0
                && Objects.equals(currentFace, simpleRow.currentFace)
                && Objects.equals(instrumentType, simpleRow.instrumentType)
                && Objects.equals(ticker, simpleRow.ticker)
                && Objects.equals(isin, simpleRow.isin)
                && Objects.equals(portfolio, simpleRow.portfolio)
                && Objects.equals(maturityDate, simpleRow.maturityDate)
                && Objects.equals(positionDate, simpleRow.positionDate)
                && Objects.equals(currency, simpleRow.currency)
                && Objects.equals(contractCode, simpleRow.contractCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                instrumentType, 
                ticker,
                coupon,
                originalFace,
                marketValue,
                isin,
                portfolio,
                maturityDate,
                price, 
                positionDate,
                currentFace,
                currency,
                contractCode
        );
    }

    @Override
    public String toString() {
        return "SimpleRow{" +
                "instrumentType='" + instrumentType + '\'' +
                ", ticker='" + ticker + '\'' +
                ", coupon=" + coupon +
                ", originalFace=" + originalFace +
                ", marketValue=" + marketValue +
                ", isin='" + isin + '\'' +
                ", portfolio='" + portfolio + '\'' +
                ", maturityDate='" + maturityDate + '\'' +
                ", price=" + price +
                ", positionDate='" + positionDate + '\'' +
                ", currentFace=" + currentFace +
                ", currency='" + currency + '\'' +
                ", contractCode='" + contractCode + '\'' +
                '}';
    }
}
