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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Kit {

	MINEUR("Mineur", 10, Material.IRON_PICKAXE, new ItemStack(Material.IRON_PICKAXE), new ItemStack(Material.TORCH, 64)),
	BUCHERON("Bûcheron", 11, Material.IRON_AXE, new ItemStack(Material.IRON_AXE), new ItemStack(Material.IRON_AXE)),
	ARCHER("Archer", 12, Material.BOW, new ItemStack(Material.BOW), new ItemStack(Material.ARROW, 64)),
	VOYAGEUR("Voyageur", 13, Material.BOAT, new ItemStack(Material.BOAT), new ItemStack(Material.TORCH, 16), new ItemStack(Material.BREAD, 8)),
	ENDERPEARL("Ender pearl", 14, Material.ENDER_PEARL, new ItemStack(Material.STONE_SWORD), new ItemStack(Material.ENDER_PEARL, 16)),
	FERMIER("Fermier", 15, Material.IRON_HOE, new ItemStack(Material.IRON_HOE), new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.SEEDS, 32)),
	BLOCKS("Blocks", 16, Material.COBBLESTONE, new ItemStack(Material.COBBLESTONE, 16), new ItemStack(Material.WOOD, 16), new ItemStack(Material.GRASS, 16));

	private String name;
	private int slot;
	private Material icon;
	private ItemStack[] items;

	Kit(String name, int slot, Material icon, ItemStack... items) {
		this.name = name;
		this.slot = slot;
		this.icon = icon;
		this.items = items;
	}

	public String getName() {
		return name;
	}

	public int getSlot() {
		return slot;
	}

	public Material getIcon() {
		return icon;
	}

	public ItemStack[] getItems() {
		return items;
	}

}
