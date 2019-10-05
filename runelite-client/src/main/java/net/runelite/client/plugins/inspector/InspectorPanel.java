package net.runelite.client.plugins.inspector;

import net.runelite.api.ItemComposition;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InspectorPanel extends PluginPanel
{
    private final InspectorConfig config;

    private List<JLabel> itemLabels = new ArrayList<>();

    private final PluginErrorPanel errorPanel = new PluginErrorPanel();

    @Inject
    public InspectorPanel(InspectorConfig config) {
        super();
        this.config = config;

        setBorder(new EmptyBorder(18, 10, 0, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new BorderLayout());

        errorPanel.setContent("Inspector", "Could not find player in local players.");
        add(errorPanel);

        final JPanel layoutPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(layoutPanel, BoxLayout.Y_AXIS);
        layoutPanel.setLayout(boxLayout);
        add(layoutPanel, BorderLayout.NORTH);

        for (int i = 0; i < 11; i++) {
            JPanel itemPanel = new JPanel();
            itemPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            itemPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

            JLabel label = new JLabel();
            label.setFont(FontManager.getRunescapeFont());

            itemLabels.add(label);
            itemPanel.add(label);

            layoutPanel.add(itemPanel);
        }
    }

    public void setInspectedItems(List<ItemComposition> items) {
        clearItems();
        remove(errorPanel);

        for (int i = 0; i < items.size(); i++) {
            ItemComposition itemComp = items.get(i);
            JLabel label = itemLabels.get(i);
            String labelText = String.format("%s | %s", itemComp.getName(), itemComp.getPrice());
            label.setText(labelText);
        }
    }

    public void clearItems() {
        for (JLabel label : itemLabels) {
            label.setText("");
        }
    }

    public void showNotFoundError() {
        clearItems();
        add(errorPanel);
    }
}
