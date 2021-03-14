/*
 * The MIT License (MIT)
 * Copyright (c) 2020 Leif Lindbäck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction,including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so,subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package se.kth.iv1351.bankjdbc.integration;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import se.kth.iv1351.bankjdbc.model.Instrument;
import se.kth.iv1351.bankjdbc.model.RentalAgreement;
import se.kth.iv1351.bankjdbc.model.Student;

/**
 * This data access object (DAO) encapsulates all database calls in the bank
 * application. No code outside this class shall have any knowledge about the
 * database.
 */
public class BankDAO {

    private static final String INSTRUMENT_TABLE_NAME = "instrument";
    private static final String INSTRUMENT_COLUMN_ID = "id";
    private static final String INSTRUMENT_COLUMN_PRICE = "price";
    private static final String INSTRUMENT_COLUMN_BRAND = "brand";
    private static final String INSTRUMENT_COLUMN_TYPE =  "type";
    private static final String INSTRUMENT_COLUMN_NUMBER_RENTED = "number_rented";

    private static final String RENAL_TABLE_NAME = "rental_agreement_for_instrument";
    private static final String RENTAL_COLUMN_ID = "rental_agreement_id";
    private static final String RENTAL_COLUMN_STUDENT = "student_id";
    private static final String RENTAL_COLUMN_INSTRUMENT = "instrument_id";
    private static final String RENTAL_COLUMN_LENGTH = "length";
    private static final String RENTAL_COLUMN_START = "start_date";
    private static final String RENTAL_COLUMN_END = "end_date";
    private static final String RENTAL_COLUMN_E_PRICE = "estimated_price";
    private static final String RENTAL_COLUMN_A_PRICE = "actual_price";
    private static final String RENTAL_COLUMN_TERMINATED = "terminated";


    private static final String STUDENT_TABLE_NAME = "student";
    private static final String STUDENT_COLUMN_ID = "id";
    private static final String STUDENT_COLUMN_NUMBER_RENTED = "number_of_instruments_rented";

    private Connection connection;
    

    private PreparedStatement listInstrumentByTypeStmt;
    private PreparedStatement listAllInstrumentsStmt;

    private PreparedStatement findStudentByIdStmt;
    private PreparedStatement rentInstrumentStmt;  
    private PreparedStatement updateNrRentedForStudentStmt;
    private PreparedStatement findInstrumentIdAndPriceStmt;
    private PreparedStatement updateNrRentedForInstrumentStmt;
    private PreparedStatement terminateRentalStmt;
    private PreparedStatement listRentalsStmt;
    private PreparedStatement findInstrumentByIdStmt;
    private PreparedStatement findInstrumentsNumberRentedAndIdByIdStmt;

    /**
     * Constructs a new DAO object connected to the bank database.
     */
    public BankDAO() throws BankDBException {
        try {
            connectToBankDB();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new BankDBException("Could not connect to datasource.", exception);
        }
    }


    /**
     * Retrieves all instruments with the right type and that are not already rented out
     * @return A list of all instruments that forfills the criteria
     * @throws BankDBException If failed to search for isntruments
     */
    public List<Instrument> listInstruments(String typeOfInstrument) throws BankDBException
    {
        String failureMsg = "Could not search for specified instruments";
        ResultSet result = null;
        List<Instrument> instruments = new ArrayList<>();
        try
        {
            listInstrumentByTypeStmt.setString(1, typeOfInstrument);
            result = listInstrumentByTypeStmt.executeQuery();
            while(result.next())
            {
                instruments.add(new Instrument(result.getString(INSTRUMENT_COLUMN_BRAND), 
                                               result.getDouble(INSTRUMENT_COLUMN_PRICE)));
            }
            connection.commit();
        }
        catch(SQLException sqlE)
        {
            handleException(failureMsg, sqlE);
        }
        finally
        {
            closeResultSet(failureMsg, result);
        }
        return instruments;
    }

    /**
     * Retrives all instruments that have not already been rented out
     * @return A list of intstruments that have not already been rented out
     * @throws BankDBException If failed to search for instruments
     */
    public List<Instrument> listAllInstruments() throws BankDBException
    {
        String failureMsg = "Could not list instruments";
        ResultSet result = null;
        List<Instrument> instruments = new ArrayList<>();
        try
        {
            result = listAllInstrumentsStmt.executeQuery();
            while(result.next())
            {
                instruments.add(new Instrument(result.getString(INSTRUMENT_COLUMN_BRAND), 
                                               result.getDouble(INSTRUMENT_COLUMN_PRICE), 
                                               result.getString(INSTRUMENT_COLUMN_TYPE)));
            }
            connection.commit();
        }
        catch(SQLException sqlE)
        {
            handleException(failureMsg, sqlE);
        }
        finally
        {
            closeResultSet(failureMsg, result);
        }
        return instruments;
    }

    public List<RentalAgreement> listAgreements(int studentId) throws BankDBException
    {
        String failureMsg = "Could not list agreements";
        ResultSet result = null;
        List<RentalAgreement> rentalAgreements = new ArrayList<>();
        try
        {
            listRentalsStmt.setInt(1, studentId);
            result = listRentalsStmt.executeQuery();
            while(result.next())
            {
                rentalAgreements.add(new RentalAgreement(result.getInt(RENTAL_COLUMN_ID),
                                                        result.getInt(RENTAL_COLUMN_STUDENT),
                                                        result.getInt(RENTAL_COLUMN_INSTRUMENT),
                                                        result.getDate(RENTAL_COLUMN_START),
                                                        result.getDate(RENTAL_COLUMN_END),
                                                        result.getBoolean(RENTAL_COLUMN_TERMINATED),
                                                        result.getInt(RENTAL_COLUMN_LENGTH),
                                                        result.getDouble(RENTAL_COLUMN_E_PRICE)));
            }
            connection.commit();
        }
        catch(SQLException sqlE)
        {
            handleException(failureMsg, sqlE);
        }
        finally
        {
            closeResultSet(failureMsg, result);
        }
        return rentalAgreements;
    }

    /**
     * Finds a specific student with the specified ID
     * @param id the specified id
     * @return the specified student
     * @throws BankDBException If failed to search for student
     */
    public Student findStudent(int id) throws BankDBException
    {
        String failureMsg = "Could not search for student";
        ResultSet result = null;
        Student student = null;
        try
        {
            findStudentByIdStmt.setInt(1, id);
            result = findStudentByIdStmt.executeQuery();
            if(result.next())
                student = new Student(result.getInt(STUDENT_COLUMN_ID), result.getInt(STUDENT_COLUMN_NUMBER_RENTED));
            connection.commit();
        }
        catch(SQLException sqlE)
        {
            handleException(failureMsg, sqlE);
        }
        finally
        {
            closeResultSet(failureMsg, result);
        }
        return student;
    }

    /**
     * Find the instrument specified by the id
     * @param instrumentId The id that specifies the instrument
     * @return The specified instrument
     * @throws BankDBException If the search was unsuccessful
     */
    public Instrument findInstrument(int instrumentId) throws BankDBException
    {
        String failureMsg = "Could not find instrument";
        ResultSet result = null;
        Instrument instrument = null;
        try
        {
            findInstrumentByIdStmt.setInt(1, instrumentId);
            result = findInstrumentByIdStmt.executeQuery();
            if(result.next())
                instrument = new Instrument(result.getString(INSTRUMENT_COLUMN_TYPE), result.getString(INSTRUMENT_COLUMN_BRAND));
            connection.commit();
        }
        catch(SQLException sqlE)
        {
            handleException(failureMsg, sqlE);
        }
        finally
        {
            closeResultSet(failureMsg, result);
        }
        return instrument;
    }

    private void updateNrRentedForStudent(Student student) throws BankDBException
    {
        int updatedRows;
        String failureMsg  = "Could not update the number of rented instruments for student";
        try
        {
            updateNrRentedForStudentStmt.setInt(1, student.getNrRented());
            updateNrRentedForStudentStmt.setInt(2, student.getId());
            updatedRows = updateNrRentedForStudentStmt.executeUpdate();
            if(updatedRows != 1)
                handleException(failureMsg, null);
            connection.commit();
        }
        catch (SQLException sqlE)
        {
            handleException(failureMsg, sqlE);
        }
    }

    private void updateNrRentedForInstrument(Instrument instrument) throws BankDBException
    {
        int updatedRows;
        String failureMsg = "Could not update number of rented for instrument";
        try
        {
            updateNrRentedForInstrumentStmt.setInt(1, instrument.getNumberRented());
            updateNrRentedForInstrumentStmt.setInt(2, instrument.getId());
            updatedRows = updateNrRentedForInstrumentStmt.executeUpdate();
            if(updatedRows != 1)
                handleException(failureMsg, null);
            connection.commit();
        }
        catch(SQLException sqlE)
        {
            handleException(failureMsg, sqlE);
        }
    }

    private Instrument findInstrumentIdAndPrice(String type, String brand) throws BankDBException
    {
        String failureMsg = "Could not search for instrument";
        ResultSet result = null;
        Instrument instrument = null;
        try
        {
            findInstrumentIdAndPriceStmt.setString(1, type);
            findInstrumentIdAndPriceStmt.setString(2, brand);
            result = findInstrumentIdAndPriceStmt.executeQuery();
            if(result.next())
                instrument = new Instrument(result.getInt(INSTRUMENT_COLUMN_ID), result.getDouble(INSTRUMENT_COLUMN_PRICE), 0);
            connection.commit();
        }
        catch (SQLException sqlE)
        {
            handleException(failureMsg, sqlE);
        }
        finally
        {
            closeResultSet(failureMsg, result);
        }
        return instrument;
    }

    private void createAgreementForRental(int studentId, Instrument instrument, int length) throws BankDBException
    {
        int updatedRows;
        String failureMsg = "Could not create Agreement";
        LocalDate today = LocalDate.now();
        LocalDate localEndDate = LocalDate.of(today.getYear(), today.getMonthValue()+length, today.getDayOfMonth());
        Date endDate = Date.valueOf(localEndDate);
        try
        {
            rentInstrumentStmt.setInt(1, studentId);
            rentInstrumentStmt.setInt(2, instrument.getId());
            rentInstrumentStmt.setInt(3, length);
            rentInstrumentStmt.setDate(4, endDate);
            rentInstrumentStmt.setDouble(5, (instrument.getPrice()*length));
            updatedRows = rentInstrumentStmt.executeUpdate();
            if(updatedRows != 1)
                handleException(failureMsg, null);
            connection.commit();
        }
        catch(SQLException sqlE)
        {
            handleException(failureMsg, sqlE);
        }
    }

    /**
     * Creates an rental Agreement for the specified length corresponding to the student specified for the instrument matching the type and brand specified
     * @param brand The brand of the instrument specified by the student 
     * @param type The type of instrument specified by the student
     * @param student The specified student
     * @param length The specified length
     * @throws BankDBException If such an agreement cannot be created
     */
    public void rentInstrument(String brand, String type, Student student, int length) throws BankDBException
    {
        String failureMsg  = "Could not create rental Agreement";
        try
        {
            Instrument instrument = findInstrumentIdAndPrice(type, brand);
            createAgreementForRental(student.getId(), instrument, length);
            student.incrementNrRented();
            updateNrRentedForStudent(student);
            instrument.incrementNrRented();
            updateNrRentedForInstrument(instrument);
        }
        catch(BankDBException sqlE)
        {
            handleException(failureMsg, sqlE);
        }
    }

    private Instrument getInstrumentIdAndNumberRented(int instrumentId) throws BankDBException
    {
        String failureMsg = "Could not find instrument";
        ResultSet result = null;
        Instrument instrument = null;
        try
        {
            findInstrumentsNumberRentedAndIdByIdStmt.setInt(1, instrumentId);
            result = findInstrumentsNumberRentedAndIdByIdStmt.executeQuery();
            if(result.next())
                instrument = new Instrument(result.getInt(INSTRUMENT_COLUMN_ID), result.getInt(INSTRUMENT_COLUMN_NUMBER_RENTED));
            connection.commit();
        }
        catch(SQLException sqlE)
        {
            handleException(failureMsg, sqlE);
        }
        finally
        {
            closeResultSet(failureMsg, result);
        }
        return instrument;
    }

    public void terminateRental(RentalAgreement rentalAgreement) throws BankDBException
    {
        String failureMsg = "Could not terminate rental agreement";
        try
        {
            Student student = findStudent(rentalAgreement.getStudentId());
            Instrument instrument = getInstrumentIdAndNumberRented(rentalAgreement.getInstrumentId());
            terminateRentalAgreement(rentalAgreement);
            student.reduceNrRented();
            updateNrRentedForStudent(student);
            instrument.reduceNrRented();
            updateNrRentedForInstrument(instrument);
        }
        catch(BankDBException e)
        {
            handleException(failureMsg, e);
        }
    }

    private void terminateRentalAgreement(RentalAgreement rentalAgreement) throws BankDBException
    {
        int updatedRows;
        String failureMsg = "Could not terminate rental";
        int monthsPassed = LocalDate.now().getMonthValue() - rentalAgreement.getStartDate().toLocalDate().getMonthValue();
        try
        {
            terminateRentalStmt.setDouble(1, ((((rentalAgreement.getEstimatedPrice()/rentalAgreement.getLength())*monthsPassed < 0) ? rentalAgreement.getEstimatedPrice()/rentalAgreement.getLength() : rentalAgreement.getEstimatedPrice()/rentalAgreement.getLength())*monthsPassed));
            terminateRentalStmt.setInt(2, rentalAgreement.getRentalId());
            updatedRows = terminateRentalStmt.executeUpdate();
            if(updatedRows != 1)
                handleException(failureMsg, null);
            connection.commit();
        }
        catch(SQLException sqlE)
        {
            handleException(failureMsg, sqlE);
        }
    }

    

    private void connectToBankDB() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/SoundGoodSchool",
                                                 "postgres", "example");
        // connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb",
        //                                          "root", "javajava");
        connection.setAutoCommit(false);
    }

    private void prepareStatements() throws SQLException {
        listInstrumentByTypeStmt = connection.prepareStatement("SELECT " + INSTRUMENT_COLUMN_BRAND + ", " + INSTRUMENT_COLUMN_PRICE +
            " FROM " + INSTRUMENT_TABLE_NAME + " WHERE " + INSTRUMENT_COLUMN_TYPE + " = ? AND " + INSTRUMENT_COLUMN_NUMBER_RENTED + " = 0");

        listAllInstrumentsStmt = connection.prepareStatement("SELECT " + INSTRUMENT_COLUMN_TYPE + ", " + INSTRUMENT_COLUMN_BRAND +
            ", " + INSTRUMENT_COLUMN_PRICE + " FROM " + INSTRUMENT_TABLE_NAME + " WHERE " + INSTRUMENT_COLUMN_NUMBER_RENTED + " = 0");

        findStudentByIdStmt = connection.prepareStatement("SELECT " + STUDENT_COLUMN_ID + ", " + STUDENT_COLUMN_NUMBER_RENTED + " FROM "
            + STUDENT_TABLE_NAME + " WHERE " + STUDENT_COLUMN_ID + " = ?");

        updateNrRentedForStudentStmt = connection.prepareStatement("UPDATE " + STUDENT_TABLE_NAME + " SET " + STUDENT_COLUMN_NUMBER_RENTED + " = ?" 
            + " WHERE " + STUDENT_COLUMN_ID + " = ?");

        findInstrumentIdAndPriceStmt = connection.prepareStatement("SELECT " + INSTRUMENT_COLUMN_ID + ", " + INSTRUMENT_COLUMN_PRICE + " FROM " + INSTRUMENT_TABLE_NAME + " WHERE "
            + INSTRUMENT_COLUMN_TYPE + " = ? AND " + INSTRUMENT_COLUMN_BRAND + " = ? AND " + INSTRUMENT_COLUMN_NUMBER_RENTED + " = 0");

        rentInstrumentStmt = connection.prepareStatement("INSERT INTO " + RENAL_TABLE_NAME + " (" + RENTAL_COLUMN_STUDENT 
            + ", " + RENTAL_COLUMN_INSTRUMENT + ", " + RENTAL_COLUMN_LENGTH + ", " + RENTAL_COLUMN_START + ", " + RENTAL_COLUMN_END + ", " 
            + RENTAL_COLUMN_E_PRICE + ", " + RENTAL_COLUMN_A_PRICE + ", " + RENTAL_COLUMN_TERMINATED + ") VALUES (?, ?, ?, current_date, ?, ?, null, false)");

        updateNrRentedForInstrumentStmt = connection.prepareStatement("UPDATE " + INSTRUMENT_TABLE_NAME + " SET " + INSTRUMENT_COLUMN_NUMBER_RENTED + " = ?"
            + " WHERE " + INSTRUMENT_COLUMN_ID + " = ?");

        listRentalsStmt = connection.prepareStatement("SELECT * FROM " + RENAL_TABLE_NAME + " WHERE " + RENTAL_COLUMN_STUDENT + " = ? AND " + RENTAL_COLUMN_TERMINATED + " = false");

        findInstrumentByIdStmt = connection.prepareStatement("SELECT " + INSTRUMENT_COLUMN_BRAND + ", " + INSTRUMENT_COLUMN_TYPE + " FROM " + INSTRUMENT_TABLE_NAME + " WHERE " 
            + INSTRUMENT_COLUMN_ID + " = ?");

        findInstrumentsNumberRentedAndIdByIdStmt = connection.prepareStatement("SELECT " + INSTRUMENT_COLUMN_ID + ", " + INSTRUMENT_COLUMN_NUMBER_RENTED + " FROM " + INSTRUMENT_TABLE_NAME
            + " WHERE " + INSTRUMENT_COLUMN_ID + " = ?");

        terminateRentalStmt = connection.prepareStatement("UPDATE " + RENAL_TABLE_NAME + " SET " + RENTAL_COLUMN_TERMINATED + " = true, " + RENTAL_COLUMN_A_PRICE + " = ? WHERE " + RENTAL_COLUMN_ID + " = ?");
    }
    private void handleException(String failureMsg, Exception cause) throws BankDBException {
        String completeFailureMsg = failureMsg;
        try {
            connection.rollback();
        } catch (SQLException rollbackExc) {
            completeFailureMsg = completeFailureMsg + 
            ". Also failed to rollback transaction because of: " + rollbackExc.getMessage();
        }

        if (cause != null) {
            throw new BankDBException(failureMsg, cause);
        } else {
            throw new BankDBException(failureMsg);
        }
    }

    private void closeResultSet(String failureMsg, ResultSet result) throws BankDBException {
        try {
            result.close();
        } catch (Exception e) {
            throw new BankDBException(failureMsg + " Could not close result set.", e);
        }
    }
}
