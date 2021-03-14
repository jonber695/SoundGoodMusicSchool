package se.kth.iv1351.bankjdbc.model;

/**
 * InstrumentDTO
 */
public interface InstrumentDTO {

    /**
     * @return The brand of an instrument
     */
    public String getBrand();

    /**
     * @return The price of an instrument
     */
    public double getPrice();
    
    /**
     * @return The type of an instrument
     */
    public String getType();
}