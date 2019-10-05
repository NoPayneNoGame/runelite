package net.runelite.client.plugins.inspector;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("inspector")
public interface InspectorConfig extends Config
{
    @ConfigItem(
            position = 1,
            keyName = "enabled",
            name = "Enabled",
            description = "Enabled"
    )
    default boolean enabled() { return true; }
}
