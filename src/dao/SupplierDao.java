package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Product;
import model.Supplier;

/**
 * Data Access Object for Supplier operations.
 */
public class SupplierDao {
    private String db_url = "jdbc:postgresql://localhost:5432/business_db";
    private String db_username = "postgres";
    private String db_passwd = "078868";
    
    /**
     * Creates a new supplier in the database
     * 
     * @param supplier The supplier to create
     * @return Number of rows affected
     */
    public int createSupplier(Supplier supplier) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Prepare statement
            String sql = "INSERT INTO suppliers (supplier_code, name, contact_person, email, phone, address) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pst.setString(1, supplier.getSupplierCode());
            pst.setString(2, supplier.getName());
            pst.setString(3, supplier.getContactPerson());
            pst.setString(4, supplier.getEmail());
            pst.setString(5, supplier.getPhone());
            pst.setString(6, supplier.getAddress());
            
            // Execute statement
            int rowsAffected = pst.executeUpdate();
            
            // Get generated ID
            if (rowsAffected > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    supplier.setId(rs.getInt(1));
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
     * Updates an existing supplier in the database
     * 
     * @param supplier The supplier to update
     * @return Number of rows affected
     */
    public int updateSupplier(Supplier supplier) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Prepare statement
            String sql = "UPDATE suppliers SET supplier_code = ?, name = ?, contact_person = ?, " +
                         "email = ?, phone = ?, address = ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, supplier.getSupplierCode());
            pst.setString(2, supplier.getName());
            pst.setString(3, supplier.getContactPerson());
            pst.setString(4, supplier.getEmail());
            pst.setString(5, supplier.getPhone());
            pst.setString(6, supplier.getAddress());
            pst.setInt(7, supplier.getId());
            
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
     * Finds a supplier by ID
     * 
     * @param id The supplier ID to search for
     * @return The supplier if found, null otherwise
     */
    public Supplier findSupplierById(int id) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM suppliers WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            
            ResultSet rs = pst.executeQuery();
            Supplier supplier = null;
            
            if (rs.next()) {
                supplier = new Supplier();
                supplier.setId(rs.getInt("id"));
                supplier.setSupplierCode(rs.getString("supplier_code"));
                supplier.setName(rs.getString("name"));
                supplier.setContactPerson(rs.getString("contact_person"));
                supplier.setEmail(rs.getString("email"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setAddress(rs.getString("address"));
            }
            
            con.close();
            return supplier;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds a supplier by supplier code
     * 
     * @param supplierCode The supplier code to search for
     * @return The supplier if found, null otherwise
     */
    public Supplier findSupplierByCode(String supplierCode) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM suppliers WHERE supplier_code = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, supplierCode);
            
            ResultSet rs = pst.executeQuery();
            Supplier supplier = null;
            
            if (rs.next()) {
                supplier = new Supplier();
                supplier.setId(rs.getInt("id"));
                supplier.setSupplierCode(rs.getString("supplier_code"));
                supplier.setName(rs.getString("name"));
                supplier.setContactPerson(rs.getString("contact_person"));
                supplier.setEmail(rs.getString("email"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setAddress(rs.getString("address"));
            }
            
            con.close();
            return supplier;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds suppliers by name
     * 
     * @param name The name to search for
     * @return List of matching suppliers
     */
    public List<Supplier> findSuppliersByName(String name) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM suppliers WHERE name LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            String searchName = "%" + name + "%";
            pst.setString(1, searchName);
            
            ResultSet rs = pst.executeQuery();
            List<Supplier> supplierList = new ArrayList<>();
            
            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setId(rs.getInt("id"));
                supplier.setSupplierCode(rs.getString("supplier_code"));
                supplier.setName(rs.getString("name"));
                supplier.setContactPerson(rs.getString("contact_person"));
                supplier.setEmail(rs.getString("email"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setAddress(rs.getString("address"));
                supplierList.add(supplier);
            }
            
            con.close();
            return supplierList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Gets all suppliers
     * 
     * @return List of all suppliers
     */
    public List<Supplier> findAllSuppliers() {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM suppliers";
            PreparedStatement pst = con.prepareStatement(sql);
            
            ResultSet rs = pst.executeQuery();
            List<Supplier> supplierList = new ArrayList<>();
            
            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setId(rs.getInt("id"));
                supplier.setSupplierCode(rs.getString("supplier_code"));
                supplier.setName(rs.getString("name"));
                supplier.setContactPerson(rs.getString("contact_person"));
                supplier.setEmail(rs.getString("email"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setAddress(rs.getString("address"));
                supplierList.add(supplier);
            }
            
            con.close();
            return supplierList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Gets a supplier with all their products
     * 
     * @param supplierId The ID of the supplier
     * @return The supplier with products loaded
     */
    public Supplier getSupplierWithProducts(int supplierId) {
        try {
            // First get the supplier
            Supplier supplier = findSupplierById(supplierId);
            if (supplier == null) {
                return null;
            }
            
            // Then get their products
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM products WHERE supplier_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, supplierId);
            
            ResultSet rs = pst.executeQuery();
            List<Product> productList = new ArrayList<>();
            
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setProductCode(rs.getString("product_code"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                product.setCategory(rs.getString("category"));
                product.setSupplierId(rs.getInt("supplier_id"));
                product.setSupplier(supplier);
                
                productList.add(product);
            }
            
            supplier.setProducts(productList);
            con.close();
            return supplier;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Deletes a supplier from the database
     * 
     * @param supplierId The ID of the supplier to delete
     * @return Number of rows affected
     */
    public int deleteSupplier(int supplierId) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "DELETE FROM suppliers WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, supplierId);
            
            int rowsAffected = pst.executeUpdate();
            con.close();
            return rowsAffected;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}