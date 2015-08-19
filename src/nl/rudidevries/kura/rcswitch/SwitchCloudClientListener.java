package nl.rudidevries.kura.rcswitch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.kura.cloud.CloudClientListener;
import org.eclipse.kura.command.CommandService;
import org.eclipse.kura.message.KuraPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Rudi de Vries
 */
class SwitchCloudClientListener implements CloudClientListener {
	private static final Logger s_logger = LoggerFactory.getLogger(RcSwitchListener.class);
	private CommandService m_commandService;
	
	private Map<SwitchId, SwitchCommand> sunRiseCommands = new HashMap<>();
	private Map<SwitchId, SwitchCommand> sunSetCommands = new HashMap<>();
	
	/**
	 * Constructor
	 * 
	 * @param commandService Command service to be used to execute commands.
	 * @param scriptPath Script that is to be executed.
	 */
	SwitchCloudClientListener(CommandService commandService) {
		m_commandService = commandService;
	}
	
	@Override
	public void onControlMessageArrived(String deviceId, String appTopic, KuraPayload msg, int qos,
			boolean retain) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void onMessageArrived(String deviceId, String appTopic, KuraPayload msg, int qos,
			boolean retain) {
		// Get parameter values from received message details.
		String switchValue = new String(msg.getBody());
		String[] switchParams = appTopic.split("/");
		
		// switch/sun/rise || switch/sun/set
		if (switchParams[0].equals("sun")) {
			handleSunCommands(switchParams[1]);
		}
		// switch/A/1/#
		else {
			SwitchId switchId = new SwitchId(switchParams[0], switchParams[1]);
			s_logger.info("Message arrived: {}, for switch: {}", switchValue, switchParams);
			
			// invalid, just ignore.
			if (switchParams.length != 2 && switchParams.length != 4) {
				return;
			}
			
			// An on or off command.
			if (switchParams.length == 2) {
				handleSimpleSwitch(switchId, switchValue);
			}
			// Else it must be a sun command.
			else if(switchParams[2].equals("sun")) {
				handleSunSwitchSetting(switchId, switchParams[3], switchValue);
			}
			else {
				s_logger.info("Message is not handled... {}", switchParams[2]);
			}
		}
	}
	
	/**
	 * Execute sunrise or sunset commands.
	 * @param event rise or set
	 */
	private void handleSunCommands(String event) {
		// determine which commands to execute.
		Collection<SwitchCommand> commands;
		if (event.equals("rise")) {
			s_logger.info("Handling sunrise");
			commands = sunRiseCommands.values();
		}
		else {
			s_logger.info("Handling sunset");
			commands = sunSetCommands.values();
		}
		
		// execute commands.
		for(SwitchCommand c : commands) {
			c.execute();
		}	
	}
	
	/**
	 * Handle a switch command directly.
	 * 
	 * @param switchId Switch identifier.
	 * @param switchValue on or off
	 */
	private void handleSimpleSwitch(SwitchId switchId, String switchValue) {
		SwitchCommand command = new SwitchValueCommand(
				switchId,
				switchValue
				);
		command.setCommandService(m_commandService);
		command.execute();
	}
	
	/**
	 * Handle the sun switch setting.
	 * 
	 * @param switchId Switch identifier.
	 * @param switchParams 
	 * @param switchValue
	 */
	private void handleSunSwitchSetting(SwitchId switchId, String event, String switchValue) {
		// Determine the command set to edit.
		Map<SwitchId, SwitchCommand> listeners = 
				event.equals("rise") ? sunRiseCommands : sunSetCommands;
	
		// Determine what command should be set/unset
		SwitchCommand command = null;
		switch (switchValue) {
			case "on":
			case "off":
				command = new SwitchValueCommand(switchId, switchValue);
				command.setCommandService(m_commandService);
				break;
			case "disabled":
				listeners.remove(switchId);
				// disabled commands don't need to be executed, so return.
				return;
		}
		
		s_logger.info("Added or changed listeners command for {}", switchId);
		listeners.put(switchId, command);
	}

	@Override
	public void onConnectionLost() {
		// Empty
	}

	@Override
	public void onConnectionEstablished() {
		// Empty
	}

	@Override
	public void onMessageConfirmed(int messageId, String appTopic) {
		// Empty
	}

	@Override
	public void onMessagePublished(int messageId, String appTopic) {
		// Empty
	}
}
