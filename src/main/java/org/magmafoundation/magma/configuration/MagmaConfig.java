/*
 * Magma Server
 * Copyright (C) 2019-2021.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.magmafoundation.magma.configuration;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.logging.Level;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.magmafoundation.magma.commands.MagmaCommand;
import org.magmafoundation.magma.configuration.value.Value;
import org.magmafoundation.magma.configuration.value.values.BooleanValue;
import org.magmafoundation.magma.configuration.value.values.IntValue;
import org.magmafoundation.magma.configuration.value.values.StringArrayValue;
import org.magmafoundation.magma.configuration.value.values.StringValue;

/**
 * MagmaConfig
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 19/08/2019 - 05:14 am
 */
public class MagmaConfig extends ConfigBase {

    public static MagmaConfig instance = new MagmaConfig();

    //============================Debug======================================
    public final BooleanValue debugPrintBukkitMatterials = new BooleanValue(this, "debug.debugPrintBukkitMatterials", false, "Prints the Forge Bukkit Materials");
    public final BooleanValue debugPrintBukkitBannerPatterns = new BooleanValue(this, "debug.debugPrintBukkitBannerPatterns", false, "Prints the Forge Bukkit Banner Patterns");
    public final BooleanValue debugPrintCommandNode = new BooleanValue(this, "debug.debugPrintCommandNode", false, "Prints out all Command Nodes for permissions");

    //============================Black List Mods=============================
    public final BooleanValue blacklistedModsEnable = new BooleanValue(this, "forge.blacklistedmods.enabled", false, "Enable blacklisting of mods");
    public final StringArrayValue blacklistedMods = new StringArrayValue(this, "forge.blacklistedmods.list", "", "A list of mods to blacklist");
    public final StringValue blacklistedModsKickMessage = new StringValue(this, "forge.blacklistedmods.kickmessage", "Please Remove Blacklisted Mods", "Mod Blacklist kick message");

    //=============================WORLD SETTINGS=============================
    public final IntValue expMergeMaxValue = new IntValue(this, "experience-merge-max-value", -1,
        "Instructs the server put a maximum value on experience orbs, preventing them all from merging down into 1 single orb.");
    public List<Integer> autoUnloadDimensions = Lists.newArrayList(13371337);

    //=============================FakePlayer SETTINGS========================
    public final StringArrayValue fakePlayerPermissions = new StringArrayValue(this, "fakeplayer.permissions", "", "A list of permissions that fake players should have");

    //=============================Misc SETTINGS==============================
    public final BooleanValue forgeBukkitPermissionHandlerEnable = new BooleanValue(this, "forge.bukkitPermissionHandler.enable", true, "Let's Bukkit permission plugins handle forge/modded commands");
    public final BooleanValue magmaAutoUpdater = new BooleanValue(this, "magma.auto-update", true, "Auto updates the Magma jar");
    public final BooleanValue overrideServerBrand = new BooleanValue(this, "magma.advanced.override-brand", false, "Enables overriding the brand string");
    public final StringValue serverBrand = new StringValue(this, "magma.advanced.override-brand-name", "Spigot", "Value to use for new brand string");

    public final StringValue serverBrandType = new StringValue(this, "magma.advanced.server-type", "FML", "Set to FML to show forge icon or BUKKIT to show bukkit icon (FML is default)");

    private final String HEADER = "This is the main configuration file for Magma.\n" +
        "\n" +
        "Site: https://magmafoundation.org\n" +
        "Discord: https://discord.gg/6rkqngA\n";


    public MagmaConfig() {
        super("magma.yml", "magma");
        init();
    }

    public static String getString(String s, String key, String defaultreturn) {
        if (s.contains(key)) {
            String string = s.substring(s.indexOf(key));
            String s1 = (string.substring(string.indexOf(": ") + 2));
            String[] ss = s1.split("\n");
            return ss[0].trim().replace("'", "").replace("\"", "");
        }
        return defaultreturn;
    }

    public static String getString(File f, String key, String defaultreturn) {
        try {
            String s = FileUtils.readFileToString(f, "UTF-8");
            if (s.contains(key)) {
                String string = s.substring(s.indexOf(key));
                String s1 = (string.substring(string.indexOf(": ") + 2));
                String[] ss = s1.split("\n");
                return ss[0].trim().replace("'", "").replace("\"", "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defaultreturn;
    }

    public void init() {
        for (Field f : this.getClass().getFields()) {
            if (Modifier.isFinal(f.getModifiers()) && Modifier.isPublic(f.getModifiers()) && !Modifier.isStatic(f.getModifiers())) {
                try {
                    Value value = (Value) f.get(this);
                    if (value == null) {
                        continue;
                    }
                    values.put(value.path, value);
                } catch (ClassCastException e) {
                } catch (Throwable t) {
                    System.out.println("[Magma] Failed to initialize a MagmaConfig values.");
                    t.printStackTrace();
                }
            }
        }
        load();
    }

    @Override
    protected void addCommands() {
        commands.put("magma", new MagmaCommand("magma"));
//        commands.put("tps", new TPSCommand("tps"));
    }

    @Override
    protected void load() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            StringBuilder header = new StringBuilder(HEADER + "\n");
            for (Value toggle : values.values()) {
                if (!toggle.description.equals("")) {
                    header.append("Value: ").append(toggle.path).append(" Default: ").append(toggle.key).append("   # ").append(toggle.description).append("\n");
                }

                config.addDefault(toggle.path, toggle.key);
                values.get(toggle.path).setValues(config.getString(toggle.path));
            }
            version = getInt("config-version", 2);
            set("config-version", 2);

            config.addDefault("forge.autoUnloadDimensions", new int[]{-1});
            this.autoUnloadDimensions = config.getIntegerList("forge.autoUnloadDimensions");

            config.options().header(header.toString());
            config.options().copyDefaults(true);

            this.save();
        } catch (Exception ex) {
            MinecraftServer.getServer().server.getLogger().log(Level.SEVERE, "Could not load " + this.configFile);
            ex.printStackTrace();
        }
    }
}
