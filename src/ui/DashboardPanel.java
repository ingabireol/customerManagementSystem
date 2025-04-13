package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Dashboard panel that displays key business metrics and visualizations.
 * Demonstrates data visualization and organized information display.
 */
public class DashboardPanel extends JPanel {
    
    // Dashboard data
    private Map<String, Integer> salesByCategory;
    private Map<String, Integer> monthlySales;
    
    /**
     * Constructor
     */
    public DashboardPanel() {
        initializeDemoData();
        initializeUI();
    }
    
    /**
     * Initialize demo data for the dashboard
     */
    private void initializeDemoData() {
        // Sales by category
        salesByCategory = new HashMap<>();
        salesByCategory.put("Electronics", 45000);
        salesByCategory.put("Clothing", 28000);
        salesByCategory.put("Food & Beverages", 15000);
        salesByCategory.put("Home & Garden", 22000);
        salesByCategory.put("Office Supplies", 18000);
        
        // Monthly sales
        monthlySales = new HashMap<>();
        monthlySales.put("Jan", 32000);
        monthlySales.put("Feb", 35000);
        monthlySales.put("Mar", 40000);
        monthlySales.put("Apr", 38000);
        monthlySales.put("May", 42000);
        monthlySales.put("Jun", 48000);
        monthlySales.put("Jul", 52000);
        monthlySales.put("Aug", 56000);
        monthlySales.put("Sep", 45000);
        monthlySales.put("Oct", 49000);
        monthlySales.put("Nov", 58000);
        monthlySales.put("Dec", 62000);
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIFactory.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Add welcome header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create content panel with GridBagLayout for dashboard items
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Top row - metric cards
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.25;
        gbc.weighty = 0.2;
        contentPanel.add(createMetricCard("Total Sales", "$507,000", "↑ 12.5%", UIFactory.SUCCESS_COLOR), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(createMetricCard("Total Orders", "1,253", "↑ 8.3%", UIFactory.SUCCESS_COLOR), gbc);
        
        gbc.gridx = 2;
        contentPanel.add(createMetricCard("New Customers", "156", "↑ 5.2%", UIFactory.SUCCESS_COLOR), gbc);
        
        gbc.gridx = 3;
        contentPanel.add(createMetricCard("Pending Orders", "32", "↓ 2.1%", UIFactory.WARNING_COLOR), gbc);
        
        // Second row - Charts
        
        // Sales by Category chart
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.4;
        contentPanel.add(createSalesByCategoryPanel(), gbc);
        
        // Monthly Sales chart
        gbc.gridx = 2;
        contentPanel.add(createMonthlySalesPanel(), gbc);
        
        // Third row - Recent activities and tasks
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.4;
        contentPanel.add(createRecentActivitiesPanel(), gbc);
        
        // Tasks panel
        gbc.gridx = 2;
        contentPanel.add(createTasksPanel(), gbc);
        
        // Add content panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Creates the header panel with welcome message
     * 
     * @return The header panel
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UIFactory.LIGHT_GRAY, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel welcomeLabel = new JLabel("Welcome to the Business Management System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JLabel dateLabel = new JLabel("Today: April 13, 2025");
        dateLabel.setFont(UIFactory.BODY_FONT);
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panel.add(welcomeLabel, BorderLayout.WEST);
        panel.add(dateLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Creates a metric card for the dashboard
     * 
     * @param title Card title
     * @param value Main value to display
     * @param change Change indicator text
     * @param changeColor Color for change indicator
     * @return The metric card panel
     */
    private JPanel createMetricCard(String title, String value, String change, Color changeColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UIFactory.LIGHT_GRAY, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIFactory.BODY_FONT);
        titleLabel.setForeground(UIFactory.MEDIUM_GRAY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        JLabel changeLabel = new JLabel(change);
        changeLabel.setFont(UIFactory.BODY_FONT);
        changeLabel.setForeground(changeColor);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(changeLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    /**
     * Creates the sales by category chart panel
     * 
     * @return The chart panel
     */
    private JPanel createSalesByCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UIFactory.LIGHT_GRAY, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Chart title
        JLabel titleLabel = new JLabel("Sales by Category");
        titleLabel.setFont(UIFactory.HEADER_FONT);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Simple bar chart implementation
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int barWidth = (width - 100) / salesByCategory.size();
                int maxValue = 0;
                
                // Find max value for scaling
                for (Integer value : salesByCategory.values()) {
                    maxValue = Math.max(maxValue, value);
                }
                
                // Draw axes
                g2d.setColor(UIFactory.DARK_GRAY);
                g2d.drawLine(50, height - 50, width - 20, height - 50); // X-axis
                g2d.drawLine(50, 20, 50, height - 50); // Y-axis
                
                // Draw bars
                int x = 60;
                int colorIndex = 0;
                Color[] barColors = {
                    new Color(0x1976D2), new Color(0x2196F3), new Color(0x42A5F5), 
                    new Color(0x64B5F6), new Color(0x90CAF9)
                };
                
                for (Map.Entry<String, Integer> entry : salesByCategory.entrySet()) {
                    String category = entry.getKey();
                    int value = entry.getValue();
                    
                    // Calculate bar height
                    int barHeight = (int) ((value / (double) maxValue) * (height - 90));
                    
                    // Draw bar
                    g2d.setColor(barColors[colorIndex % barColors.length]);
                    g2d.fillRect(x, height - 50 - barHeight, barWidth - 10, barHeight);
                    
                    // Draw value
                    g2d.setColor(UIFactory.DARK_GRAY);
                    String valueText = "$" + (value / 1000) + "k";
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(valueText);
                    g2d.drawString(valueText, x + (barWidth - 10) / 2 - textWidth / 2, height - 60 - barHeight);
                    
                    // Draw category name
                    String shortCategory = category.length() > 10 ? category.substring(0, 10) + "..." : category;
                    textWidth = fm.stringWidth(shortCategory);
                    g2d.drawString(shortCategory, x + (barWidth - 10) / 2 - textWidth / 2, height - 30);
                    
                    x += barWidth;
                    colorIndex++;
                }
                
                g2d.dispose();
            }
        };
        
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBackground(Color.WHITE);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the monthly sales chart panel
     * 
     * @return The chart panel
     */
    private JPanel createMonthlySalesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UIFactory.LIGHT_GRAY, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Chart title
        JLabel titleLabel = new JLabel("Monthly Sales (2024)");
        titleLabel.setFont(UIFactory.HEADER_FONT);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Simple line chart implementation
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int xInterval = (width - 100) / (monthlySales.size() - 1);
                int maxValue = 0;
                
                // Find max value for scaling
                for (Integer value : monthlySales.values()) {
                    maxValue = Math.max(maxValue, value);
                }
                
                // Draw axes
                g2d.setColor(UIFactory.DARK_GRAY);
                g2d.drawLine(50, height - 50, width - 20, height - 50); // X-axis
                g2d.drawLine(50, 20, 50, height - 50); // Y-axis
                
                // Draw line chart
                int[] xPoints = new int[monthlySales.size()];
                int[] yPoints = new int[monthlySales.size()];
                
                int x = 50;
                int i = 0;
                
                for (Map.Entry<String, Integer> entry : monthlySales.entrySet()) {
                    String month = entry.getKey();
                    int value = entry.getValue();
                    
                    // Calculate point coordinates
                    xPoints[i] = x;
                    yPoints[i] = height - 50 - (int) ((value / (double) maxValue) * (height - 90));
                    
                    // Draw point
                    g2d.setColor(UIFactory.PRIMARY_COLOR);
                    g2d.fillOval(xPoints[i] - 3, yPoints[i] - 3, 6, 6);
                    
                    // Draw month label (for every other month to avoid crowding)
                    if (i % 2 == 0) {
                        g2d.setColor(UIFactory.DARK_GRAY);
                        g2d.drawString(month, x - 10, height - 30);
                    }
                    
                    x += xInterval;
                    i++;
                }
                
                // Draw the line connecting points
                g2d.setColor(UIFactory.PRIMARY_COLOR);
                g2d.setStroke(new BasicStroke(2f));
                
                for (i = 0; i < xPoints.length - 1; i++) {
                    g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
                }
                
                // Draw area under the line
                g2d.setColor(new Color(UIFactory.PRIMARY_COLOR.getRed(), 
                                       UIFactory.PRIMARY_COLOR.getGreen(), 
                                       UIFactory.PRIMARY_COLOR.getBlue(), 50));
                
                int[] areaXPoints = new int[xPoints.length + 2];
                int[] areaYPoints = new int[yPoints.length + 2];
                
                // Start at bottom left
                areaXPoints[0] = xPoints[0];
                areaYPoints[0] = height - 50;
                
                // Copy all points
                System.arraycopy(xPoints, 0, areaXPoints, 1, xPoints.length);
                System.arraycopy(yPoints, 0, areaYPoints, 1, yPoints.length);
                
                // End at bottom right
                areaXPoints[areaXPoints.length - 1] = xPoints[xPoints.length - 1];
                areaYPoints[areaYPoints.length - 1] = height - 50;
                
                g2d.fillPolygon(areaXPoints, areaYPoints, areaXPoints.length);
                
                g2d.dispose();
            }
        };
        
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBackground(Color.WHITE);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the recent activities panel
     * 
     * @return The activities panel
     */
    private JPanel createRecentActivitiesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UIFactory.LIGHT_GRAY, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Panel title
        JLabel titleLabel = new JLabel("Recent Activities");
        titleLabel.setFont(UIFactory.HEADER_FONT);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Activity list
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        
        // Add some sample activities
        listPanel.add(createActivityItem("New order #ORD-2025-0142 created", "2 hours ago", 
                                       UIFactory.PRIMARY_COLOR));
        listPanel.add(createActivityItem("Invoice #INV-2025-0089 paid", "3 hours ago", 
                                       UIFactory.SUCCESS_COLOR));
        listPanel.add(createActivityItem("New customer John Smith registered", "Yesterday", 
                                       UIFactory.PRIMARY_COLOR));
        listPanel.add(createActivityItem("Product 'Wireless Headphones' stock low", "Yesterday", 
                                       UIFactory.WARNING_COLOR));
        listPanel.add(createActivityItem("Order #ORD-2025-0138 status changed to 'Delivered'", "2 days ago", 
                                       UIFactory.SUCCESS_COLOR));
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // View all button
        JButton viewAllButton = UIFactory.createSecondaryButton("View All Activities");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(viewAllButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates an activity item for the recent activities list
     * 
     * @param activity Activity description
     * @param time Time of activity
     * @param indicatorColor Color indicator for activity type
     * @return The activity panel
     */
    private JPanel createActivityItem(String activity, String time, Color indicatorColor) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 0, 8, 0));
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        
        // Color indicator
        JPanel indicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(indicatorColor);
                g.fillRect(0, 0, 5, getHeight());
            }
        };
        indicator.setPreferredSize(new Dimension(5, 0));
        
        // Activity text
        JLabel activityLabel = new JLabel(activity);
        activityLabel.setFont(UIFactory.BODY_FONT);
        
        // Time text
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(UIFactory.SMALL_FONT);
        timeLabel.setForeground(UIFactory.MEDIUM_GRAY);
        
        JPanel textPanel = new JPanel(new BorderLayout(0, 3));
        textPanel.setOpaque(false);
        textPanel.add(activityLabel, BorderLayout.NORTH);
        textPanel.add(timeLabel, BorderLayout.SOUTH);
        
        panel.add(indicator, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        
        // Add separator
        JSeparator separator = new JSeparator();
        separator.setForeground(UIFactory.LIGHT_GRAY);
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapperPanel.add(panel, BorderLayout.CENTER);
        wrapperPanel.add(separator, BorderLayout.SOUTH);
        
        return wrapperPanel;
    }
    
    /**
     * Creates the tasks panel
     * 
     * @return The tasks panel
     */
    private JPanel createTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UIFactory.LIGHT_GRAY, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Panel title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Tasks");
        titleLabel.setFont(UIFactory.HEADER_FONT);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        JButton addTaskButton = new JButton("+");
        addTaskButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addTaskButton.setForeground(UIFactory.PRIMARY_COLOR);
        addTaskButton.setBorderPainted(false);
        addTaskButton.setContentAreaFilled(false);
        addTaskButton.setFocusPainted(false);
        addTaskButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        titlePanel.add(addTaskButton, BorderLayout.EAST);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Task list
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        
        // Add some sample tasks
        listPanel.add(createTaskItem("Review new customer applications", "Due: Today", false));
        listPanel.add(createTaskItem("Follow up on overdue invoices", "Due: Today", false));
        listPanel.add(createTaskItem("Update product catalog", "Due: Tomorrow", false));
        listPanel.add(createTaskItem("Generate monthly sales report", "Due: Apr 15", false));
        listPanel.add(createTaskItem("Order inventory for low stock items", "Due: Apr 16", false));
        listPanel.add(createTaskItem("Contact supplier for price updates", "Due: Apr 17", true));
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates a task item for the tasks list
     * 
     * @param task Task description
     * @param dueDate Due date text
     * @param completed Whether the task is completed
     * @return The task panel
     */
    private JPanel createTaskItem(String task, String dueDate, boolean completed) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 0, 8, 0));
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        
        // Checkbox
        JCheckBox checkbox = new JCheckBox();
        checkbox.setOpaque(false);
        checkbox.setSelected(completed);
        
        // Task text
        JLabel taskLabel = new JLabel(task);
        taskLabel.setFont(UIFactory.BODY_FONT);
        
        if (completed) {
            taskLabel.setForeground(UIFactory.MEDIUM_GRAY);
            // Add strikethrough effect
            taskLabel.setText("<html><strike>" + task + "</strike></html>");
        }
        
        // Due date text
        JLabel dueDateLabel = new JLabel(dueDate);
        dueDateLabel.setFont(UIFactory.SMALL_FONT);
        
        if (dueDate.contains("Today")) {
            dueDateLabel.setForeground(UIFactory.ERROR_COLOR);
        } else if (dueDate.contains("Tomorrow")) {
            dueDateLabel.setForeground(UIFactory.WARNING_COLOR);
        } else {
            dueDateLabel.setForeground(UIFactory.MEDIUM_GRAY);
        }
        
        JPanel textPanel = new JPanel(new BorderLayout(0, 3));
        textPanel.setOpaque(false);
        textPanel.add(taskLabel, BorderLayout.NORTH);
        textPanel.add(dueDateLabel, BorderLayout.SOUTH);
        
        panel.add(checkbox, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        
        // Add separator
        JSeparator separator = new JSeparator();
        separator.setForeground(UIFactory.LIGHT_GRAY);
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapperPanel.add(panel, BorderLayout.CENTER);
        wrapperPanel.add(separator, BorderLayout.SOUTH);
        
        // Add checkbox listener to update task appearance
        checkbox.addActionListener(e -> {
            boolean isSelected = checkbox.isSelected();
            if (isSelected) {
                taskLabel.setForeground(UIFactory.MEDIUM_GRAY);
                taskLabel.setText("<html><strike>" + task + "</strike></html>");
            } else {
                taskLabel.setForeground(UIFactory.DARK_GRAY);
                taskLabel.setText(task);
            }
        });
        
        return wrapperPanel;
    }
}