package nl.rudidevries.kura.rcswitch;

import org.eclipse.kura.command.CommandService;

abstract class SwitchCommand {
	protected CommandService m_commandService;
	
	void setCommandService(CommandService commandService) {
		m_commandService = commandService;
	}
	
	abstract void execute();
}
