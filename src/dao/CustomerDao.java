package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Customer;
import model.Order;

/**
 * Data Access Object for Customer operations.
 */
public class CustomerDao {
    private String db_url = "jdbc:postgresql://localhost:5432/business_db";
    private String db_username = "postgres";
    private String db_passwd = "078868";
    
    /**
     * Creates a new customer in the database
     * 
     * @param customer The customer to create
     * @return Number of rows affected
     */
    public int createCustomer(Customer customer) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Prepare statement
            String sql = "INSERT INTO customers (customer_id, first_name, last_name, email, phone, address, registration_date) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pst.setString(1, customer.getCustomerId());
            pst.setString(2, customer.getFirstName());
            pst.setString(3, customer.getLastName());
            pst.setString(4, customer.getEmail());
            pst.setString(5, customer.getPhone());
            pst.setString(6, customer.getAddress());
            pst.setDate(7, java.sql.Date.valueOf(customer.getRegistrationDate()));
            
            // Execute statement
            int rowsAffected = pst.executeUpdate();
            
            // Get generated ID
            if (rowsAffected > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    customer.setId(rs.getInt(1));
                }
                rs.close();
            }
            
            // Close connection
            con.close();
            return rowsAffected;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Updates an existing customer in the database
     * 
     * @param customer The customer to update
     * @return Number of rows affected
     */
    public int updateCustomer(Customer customer) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Prepare statement
            String sql = "UPDATE customers SET customer_id = ?, first_name = ?, last_name = ?, " +
                         "email = ?, phone = ?, address = ?, registration_date = ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, customer.getCustomerId());
            pst.setString(2, customer.getFirstName());
            pst.setString(3, customer.getLastName());
            pst.setString(4, customer.getEmail());
            pst.setString(5, customer.getPhone());
            pst.setString(6, customer.getAddress());
            pst.setDate(7, java.sql.Date.valueOf(customer.getRegistrationDate()));
            pst.setInt(8, customer.getId());
            
            // Execute statement
            int rowsAffected = pst.executeUpdate();
            
            // Close connection
            con.close();
            return rowsAffected;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Finds a customer by ID
     * 
     * @param id The customer ID to search for
     * @return The customer if found, null otherwise
     */
    public Customer findCustomerById(int id) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM customers WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            
            ResultSet rs = pst.executeQuery();
            Customer customer = null;
            
            if (rs.next()) {
                customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setCustomerId(rs.getString("customer_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("address"));
                customer.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
            }
            
            con.close();
            return customer;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds a customer by their customer ID
     * 
     * @param customerId The customer ID to search for
     * @return The customer if found, null otherwise
     */
    public Customer findCustomerByCustomerId(String customerId) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM customers WHERE customer_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, customerId);
            
            ResultSet rs = pst.executeQuery();
            Customer customer = null;
            
            if (rs.next()) {
                customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setCustomerId(rs.getString("customer_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("address"));
                customer.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
            }
            
            con.close();
            return customer;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds customers by name (first name, last name, or full name)
     * 
     * @param name The name to search for
     * @return List of matching customers
     */
    public List<Customer> findCustomersByName(String name) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM customers WHERE first_name LIKE ? OR last_name LIKE ? OR " +
                        "CONCAT(first_name, ' ', last_name) LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            String searchName = "%" + name + "%";
            pst.setString(1, searchName);
            pst.setString(2, searchName);
            pst.setString(3, searchName);
            
            ResultSet rs = pst.executeQuery();
            List<Customer> customerList = new ArrayList<>();
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setCustomerId(rs.getString("customer_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("address"));
                customer.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
                customerList.add(customer);
            }
            
            con.close();
            return customerList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Finds a customer by email
     * 
     * @param email The email to search for
     * @return The customer if found, null otherwise
     */
    public Customer findCustomerByEmail(String email) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, email);
            
            ResultSet rs = pst.executeQuery();
            Customer customer = null;
            
            if (rs.next()) {
                customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setCustomerId(rs.getString("customer_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("address"));
                customer.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
            }
            
            con.close();
            return customer;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets all customers
     * 
     * @return List of all customers
     */
    public List<Customer> findAllCustomers() {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM customers";
            PreparedStatement pst = con.prepareStatement(sql);
            
            ResultSet rs = pst.executeQuery();
            List<Customer> customerList = new ArrayList<>();
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setCustomerId(rs.getString("customer_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("address"));
                customer.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
                customerList.add(customer);
            }
            
            con.close();
            return customerList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Gets a customer with all their orders
     * 
     * @param customerId The ID of the customer
     * @return The customer with orders loaded
     */
    public Customer getCustomerWithOrders(int customerId) {
        try {
            // First get the customer
            Customer customer = findCustomerById(customerId);
            if (customer == null) {
                return null;
            }
            
            // Then get their orders
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM orders WHERE customer_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, customerId);
            
            ResultSet rs = pst.executeQuery();
            List<Order> orderList = new ArrayList<>();
            
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderId(rs.getString("order_id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setOrderDate(rs.getDate("order_date").toLocalDate());
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setCustomer(customer);
                
                orderList.add(order);
            }
            
            customer.setOrders(orderList);
            con.close();
            return customer;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Deletes a customer from the database
     * 
     * @param customerId The ID of the customer to delete
     * @return Number of rows affected
     */
    public int deleteCustomer(int customerId) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "DELETE FROM customers WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, customerId);
            
            int rowsAffected = pst.executeUpdate();
            con.close();
            return rowsAffected;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}