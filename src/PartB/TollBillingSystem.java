/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PartB;

import Exceptions.DaoException;
import java.util.ArrayList;

/**
 *
 * @author HP
 */
public class TollBillingSystem
{

    //variable of the class
    private Customer customer;
    private Customer temp;
    private double totalFee;
    private ArrayList<TollEvent> tollEvents;
    private boolean PayStatus = false;

    //DAO 
    private final AllDAOInterface IAllDAO = new MySqlAllDao();

    //initialize customer 
    public TollBillingSystem(Customer c)
    {
        this.customer = c;
        this.totalFee = 0;
        this.tollEvents = new ArrayList<>();
        this.PayStatus = false;
    }

    public TollBillingSystem()
    {
        this.customer = new Customer();
        this.totalFee = 0;
        this.tollEvents = new ArrayList<>();
        this.PayStatus = false;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public void setPayStatus(boolean PayStatus)
    {
        this.PayStatus = PayStatus;
    }

    public void setTotalFee(double totalFee)
    {
        this.totalFee = totalFee;
    }

    public void setTollEvents(ArrayList<TollEvent> tollEvents)
    {
        this.tollEvents = tollEvents;
    }

    public boolean isPayStatus()
    {
        return PayStatus;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public double getTotalFee()
    {
        return totalFee;
    }

    public ArrayList<TollEvent> getTollEvents()
    {
        return tollEvents;
    }

    public void displayAllFees()
    {
        int totalFee = 0;
        for (TollEvent te : this.tollEvents)
        {
            System.out.println(te);
            totalFee += te.getCost();
        }
        System.out.println("Total Fee :" + totalFee);
    }

    public void getCustomerDetails(int id)
    {
        ArrayList<TollEvent> tollEventLists = new ArrayList<>();
        try
        {
            this.temp = IAllDAO.getCustomerDetailLogIn(id);
        } catch (DaoException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void run()
    {
        loginMenu();
    }

    public void loginMenu()
    {
        boolean running = true;
        System.out.println("Hello, welcome to toll billig system.");
        displayLoginMenu();
        int option = Utilities.getValidInt(1, 2, "Please select an option ");
        while (running)
        {
            if (option == 1)
            {
                int CID = Utilities.getValidInt(0, "Please enter your ID");
                getCustomerDetails(CID);
                if (this.temp.getCustomer_id() != -1)
                {
                    System.out.println("ID:" + this.temp.getCustomer_id());
                    System.out.println("Name:" + this.temp.getCustomer_name());
                    System.out.println("Address:" + this.temp.getCustomer_address());
                    boolean isCustomer = Utilities.getValidBoolean("Is this you? Enter Y to Login.(Y/N)");
                    if (isCustomer)
                    {
                        this.customer = this.temp;
                        getAllTollEventsAndSumByCustomerIdFromDatabase();
                        runMainMenu();
                    }
                }
                else
                {
                    System.out.println("ID not found.");
                    Utilities.awaitForEnter();
                }
            }
            else if (option == 2)
            {
                running = false;
                System.out.println("GoodBye.");
            }
            if (running)
            {
                displayLoginMenu();
                option = Utilities.getValidInt(1, 2, "Please select an option ");
            }
        }
    }

    public void displayLoginMenu()
    {
        ArrayList<String> menu = new ArrayList<>();
        menu.add("\n******************************************************************");
        menu.add("Log in Menu");
        menu.add("1. Log in with customer ID");
        menu.add("2. Quit");
        menu.add("******************************************************************");
        for (String m : menu)
        {
            System.out.println(m);
        }
    }

    public void runMainMenu()
    {
        System.out.println("\nWelcome " + this.customer.getCustomer_name() + ", You have been logged in successfully.");
        boolean running = true;
        displayMainMenu();
        int option = Utilities.getValidInt(1, 4, "Please enter an option(1~3)");
        while (running)
        {
            switch (option)
            {
                case 1:
                    checkBillStatus();
                    break;
                case 2:
                    displayAllBills();
                    break;
                case 3:
                    payBill();
                    break;
                default:
                    running = false;
                    System.out.println("Quit Main Menu");
                    System.out.println("You have been logged out\n");
                    break;
            }
            if (running)
            {
                displayMainMenu();
                option = Utilities.getValidInt(1, 4, "Please enter an option(1~3)");
            }
        }

    }

    public void displayMainMenu()
    {
        ArrayList<String> menu = new ArrayList<>();
        menu.add("******************************************************************");
        menu.add("Main Menu");
        menu.add("1. Check Bills Status");
        menu.add("2. Display All Toll Event");
        menu.add("3. Pay Bills");
        menu.add("4. Logout");
        menu.add("******************************************************************");
        for (String m : menu)
        {
            System.out.println(m);
        }
    }

    public void getAllTollEventsAndSumByCustomerIdFromDatabase()
    {
        try
        {
            this.tollEvents = IAllDAO.getAllTollEventsByCustomerId(this.customer.getCustomer_id());
            this.totalFee = IAllDAO.getTotalBill();
        } catch (DaoException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void checkBillStatus()
    {
        if (this.PayStatus)
        {
            System.out.println("You have paid all bills");
        }
        else
        {
            if(this.totalFee != 0)
            {
                System.out.println("You have owned " + this.totalFee + " Fees.");
            }
            else
            {
                System.out.println("There is no owned amount.");
            }
        }
        Utilities.awaitForEnter();
    }

    public void displayAllBills()
    {
        for (TollEvent t : this.tollEvents)
        {
            System.out.printf("%-10s%-15s%-15s%-18s%-10s%-12s%-10s\n", "EventID", "VehicleID" , "VehicleReg", "VehicleType", "Cost"," ImageID", "Time");
            System.out.printf("%-10s%-15s%-15s%-18s%-11.2f%-12d%-10s\n\n", t.getEventId(), t.getVehicleId(), t.getRegistrationNumber(), t.getVehicleType(), t.getCost(), t.getImageID(), t.getTimestamp().toString());
        }
        System.out.println("Total Fees owned : " + this.totalFee);
        Utilities.awaitForEnter();
    }

    public void payBill()
    {
        if(this.totalFee != 0)
        {
            System.out.println("Total Fees : " + this.totalFee);
            boolean pay = Utilities.getValidBoolean("Do you want to pay the fee now? Enter Y to pay.(Y/N)");
            if (pay)
            {
                this.PayStatus = true;
                this.totalFee = 0;
                System.out.println("You have paid the bill.");
            }
            else
            {
                System.out.println("The bill is not paid yet.");
            }
        }
        else
            System.out.println("There is no owned amount.");
        Utilities.awaitForEnter();
    }

}
