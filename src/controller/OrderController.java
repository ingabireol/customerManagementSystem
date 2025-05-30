package controller;

import dao.OrderDao;
import dao.InvoiceDao;
import model.Order;
import model.Customer;
import model.Invoice;
import ui.DialogFactory;
import ui.order.OrderDetailsView;
import ui.order.OrderFormView;
import ui.order.OrderListView;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for Order module operations.
 * Manages interactions between the order views and the data access layer.
 */
public class OrderController {
    // Data access
    private OrderDao orderDao;
    private InvoiceDao invoiceDao;
    
    // Views
    private OrderListView listView;
    private Component parentComponent;
    
    /**
     * Constructor
     * 
     * @param parentComponent Parent component for dialogs
     */
    public OrderController(Component parentComponent) {
        this.orderDao = new OrderDao();
        this.invoiceDao = new InvoiceDao();
        this.parentComponent = parentComponent;
        
        // Initialize the list view
        initializeListView();
    }
    
    /**
     * Gets the order list view
     * 
     * @return The order list view
     */
    public OrderListView getListView() {
        return listView;
    }
    
    /**
     * Initializes the order list view with callbacks
     */
    private void initializeListView() {
        listView = new OrderListView(new OrderListView.OrderListCallback() {
            @Override
            public void onAddOrder() {
                showAddOrderDialog();
            }
            
            @Override
            public void onEditOrder(Order order) {
                showEditOrderDialog(order);
            }
            
            @Override
            public void onDeleteOrder(Order order) {
                // Nothing needed here - handled within the list view
            }
            
            @Override
            public void onViewOrderDetails(Order order) {
                showOrderDetailsDialog(order);
            }
            
            @Override
            public void onCreateInvoice(Order order) {
                createInvoiceForOrder(order);
            }
        });
    }
    
    /**
     * Shows the add order dialog
     */
    private void showAddOrderDialog() {
        final OrderFormView[] formView = new OrderFormView[1];
        formView[0] = new OrderFormView(new OrderFormView.FormSubmissionCallback() {
            @Override
            public void onSave(Order order) {
                // Update the list view with the new order
                listView.addOrder(order);
                
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
            "Add New Order",
            formView[0],
            null, // onSave handled in callback
            null, // onCancel handled in callback
            800,
            600
        );
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows the edit order dialog
     * 
     * @param order The order to edit
     */
    private void showEditOrderDialog(Order order) {
        OrderFormView[] formView = new OrderFormView[1];
        formView[0] = new OrderFormView(order, new OrderFormView.FormSubmissionCallback() {
            @Override
            public void onSave(Order updatedOrder) {
                // Update the list view with the modified order
                listView.updateOrder(updatedOrder);
                
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
            "Edit Order",
            formView[0],
            null, // onSave handled in callback
            null, // onCancel handled in callback
            800,
            600
        );
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows the order details dialog
     * 
     * @param order The order to view
     */
    private void showOrderDetailsDialog(Order order) {
        // First, load the order with all details
        Order orderWithDetails = orderDao.getOrderWithDetails(order.getId());
        if (orderWithDetails == null) {
            orderWithDetails = order;
        }
        
        // Create the details view with the order that has complete information
        final OrderDetailsView[] detailsView = new OrderDetailsView[1];
        detailsView[0] = new OrderDetailsView(orderWithDetails, 
                new OrderDetailsView.DetailsViewCallback() {
                    @Override
                    public void onEditOrder(Order orderToEdit) {
                        // Close the details dialog
                        Window window = SwingUtilities.getWindowAncestor(detailsView[0]);
                        if (window instanceof JDialog) {
                            ((JDialog) window).dispose();
                        }
                        
                        // Show the edit dialog
                        showEditOrderDialog(orderToEdit);
                    }
                    
                    @Override
                    public void onViewCustomer(Customer customer) {
                        // Redirect to customer details view
                        JOptionPane.showMessageDialog(
                                parentComponent,
                                "Would navigate to Customer Details view for: " + customer.getFullName(),
                                "View Customer",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                    
                    @Override
                    public void onCreateInvoice(Order order) {
                        // Close the details dialog
                        Window window = SwingUtilities.getWindowAncestor(detailsView[0]);
                        if (window instanceof JDialog) {
                            ((JDialog) window).dispose();
                        }
                        
                        // Create invoice for the order
                        createInvoiceForOrder(order);
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
            "Order Details",
            detailsView[0],
            null, // onEdit handled in callback
            800,
            650
        );
        
        dialog.setVisible(true);
    }
    
    /**
     * Creates an invoice for the selected order
     * 
     * @param order The order to create an invoice for
     */
    private void createInvoiceForOrder(Order order) {
        // Only allow invoices for delivered orders
        if (!Order.STATUS_DELIVERED.equals(order.getStatus())) {
            JOptionPane.showMessageDialog(
                parentComponent,
                "Invoices can only be created for delivered orders.",
                "Cannot Create Invoice",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // Check if order already has invoices
        try {
            List<Invoice> existingInvoices = invoiceDao.findInvoicesByOrderId(order.getId());
            if (!existingInvoices.isEmpty()) {
                // Ask if user wants to create another invoice
                int response = JOptionPane.showConfirmDialog(
                    parentComponent,
                    "This order already has " + existingInvoices.size() + " invoice(s).\n" +
                    "Would you like to create another invoice?",
                    "Invoice Exists",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (response != JOptionPane.YES_OPTION) {
                    // Show the existing invoice instead
                    JOptionPane.showMessageDialog(
                        parentComponent,
                        "Would display existing invoice details here.",
                        "Existing Invoice",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
            }
            
            // Create a new invoice
            Invoice invoice = new Invoice();
            
            // Generate invoice number (could be more sophisticated in real app)
            String invoiceNumber = "INV-" + LocalDate.now().getYear() + "-" + 
                                 String.format("%04d", (int)(Math.random() * 10000));
            
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setOrder(order);
            invoice.setAmount(order.getTotalAmount());
            invoice.setIssueDate(LocalDate.now());
            invoice.setDueDate(LocalDate.now().plusDays(30)); // Due in 30 days
            invoice.setStatus(Invoice.STATUS_ISSUED);
            
            int result = invoiceDao.createInvoice(invoice);
            
            if (result > 0) {
                JOptionPane.showMessageDialog(
                    parentComponent,
                    "Invoice created successfully.\nInvoice Number: " + invoice.getInvoiceNumber(),
                    "Invoice Created",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                // Refresh the order details
                showOrderDetailsDialog(order);
            } else {
                JOptionPane.showMessageDialog(
                    parentComponent,
                    "Failed to create invoice. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                parentComponent,
                "Error creating invoice: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }
    
    /**
     * Refreshes the order list with data from the database
     */
    public void refreshOrderList() {
        try {
            List<Order> orders = orderDao.findAllOrders();
            listView.updateOrders(orders);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                parentComponent,
                "Error loading orders: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }
}