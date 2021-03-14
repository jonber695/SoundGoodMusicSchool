package se.kth.iv1351.bankjdbc.model;


/**
 * Instrument
 */
public class Instrument implements InstrumentDTO
{
    private String brand;
    private double price;
    private String type;
    private int id;
    private int numberRented;

    public Instrument(String brand, double price, String type)
    {
        this.brand = brand;
        this.price = price;
        this.type = type;
    }

    public Instrument(String brand, double price)
    {
        this(brand, price, null);
    }

    public Instrument(String type, String brand)
    {
        this.brand = brand;
        this.type = type;
    }

    public Instrument(int id, double price, int numberRented)
    {
        this.price = price;
        this.id = id;
        this.numberRented = numberRented;
    }

    public Instrument(int id, int numberRented)
    {
        this.id = id;
        this.numberRented = numberRented;
    }

    public String getBrand()
    {
        return brand;
    }

    public double getPrice()
    {
        return price;
    }

    public String getType()
    {
        return type;
    }

    public int getId()
    {
        return id;
    }

    public void incrementNrRented()
    {
        numberRented++;
    }

    public void reduceNrRented()
    {
        numberRented--;
    }

    public int getNumberRented()
    {
        return numberRented;
    }
}