package controller;

import dao.CustomerDao;
import model.Customer;
import ui.DialogFactory;
import ui.customer.CustomerDetailsView;
import ui.customer.CustomerFormView;
import ui.customer.CustomerListView;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Controller for Customer module operations.
 * Manages interactions between the customer views and the data access layer.
 */
public class CustomerController {
    // Data access
    private CustomerDao customerDao;
    
    // Views
    private CustomerListView listView;
    private Component parentComponent;
    
    /**
     * Constructor
     * 
     * @param parentComponent Parent component for dialogs
     */
    public CustomerController(Component parentComponent) {
        this.customerDao = new CustomerDao();
        this.parentComponent = parentComponent;
        
        // Initialize the list view
        initializeListView();
    }
    
    /**
     * Gets the customer list view
     * 
     * @return The customer list view
     */
    public CustomerListView getListView() {
        return listView;
    }
    
    /**
     * Initializes the customer list view with callbacks
     */
    private void initializeListView() {
        listView = new CustomerListView(new CustomerListView.CustomerListCallback() {
            @Override
            public void onAddCustomer() {
                showAddCustomerDialog();
            }
            
            @Override
            public void onEditCustomer(Customer customer) {
                showEditCustomerDialog(customer);
            }
            
            @Override
            public void onDeleteCustomer(Customer customer) {
                // Nothing needed here - handled within the list view
            }
            
            @Override
            public void onViewCustomerDetails(Customer customer) {
                showCustomerDetailsDialog(customer);
            }
            
            @Override
            public void onViewCustomerOrders(Customer customer) {
                showCustomerWithOrders(customer);
            }
        });
    }
    
    /**
     * Shows the add customer dialog
     */
    private void showAddCustomerDialog() {
        final CustomerFormView[] formView = new CustomerFormView[1];
        formView[0] = new CustomerFormView(new CustomerFormView.FormSubmissionCallback() {
            @Override
            public void onSave(Customer customer) {
                // Update the list view with the new customer
                listView.addCustomer(customer);
                
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
            "Add New Customer",
            formView[0],
            null, // onSave handled in callback
            null, // onCancel handled in callback
            600,
            550
        );
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows the edit customer dialog
     * 
     * @param customer The customer to edit
     */
    private void showEditCustomerDialog(Customer customer) {
        CustomerFormView[] formView = new CustomerFormView[1];
        formView[0] = new CustomerFormView(customer, new CustomerFormView.FormSubmissionCallback() {
            @Override
            public void onSave(Customer updatedCustomer) {
                // Update the list view with the modified customer
                listView.updateCustomer(updatedCustomer);
                
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
            "Edit Customer",
            formView[0],
            null, // onSave handled in callback
            null, // onCancel handled in callback
            600,
            550
        );
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows the customer details dialog
     * 
     * @param customer The customer to view
     */
    private void showCustomerDetailsDialog(Customer customer) {
        // First, load the customer with orders information
        Customer customerWithOrders = customerDao.getCustomerWithOrders(customer.getId());
        if (customerWithOrders == null) {
            customerWithOrders = customer;
        }
        
        // Create the details view with the customer that has orders information
        final CustomerDetailsView[] detailsView = new CustomerDetailsView[1];
        detailsView[0] = new CustomerDetailsView(customerWithOrders, 
                new CustomerDetailsView.DetailsViewCallback() {
                    @Override
                    public void onEditCustomer(Customer customerToEdit) {
                        // Close the details dialog
                        Window window = SwingUtilities.getWindowAncestor(detailsView[0]);
                        if (window instanceof JDialog) {
                            ((JDialog) window).dispose();
                        }
                        
                        // Show the edit dialog
                        showEditCustomerDialog(customerToEdit);
                    }
                    
                    @Override
                    public void onViewOrders(Customer customerWithOrders) {
                        // This would navigate to the orders view for this customer
                        JOptionPane.showMessageDialog(
                                parentComponent,
                                "View Orders functionality would show all orders for " +
                                        customerWithOrders.getFullName(),
                                "View Orders",
                                JOptionPane.INFORMATION_MESSAGE
                        );
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
            "Customer Details",
            detailsView[0],
            null, // onEdit handled in callback
            700,
            600
        );
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows the customer with their order history
     * 
     * @param customer The customer to view with orders
     */
    private void showCustomerWithOrders(Customer customer) {
        try {
            // Load the customer with orders
            Customer customerWithOrders = customerDao.getCustomerWithOrders(customer.getId());
            
            if (customerWithOrders != null) {
                showCustomerDetailsDialog(customerWithOrders);
            } else {
                JOptionPane.showMessageDialog(
                    parentComponent,
                    "Failed to load customer orders.",
                    "Data Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                parentComponent,
                "Error loading customer orders: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }
    
    /**
     * Refreshes the customer list with data from the database
     */
    public void refreshCustomerList() {
        try {
            List<Customer> customers = customerDao.findAllCustomers();
            listView.updateCustomers(customers);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                parentComponent,
                "Error loading customers: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }
}