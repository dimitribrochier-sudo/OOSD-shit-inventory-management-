package app.inventory_management.views;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class AnalyticScreen extends JFrame {

    public AnalyticScreen() {
        setTitle("Rudimentary Sales Graph (Swing)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Get your data (Simulating your database results)
        // In a real app, you would run your SQL query here and populate this list
        List<Double> salesData = getSalesDataFromDatabase();

        // 2. Add the custom drawing panel
        add(new ChartPanel(salesData));

        setVisible(true);
    }

    // Simulate fetching data from your SQL table
    private List<Double> getSalesDataFromDatabase() {
        List<Double> data = new ArrayList<>();
        // Mimicking the totals from your screenshot: 90000, 1200, 90000, etc.
        data.add(90000.0);
        data.add(1200.0);
        data.add(90000.0);
        data.add(1200.0);
        data.add(450000.0);
        data.add(45000.0);
        data.add(135000.0);
        data.add(1000.0);
        return data;
    }

    // --- INNER CLASS: The Custom Graph Panel ---
    static class ChartPanel extends JPanel {
        private final List<Double> data;
        private final int padding = 50; // Space for labels

        public ChartPanel(List<Double> data) {
            this.data = data;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Enable anti-aliasing for smoother lines
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // 1. Draw Axes
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            // Y Axis
            g2.drawLine(padding, padding, padding, height - padding);
            // X Axis
            g2.drawLine(padding, height - padding, width - padding, height - padding);

            if (data.isEmpty()) return;

            // 2. Calculate Scale
            double maxVal = data.stream().max(Double::compare).orElse(1.0);
            double minVal = 0; // Start graph at 0

            // Available drawing space
            double graphWidth = width - 2 * padding;
            double graphHeight = height - 2 * padding;

            // 3. Draw the Line Graph
            g2.setColor(Color.BLUE);
            g2.setStroke(new BasicStroke(3));

            for (int i = 0; i < data.size() - 1; i++) {
                // Calculate X1, Y1 (Current point)
                int x1 = padding + (int) (i * (graphWidth / (data.size() - 1)));
                int y1 = height - padding - (int) ((data.get(i) / maxVal) * graphHeight);

                // Calculate X2, Y2 (Next point)
                int x2 = padding + (int) ((i + 1) * (graphWidth / (data.size() - 1)));
                int y2 = height - padding - (int) ((data.get(i + 1) / maxVal) * graphHeight);

                // Draw line segment
                g2.drawLine(x1, y1, x2, y2);

                // Draw dots at points
                g2.fillOval(x1 - 3, y1 - 3, 6, 6);
            }

            // Draw last dot
            int lastIndex = data.size() - 1;
            int lastX = padding + (int) (lastIndex * (graphWidth / (data.size() - 1)));
            int lastY = height - padding - (int) ((data.get(lastIndex) / maxVal) * graphHeight);
            g2.fillOval(lastX - 3, lastY - 3, 6, 6);

            // 4. Draw Labels (Optional rudimentary labels)
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.drawString("$" + (int)maxVal, 5, padding);
            g2.drawString("$0", 5, height - padding);
            g2.drawString("Time (Order ID)", width / 2, height - 10);
        }
    }

    public static void main(String[] args) {
        // Run on Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(AnalyticScreen::new);
    }

}



