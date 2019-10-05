package net.runelite.client.plugins.inspector;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.Player;
import net.runelite.api.events.PlayerMenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@PluginDescriptor(
        name = "Inspector",
        description = "Add an Inspect option to other players.",
        tags = {"panel", "players"},
        loadWhenOutdated = true
)

@Slf4j
public class InspectorPlugin extends Plugin
{
    private static final String INSPECT = "Inspect";

    @Inject
    @Nullable
    private Client client;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private Provider<MenuManager> menuManager;

    private NavigationButton navButton;
    private InspectorPanel inspectorPanel;

    @Provides
    InspectorConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(InspectorConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        inspectorPanel = injector.getInstance(InspectorPanel.class);
        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "inspector_toolbar.png");

        navButton = NavigationButton.builder()
                .tooltip("Inspect")
                .icon(icon)
                .priority(9)
                .panel(inspectorPanel)
                .build();

        clientToolbar.addNavigation(navButton);

        if (client != null)
        {
            menuManager.get().addPlayerMenuItem(INSPECT);
        }
    }

    @Override
    protected void shutDown() throws Exception
    {
        clientToolbar.removeNavigation(navButton);
        if (client != null)
        {
            menuManager.get().removePlayerMenuItem(INSPECT);
        }
    }

    @Subscribe
    public void onPlayerMenuOptionClicked(PlayerMenuOptionClicked event)
    {
        if (event.getMenuOption().equals(INSPECT))
        {
            inspectPlayer(Text.removeTags(event.getMenuTarget()));
        }
    }

    private Player findPlayer(String playerName) {
        List<Player> players = client.getPlayers();
        playerName = playerName.replace(" ", "_");

        for (Player player : players) {
            String pName = player.getName().replace(" ", "_");
            if (playerName.equals(pName)) {
                return player;
            }
        }
//        List<String> playerNames = players.stream().map(player -> player.getName()).collect(Collectors.toList());
//        log.debug("Possible players: {}", playerNames.toString());
        return null;
    }

    private void inspectPlayer(String playerName)
    {
        if (client == null) {
            return;
        }

        Player inspectedPlayer = findPlayer(playerName);

        if (inspectedPlayer == null) {
            log.warn("Inspected Player: {} is not in client.players", playerName);
            inspectorPanel.showNotFoundError();
            return;
        }

        int[] equipmentIds = inspectedPlayer.getPlayerComposition().getEquipmentIds();
        List<ItemComposition> items = new ArrayList<>();
        List<Integer> kits = new ArrayList<>();

        for (int id : equipmentIds) {
            if (id > 512) {
                items.add(client.getItemDefinition(id - 512));
            } else if (id > 256) {
                kits.add(id - 256);
            }
        }

        for (ItemComposition itemComp : items) {
            log.debug(itemComp.getName());
        }

        inspectorPanel.setInspectedItems(items);

        log.debug("Inspect Player Items: {}", items);
    }

}
