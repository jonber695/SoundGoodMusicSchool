package se.kth.iv1351.bankjdbc.model;

import java.sql.Date;

/**
 * RentalAgreementDTO
 */
public interface RentalAgreementDTO {

    /**
     * @return True if the rentalAgreement is terminated, otherwise false
     */
    public boolean isTerminated();

    /**
     * @return The start date of the agreement
     */
    public Date getStartDate();

    /**
     * @return The end date of the agreement
     */
    public Date getEndDate();

    /**
     * @return The estimated price (which is if the agreement runs its course)
     */
    public double getEstimatedPrice();

    /**
     * @return The student ID corresponding to the agreement
     */
    public int getStudentId();

    /**
     * @return The instrument ID corresponding to the agreement
     */
    public int getInstrumentId();

    /**
     * @return The original length of the agreement
     */
    public int getLength();

    /**
     * @return The actual price, what the student ended up paying
     */
    public double getActualPrice();

    /**
     * @return The rental id from the database
     */
    public int getRentalId();
}