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
import model.Payment;

/**
 * Data Access Object for Payment operations.
 */
public class PaymentDao {
    private String db_url = "jdbc:postgresql://localhost:5432/business_db";
    private String db_username = "postgres";
    private String db_passwd = "078868";
    
    /**
     * Creates a new payment in the database
     * 
     * @param payment The payment to create
     * @return Number of rows affected
     */
    public int createPayment(Payment payment) {
        Connection con = null;
        try {
            // Create connection
            con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Start transaction
            con.setAutoCommit(false);
            
            // Prepare statement
            String sql = "INSERT INTO payments (payment_id, invoice_id, amount, payment_date, payment_method) " +
                         "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pst.setString(1, payment.getPaymentId());
            pst.setInt(2, payment.getInvoiceId());
            pst.setBigDecimal(3, payment.getAmount());
            pst.setDate(4, java.sql.Date.valueOf(payment.getPaymentDate()));
            pst.setString(5, payment.getPaymentMethod());
            
            // Execute statement
            int rowsAffected = pst.executeUpdate();
            
            // Get generated ID
            if (rowsAffected > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    payment.setId(rs.getInt(1));
                }
                rs.close();
            }
            
            // Update invoice status if needed
            sql = "SELECT * FROM invoices WHERE id = ?";
            PreparedStatement invPst = con.prepareStatement(sql);
            invPst.setInt(1, payment.getInvoiceId());
            
            ResultSet rs = invPst.executeQuery();
            if (rs.next()) {
                // Get total payments for this invoice
                sql = "SELECT SUM(amount) FROM payments WHERE invoice_id = ?";
                PreparedStatement sumPst = con.prepareStatement(sql);
                sumPst.setInt(1, payment.getInvoiceId());
                
                ResultSet sumRs = sumPst.executeQuery();
                if (sumRs.next()) {
                    java.math.BigDecimal totalPaid = sumRs.getBigDecimal(1);
                    java.math.BigDecimal invoiceAmount = rs.getBigDecimal("amount");
                    
                    // Update invoice status if fully paid
                    if (totalPaid.compareTo(invoiceAmount) >= 0) {
                        sql = "UPDATE invoices SET status = ? WHERE id = ?";
                        PreparedStatement updatePst = con.prepareStatement(sql);
                        updatePst.setString(1, Invoice.STATUS_PAID);
                        updatePst.setInt(2, payment.getInvoiceId());
                        updatePst.executeUpdate();
                        updatePst.close();
                    }
                }
                sumRs.close();
                sumPst.close();
            }
            rs.close();
            invPst.close();
            
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
    
    /**
     * Updates an existing payment in the database
     * 
     * @param payment The payment to update
     * @return Number of rows affected
     */
    public int updatePayment(Payment payment) {
        Connection con = null;
        try {
            // Create connection
            con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Start transaction
            con.setAutoCommit(false);
            
            // Prepare statement
            String sql = "UPDATE payments SET payment_id = ?, invoice_id = ?, amount = ?, " +
                         "payment_date = ?, payment_method = ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, payment.getPaymentId());
            pst.setInt(2, payment.getInvoiceId());
            pst.setBigDecimal(3, payment.getAmount());
            pst.setDate(4, java.sql.Date.valueOf(payment.getPaymentDate()));
            pst.setString(5, payment.getPaymentMethod());
            pst.setInt(6, payment.getId());
            
            // Execute statement
            int rowsAffected = pst.executeUpdate();
            
            // Update invoice status
            sql = "SELECT * FROM invoices WHERE id = ?";
            PreparedStatement invPst = con.prepareStatement(sql);
            invPst.setInt(1, payment.getInvoiceId());
            
            ResultSet rs = invPst.executeQuery();
            if (rs.next()) {
                // Get total payments for this invoice
                sql = "SELECT SUM(amount) FROM payments WHERE invoice_id = ?";
                PreparedStatement sumPst = con.prepareStatement(sql);
                sumPst.setInt(1, payment.getInvoiceId());
                
                ResultSet sumRs = sumPst.executeQuery();
                if (sumRs.next()) {
                    java.math.BigDecimal totalPaid = sumRs.getBigDecimal(1);
                    java.math.BigDecimal invoiceAmount = rs.getBigDecimal("amount");
                    
                    // Update invoice status based on payment
                    sql = "UPDATE invoices SET status = ? WHERE id = ?";
                    PreparedStatement updatePst = con.prepareStatement(sql);
                    
                    if (totalPaid.compareTo(invoiceAmount) >= 0) {
                        updatePst.setString(1, Invoice.STATUS_PAID);
                    } else {
                        LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                        if (LocalDate.now().isAfter(dueDate)) {
                            updatePst.setString(1, Invoice.STATUS_OVERDUE);
                        } else {
                            updatePst.setString(1, Invoice.STATUS_ISSUED);
                        }
                    }
                    
                    updatePst.setInt(2, payment.getInvoiceId());
                    updatePst.executeUpdate();
                    updatePst.close();
                }
                sumRs.close();
                sumPst.close();
            }
            rs.close();
            invPst.close();
            
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
    
    /**
     * Finds a payment by ID
     * 
     * @param id The payment ID to search for
     * @return The payment if found, null otherwise
     */
    public Payment findPaymentById(int id) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM payments WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            
            ResultSet rs = pst.executeQuery();
            Payment payment = null;
            
            if (rs.next()) {
                payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setPaymentId(rs.getString("payment_id"));
                payment.setInvoiceId(rs.getInt("invoice_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                payment.setPaymentMethod(rs.getString("payment_method"));
            }
            
            con.close();
            return payment;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds a payment by payment ID
     * 
     * @param paymentId The payment ID to search for
     * @return The payment if found, null otherwise
     */
    public Payment findPaymentByPaymentId(String paymentId) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM payments WHERE payment_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, paymentId);
            
            ResultSet rs = pst.executeQuery();
            Payment payment = null;
            
            if (rs.next()) {
                payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setPaymentId(rs.getString("payment_id"));
                payment.setInvoiceId(rs.getInt("invoice_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                payment.setPaymentMethod(rs.getString("payment_method"));
            }
            
            con.close();
            return payment;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds payments by invoice ID
     * 
     * @param invoiceId The invoice ID to search for
     * @return List of matching payments
     */
    public List<Payment> findPaymentsByInvoiceId(int invoiceId) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM payments WHERE invoice_id = ? ORDER BY payment_date";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, invoiceId);
            
            ResultSet rs = pst.executeQuery();
            List<Payment> paymentList = new ArrayList<>();
            
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setPaymentId(rs.getString("payment_id"));
                payment.setInvoiceId(rs.getInt("invoice_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                payment.setPaymentMethod(rs.getString("payment_method"));
                paymentList.add(payment);
            }
            
            con.close();
            return paymentList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Finds payments by date range
     * 
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of matching payments
     */
    public List<Payment> findPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM payments WHERE payment_date BETWEEN ? AND ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setDate(1, java.sql.Date.valueOf(startDate));
            pst.setDate(2, java.sql.Date.valueOf(endDate));
            
            ResultSet rs = pst.executeQuery();
            List<Payment> paymentList = new ArrayList<>();
            
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setPaymentId(rs.getString("payment_id"));
                payment.setInvoiceId(rs.getInt("invoice_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                payment.setPaymentMethod(rs.getString("payment_method"));
                paymentList.add(payment);
            }
            
            con.close();
            return paymentList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Finds payments by payment method
     * 
     * @param paymentMethod The payment method to search for
     * @return List of matching payments
     */
    public List<Payment> findPaymentsByMethod(String paymentMethod) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM payments WHERE payment_method = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, paymentMethod);
            
            ResultSet rs = pst.executeQuery();
            List<Payment> paymentList = new ArrayList<>();
            
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setPaymentId(rs.getString("payment_id"));
                payment.setInvoiceId(rs.getInt("invoice_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                payment.setPaymentMethod(rs.getString("payment_method"));
                paymentList.add(payment);
            }
            
            con.close();
            return paymentList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Gets all payments
     * 
     * @return List of all payments
     */
    public List<Payment> findAllPayments() {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM payments";
            PreparedStatement pst = con.prepareStatement(sql);
            
            ResultSet rs = pst.executeQuery();
            List<Payment> paymentList = new ArrayList<>();
            
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setPaymentId(rs.getString("payment_id"));
                payment.setInvoiceId(rs.getInt("invoice_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                payment.setPaymentMethod(rs.getString("payment_method"));
                paymentList.add(payment);
            }
            
            con.close();
            return paymentList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Gets a payment with its invoice information
     * 
     * @param paymentId The ID of the payment
     * @return The payment with invoice loaded
     */
    public Payment getPaymentWithInvoice(int paymentId) {
        try {
            // First get the payment
            Payment payment = findPaymentById(paymentId);
            if (payment == null) {
                return null;
            }
            
            // Then get its invoice
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM invoices WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, payment.getInvoiceId());
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setId(rs.getInt("id"));
                invoice.setInvoiceNumber(rs.getString("invoice_number"));
                invoice.setOrderId(rs.getInt("order_id"));
                invoice.setIssueDate(rs.getDate("issue_date").toLocalDate());
                invoice.setDueDate(rs.getDate("due_date").toLocalDate());
                invoice.setAmount(rs.getBigDecimal("amount"));
                invoice.setStatus(rs.getString("status"));
                
                payment.setInvoice(invoice);
            }
            
            con.close();
            return payment;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Deletes a payment from the database
     * 
     * @param paymentId The ID of the payment to delete
     * @return Number of rows affected
     */
    public int deletePayment(int paymentId) {
        Connection con = null;
        try {
            // First get the payment to know which invoice to update
            Payment payment = findPaymentById(paymentId);
            if (payment == null) {
                return 0;
            }
            
            // Create connection
            con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Start transaction
            con.setAutoCommit(false);
            
            // Delete the payment
            String sql = "DELETE FROM payments WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, paymentId);
            
            int rowsAffected = pst.executeUpdate();
            
            // Update invoice status
            sql = "SELECT * FROM invoices WHERE id = ?";
            PreparedStatement invPst = con.prepareStatement(sql);
            invPst.setInt(1, payment.getInvoiceId());
            
            ResultSet rs = invPst.executeQuery();
            if (rs.next()) {
                // Get total remaining payments for this invoice
                sql = "SELECT SUM(amount) FROM payments WHERE invoice_id = ?";
                PreparedStatement sumPst = con.prepareStatement(sql);
                sumPst.setInt(1, payment.getInvoiceId());
                
                ResultSet sumRs = sumPst.executeQuery();
                
                sql = "UPDATE invoices SET status = ? WHERE id = ?";
                PreparedStatement updatePst = con.prepareStatement(sql);
                
                if (sumRs.next() && sumRs.getBigDecimal(1) != null) {
                    java.math.BigDecimal totalPaid = sumRs.getBigDecimal(1);
                    java.math.BigDecimal invoiceAmount = rs.getBigDecimal("amount");
                    
                    if (totalPaid.compareTo(invoiceAmount) >= 0) {
                        updatePst.setString(1, Invoice.STATUS_PAID);
                    } else {
                        LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                        if (LocalDate.now().isAfter(dueDate)) {
                            updatePst.setString(1, Invoice.STATUS_OVERDUE);
                        } else {
                            updatePst.setString(1, Invoice.STATUS_ISSUED);
                        }
                    }
                } else {
                    // No payments left
                    LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                    if (LocalDate.now().isAfter(dueDate)) {
                        updatePst.setString(1, Invoice.STATUS_OVERDUE);
                    } else {
                        updatePst.setString(1, Invoice.STATUS_ISSUED);
                    }
                }
                
                updatePst.setInt(2, payment.getInvoiceId());
                updatePst.executeUpdate();
            }
            
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