package com.thezorro266.bukkit.srm;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.Configuration;

import com.thezorro266.bukkit.srm.helpers.Location;
import com.thezorro266.bukkit.srm.helpers.Options;
import com.thezorro266.bukkit.srm.region.Region;

public class Sign
{
    // because mojoag will never increase the number of lines on a sign -_-
    public static final int SIGN_LINE_COUNT = 4;
    
    // I think this is the region the sign is for
    final Region region;
    
    // Location of the sign?
    final Location location;
    
    // Is the sign on a wall?
    final boolean isWallSign;
    
    // Dirrection the sign is faceing
    final BlockFace direction;
    
    // What ever the hell the options are...
    private final Options options;

    public Sign(Region region, Location location, boolean isWallSign, BlockFace direction)
    {
        if (region == null)
        {
            throw new IllegalArgumentException("Region must not be null");
        }
        if (location == null)
        {
            throw new IllegalArgumentException("Location must not be null");
        }
        if (direction == null)
        {
            throw new IllegalArgumentException("Direction must not be null");
        }

        this.region = region;
        this.location = new Location(location);
        this.isWallSign = isWallSign;
        this.direction = direction;
        options = new Options();
    }

    public Options getOptions()
    {
        // TODO Auto-generated method stub
        return options;
    }

    public void clear()
    {
        setContent(new String[SIGN_LINE_COUNT]);
    }

    public void setContent(String[] lines)
    {
        if (lines == null || lines.length != SIGN_LINE_COUNT)
        {
            clear();
            throw new IllegalArgumentException("Lines array must be in the correct format and must not be null");
        }

        Block block = location.getBlock();
        if (!isSign(block))
        {
            block.setType(isWallSign ? Material.WALL_SIGN : Material.SIGN_POST);
        }
        org.bukkit.block.Sign signBlock = (org.bukkit.block.Sign) block.getState();
        org.bukkit.material.Sign signMaterial = (org.bukkit.material.Sign) signBlock.getData();
        if (!signMaterial.getFacing().equals(direction))
        {
            signMaterial.setFacingDirection(direction);
        }

        for (int i = 0; i < SIGN_LINE_COUNT; i++)
        {
            signBlock.setLine(i, lines[i]);
        }
        signBlock.update(true, false);
    }

    private boolean isSign(Block block)
    {
        // TODO Auto-generated method stub
        if((block.getType() == Material.SIGN)||(block.getType()== Material.SIGN_POST))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void saveToConfiguration(Configuration config, String path)
    {
        config.set(path + "region", region.getName());
        location.saveToConfiguration(config, path + "location.");
        config.set(path + "is_wall_sign", isWallSign);
        config.set(path + "direction", direction.toString());
        saveOptions(config, path + "options.");
    }

    private void saveOptions(Configuration config, String path)
    {
        synchronized (options)
        {
            for (Map.Entry<String, Object> optionEntry : options)
            {
                config.set(path + optionEntry.getKey(), optionEntry.getValue());
            }
        }
    }

    @Override
    public String toString()
    {
        return String.format("Sign[r:%s,l:%s]", region.getName(), location);
    }

    public Region getRegion()
    {
        // TODO Auto-generated method stub
        return region;
    }

    public Location getLocation()
    {
        // TODO Auto-generated method stub
        return location;
    }
}