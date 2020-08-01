package com.volmit.iris.command;

import com.volmit.iris.Iris;
import com.volmit.iris.command.util.Command;
import com.volmit.iris.command.util.MortarCommand;
import com.volmit.iris.command.util.MortarSender;

public class CommandIris extends MortarCommand
{
	@Command
	private CommandIrisStudio studio;

	@Command
	private CommandIrisWorld world;

	@Command
	private CommandIrisWhat what;

	@Command
	private CommandIrisObject object;

	public CommandIris()
	{
		super("iris", "ir", "irs");
		requiresPermission(Iris.perm);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		sender.sendMessage("Iris v" + Iris.instance.getDescription().getVersion() + " by Volmit Software");
		printHelp(sender);
		return true;
	}

	@Override
	protected String getArgsUsage()
	{
		return "";
	}
}
