package com.thezorro266.bukkit.srm.region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.bukkit.srm.Sign;
import com.thezorro266.bukkit.srm.factories.SignFactory;
import com.thezorro266.bukkit.srm.helpers.Location;
import com.thezorro266.bukkit.srm.helpers.Options;
import com.thezorro266.bukkit.srm.templates.SignTemplate;
import com.thezorro266.bukkit.srm.templates.Template;

public class Region
{

    private Template template;

    private World world;

    private ProtectedRegion worldguardRegion;

    private ArrayList<Sign> signList;

    private Options options;

    public Region(Template template, World world, ProtectedRegion worldguardRegion)
    {
        if (template == null)
        {
            throw new IllegalArgumentException("Template must not be null");
        }
        if (world == null)
        {
            throw new IllegalArgumentException("World must not be null");
        }
        if (worldguardRegion == null)
        {
            throw new IllegalArgumentException("WorldGuard region must not be null");
        }

        this.template = template;
        this.world = world;
        this.worldguardRegion = worldguardRegion;
        signList = new ArrayList<Sign>();
        options = new Options();
    }

    public ArrayList<Sign> getSignList()
    {
        // TODO Auto-generated method stub
        return signList;
    }

    public Options getOptions()
    {
        // TODO Auto-generated method stub
        return options;
    }

    public String getName()
    {
        return worldguardRegion.getId();
    }

    public Sign addBlockAsSign(Block block)
    {
        if (SignFactory.instance.isSign(block))
        {
            org.bukkit.material.Sign signMat = (org.bukkit.material.Sign) block.getState().getData();
            return SignFactory.instance.createSign(this, Location.fromBlock(block), block.getType().equals(Material.WALL_SIGN), signMat.getFacing());
        }
        return null;
    }

    public void updateSigns()
    {
        for (Sign sign : signList)
        {
            template.updateSign(sign);
        }
    }

    public HashMap<String, String> getReplacementMap()
    {
        if (!(template instanceof SignTemplate))
        {
            throw new IllegalStateException(String.format("Template '%s' is not a sign template", template.getId())); // NON-NLS
        }

        HashMap<String, String> replacementMap = new HashMap<String, String>();
        replacementMap.put("region", getName());
        replacementMap.put("world", world.getName());
        if (getWorldguardRegion() instanceof ProtectedCuboidRegion)
        {
            replacementMap.put("x", Integer.toString(Math.abs((int) worldguardRegion.getMaximumPoint().getX() - (int) (worldguardRegion.getMinimumPoint().getX() - 1))));
            replacementMap.put("y", Integer.toString(Math.abs((int) worldguardRegion.getMaximumPoint().getY() - (int) (worldguardRegion.getMinimumPoint().getY() - 1))));
            replacementMap.put("z", Integer.toString(Math.abs((int) worldguardRegion.getMaximumPoint().getZ() - (int) (worldguardRegion.getMinimumPoint().getZ() - 1))));
        }

        ((SignTemplate) template).replacementMap(this, replacementMap);

        return replacementMap;
    }

    public void saveToConfiguration(Configuration config, String path)
    {
        config.set(path + "template_id", template.getId());
        config.set(path + "world", world.getName());
        config.set(path + "worldguard_region", worldguardRegion.getId());
        saveOptions(config, path + "options.");

        int signCount = 0;
        for (Sign sign : signList)
        {
            sign.saveToConfiguration(config, String.format("signs.%d.", signCount));
            ++signCount;
        }
    }

    private void saveOptions(Configuration config, String path)
    {
        synchronized (options)
        {
            for (Entry<String, Object> optionEntry : options)
            {
                config.set(path + optionEntry.getKey(), optionEntry.getValue());
            }
        }
    }

    @Override
    public String toString()
    {
        return String.format("Region[%s,w:%s,t:%s]", getName(), world.getName(), template.toString());
    }

    public World getWorld()
    {
        // TODO Auto-generated method stub
        return world;
    }

    public Template getTemplate()
    {
        // TODO Auto-generated method stub
        return template;
    }

    public ProtectedRegion getWorldguardRegion()
    {
        // TODO Auto-generated method stub
        return worldguardRegion;
    }
}
