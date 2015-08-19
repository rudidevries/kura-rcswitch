package nl.rudidevries.kura.rcswitch;

import org.eclipse.kura.KuraException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SwitchValueCommand extends SwitchCommand {
	private static final Logger s_logger = LoggerFactory.getLogger(SwitchValueCommand.class);
	
	private static String scriptPath;
	
	private SwitchId switchId;
	private String switchValue;
	
	SwitchValueCommand(SwitchId switchId, String switchValue) {
		this.switchId = switchId;
		this.switchValue = switchValue;
	}
	
	static void setScriptPath(String scriptPath) {
		SwitchValueCommand.scriptPath = scriptPath;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		String command = String.format("%s %s %s", 
				scriptPath, 
				switchId, 
				switchValue
		);
		
		try {
			m_commandService.execute(command);
			s_logger.info("Executed command: {}", command);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
