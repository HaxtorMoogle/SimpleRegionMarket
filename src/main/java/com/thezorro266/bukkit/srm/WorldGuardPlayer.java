package com.thezorro266.bukkit.srm;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.sk89q.wepif.PermissionsResolverManager;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;

public class WorldGuardPlayer extends LocalPlayer
{
    private OfflinePlayer player;

    WorldGuardPlayer(OfflinePlayer player)
    {
        if (player == null)
        {
            throw new IllegalArgumentException("Player must not be null");
        }
        
        this.player = player;
    }

    @Override
    public String getName()
    {
        return player.getName();
    }

    @Override
    public boolean hasGroup(String group)
    {
        return PermissionsResolverManager.getInstance().inGroup(player, group);
    }

    @Override
    public Vector getPosition()
    {
        if (player.isOnline())
        {
            org.bukkit.Location loc = player.getPlayer().getLocation();
            return new Vector(loc.getX(), loc.getY(), loc.getZ());
        }
        else
        {
            SimpleRegionMarket.getInstance().getLogger().warning(LanguageSupport.instance.getString("worldguard.offlineplayer.getposition"));
            return new Vector(0, 0, 0);
        }
    }

    @Override
    public void kick(String msg)
    {
        if (player.isOnline())
        {
            player.getPlayer().kickPlayer(msg);
        }
        else
        {
            SimpleRegionMarket.getInstance().getLogger().warning(LanguageSupport.instance.getString("worldguard.offlineplayer.kick"));
        }
    }

    @Override
    public void ban(String msg)
    {
        if (player.isOnline())
        {
            player.getPlayer().setBanned(true);
            player.getPlayer().kickPlayer(msg);
        }
        else
        {
            SimpleRegionMarket.getInstance().getLogger().warning(LanguageSupport.instance.getString("worldguard.offlineplayer.ban"));
        }
    }

    @Override
    public void printRaw(String msg)
    {
        if (player.isOnline())
        {
            player.getPlayer().sendMessage(msg);
        }
        else
        {
            SimpleRegionMarket.getInstance().getLogger().warning(LanguageSupport.instance.getString("worldguard.offlineplayer.message"));
        }
    }

    @Override
    public String[] getGroups()
    {
        return PermissionsResolverManager.getInstance().getGroups(player);
    }

    @Override
    public boolean hasPermission(String perm)
    {
        if (player.isOnline())
        {
            return SimpleRegionMarket.getInstance().getWorldGuardManager().getWG().hasPermission(player.getPlayer(), perm);
        }
        else
        {
            SimpleRegionMarket.getInstance().getLogger().warning(LanguageSupport.instance.getString("worldguard.offlineplayer.getpermission"));
            return true;
        }
    }

    @Override
    public UUID getUniqueId()
    {
        // TODO Auto-generated method stub
        return player.getUniqueId();
    }

}