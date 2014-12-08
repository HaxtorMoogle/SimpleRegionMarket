/*
 * SimpleRegionMarket 
 * was Copyright (C) 2014  theZorro266 <http://www.thezorro266.com>
 * Now is By HaxtorMoogle (C) 2014 I don forked your shit
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

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;

import com.moogle.MoogleGenericException;
import com.sk89q.wepif.PermissionsResolverManager;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.bukkit.srm.factories.RegionFactory;
import com.thezorro266.bukkit.srm.region.Region;

public class WorldGuardManager
{

    private WorldGuardPlugin worldguardPlugin;
    private WeakHashMap<Region, WeakReference<WorldGuardOwnable>> ownableMap = new WeakHashMap<Region, WeakReference<WorldGuardOwnable>>();

    public WorldGuardOwnable getOwnable(Region region)
    {
        WorldGuardOwnable wgo;
        if (ownableMap.containsKey(region))
        {
            WeakReference<WorldGuardOwnable> wr = ownableMap.get(region);
            wgo = wr.get();
            if (wgo != null)
            {
                return wgo;
            }
        }

        wgo = new WorldGuardOwnable(region);
        ownableMap.put(region, new WeakReference<WorldGuardOwnable>(wgo));
        return wgo;
    }
    public WorldGuardManager ()
    {
        
    }

    public void load()
    {
        try
        {
            this.getWorldGuard();
        }
        catch (MoogleGenericException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public WorldGuardPlugin getWG()
    {
        return worldguardPlugin;
    }

    /**
     * Gets a copy of the WorldGuard plugin.
     * 
     * @return
     * @throws MoogleGenericException
     */
    private WorldGuardPlugin getWorldGuard() throws MoogleGenericException
    {
        Plugin worldGuard = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (worldGuard == null || !(worldGuard instanceof WorldGuardPlugin))
        {
            throw new MoogleGenericException("WorldGuard does not appear to be installed.");
        }
        if (worldGuard instanceof WorldGuardPlugin)
        {
            return (WorldGuardPlugin) worldGuard;
        }
        else
        {
            throw new MoogleGenericException("WorldGuard detection failed (report error).");
        }

    }

    public ProtectedRegion getProtectedRegion(World world, String region)
    {
        if (world != null)
        {
            return worldguardPlugin.getRegionManager(world).getRegion(region);
        }
        return null;
    }

    public LocalPlayer wrapPlayer(OfflinePlayer player)
    {
        if (player != null)
        {
            return new WorldGuardPlayer(player);
        }
        return null;
    }

    public LocalPlayer wrapPlayer(Player player)
    {
        if (player != null)
        {
            return worldguardPlugin.wrapPlayer(player);
        }
        return null;
    }

}
