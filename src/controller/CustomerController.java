package controller;

import dao.CustomerDao;
import model.Customer;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller class for Customer operations.
 * Acts as an intermediary between the view and the data access layer.
 */
public class CustomerController {
    private CustomerDao customerDao;
    
    /**
     * Default constructor
     */
    public CustomerController() {
        this.customerDao = new CustomerDao();
    }
    
    /**
     * Constructor with dependency injection for testing
     * 
     * @param customerDao The customer DAO to use
     */
    public CustomerController(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }
    
    /**
     * Adds a new customer
     * 
     * @param customerId Unique customer ID
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @param phone Phone number
     * @param address Address
     * @return The created customer if successful, null otherwise
     */
    public Customer addCustomer(String customerId, String firstName, String lastName, 
                              String email, String phone, String address) {
        // Validate inputs
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be empty");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        // Check if customer ID already exists
        Customer existingCustomer = customerDao.findCustomerByCustomerId(customerId);
        if (existingCustomer != null) {
            throw new IllegalArgumentException("Customer ID already exists");
        }
        
        // Check if email already exists
        existingCustomer = customerDao.findCustomerByEmail(email);
        if (existingCustomer != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Create customer object
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);
        customer.setRegistrationDate(LocalDate.now());
        
        // Save customer
        int result = customerDao.createCustomer(customer);
        if (result > 0) {
            return customer;
        } else {
            return null;
        }
    }
    
    /**
     * Updates an existing customer
     * 
     * @param id The ID of the customer to update
     * @param customerId Unique customer ID
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @param phone Phone number
     * @param address Address
     * @return The updated customer if successful, null otherwise
     */
    public Customer updateCustomer(int id, String customerId, String firstName, String lastName, 
                                 String email, String phone, String address) {
        // Get the existing customer
        Customer customer = customerDao.findCustomerById(id);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }
        
        // Validate inputs
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be empty");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        // Check if customer ID already exists and is not this customer
        Customer existingCustomer = customerDao.findCustomerByCustomerId(customerId);
        if (existingCustomer != null && existingCustomer.getId() != id) {
            throw new IllegalArgumentException("Customer ID already exists");
        }
        
        // Check if email already exists and is not this customer
        existingCustomer = customerDao.findCustomerByEmail(email);
        if (existingCustomer != null && existingCustomer.getId() != id) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Update customer object
        customer.setCustomerId(customerId);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);
        
        // Save updated customer
        int result = customerDao.updateCustomer(customer);
        if (result > 0) {
            return customer;
        } else {
            return null;
        }
    }
    
    /**
     * Gets a customer by ID
     * 
     * @param id The ID of the customer
     * @return The customer if found, null otherwise
     */
    public Customer getCustomerById(int id) {
        return customerDao.findCustomerById(id);
    }
    
    /**
     * Gets a customer by customer ID
     * 
     * @param customerId The customer ID to search for
     * @return The customer if found, null otherwise
     */
    public Customer getCustomerByCustomerId(String customerId) {
        return customerDao.findCustomerByCustomerId(customerId);
    }
    
    /**
     * Gets a customer by email
     * 
     * @param email The email to search for
     * @return The customer if found, null otherwise
     */
    public Customer getCustomerByEmail(String email) {
        return customerDao.findCustomerByEmail(email);
    }
    
    /**
     * Searches for customers by name
     * 
     * @param name The name to search for
     * @return List of matching customers
     */
    public List<Customer> searchCustomersByName(String name) {
        return customerDao.findCustomersByName(name);
    }
    
    /**
     * Gets all customers
     * 
     * @return List of all customers
     */
    public List<Customer> getAllCustomers() {
        return customerDao.findAllCustomers();
    }
    
    /**
     * Gets a customer with their order history
     * 
     * @param customerId The ID of the customer
     * @return The customer with orders loaded, null if not found
     */
    public Customer getCustomerWithOrders(int customerId) {
        return customerDao.getCustomerWithOrders(customerId);
    }
    
    /**
     * Deletes a customer
     * 
     * @param id The ID of the customer to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteCustomer(int id) {
        // Check if customer exists
        Customer customer = customerDao.findCustomerById(id);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }
        
        // Check if customer has orders
        Customer customerWithOrders = customerDao.getCustomerWithOrders(id);
        if (customerWithOrders != null && !customerWithOrders.getOrders().isEmpty()) {
            throw new IllegalStateException("Cannot delete customer with orders");
        }
        
        // Delete customer
        int result = customerDao.deleteCustomer(id);
        return result > 0;
    }
}