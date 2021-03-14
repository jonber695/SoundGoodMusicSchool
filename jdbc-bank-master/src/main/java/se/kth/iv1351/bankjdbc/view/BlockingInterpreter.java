/*
 * The MIT License
 *
 * Copyright 2017 Leif Lindbäck <leifl@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
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

package se.kth.iv1351.bankjdbc.view;

import java.util.List;
import java.util.Scanner;


import se.kth.iv1351.bankjdbc.controller.Controller;
import se.kth.iv1351.bankjdbc.model.InstrumentDTO;
import se.kth.iv1351.bankjdbc.model.RentalAgreement;
import se.kth.iv1351.bankjdbc.model.RentalAgreementDTO;
import se.kth.iv1351.bankjdbc.model.Student;

/**
 * Reads and interprets user commands. This command interpreter is blocking, the user
 * interface does not react to user input while a command is being executed.
 */
public class BlockingInterpreter {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private Controller ctrl;
    private boolean keepReceivingCmds = false;

    /**
     * Creates a new instance that will use the specified controller for all operations.
     * 
     * @param ctrl The controller used by this instance.
     */
    public BlockingInterpreter(Controller ctrl) {
        this.ctrl = ctrl;
    }

    /**
     * Stops the commend interpreter.
     */
    public void stop() {
        keepReceivingCmds = false;
    }

    /**
     * Interprets and performs user commands. This method will not return until the
     * UI has been stopped. The UI is stopped either when the user gives the
     * "quit" command, or when the method <code>stop()</code> is called.
     */
    public void handleCmds() {
        keepReceivingCmds = true;
        while (keepReceivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                    case HELP:
                        for (Command command : Command.values()) {
                            if (command == Command.ILLEGAL_COMMAND) {
                                continue;
                            }
                            System.out.println(command.toString().toLowerCase());
                        }
                        break;
                    case QUIT:
                        keepReceivingCmds = false;
                        break;
                    case SHOW:
                        listInstruments(cmdLine);
                        break;
                    case RENT:
                        rentInstrument(cmdLine);
                        break;
                    case TERMINATE:
                        terminateRental(cmdLine);
                        break;
                    default:
                        System.out.println("illegal command");
                }
            } catch (Exception e) {
                System.out.println("Operation failed");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String readNextLine() {
        System.out.print(PROMPT);
        return console.nextLine();
    }

    private void listInstruments(CmdLine cmdLine) throws Exception
    {
        List<? extends InstrumentDTO> instruments = null;
        if(cmdLine.getParameter(0).equals(""))
        {
            instruments = ctrl.listAllInstruments();
            for(InstrumentDTO instrument : instruments)
            {
                System.out.println("Type: " + instrument.getType() + ", "
                                + "Brand: " + instrument.getBrand() + ", "
                                + "Price: " + instrument.getPrice());
            }
        }
        else
        {
            instruments = ctrl.listInstruments(cmdLine.getParameter(0));
            for(InstrumentDTO instrument : instruments)
            {
                System.out.println("Brand: " + instrument.getBrand() + ", "
                                + "Price: " + instrument.getPrice());
            }
        }
    }

    private void rentInstrument(CmdLine cmdLine) throws Exception
    {
        Student student = ctrl.getStudent(Integer.parseInt(cmdLine.getParameter(0)));
        if(student.getNrRented() >= 2)
        {
            System.out.println("You have already rented the max amount of instruments");
        }
            
        else
        {
            ctrl.rentInstrument(cmdLine.getParameter(1), cmdLine.getParameter(2), student, Integer.parseInt(cmdLine.getParameter(3)));
            System.out.println("You have successfully rented your instument");
        }
    }

    private void terminateRental(CmdLine cmdLine) throws Exception
    {
        Student student = ctrl.getStudent(Integer.parseInt(cmdLine.getParameter(0)));
        if(student.getNrRented() <=0)
            System.out.println("You have no active rentals and can therefore not terminate any");
        else
        {
            int rentalToTerminate;
            int count = 1;
            List<RentalAgreement> rentals = ctrl.getRentalAgreement(Integer.parseInt(cmdLine.getParameter(0)));
            
            for (RentalAgreementDTO rental : rentals) 
            {
                InstrumentDTO instrument = ctrl.getInstrumentById(rental.getInstrumentId());
                System.out.println(count++ + ": Brand of instrument: " + instrument.getBrand() + ", type of instrument: " + instrument.getType() + ", start date for agreement: " + rental.getStartDate() 
                                  + ", end date for agreement: " + rental.getEndDate() + ", price if agreement if fulfilled: " + rental.getEstimatedPrice());
            }
            System.out.print("Which agreement do you want terminate? \n");
            rentalToTerminate = Integer.parseInt(readNextLine());
            switch(rentalToTerminate)
            {
                case 1:
                    ctrl.terminateRental(rentals.get(0));
                    System.out.println("You have successfully removed the selected agreement");
                    break;
                case 2:
                    ctrl.terminateRental(rentals.get(1));
                    System.out.println("You have successfully removed the selected agreement");
                    break;
                default: System.out.println("Not an alternative");
            }
        }
    }
}
