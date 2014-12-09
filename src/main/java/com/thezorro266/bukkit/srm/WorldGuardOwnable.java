package com.thezorro266.bukkit.srm;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.thezorro266.bukkit.srm.region.Region;

public class WorldGuardOwnable
{
   // private World world;
    private DefaultDomain owners;
    private DefaultDomain members;
  //  private WorldGuardPlugin worldguardPlugin;

    WorldGuardOwnable(Region region)
    {
      //  this.world = region.getWorld();
        this.owners = region.getWorldguardRegion().getOwners();
        this.members = region.getWorldguardRegion().getMembers();
        //worldguardPlugin = wg;
    }

    public void addMember(LocalPlayer localPlayer)
    {
        members.addPlayer(localPlayer);
    }

    public void addMemberGroup(String group)
    {
        members.addGroup(group);
    }

    public void addOwner(LocalPlayer localPlayer)
    {
        owners.addPlayer(localPlayer);
    }

    public void addOwnerGroup(String group)
    {
        owners.addGroup(group);
    }

    public void removeMember(LocalPlayer localPlayer)
    {
        members.removePlayer(localPlayer);
    }

    public void removeMemberGroup(String group)
    {
        members.removeGroup(group);
    }

    public void removeOwner(LocalPlayer localPlayer)
    {
        owners.removePlayer(localPlayer);
    }

    public void removeOwnerGroup(String group)
    {
        owners.removeGroup(group);
    }

    public void removeAllMembers()
    {
        members.removeAll();
    }

    public void removeAllOwners()
    {
        owners.removeAll();
    }

    public boolean isPlayerMember(LocalPlayer localPlayer)
    {
        return members.contains(localPlayer);
    }

    public boolean isPlayerOwner(LocalPlayer localPlayer)
    {
        return owners.contains(localPlayer);
    }

    public OfflinePlayer[] getMembers()
    {
        OfflinePlayer[] list;
        Set<String> playerSet = members.getPlayers();
        list = new OfflinePlayer[playerSet.size()];
        int index = 0;
        for (String playerName : playerSet)
        {
            list[index] = Bukkit.getOfflinePlayer(playerName);
            ++index;
        }
        return list;
    }

    public OfflinePlayer[] getOwners()
    {
        OfflinePlayer[] list;
        Set<String> playerSet = owners.getPlayers();
        list = new OfflinePlayer[playerSet.size()];
        int index = 0;
        for (String playerName : playerSet)
        {
            list[index] = Bukkit.getOfflinePlayer(playerName);
            ++index;
        }
        return list;
    }

   /*public void saveChanges() throws StorageException
    {
        worldguardPlugin.getRegionManager(world).save();
    }*/
}
