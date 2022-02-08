package com.mjkrt.rendr.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SimpleRow {

    public SimpleRow() {}

    @GeneratedValue(strategy = GenerationType.AUTO)
    private String instrumentType;
    @Id
    private String ticker;
    private int coupon;
    private double originalFace;
    private double marketValue;
    private String portfolio;
    private String maturityDate;
    private double price;
    private String positionDate;
    private int currentFace;
    private String currency;
    private String contractCode;


//    public SimpleRow( String instrumentType, String ticker, int coupon,
//                      double originalFace,  double marketValue,
//                      String ISIN,  String portfolio,  Date maturityDate,
//                      double price,  String positionDate,  int currentFace,
//                      String currency,  String contractCode) {
//        this.instrumentType = instrumentType;
//        this.ticker = ticker;
//        this.coupon = coupon;
//        this.originalFace = originalFace;
//        this.marketValue = marketValue;
//        this.ISIN = ISIN;
//        this.portfolio = portfolio;
//        this.maturityDate = maturityDate;
//        this.price = price;
//        this.positionDate = positionDate;
//        this.currentFace = currentFace;
//        this.currency = currency;
//        this.contractCode = contractCode;
//    }

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

    public int getCoupon() {
        return coupon;
    }

    public void setCoupon(int coupon) {
        this.coupon = coupon;
    }

    public double getOriginalFace() {
        return originalFace;
    }

    public void setOriginalFace(double originalFace) {
        this.originalFace = originalFace;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPositionDate() {
        return positionDate;
    }

    public void setPositionDate(String positionDate) {
        this.positionDate = positionDate;
    }

    public int getCurrentFace() {
        return currentFace;
    }

    public void setCurrentFace(int currentFace) {
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


}
