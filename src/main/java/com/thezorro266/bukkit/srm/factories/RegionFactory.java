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

package com.thezorro266.bukkit.srm.factories;

import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.bukkit.srm.Sign;
import com.thezorro266.bukkit.srm.SimpleRegionMarket;
import com.thezorro266.bukkit.srm.exceptions.ContentLoadException;
import com.thezorro266.bukkit.srm.helpers.Location;
import com.thezorro266.bukkit.srm.region.Region;
import com.thezorro266.bukkit.srm.templates.Template;

public class RegionFactory
{
    public static final RegionFactory instance = new RegionFactory();

   
    private int regionCount = 0;

    RegionFactory()
    {
    }

    public static ProtectedRegion getProtectedRegionFromLocation(Location loc, String region)
    {
        ProtectedRegion protectedRegion = null;
        final RegionManager worldRegionManager = SimpleRegionMarket.getInstance().getWorldGuardManager().getWG().getRegionManager(loc.getWorld());
        if (region == null)
        {
            ApplicableRegionSet regionSet = worldRegionManager.getApplicableRegions(loc.getBukkitLocation());
            if (regionSet.size() == 1)
            {
                protectedRegion = regionSet.iterator().next();
            }
            else
            {
                System.out.println("More than one region detected at " + loc.toString());
                // TODO Take child region or region with highest priority
            }
        }
        else
        {
            protectedRegion = worldRegionManager.getRegion(region);
        }
        return protectedRegion;
    }

    public Region createRegion(Template template, World world, ProtectedRegion worldguardRegion)
    {
        Region region = new Region(template, world, worldguardRegion);

        synchronized (template.getRegionList())
        {
            template.getRegionList().add(region);
        }
        SimpleRegionMarket.getInstance().getWorldHelper().putRegion(region, world);

        ++regionCount;

        return region;
    }

    public void destroyRegion(Region region)
    {
        {
            Sign[] signArray = new Sign[region.getSignList().size()];
            signArray = region.getSignList().toArray(signArray);
            for (Sign sign : signArray)
            {
                sign.clear();
                SignFactory.instance.destroySign(sign);
            }
        }

        synchronized (region.getTemplate().getRegionList())
        {
            region.getTemplate().getRegionList().remove(region);
        }

        --regionCount;
    }

    public void loadFromConfiguration(Configuration config, String path) throws ContentLoadException
    {
        Template template = SimpleRegionMarket.getInstance().getTemplateManager().getTemplateFromId(config.getString(path + "template_id"));
        World world = Bukkit.getWorld(config.getString(path + "world"));
        ProtectedRegion worldguardRegion = SimpleRegionMarket.getInstance().getWorldGuardManager().getProtectedRegion(world, config.getString(path + "worldguard_region"));

        Region region;
        try
        {
            region = createRegion(template, world, worldguardRegion);
        }
        catch (IllegalArgumentException e)
        {
            throw new ContentLoadException("Could not create region", e);
        }

        // Check if there are options
        if (config.isSet(path + "options"))
        {
            // Set region options from values from options path
            Set<Entry<String, Object>> optionEntrySet = config.getConfigurationSection(path + "options").getValues(true).entrySet();
            for (Entry<String, Object> optionEntry : optionEntrySet)
            {
                if (!(optionEntry.getValue() instanceof ConfigurationSection))
                {
                    region.getOptions().set(optionEntry.getKey(), optionEntry.getValue());
                }
            }
        }

        ConfigurationSection signSection = config.getConfigurationSection(path + "signs");
        if (signSection != null)
        {
            for (String signKey : signSection.getKeys(false))
            {
                try
                {
                    SignFactory.instance.loadFromConfiguration(config, region, path + String.format("signs.%s.", signKey));
                }
                catch (IllegalArgumentException e)
                {
                    throw new ContentLoadException("Could not create sign " + signKey, e);
                }
            }
        }
    }

    public int getRegionCount()
    {
        // TODO Auto-generated method stub
        return regionCount;
    }
}
