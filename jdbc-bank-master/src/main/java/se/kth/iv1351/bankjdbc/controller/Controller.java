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

package se.kth.iv1351.bankjdbc.controller;

import java.util.ArrayList;
import java.util.List;

import se.kth.iv1351.bankjdbc.integration.BankDAO;
import se.kth.iv1351.bankjdbc.integration.BankDBException;
import se.kth.iv1351.bankjdbc.model.Instrument;
import se.kth.iv1351.bankjdbc.model.InstrumentException;
import se.kth.iv1351.bankjdbc.model.RentalAgreement;
import se.kth.iv1351.bankjdbc.model.RentalAgreementException;
import se.kth.iv1351.bankjdbc.model.Student;
import se.kth.iv1351.bankjdbc.model.StudentException;

/**
 * This is the application's only controller, all calls to the model pass here.
 * The controller is also responsible for calling the DAO. Typically, the
 * controller first calls the DAO to retrieve data (if needed), then operates on
 * the data, and finally tells the DAO to store the updated data (if any).
 */
public class Controller {
    private final BankDAO bankDb;

    /**
     * Creates a new instance, and retrieves a connection to the database.
     * 
     * @throws BankDBException If unable to connect to the database.
     */
    public Controller() throws BankDBException {
        bankDb = new BankDAO();
    }


    /**
     * Lists all instruments that are of the specified type and that are not already rented
     * 
     * @param typeOfInstrument The specified type
     * @return A list of instruments that fulfills the previously named critera
     * @throws InstrumentException If the instrument was not retrivable
     */
    public List<Instrument> listInstruments(String typeOfInstrument) throws InstrumentException
    {
        if(typeOfInstrument == null)
            return new ArrayList<>();
        
        try
        {
            return bankDb.listInstruments(typeOfInstrument);
        }
        catch (Exception e)
        {
            throw new InstrumentException("Could not search for instrument", e);
        }
    }

    /**
     * Lists all instruments that have not been rented out regarless of type
     * @return A list that fulfills the criteria named above
     * @throws InstrumentException If the instruments could not be listed
     */
    public List<Instrument> listAllInstruments() throws InstrumentException
    {
        try
        {
            return bankDb.listAllInstruments();
        }
        catch(Exception e)
        {
            throw new InstrumentException("Unable to list instruments", e);
        }
    }

    /**
     * Creates an rental agreement for the specified student corresponding to the instrument with the brand and type with the specified length
     * @param type The type specified by the student
     * @param brand The brand specified by the student
     * @param student The specified student
     * @param length The specified length
     * @throws RentalAgreementException If the agreement could not be created
     */
    public void rentInstrument(String type, String brand, Student student, int length) throws RentalAgreementException
    {
        try
        {
            bankDb.rentInstrument(brand, type, student, length);
        }
        catch (Exception e)
        {
            throw new RentalAgreementException("Could not rent instrument", e);
        }
    }

    /**
     * Gets the student with the specified id
     * @param id The specified ID
     * @return The student
     * @throws StudentException If the student could not be found
     */
    public Student getStudent(int id) throws StudentException
    {
        try
        {
            return bankDb.findStudent(id);
        }
        catch(Exception e)
        {
            throw new StudentException("Unable to search for student", e);
        }
    }

    /**
     * Lists all rental agreements connected to the specified student
     * @param studentId the specified students id
     * @return A list of agreements for which the student is responsible for
     * @throws RentalAgreementException If the list could not be created
     */
    public List<RentalAgreement> getRentalAgreement(int studentId) throws RentalAgreementException
    {
        try
        {
            return bankDb.listAgreements(studentId);
        }
        catch(Exception e)
        {
            throw new RentalAgreementException("Could not list agreements", e);
        }
    }

    /**
     * Finds the Instrument with the specified ID
     * @param instrumentId The specified ID
     * @return The instrument wanted 
     * @throws InstrumentException If no search was possible
     */
    public Instrument getInstrumentById(int instrumentId) throws InstrumentException
    {
        try
        {
            return bankDb.findInstrument(instrumentId);
        }
        catch(Exception e)
        {
            throw new InstrumentException("Unable to search for insrument", e);
        }
    }

    public void terminateRental(RentalAgreement rentalAgreement) throws RentalAgreementException
    {
        try
        {
            bankDb.terminateRental(rentalAgreement);
        }
        catch(Exception e)
        {
            throw new RentalAgreementException("Could not terminate Rental", e);
        }
    }
}
