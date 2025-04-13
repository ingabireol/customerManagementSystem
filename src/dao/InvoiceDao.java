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
import model.Invoice;
import model.Order;

/**
 * Data Access Object for Invoice operations.
 */
public class InvoiceDao {
    private String db_url = "jdbc:postgresql://localhost:5432/business_db";
    private String db_username = "postgres";
    private String db_passwd = "078868";
    
    /**
     * Creates a new invoice in the database
     * 
     * @param invoice The invoice to create
     * @return Number of rows affected
     */
    public int createInvoice(Invoice invoice) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Prepare statement
            String sql = "INSERT INTO invoices (invoice_number, order_id, issue_date, due_date, amount, status) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pst.setString(1, invoice.getInvoiceNumber());
            pst.setInt(2, invoice.getOrderId());
            pst.setDate(3, java.sql.Date.valueOf(invoice.getIssueDate()));
            pst.setDate(4, java.sql.Date.valueOf(invoice.getDueDate()));
            pst.setBigDecimal(5, invoice.getAmount());
            pst.setString(6, invoice.getStatus());
            
            // Execute statement
            int rowsAffected = pst.executeUpdate();
            
            // Get generated ID
            if (rowsAffected > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    invoice.setId(rs.getInt(1));
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
     * Updates an existing invoice in the database
     * 
     * @param invoice The invoice to update
     * @return Number of rows affected
     */
    public int updateInvoice(Invoice invoice) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Prepare statement
            String sql = "UPDATE invoices SET invoice_number = ?, order_id = ?, issue_date = ?, " +
                         "due_date = ?, amount = ?, status = ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, invoice.getInvoiceNumber());
            pst.setInt(2, invoice.getOrderId());
            pst.setDate(3, java.sql.Date.valueOf(invoice.getIssueDate()));
            pst.setDate(4, java.sql.Date.valueOf(invoice.getDueDate()));
            pst.setBigDecimal(5, invoice.getAmount());
            pst.setString(6, invoice.getStatus());
            pst.setInt(7, invoice.getId());
            
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
     * Updates the status of an invoice
     * 
     * @param invoiceId The ID of the invoice
     * @param status The new status
     * @return Number of rows affected
     */
    public int updateInvoiceStatus(int invoiceId, String status) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Prepare statement
            String sql = "UPDATE invoices SET status = ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, status);
            pst.setInt(2, invoiceId);
            
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
     * Finds an invoice by ID
     * 
     * @param id The invoice ID to search for
     * @return The invoice if found, null otherwise
     */
    public Invoice findInvoiceById(int id) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM invoices WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            
            ResultSet rs = pst.executeQuery();
            Invoice invoice = null;
            
            if (rs.next()) {
                invoice = new Invoice();
                invoice.setId(rs.getInt("id"));
                invoice.setInvoiceNumber(rs.getString("invoice_number"));
                invoice.setOrderId(rs.getInt("order_id"));
                invoice.setIssueDate(rs.getDate("issue_date").toLocalDate());
                invoice.setDueDate(rs.getDate("due_date").toLocalDate());
                invoice.setAmount(rs.getBigDecimal("amount"));
                invoice.setStatus(rs.getString("status"));
            }
            
            con.close();
            return invoice;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds an invoice by invoice number
     * 
     * @param invoiceNumber The invoice number to search for
     * @return The invoice if found, null otherwise
     */
    public Invoice findInvoiceByNumber(String invoiceNumber) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM invoices WHERE invoice_number = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, invoiceNumber);
            
            ResultSet rs = pst.executeQuery();
            Invoice invoice = null;
            
            if (rs.next()) {
                invoice = new Invoice();
                invoice.setId(rs.getInt("id"));
                invoice.setInvoiceNumber(rs.getString("invoice_number"));
                invoice.setOrderId(rs.getInt("order_id"));
                invoice.setIssueDate(rs.getDate("issue_date").toLocalDate());
                invoice.setDueDate(rs.getDate("due_date").toLocalDate());
                invoice.setAmount(rs.getBigDecimal("amount"));
                invoice.setStatus(rs.getString("status"));
            }
            
            con.close();
            return invoice;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds invoices by order ID
     * 
     * @param orderId The order ID to search for
     * @return List of matching invoices
     */
    public List<Invoice> findInvoicesByOrderId(int orderId) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM invoices WHERE order_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, orderId);
            
            ResultSet rs = pst.executeQuery();
            List<Invoice> invoiceList = new ArrayList<>();
            
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setId(rs.getInt("id"));
                invoice.setInvoiceNumber(rs.getString("invoice_number"));
                invoice.setOrderId(rs.getInt("order_id"));
                invoice.setIssueDate(rs.getDate("issue_date").toLocalDate());
                invoice.setDueDate(rs.getDate("due_date").toLocalDate());
                invoice.setAmount(rs.getBigDecimal("amount"));
                invoice.setStatus(rs.getString("status"));
                invoiceList.add(invoice);
            }
            
            con.close();
            return invoiceList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Finds invoices by status
     * 
     * @param status The status to search for
     * @return List of matching invoices
     */
    public List<Invoice> findInvoicesByStatus(String status) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM invoices WHERE status = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, status);
            
            ResultSet rs = pst.executeQuery();
            List<Invoice> invoiceList = new ArrayList<>();
            
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setId(rs.getInt("id"));
                invoice.setInvoiceNumber(rs.getString("invoice_number"));
                invoice.setOrderId(rs.getInt("order_id"));
                invoice.setIssueDate(rs.getDate("issue_date").toLocalDate());
                invoice.setDueDate(rs.getDate("due_date").toLocalDate());
                invoice.setAmount(rs.getBigDecimal("amount"));
                invoice.setStatus(rs.getString("status"));
                invoiceList.add(invoice);
            }
            
            con.close();
            return invoiceList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Finds invoices that are overdue
     * 
     * @return List of overdue invoices
     */
    public List<Invoice> findOverdueInvoices() {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM invoices WHERE due_date < ? AND status != ? AND status != ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            pst.setString(2, Invoice.STATUS_PAID);
            pst.setString(3, Invoice.STATUS_CANCELLED);
            
            ResultSet rs = pst.executeQuery();
            List<Invoice> invoiceList = new ArrayList<>();
            
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setId(rs.getInt("id"));
                invoice.setInvoiceNumber(rs.getString("invoice_number"));
                invoice.setOrderId(rs.getInt("order_id"));
                invoice.setIssueDate(rs.getDate("issue_date").toLocalDate());
                invoice.setDueDate(rs.getDate("due_date").toLocalDate());
                invoice.setAmount(rs.getBigDecimal("amount"));
                invoice.setStatus(rs.getString("status"));
                invoiceList.add(invoice);
            }
            
            con.close();
            return invoiceList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Finds invoices by date range
     * 
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of matching invoices
     */
    public List<Invoice> findInvoicesByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM invoices WHERE issue_date BETWEEN ? AND ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setDate(1, java.sql.Date.valueOf(startDate));
            pst.setDate(2, java.sql.Date.valueOf(endDate));
            
            ResultSet rs = pst.executeQuery();
            List<Invoice> invoiceList = new ArrayList<>();
            
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setId(rs.getInt("id"));
                invoice.setInvoiceNumber(rs.getString("invoice_number"));
                invoice.setOrderId(rs.getInt("order_id"));
                invoice.setIssueDate(rs.getDate("issue_date").toLocalDate());
                invoice.setDueDate(rs.getDate("due_date").toLocalDate());
                invoice.setAmount(rs.getBigDecimal("amount"));
                invoice.setStatus(rs.getString("status"));
                invoiceList.add(invoice);
            }
            
            con.close();
            return invoiceList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Gets all invoices
     * 
     * @return List of all invoices
     */
    public List<Invoice> findAllInvoices() {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM invoices";
            PreparedStatement pst = con.prepareStatement(sql);
            
            ResultSet rs = pst.executeQuery();
            List<Invoice> invoiceList = new ArrayList<>();
            
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setId(rs.getInt("id"));
                invoice.setInvoiceNumber(rs.getString("invoice_number"));
                invoice.setOrderId(rs.getInt("order_id"));
                invoice.setIssueDate(rs.getDate("issue_date").toLocalDate());
                invoice.setDueDate(rs.getDate("due_date").toLocalDate());
                invoice.setAmount(rs.getBigDecimal("amount"));
                invoice.setStatus(rs.getString("status"));
                invoiceList.add(invoice);
            }
            
            con.close();
            return invoiceList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Gets an invoice with its order information
     * 
     * @param invoiceId The ID of the invoice
     * @return The invoice with order loaded
     */
    public Invoice getInvoiceWithOrder(int invoiceId) {
        try {
            // First get the invoice
            Invoice invoice = findInvoiceById(invoiceId);
            if (invoice == null) {
                return null;
            }
            
            // Then get its order
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM orders WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, invoice.getOrderId());
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderId(rs.getString("order_id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setOrderDate(rs.getDate("order_date").toLocalDate());
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setPaymentMethod(rs.getString("payment_method"));
                
                invoice.setOrder(order);
            }
            
            con.close();
            return invoice;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets an invoice with its payments
     * 
     * @param invoiceId The ID of the invoice
     * @return The invoice with payments loaded
     */
    public Invoice getInvoiceWithPayments(int invoiceId) {
        try {
            // First get the invoice
            Invoice invoice = findInvoiceById(invoiceId);
            if (invoice == null) {
                return null;
            }
            
            // Then get its payments
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM payments WHERE invoice_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, invoiceId);
            
            ResultSet rs = pst.executeQuery();
            List<model.Payment> paymentList = new ArrayList<>();
            
            while (rs.next()) {
                model.Payment payment = new model.Payment();
                payment.setId(rs.getInt("id"));
                payment.setPaymentId(rs.getString("payment_id"));
                payment.setInvoiceId(rs.getInt("invoice_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                payment.setPaymentMethod(rs.getString("payment_method"));
                payment.setInvoice(invoice);
                
                paymentList.add(payment);
            }
            
            invoice.setPayments(paymentList);
            
            con.close();
            return invoice;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Deletes an invoice from the database
     * 
     * @param invoiceId The ID of the invoice to delete
     * @return Number of rows affected
     */
    public int deleteInvoice(int invoiceId) {
        Connection con = null;
        try {
            // Create connection
            con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Start transaction
            con.setAutoCommit(false);
            
            // First check if the invoice has payments
            String sql = "SELECT COUNT(*) FROM payments WHERE invoice_id = ?";
            PreparedStatement checkPst = con.prepareStatement(sql);
            checkPst.setInt(1, invoiceId);
            ResultSet rs = checkPst.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                // If there are payments, we can't delete the invoice
                con.rollback();
                return 0;
            }
            
            // Delete the invoice
            sql = "DELETE FROM invoices WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, invoiceId);
            
            int rowsAffected = pst.executeUpdate();
            
            // Commit transaction
            con.commit();
            
            // Close connection
            con.close();
            return rowsAffected;
            
        } catch (Exception ex) {
            try {
                // Rollback transaction on error
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            
            ex.printStackTrace();
            return 0;
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
    }
}