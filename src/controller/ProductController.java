package controller;

import dao.ProductDao;
import dao.SupplierDao;
import model.Product;
import model.Supplier;
import ui.DialogFactory;
import ui.product.ProductDetailsView;
import ui.product.ProductFormView;
import ui.product.ProductListView;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Controller for Product module operations.
 * Manages interactions between the product views and the data access layer.
 */
public class ProductController {
    // Data access
    private ProductDao productDao;
    private SupplierDao supplierDao;
    
    // Views
    private ProductListView listView;
    private Component parentComponent;
    
    /**
     * Constructor
     * 
     * @param parentComponent Parent component for dialogs
     */
    public ProductController(Component parentComponent) {
        this.productDao = new ProductDao();
        this.supplierDao = new SupplierDao();
        this.parentComponent = parentComponent;
        
        // Initialize the list view
        initializeListView();
    }
    
    /**
     * Gets the product list view
     * 
     * @return The product list view
     */
    public ProductListView getListView() {
        return listView;
    }
    
    /**
     * Initializes the product list view with callbacks
     */
    private void initializeListView() {
        listView = new ProductListView(new ProductListView.ProductListCallback() {
            @Override
            public void onAddProduct() {
                showAddProductDialog();
            }
            
            @Override
            public void onEditProduct(Product product) {
                showEditProductDialog(product);
            }
            
            @Override
            public void onDeleteProduct(Product product) {
                // Nothing needed here - handled within the list view
            }
            
            @Override
            public void onViewProductDetails(Product product) {
                showProductDetailsDialog(product);
            }
        });
    }
    
    /**
     * Shows the add product dialog
     */
    private void showAddProductDialog() {
        ProductFormView[] formView = new ProductFormView[1];
        formView[0] = new ProductFormView(new ProductFormView.FormSubmissionCallback() {
            @Override
            public void onSave(Product product) {
                // Update the list view with the new product
                listView.addProduct(product);
                
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(formView[0]);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            }
            
            @Override
            public void onCancel() {
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(formView[0]);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            }
        });
        
        // Create and show the dialog
        JDialog dialog = DialogFactory.createFormDialog(
            parentComponent,
            "Add New Product",
            formView[0],
            null, // onSave handled in callback
            null, // onCancel handled in callback
            600,
            550
        );
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows the edit product dialog
     * 
     * @param product The product to edit
     */
    private void showEditProductDialog(Product product) {
        ProductFormView[] formView = new ProductFormView[1];
//        final ProductFormView formView;
        formView[0] = new ProductFormView(product, new ProductFormView.FormSubmissionCallback() {
            @Override
            public void onSave(Product updatedProduct) {
                // Update the list view with the modified product
                listView.updateProduct(updatedProduct);
                
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(formView[0]);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            }
            
            @Override
            public void onCancel() {
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(formView[0]);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            }
        });
        
        // Create and show the dialog
        JDialog dialog = DialogFactory.createFormDialog(
            parentComponent,
            "Edit Product",
            formView[0],
            null, // onSave handled in callback
            null, // onCancel handled in callback
            600,
            550
        );
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows the product details dialog
     * 
     * @param product The product to view
     */
    private void showProductDetailsDialog(Product product) {
        // First, load the product with supplier information
        Product productWithSupplier = productDao.getProductWithSupplier(product.getId());
        if (productWithSupplier == null) {
            productWithSupplier = product;
        }
        
        // Create the details view with the product that has supplier information
        final ProductDetailsView[] detailsView = new ProductDetailsView[1];
        detailsView[0] = new ProductDetailsView(productWithSupplier, new ProductDetailsView.DetailsViewCallback() {
            @Override
            public void onEditProduct(Product productToEdit) {
                // Close the details dialog
                Window window = SwingUtilities.getWindowAncestor(detailsView[0]);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
                
                // Show the edit dialog
                showEditProductDialog(productToEdit);
            }
            
            @Override
            public void onClose() {
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(detailsView[0]);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            }
        });
        
        // Create and show the dialog
        JDialog dialog = DialogFactory.createDetailsDialog(
            parentComponent,
            "Product Details",
            detailsView[0],
            null, // onEdit handled in callback
            700,
            600
        );
        
        dialog.setVisible(true);
    }
    
    /**
     * Refreshes the product list with data from the database
     */
    public void refreshProductList() {
        try {
            List<Product> products = productDao.findAllProducts();
            listView.updateProducts(products);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                parentComponent,
                "Error loading products: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }
}