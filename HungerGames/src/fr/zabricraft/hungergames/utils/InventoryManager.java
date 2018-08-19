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

package fr.zabricraft.hungergames.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.zabricraft.plugnapi.utils.Items;

public class InventoryManager {

	public static void openKits(Player player) {
		Inventory i = Bukkit.createInventory(null, 36, "§r§rKits");
		for (Kit kit : Kit.values()) {
			i.setItem(kit.getSlot(), Items.setName(new ItemStack(kit.getIcon()), "§e" + kit.getName()));
		}
		i.setItem(31, Items.setName(new ItemStack(Material.WOOL), "§aFermer"));
		player.openInventory(i);
	}

}
