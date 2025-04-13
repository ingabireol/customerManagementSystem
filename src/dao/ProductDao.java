package dao;

import java.math.BigDecimal;
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
 * Data Access Object for Product operations.
 */
public class ProductDao {
    private String db_url = "jdbc:postgresql://localhost:5432/business_db";
    private String db_username = "postgres";
    private String db_passwd = "078868";
    
    /**
     * Creates a new product in the database
     * 
     * @param product The product to create
     * @return Number of rows affected
     */
    public int createProduct(Product product) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Prepare statement
            String sql = "INSERT INTO products (product_code, name, description, price, stock_quantity, category, supplier_id) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pst.setString(1, product.getProductCode());
            pst.setString(2, product.getName());
            pst.setString(3, product.getDescription());
            pst.setBigDecimal(4, product.getPrice());
            pst.setInt(5, product.getStockQuantity());
            pst.setString(6, product.getCategory());
            pst.setInt(7, product.getSupplierId());
            
            // Execute statement
            int rowsAffected = pst.executeUpdate();
            
            // Get generated ID
            if (rowsAffected > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    product.setId(rs.getInt(1));
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
     * Updates an existing product in the database
     * 
     * @param product The product to update
     * @return Number of rows affected
     */
    public int updateProduct(Product product) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Prepare statement
            String sql = "UPDATE products SET product_code = ?, name = ?, description = ?, " +
                         "price = ?, stock_quantity = ?, category = ?, supplier_id = ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, product.getProductCode());
            pst.setString(2, product.getName());
            pst.setString(3, product.getDescription());
            pst.setBigDecimal(4, product.getPrice());
            pst.setInt(5, product.getStockQuantity());
            pst.setString(6, product.getCategory());
            pst.setInt(7, product.getSupplierId());
            pst.setInt(8, product.getId());
            
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
     * Updates the stock quantity of a product
     * 
     * @param productId The ID of the product
     * @param quantity The quantity to add (negative to remove)
     * @return Number of rows affected
     */
    public int updateProductStock(int productId, int quantity) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Prepare statement
            String sql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setInt(1, quantity);
            pst.setInt(2, productId);
            
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
     * Finds a product by ID
     * 
     * @param id The product ID to search for
     * @return The product if found, null otherwise
     */
    public Product findProductById(int id) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM products WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            
            ResultSet rs = pst.executeQuery();
            Product product = null;
            
            if (rs.next()) {
                product = new Product();
                product.setId(rs.getInt("id"));
                product.setProductCode(rs.getString("product_code"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                product.setCategory(rs.getString("category"));
                product.setSupplierId(rs.getInt("supplier_id"));
            }
            
            con.close();
            return product;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds a product by product code
     * 
     * @param productCode The product code to search for
     * @return The product if found, null otherwise
     */
    public Product findProductByCode(String productCode) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM products WHERE product_code = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, productCode);
            
            ResultSet rs = pst.executeQuery();
            Product product = null;
            
            if (rs.next()) {
                product = new Product();
                product.setId(rs.getInt("id"));
                product.setProductCode(rs.getString("product_code"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                product.setCategory(rs.getString("category"));
                product.setSupplierId(rs.getInt("supplier_id"));
            }
            
            con.close();
            return product;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds products by name
     * 
     * @param name The name to search for
     * @return List of matching products
     */
    public List<Product> findProductsByName(String name) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM products WHERE name LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            String searchName = "%" + name + "%";
            pst.setString(1, searchName);
            
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
                productList.add(product);
            }
            
            con.close();
            return productList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Finds products by category
     * 
     * @param category The category to search for
     * @return List of matching products
     */
    public List<Product> findProductsByCategory(String category) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM products WHERE category = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, category);
            
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
                productList.add(product);
            }
            
            con.close();
            return productList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Finds products by supplier ID
     * 
     * @param supplierId The supplier ID to search for
     * @return List of matching products
     */
    public List<Product> findProductsBySupplier(int supplierId) {
        try {
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
                productList.add(product);
            }
            
            con.close();
            return productList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Finds products with low stock below a threshold
     * 
     * @param threshold The stock threshold
     * @return List of products with stock below threshold
     */
    public List<Product> findLowStockProducts(int threshold) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM products WHERE stock_quantity < ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, threshold);
            
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
                productList.add(product);
            }
            
            con.close();
            return productList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Gets all products
     * 
     * @return List of all products
     */
    public List<Product> findAllProducts() {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM products";
            PreparedStatement pst = con.prepareStatement(sql);
            
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
                productList.add(product);
            }
            
            con.close();
            return productList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Gets a product with its supplier information
     * 
     * @param productId The ID of the product
     * @return The product with supplier loaded
     */
    public Product getProductWithSupplier(int productId) {
        try {
            // First get the product
            Product product = findProductById(productId);
            if (product == null) {
                return null;
            }
            
            // Then get its supplier
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM suppliers WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, product.getSupplierId());
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setId(rs.getInt("id"));
                supplier.setSupplierCode(rs.getString("supplier_code"));
                supplier.setName(rs.getString("name"));
                supplier.setContactPerson(rs.getString("contact_person"));
                supplier.setEmail(rs.getString("email"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setAddress(rs.getString("address"));
                
                product.setSupplier(supplier);
            }
            
            con.close();
            return product;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Deletes a product from the database
     * 
     * @param productId The ID of the product to delete
     * @return Number of rows affected
     */
    public int deleteProduct(int productId) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "DELETE FROM products WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, productId);
            
            int rowsAffected = pst.executeUpdate();
            con.close();
            return rowsAffected;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}