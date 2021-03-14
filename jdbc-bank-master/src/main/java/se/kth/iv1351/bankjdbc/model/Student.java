package se.kth.iv1351.bankjdbc.model;

/**
 * Student
 */
public class Student {

    private int id;
    private int numberRented;

    public Student(int id, int numberRented)
    {
        this.id = id;
        this.numberRented = numberRented;
    }

    public int getId()
    {
        return id;
    }

    public int getNrRented()
    {
        return numberRented;
    }

    /**
     * increments the number representing number of instruments rented by 1
     */
    public void incrementNrRented()
    {
        numberRented++;
    }

    /**
     * reduces the number representing number of instruments rented by 1
     */
    public void reduceNrRented()
    {
        numberRented--;
    }
}