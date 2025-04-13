package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Customer;
import model.Order;
import model.OrderItem;
import model.Product;

/**
 * Data Access Object for Order operations.
 */
public class OrderDao {
    private String db_url = "jdbc:postgresql://localhost:5432/business_db";
    private String db_username = "postgres";
    private String db_passwd = "078868";
    
    /**
     * Creates a new order in the database
     * 
     * @param order The order to create
     * @return Number of rows affected
     */
    public int createOrder(Order order) {
        Connection con = null;
        try {
            // Create connection
            con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Start transaction
            con.setAutoCommit(false);
            
            // Prepare statement for order
            String sql = "INSERT INTO orders (order_id, customer_id, order_date, total_amount, status, payment_method) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pst.setString(1, order.getOrderId());
            pst.setInt(2, order.getCustomerId());
            pst.setDate(3, java.sql.Date.valueOf(order.getOrderDate()));
            pst.setBigDecimal(4, order.getTotalAmount());
            pst.setString(5, order.getStatus());
            pst.setString(6, order.getPaymentMethod());
            
            // Execute statement
            int rowsAffected = pst.executeUpdate();
            
            // Get generated ID
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                order.setId(rs.getInt(1));
            } else {
                throw new Exception("Failed to get order ID");
            }
            rs.close();
            
            // Insert order items if any
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                for (OrderItem item : order.getOrderItems()) {
                    sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) " +
                         "VALUES (?, ?, ?, ?)";
                    PreparedStatement itemPst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    
                    itemPst.setInt(1, order.getId());
                    itemPst.setInt(2, item.getProductId());
                    itemPst.setInt(3, item.getQuantity());
                    itemPst.setBigDecimal(4, item.getUnitPrice());
                    
                    itemPst.executeUpdate();
                    
                    // Get generated ID for the item
                    ResultSet itemRs = itemPst.getGeneratedKeys();
                    if (itemRs.next()) {
                        item.setId(itemRs.getInt(1));
                    }
                    itemRs.close();
                    itemPst.close();
                    
                    // Update product stock
                    sql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ?";
                    PreparedStatement stockPst = con.prepareStatement(sql);
                    stockPst.setInt(1, item.getQuantity());
                    stockPst.setInt(2, item.getProductId());
                    stockPst.executeUpdate();
                    stockPst.close();
                }
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
    
    /**
     * Updates an existing order in the database
     * 
     * @param order The order to update
     * @return Number of rows affected
     */
    public int updateOrder(Order order) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Prepare statement
            String sql = "UPDATE orders SET order_id = ?, customer_id = ?, order_date = ?, " +
                         "total_amount = ?, status = ?, payment_method = ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, order.getOrderId());
            pst.setInt(2, order.getCustomerId());
            pst.setDate(3, java.sql.Date.valueOf(order.getOrderDate()));
            pst.setBigDecimal(4, order.getTotalAmount());
            pst.setString(5, order.getStatus());
            pst.setString(6, order.getPaymentMethod());
            pst.setInt(7, order.getId());
            
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
     * Updates the status of an order
     * 
     * @param orderId The ID of the order
     * @param status The new status
     * @return Number of rows affected
     */
    public int updateOrderStatus(int orderId, String status) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Prepare statement
            String sql = "UPDATE orders SET status = ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, status);
            pst.setInt(2, orderId);
            
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
     * Finds an order by ID
     * 
     * @param id The order ID to search for
     * @return The order if found, null otherwise
     */
    public Order findOrderById(int id) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM orders WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            
            ResultSet rs = pst.executeQuery();
            Order order = null;
            
            if (rs.next()) {
                order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderId(rs.getString("order_id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setOrderDate(rs.getDate("order_date").toLocalDate());
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setPaymentMethod(rs.getString("payment_method"));
            }
            
            con.close();
            return order;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds an order by order ID
     * 
     * @param orderId The order ID to search for
     * @return The order if found, null otherwise
     */
    public Order findOrderByOrderId(String orderId) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM orders WHERE order_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, orderId);
            
            ResultSet rs = pst.executeQuery();
            Order order = null;
            
            if (rs.next()) {
                order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderId(rs.getString("order_id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setOrderDate(rs.getDate("order_date").toLocalDate());
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setPaymentMethod(rs.getString("payment_method"));
            }
            
            con.close();
            return order;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds orders by customer ID
     * 
     * @param customerId The customer ID to search for
     * @return List of matching orders
     */
    public List<Order> findOrdersByCustomer(int customerId) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_date DESC";
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
                orderList.add(order);
            }
            
            con.close();
            return orderList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Finds orders by status
     * 
     * @param status The status to search for
     * @return List of matching orders
     */
    public List<Order> findOrdersByStatus(String status) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM orders WHERE status = ? ORDER BY order_date DESC";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, status);
            
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
                orderList.add(order);
            }
            
            con.close();
            return orderList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Finds orders by date range
     * 
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of matching orders
     */
    public List<Order> findOrdersByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM orders WHERE order_date BETWEEN ? AND ? ORDER BY order_date DESC";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setDate(1, java.sql.Date.valueOf(startDate));
            pst.setDate(2, java.sql.Date.valueOf(endDate));
            
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
                orderList.add(order);
            }
            
            con.close();
            return orderList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Gets all orders
     * 
     * @return List of all orders
     */
    public List<Order> findAllOrders() {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM orders ORDER BY order_date DESC";
            PreparedStatement pst = con.prepareStatement(sql);
            
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
                orderList.add(order);
            }
            
            con.close();
            return orderList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Gets an order with all its items and customer information
     * 
     * @param orderId The ID of the order
     * @return The order with items and customer loaded
     */
    public Order getOrderWithDetails(int orderId) {
        try {
            // First get the order
            Order order = findOrderById(orderId);
            if (order == null) {
                return null;
            }
            
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Get the customer
            String sql = "SELECT * FROM customers WHERE id = ?";
            PreparedStatement custPst = con.prepareStatement(sql);
            custPst.setInt(1, order.getCustomerId());
            
            ResultSet custRs = custPst.executeQuery();
            if (custRs.next()) {
                Customer customer = new Customer();
                customer.setId(custRs.getInt("id"));
                customer.setCustomerId(custRs.getString("customer_id"));
                customer.setFirstName(custRs.getString("first_name"));
                customer.setLastName(custRs.getString("last_name"));
                customer.setEmail(custRs.getString("email"));
                customer.setPhone(custRs.getString("phone"));
                customer.setAddress(custRs.getString("address"));
                customer.setRegistrationDate(custRs.getDate("registration_date").toLocalDate());
                
                order.setCustomer(customer);
            }
            custRs.close();
            custPst.close();
            
            // Get the order items
            sql = "SELECT oi.*, p.name as product_name, p.price as product_price " +
                 "FROM order_items oi " +
                 "JOIN products p ON oi.product_id = p.id " +
                 "WHERE oi.order_id = ?";
            PreparedStatement itemPst = con.prepareStatement(sql);
            itemPst.setInt(1, orderId);
            
            ResultSet itemRs = itemPst.executeQuery();
            List<OrderItem> itemList = new ArrayList<>();
            
            while (itemRs.next()) {
                OrderItem item = new OrderItem();
                item.setId(itemRs.getInt("id"));
                item.setOrderId(itemRs.getInt("order_id"));
                item.setProductId(itemRs.getInt("product_id"));
                item.setQuantity(itemRs.getInt("quantity"));
                item.setUnitPrice(itemRs.getBigDecimal("unit_price"));
                
                // Create a simple product with basic info
                Product product = new Product();
                product.setId(item.getProductId());
                product.setName(itemRs.getString("product_name"));
                product.setPrice(itemRs.getBigDecimal("product_price"));
                
                item.setProduct(product);
                item.setOrder(order);
                
                itemList.add(item);
            }
            
            order.setOrderItems(itemList);
            
            con.close();
            return order;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Deletes an order from the database
     * 
     * @param orderId The ID of the order to delete
     * @return Number of rows affected
     */
    public int deleteOrder(int orderId) {
        Connection con = null;
        try {
            // Create connection
            con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Start transaction
            con.setAutoCommit(false);
            
            // First delete associated order items
            String sql = "DELETE FROM order_items WHERE order_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, orderId);
            pst.executeUpdate();
            
            // Then delete the order
            sql = "DELETE FROM orders WHERE id = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, orderId);
            
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