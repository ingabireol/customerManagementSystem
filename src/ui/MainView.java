package ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Main application frame that provides the foundation for the Business Management System UI.
 * Implements the modern UI design with top bar, side navigation, content area, and status bar.
 */
public class MainView extends JFrame {
    // UI Components
    private JPanel sideNavPanel;
    private JPanel contentPanel;
    private CardLayout contentCardLayout;
    private JLabel statusLabel;
    
    // Dashboard components
    private JPanel dashboardPanel;
    
    // Module panels
    private JPanel customersPanel;
    private JPanel productsPanel;
    private JPanel ordersPanel;
    private JPanel invoicesPanel;
    private JPanel suppliersPanel;
    
    // Constants
    private static final Color PRIMARY_COLOR = new Color(0x1976D2);
    private static final Color SECONDARY_COLOR = new Color(0xFF5722);
    private static final Color BACKGROUND_COLOR = new Color(0xF5F5F5);
    private static final Color DARK_GRAY = new Color(0x424242);
    private static final Color MEDIUM_GRAY = new Color(0x9E9E9E);
    private static final Color LIGHT_GRAY = new Color(0xF5F5F5);
    private static final Color SUCCESS_COLOR = new Color(0x4CAF50);
    private static final Color WARNING_COLOR = new Color(0xFFC107);
    private static final Color ERROR_COLOR = new Color(0xF44336);
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    private boolean isMenuExpanded = true;
    
    public MainView() {
        initializeUI();
    }
    
    private void initializeUI() {
        // Configure the main frame
        setTitle("Business Management System");
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        
        // Set up look and feel
        setupLookAndFeel();
        
        // Create main components
        JPanel topBar = createTopBar();
        sideNavPanel = createSideNav();
        JPanel contentWrapper = createContentPanel(); // This creates contentPanel with CardLayout
        JPanel statusBar = createStatusBar();
        
        // Add components to the frame
        add(topBar, BorderLayout.NORTH);
        add(sideNavPanel, BorderLayout.WEST);
        add(contentWrapper, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        
        // Set up responsive behavior
        setupResponsiveBehavior();
        
        // Set initial content - show dashboard
        contentCardLayout.first(contentPanel);
        
        // Display status message
        setStatusMessage("System ready");
    }
    
    private void setupLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
            // Set global font settings
            UIManager.put("Label.font", BODY_FONT);
            UIManager.put("Button.font", BODY_FONT);
            UIManager.put("TextField.font", BODY_FONT);
            UIManager.put("TextArea.font", BODY_FONT);
            UIManager.put("ComboBox.font", BODY_FONT);
            UIManager.put("Table.font", BODY_FONT);
            UIManager.put("TableHeader.font", HEADER_FONT);
            UIManager.put("TabbedPane.font", BODY_FONT);
            
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_COLOR);
        topBar.setPreferredSize(new Dimension(getWidth(), 50));
        topBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        // Application title
        JLabel titleLabel = new JLabel("Business Management System");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        topBar.add(titleLabel, BorderLayout.WEST);
        
        // Global actions (right side)
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setOpaque(false);
        
        // User info
        JLabel userLabel = new JLabel("Admin User");
        userLabel.setForeground(Color.WHITE);
        
        // Settings button
        JButton settingsButton = createIconButton("Settings");
        settingsButton.setForeground(Color.WHITE);
        
        // Logout button
        JButton logoutButton = createIconButton("Logout");
        logoutButton.setForeground(Color.WHITE);
        
        actionsPanel.add(userLabel);
        actionsPanel.add(settingsButton);
        actionsPanel.add(logoutButton);
        
        topBar.add(actionsPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private JPanel createSideNav() {
        JPanel sideNav = new JPanel();
        sideNav.setBackground(DARK_GRAY);
        sideNav.setPreferredSize(new Dimension(200, getHeight()));
        sideNav.setLayout(new BoxLayout(sideNav, BoxLayout.Y_AXIS));
        sideNav.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Create navigation buttons
        sideNav.add(createNavButton("Dashboard", true));
        sideNav.add(Box.createRigidArea(new Dimension(0, 10)));
        sideNav.add(createNavButton("Customers", false));
        sideNav.add(Box.createRigidArea(new Dimension(0, 10)));
        sideNav.add(createNavButton("Products", false));
        sideNav.add(Box.createRigidArea(new Dimension(0, 10)));
        sideNav.add(createNavButton("Orders", false));
        sideNav.add(Box.createRigidArea(new Dimension(0, 10)));
        sideNav.add(createNavButton("Invoices", false));
        sideNav.add(Box.createRigidArea(new Dimension(0, 10)));
        sideNav.add(createNavButton("Suppliers", false));
        
        // Add glue to push everything to the top
        sideNav.add(Box.createVerticalGlue());
        
        // Toggle theme button at bottom
        JButton themeToggleButton = createNavButton("Toggle Theme", false);
        themeToggleButton.addActionListener(e -> toggleTheme());
        sideNav.add(themeToggleButton);
        
        return sideNav;
    }
    
    private JButton createNavButton(String text, boolean isSelected) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setFocusPainted(false);
        
        if (isSelected) {
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(DARK_GRAY);
            button.setForeground(Color.WHITE);
            
            // Add hover effect
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(0x555555));
                }
                
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (!button.isSelected()) {
                        button.setBackground(DARK_GRAY);
                    }
                }
            });
        }
        
        // Add action to switch panels
        button.addActionListener(e -> {
            // Deselect all buttons
            for (Component comp : sideNavPanel.getComponents()) {
                if (comp instanceof JButton) {
                    comp.setBackground(DARK_GRAY);
                }
            }
            
            // Select this button
            button.setBackground(PRIMARY_COLOR);
            
            // Show the corresponding panel
            if (contentCardLayout != null && contentPanel != null) {
                contentCardLayout.show(contentPanel, text);
                setStatusMessage("Viewing " + text);
            }
        });
        
        return button;
    }
    
    private JPanel createContentPanel() {
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(BACKGROUND_COLOR);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Create card layout for switching between panels
        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        // Create module panels
        dashboardPanel = createDashboardPanel();
        customersPanel = createModulePanel("Customers");
        productsPanel = createModulePanel("Products");
        ordersPanel = createModulePanel("Orders");
        invoicesPanel = createModulePanel("Invoices");
        suppliersPanel = createModulePanel("Suppliers");
        
        // Add panels to card layout
        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(customersPanel, "Customers");
        contentPanel.add(productsPanel, "Products");
        contentPanel.add(ordersPanel, "Orders");
        contentPanel.add(invoicesPanel, "Invoices");
        contentPanel.add(suppliersPanel, "Suppliers");
        
        contentWrapper.add(contentPanel, BorderLayout.CENTER);
        
        return contentWrapper;
    }
    
    private JPanel createDashboardPanel() {
        return new DashboardPanel();
    }
    
    private JPanel createMetricCard(String title, String value, String change, Color indicatorColor) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        
        // Add a subtle border
        Border roundedBorder = new EmptyBorder(15, 15, 15, 15);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xE0E0E0), 1, true),
            roundedBorder
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(BODY_FONT);
        titleLabel.setForeground(MEDIUM_GRAY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        JLabel changeLabel = new JLabel(change);
        changeLabel.setFont(BODY_FONT);
        changeLabel.setForeground(indicatorColor);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(valueLabel);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(changeLabel, BorderLayout.WEST);
        
        card.add(topPanel, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createRecentOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Recent Orders");
        titleLabel.setFont(HEADER_FONT);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Sample table model
        String[] columns = {"Order ID", "Customer", "Date", "Amount", "Status"};
        Object[][] data = {
            {"ORD-2023-001", "John Smith", "2023-04-10", "$345.67", "Delivered"},
            {"ORD-2023-002", "Alice Johnson", "2023-04-09", "$127.80", "Processing"},
            {"ORD-2023-003", "Robert Davis", "2023-04-08", "$582.25", "Shipped"},
            {"ORD-2023-004", "Emma Wilson", "2023-04-07", "$210.50", "Delivered"},
            {"ORD-2023-005", "Michael Brown", "2023-04-06", "$78.99", "Processing"}
        };
        
        JTable table = new JTable(data, columns);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.getTableHeader().setBackground(LIGHT_GRAY);
        table.getTableHeader().setFont(HEADER_FONT);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton viewAllButton = new JButton("View All Orders");
        viewAllButton.setBackground(LIGHT_GRAY);
        viewAllButton.setForeground(DARK_GRAY);
        viewAllButton.setFocusPainted(false);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(viewAllButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLowStockPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Low Stock Items");
        titleLabel.setFont(HEADER_FONT);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Sample table model
        String[] columns = {"Product Code", "Name", "Current Stock", "Min. Stock"};
        Object[][] data = {
            {"PRD-00123", "USB Cable Type-C", 5, 15},
            {"PRD-00456", "Wireless Mouse", 3, 10},
            {"PRD-00789", "Bluetooth Speaker", 2, 8},
            {"PRD-00234", "HDMI Cable 2m", 4, 12},
            {"PRD-00567", "Power Bank 10000mAh", 1, 5}
        };
        
        JTable table = new JTable(data, columns);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.getTableHeader().setBackground(LIGHT_GRAY);
        table.getTableHeader().setFont(HEADER_FONT);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton restockButton = new JButton("Restock Items");
        restockButton.setBackground(WARNING_COLOR);
        restockButton.setForeground(Color.WHITE);
        restockButton.setFocusPainted(false);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(restockButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);
        
        panel.add(createActionButton("New Customer", SECONDARY_COLOR));
        panel.add(createActionButton("New Order", PRIMARY_COLOR));
        panel.add(createActionButton("Add Product", SUCCESS_COLOR));
        panel.add(createActionButton("Create Invoice", WARNING_COLOR));
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(BODY_FONT);
        button.setPreferredSize(new Dimension(150, 40));
        
        return button;
    }
    
    private JPanel createModulePanel(String moduleName) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Module header with title and actions
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(moduleName);
        titleLabel.setFont(TITLE_FONT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Search and action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        
        JTextField searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search " + moduleName.toLowerCase() + "...");
        
        JButton addButton = new JButton("Add New");
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        
        actionPanel.add(searchField);
        actionPanel.add(addButton);
        headerPanel.add(actionPanel, BorderLayout.EAST);
        
        // Main content - placeholder for now
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel placeholderLabel = new JLabel(moduleName + " content will be implemented here");
        placeholderLabel.setFont(BODY_FONT);
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(LIGHT_GRAY);
        statusBar.setPreferredSize(new Dimension(getWidth(), 25));
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        JLabel versionLabel = new JLabel("v1.0.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.add(versionLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private JButton createIconButton(String text) {
        JButton button = new JButton(text);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        
        return button;
    }
    
    private void setupResponsiveBehavior() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                if (width < 1000 && isMenuExpanded) {
                    collapseMenu();
                } else if (width >= 1000 && !isMenuExpanded) {
                    expandMenu();
                }
            }
        });
    }
    
    private void collapseMenu() {
        sideNavPanel.setPreferredSize(new Dimension(60, getHeight()));
        isMenuExpanded = false;
        
        // Update nav buttons to show only icons
        for (Component comp : sideNavPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setText("");
                button.setPreferredSize(new Dimension(40, 40));
            }
        }
        
        revalidate();
    }
    
    private void expandMenu() {
        sideNavPanel.setPreferredSize(new Dimension(200, getHeight()));
        isMenuExpanded = true;
        
        // Restore button text
        String[] buttonTexts = {"Dashboard", "Customers", "Products", "Orders", "Invoices", "Suppliers", "Toggle Theme"};
        int buttonIndex = 0;
        
        for (Component comp : sideNavPanel.getComponents()) {
            if (comp instanceof JButton && buttonIndex < buttonTexts.length) {
                JButton button = (JButton) comp;
                button.setText(buttonTexts[buttonIndex++]);
                button.setPreferredSize(null); // Reset to default
            }
        }
        
        revalidate();
    }
    
    private void toggleTheme() {
        // This would be replaced with actual theme switching when FlatLaf is implemented
        JOptionPane.showMessageDialog(this, "Theme toggle functionality would be implemented with FlatLaf library", 
                                     "Theme Toggle", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showPanel(String panelName) {
        if (contentPanel != null && contentCardLayout != null) {
            contentCardLayout.show(contentPanel, panelName);
            setStatusMessage("Viewing " + panelName);
        }
    }
    
    private void setStatusMessage(String message) {
        statusLabel.setText(message);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            mainView.setLocationRelativeTo(null);
            mainView.setVisible(true);
        });
    }
}