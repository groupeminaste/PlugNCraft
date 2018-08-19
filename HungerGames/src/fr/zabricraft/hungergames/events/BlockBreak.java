/*
 *  Copyright (C) 2018 FALLET Nathan
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 */

package fr.zabricraft.hungergames.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fr.zabricraft.hungergames.HungerGames;
import fr.zabricraft.hungergames.utils.Kit;
import fr.zabricraft.hungergames.utils.ZabriPlayer;

public class BlockBreak implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		ZabriPlayer zp = HungerGames.getInstance().getPlayer(e.getPlayer().getUniqueId());
		if (zp != null) {
			if (zp.getKit().equals(Kit.BUCHERON)) {
				breakTree(e.getBlock());
			} else if (zp.getKit().equals(Kit.MINEUR)) {
				int multiplicateur = 1;
				if (e.getPlayer().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
					multiplicateur = e.getPlayer().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
				}
				if (e.getBlock().getType().equals(Material.COAL_ORE)) {
					e.setCancelled(true);
					e.getBlock().setType(Material.AIR);
					e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(),
							new ItemStack(Material.COAL, 5 * multiplicateur));
				} else if (e.getBlock().getType().equals(Material.IRON_ORE)) {
					e.setCancelled(true);
					e.getBlock().setType(Material.AIR);
					e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(),
							new ItemStack(Material.IRON_INGOT, 3 * multiplicateur));
				} else if (e.getBlock().getType().equals(Material.GOLD_ORE)) {
					e.setCancelled(true);
					e.getBlock().setType(Material.AIR);
					e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(),
							new ItemStack(Material.GOLD_INGOT, 2 * multiplicateur));
				} else if (e.getBlock().getType().equals(Material.DIAMOND_ORE)) {
					e.setCancelled(true);
					e.getBlock().setType(Material.AIR);
					e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(),
							new ItemStack(Material.DIAMOND, 2 * multiplicateur));
				} else if (e.getBlock().getType().equals(Material.EMERALD_ORE)) {
					e.setCancelled(true);
					e.getBlock().setType(Material.AIR);
					e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(),
							new ItemStack(Material.EMERALD, 2 * multiplicateur));
				}
			}
		}
	}

	public void breakTree(Block block) {
		if (block.getType().equals(Material.LOG) || block.getType().equals(Material.LOG_2)
				|| block.getType().equals(Material.LEAVES)) {
			block.breakNaturally();
			for (BlockFace f : BlockFace.values()) {
				Block b2 = block.getRelative(f);
				breakTree(b2);
			}
		}
	}

}
