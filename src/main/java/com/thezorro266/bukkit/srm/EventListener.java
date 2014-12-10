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

import java.text.MessageFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import com.thezorro266.bukkit.srm.exceptions.ContentSaveException;
import com.thezorro266.bukkit.srm.factories.SignFactory;
import com.thezorro266.bukkit.srm.factories.SignFactory.Sign;
import com.thezorro266.bukkit.srm.helpers.Location;
import com.thezorro266.bukkit.srm.templates.Template;


public class EventListener implements Listener
{
    SimpleRegionMarket thePlugin;
    public EventListener(SimpleRegionMarket plug)
    {
        thePlugin = plug;
        plug.getServer().getPluginManager().registerEvents(this, plug);
    }

    @EventHandler
    public void onSignChanged(SignChangeEvent event)
    {
        if (!event.isCancelled())
        {
            Player player = event.getPlayer();
            Sign sign = SignFactory.instance.getSignFromLocation(Location.fromBlock(event.getBlock()));
            if (sign != null)
            {
                if (!sign.getRegion().getTemplate().breakSign(player, sign))
                {
                    event.setCancelled(true);
                    return;
                }
            }

            synchronized (thePlugin.getTemplateManager().getTemplateList())
            {
                for (Template template : SimpleRegionMarket.getInstance().getTemplateManager().getTemplateList())
                {
                    String[] lines = event.getLines();
                    if (template.isSignApplicable(Location.fromBlock(event.getBlock()), lines))
                    {
                        if (template.createSign(player, event.getBlock(), lines))
                        {
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (!event.isCancelled())
        {
            Sign sign = SignFactory.instance.getSignFromLocation(Location.fromBlock(event.getBlock()));
            if (sign != null)
            {
                if (sign.getRegion().getTemplate().breakSign(event.getPlayer(), sign))
                {
                    try
                    {
                        thePlugin.getTemplateManager().saveRegion(sign.getRegion());
                    }
                    catch (ContentSaveException e)
                    {
                        if (event.getPlayer() != null)
                            event.getPlayer().sendMessage(ChatColor.RED + LanguageSupport.instance.getString("region.save.problem.playermsg"));

                        thePlugin.getLogger().severe(MessageFormat.format(LanguageSupport.instance.getString("region.save.problem.console"), sign.getRegion().getName()));
                        thePlugin.printError(e);
                    }
                }
                else
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (event.hasBlock())
        {
           
            if (SignFactory.instance.isSign(event.getClickedBlock()))
            {
               
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                {
                    event.getPlayer().sendMessage("you right clicked a sign");
                    event.getPlayer().sendMessage(event.getClickedBlock().getType().name());
                    Sign sign = thePlugin.getLocationSignHelper().getSign(Location.fromBlock(event.getClickedBlock()));
                    event.getPlayer().sendMessage(sign.toString());
                    if (sign != null)
                    {   
                        event.getPlayer().sendMessage("you right clicked a fucking sign");
                        sign.getRegion().getTemplate().clickSign(event.getPlayer(), sign);
                    }
                }
            }
        }
    }
}
