package se.kth.iv1351.bankjdbc.model;

import java.sql.Date;

/**
 * RentalAgreement
 */
public class RentalAgreement implements RentalAgreementDTO
{

    private int rentalId;
    private int studentId;
    private int instrumentId;
    private int length;
    private Date startDate;
    private Date endDate;
    private double estimatedPrice;
    private double actualPrice;
    private boolean terminated;

    public RentalAgreement(int rentalId, int studentId, int instrumentId, Date startDate, Date endDate, boolean terminated, int length, double estimatedPrice)
    {
        this.rentalId = rentalId;
        this.studentId = studentId;
        this.instrumentId = instrumentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.terminated = terminated;
        this.length = length;
        this.estimatedPrice = estimatedPrice;
    }

    public int getStudentId()
    {
        return studentId;
    }

    public int getInstrumentId()
    {
        return instrumentId;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public boolean isTerminated()
    {
        return terminated;
    }

    public int getLength()
    {
        return length;
    }

    public double getEstimatedPrice()
    {
        return estimatedPrice;
    }

    public void setActualPrice(double price)
    {
        actualPrice = price;
    }

    public double getActualPrice()
    {
        return actualPrice;
    }

    public int getRentalId()
    {
        return rentalId;
    }

    public void setTerminated()
    {
        terminated = true;
    }
}