package com.volmit.iris.command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.volmit.iris.Iris;
import com.volmit.iris.command.util.MortarCommand;
import com.volmit.iris.command.util.MortarSender;
import com.volmit.iris.util.Cuboid;
import com.volmit.iris.util.Cuboid.CuboidDirection;
import com.volmit.iris.wand.WandController;

public class CommandIrisObjectXPY extends MortarCommand
{
	public CommandIrisObjectXPY()
	{
		super("x+y");
		requiresPermission(Iris.perm);
		setCategory("Object");
		setDescription("Auto select up and out");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(!sender.isPlayer())
		{
			sender.sendMessage("You don't have a wand");
			return true;
		}

		Player p = sender.player();

		if(!WandController.isWand(p))
		{
			sender.sendMessage("Ready your Wand.");
			return true;
		}
		Location[] b = WandController.getCuboid(p.getInventory().getItemInMainHand());
		b[0].add(new Vector(0, 1, 0));
		b[1].add(new Vector(0, 1, 0));
		Location a1 = b[0].clone();
		Location a2 = b[1].clone();
		Cuboid cursor = new Cuboid(a1, a2);

		while(!cursor.containsOnly(Material.AIR))
		{
			a1.add(new Vector(0, 1, 0));
			a2.add(new Vector(0, 1, 0));
			cursor = new Cuboid(a1, a2);
		}

		a1.add(new Vector(0, -1, 0));
		a2.add(new Vector(0, -1, 0));
		b[0] = a1;
		a2 = b[1];
		cursor = new Cuboid(a1, a2);
		cursor = cursor.contract(CuboidDirection.North);
		cursor = cursor.contract(CuboidDirection.South);
		cursor = cursor.contract(CuboidDirection.East);
		cursor = cursor.contract(CuboidDirection.West);
		b[0] = cursor.getLowerNE();
		b[1] = cursor.getUpperSW();
		p.getInventory().setItemInMainHand(WandController.createWand(b[0], b[1]));
		p.updateInventory();
		p.playSound(p.getLocation(), Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1f, 0.55f);

		return true;
	}

	@Override
	protected String getArgsUsage()
	{
		return "[subcommand]";
	}
}
