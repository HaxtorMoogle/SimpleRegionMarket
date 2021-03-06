/*
 * SimpleRegionMarket
 * Copyright (C) 2014  theZorro266 <http://www.thezorro266.com>
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.thezorro266.bukkit.srm;


import java.io.IOException;
import java.text.Format;
import java.text.MessageFormat;
import java.util.logging.Level;

import com.moogle.MoogleGenericException;
import com.thezorro266.bukkit.srm.hooks.*;

import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;

import com.thezorro266.bukkit.srm.exceptions.ContentLoadException;
import com.thezorro266.bukkit.srm.exceptions.TemplateFormatException;
import com.thezorro266.bukkit.srm.factories.RegionFactory;
import com.thezorro266.bukkit.srm.helpers.LocationSignHelper;
import com.thezorro266.bukkit.srm.helpers.WorldHelper;
import com.thezorro266.bukkit.srm.templates.Template;
import com.thezorro266.bukkit.srm.templates.interfaces.TimedTemplate;

public class SimpleRegionMarket extends JavaPlugin
{
    private static final boolean PRINT_STACKTRACE = false;
    public static final String SRM_COMMAND = "regionmarket"; // NON-NLS

    private static SimpleRegionMarket thisPlugin;
    private LocationSignHelper locationSignHelper;
    private WorldHelper worldHelper;
    private TemplateManager templateManager;
    private WorldEditManager worldEditManager;
    private WorldGuardManager worldGuardManager;
    private PlayerManager playerManager;
    
    private Economy economy;
    private Permissions permissions;
    private VaultHook vaultHook;
    private boolean loading = true;
    private boolean disable = false;
    private Utils.TimeMeasurement tm;

    public SimpleRegionMarket()
    {
        super();
    }

    public static String getCopyright()
    {
        return "(c) 2013-2014  theZorro266 and SRM Team and HaxtorMoogle"; // NON-NLS
    }

    @Override
    public void onDisable()
    {
   
        
    }

    @Override
    public void onLoad()
    {
        this.templateManager = new TemplateManager();
        tm = new Utils.TimeMeasurement();
        {
            try
            {
                templateManager.load();
            }
            catch (TemplateFormatException e)
            {
                except(e);
                return;
            }
            catch (IOException e)
            {
                except(e);
                return;
            }
        }
        int templateCount;
        synchronized (templateManager.getTemplateList())
        {
            templateCount = templateManager.getTemplateList().size();
        }
        getLogger().info(MessageFormat.format(LanguageSupport.instance.getString("template.load.report"), templateCount, tm.diff()));
    }

    @Override
    public void onEnable()
    {
        this.setLocationSignHelper(new LocationSignHelper());
        this.setWorldHelper(new WorldHelper());
        
        this.worldEditManager = new WorldEditManager();
        this.worldGuardManager = new WorldGuardManager();
        this.vaultHook = new VaultHook();
        this.playerManager = new PlayerManager();
        setPlugin(this);
        if (!disable)
        {
            // Try to load dependencies
            try
            {
                vaultHook.load();
                worldGuardManager.load();
                worldEditManager.load();
            }
            catch (NullPointerException e)
            {
                except(e);
            }
        }

        // Check if the plugin should be disabled because of an exception
        if (disable)
        {
            getPluginLoader().disablePlugin(this);
            return;
        }
        loading = false;

        // TODO: permissions = new <insert permissions class with list support
        // here>();
        if (vaultHook.isVaultEnabled())
        {
            economy = new VaultEconomy();
            if (permissions == null)
            {
                permissions = new VaultPermissions();
            }
        }
        if (permissions == null)
        {
            permissions = new BasicPermissions();
        }
        if (economy == null)
        {
            economy = new NoEconomy();
        }

        // Load regions in templates
        Utils.TimeMeasurement tm = new Utils.TimeMeasurement();
        {
            try
            {
                templateManager.loadContent();
            }
            catch (ContentLoadException e)
            {
                except(e);
            }
        }

        getLogger().info(MessageFormat.format(LanguageSupport.instance.getString("region.load.report"), RegionFactory.instance.getRegionCount(), tm.diff()));

        // Register events
        playerManager.registerEvents();
        new EventListener();

        // Set command executor
        getCommand(SRM_COMMAND).setExecutor(new CommandHandler());

        // Set up async timer
        getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable()
        {
            @Override
            public void run()
            {
                Utils.TimeMeasurement tm = new Utils.TimeMeasurement();
                {
                    synchronized (templateManager.getTemplateList())
                    {
                        for (Template template : templateManager.getTemplateList())
                        {
                            if (template instanceof TimedTemplate)
                            {
                                ((TimedTemplate) template).schedule();
                            }
                        }
                    }
                }
                getLogger().log(Level.FINEST, MessageFormat.format(LanguageSupport.instance.getString("schedule.report"), tm.diff()));
            }
        }, 1200L, 1200L);
    }

    private void except(Throwable t)
    {
        disable = true;
        getLogger().severe(LanguageSupport.instance.getString("plugin.problem.unload"));
        printError(t);

        if (!loading)
        {
            getPluginLoader().disablePlugin(this);
        }
    }

    public void printError(Throwable t)
    {
        if (PRINT_STACKTRACE)
        {
            t.printStackTrace();
        }
        else
        {
            getLogger().severe(t.toString());
            for (StackTraceElement element : t.getStackTrace())
            {
                getLogger().severe("  " + element.toString());
            }

            Throwable cause = t.getCause();
            if (cause != null)
            {
                getLogger().severe("=== Caused by:"); // NON-NLS
                printError(cause);
            }
        }
    }

    public static SimpleRegionMarket getPlugin()
    {
        return thisPlugin;
    }

    // This should never be used lol
    public static void setPlugin(SimpleRegionMarket instance)
    {
        SimpleRegionMarket.thisPlugin = instance;
    }

    public WorldGuardManager getWorldGuardManager() throws MoogleGenericException
    {
        if(worldGuardManager != null)
        {
            return (worldGuardManager);
        }
        else
        {
            throw new MoogleGenericException("WorldGuardManager is Null (report error).");
        }
    }

    public LocationSignHelper getLocationSignHelper()
    {
        return locationSignHelper;
    }

    public void setLocationSignHelper(LocationSignHelper locationSignHelper)
    {
        this.locationSignHelper = locationSignHelper;
    }

    public WorldHelper getWorldHelper()
    {
        return worldHelper;
    }

    public void setWorldHelper(WorldHelper worldHelper)
    {
        this.worldHelper = worldHelper;
    }

    public Object getPlayerManager()
    {
        // TODO Auto-generated method stub
        return playerManager;
    }

    public Object getTemplateManager()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public static SimpleRegionMarket getInstance()
    {
        // TODO Auto-generated method stub
        return getPlugin();
    }

    public Object getWorldEditManager()
    {
        // TODO Auto-generated method stub
        return this.worldEditManager;
    }

    public Format getEconomy()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
