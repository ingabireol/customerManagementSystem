package controller;

import dao.SupplierDao;
import model.Supplier;
import ui.DialogFactory;
import ui.supplier.SupplierDetailsView;
import ui.supplier.SupplierFormView;
import ui.supplier.SupplierListView;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Controller for Supplier module operations.
 * Manages interactions between the supplier views and the data access layer.
 */
public class SupplierController {
    // Data access
    private SupplierDao supplierDao;
    
    // Views
    private SupplierListView listView;
    private Component parentComponent;
    
    /**
     * Constructor
     * 
     * @param parentComponent Parent component for dialogs
     */
    public SupplierController(Component parentComponent) {
        this.supplierDao = new SupplierDao();
        this.parentComponent = parentComponent;
        
        // Initialize the list view
        initializeListView();
    }
    
    /**
     * Gets the supplier list view
     * 
     * @return The supplier list view
     */
    public SupplierListView getListView() {
        return listView;
    }
    
    /**
     * Initializes the supplier list view with callbacks
     */
    private void initializeListView() {
        listView = new SupplierListView(new SupplierListView.SupplierListCallback() {
            @Override
            public void onAddSupplier() {
                showAddSupplierDialog();
            }
            
            @Override
            public void onEditSupplier(Supplier supplier) {
                showEditSupplierDialog(supplier);
            }
            
            @Override
            public void onDeleteSupplier(Supplier supplier) {
                // Nothing needed here - handled within the list view
            }
            
            @Override
            public void onViewSupplierDetails(Supplier supplier) {
                showSupplierDetailsDialog(supplier);
            }
            
            @Override
            public void onViewSupplierProducts(Supplier supplier) {
                showSupplierWithProducts(supplier);
            }
        });
    }
    
    /**
     * Shows the add supplier dialog
     */
    private void showAddSupplierDialog() {
        SupplierFormView formView = new SupplierFormView(new SupplierFormView.FormSubmissionCallback() {
            @Override
            public void onSave(Supplier supplier) {
                // Update the list view with the new supplier
                listView.addSupplier(supplier);
                
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(parentComponent);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            }
            
            @Override
            public void onCancel() {
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(parentComponent);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            }
        });
        
        // Create and show the dialog
        JDialog dialog = DialogFactory.createFormDialog(
            parentComponent,
            "Add New Supplier",
            formView,
            null, // onSave handled in callback
            null, // onCancel handled in callback
            600,
            500
        );
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows the edit supplier dialog
     * 
     * @param supplier The supplier to edit
     */
    private void showEditSupplierDialog(Supplier supplier) {
        SupplierFormView formView = new SupplierFormView(supplier, new SupplierFormView.FormSubmissionCallback() {
            @Override
            public void onSave(Supplier updatedSupplier) {
                // Update the list view with the modified supplier
                listView.updateSupplier(updatedSupplier);
                
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(parentComponent);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            }
            
            @Override
            public void onCancel() {
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(parentComponent);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            }
        });
        
        // Create and show the dialog
        JDialog dialog = DialogFactory.createFormDialog(
            parentComponent,
            "Edit Supplier",
            formView,
            null, // onSave handled in callback
            null, // onCancel handled in callback
            600,
            500
        );
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows the supplier details dialog
     * 
     * @param supplier The supplier to view
     */
    private void showSupplierDetailsDialog(Supplier supplier) {
        SupplierDetailsView detailsView = new SupplierDetailsView(supplier, new SupplierDetailsView.DetailsViewCallback() {
            @Override
            public void onEditSupplier(Supplier supplierToEdit) {
                // Close the details dialog
                Window window = SwingUtilities.getWindowAncestor(parentComponent);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
                
                // Show the edit dialog
                showEditSupplierDialog(supplierToEdit);
            }
            
            @Override
            public void onClose() {
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(parentComponent);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            }
        });
        
        // Create and show the dialog
        JDialog dialog = DialogFactory.createDetailsDialog(
            parentComponent,
            "Supplier Details",
            detailsView,
            null, // onEdit handled in callback
            700,
            600
        );
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows the supplier with its products
     * 
     * @param supplier The supplier to view with products
     */
    private void showSupplierWithProducts(Supplier supplier) {
        try {
            // Load the supplier with products
            Supplier supplierWithProducts = supplierDao.getSupplierWithProducts(supplier.getId());
            
            if (supplierWithProducts != null) {
                showSupplierDetailsDialog(supplierWithProducts);
            } else {
                JOptionPane.showMessageDialog(
                    parentComponent,
                    "Failed to load supplier products.",
                    "Data Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                parentComponent,
                "Error loading supplier products: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }
    
    /**
     * Refreshes the supplier list with data from the database
     */
    public void refreshSupplierList() {
        try {
            List<Supplier> suppliers = supplierDao.findAllSuppliers();
            listView.updateSuppliers(suppliers);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                parentComponent,
                "Error loading suppliers: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }
}