package app.inventory_management.utils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class BarChartPanel extends JPanel {

    private Map<String, Integer> data;

    public BarChartPanel(Map<String, Integer> data) {
        this.data = data;
        setBorder(BorderFactory.createLineBorder(Color.black));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        int barWidth = 50;
        int gap = 30;
        int x = 50;

        int maxValue = data.values().stream().max(Integer::compare).orElse(1);

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            int value = entry.getValue();

            // Scale height
            int barHeight = (int) ((double) value / maxValue * (height - 100));

            // Draw bar
            g.setColor(Color.BLUE);
            g.fillRect(x, height - barHeight - 50, barWidth, barHeight);

            // Draw label (product)
            g.setColor(Color.BLACK);
            g.drawString(entry.getKey(), x, height - 30);

            // Draw value
            g.drawString(String.valueOf(value), x, height - barHeight - 60);

            //Draws Axis
            g.drawLine(40, height - 50, width - 20, height - 50); // X-axis
            g.drawLine(40, 20, 40, height - 50); // Y-axis

            x += barWidth + gap;
        }
    }
}
