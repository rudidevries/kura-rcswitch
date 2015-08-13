package nl.rudidevries.kura.rcswitch;

import org.eclipse.kura.KuraException;
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
	private String m_scriptPath;
	
	/**
	 * Constructor
	 * 
	 * @param commandService Command service to be used to execute commands.
	 * @param scriptPath Script that is to be executed.
	 */
	SwitchCloudClientListener(CommandService commandService, String scriptPath) {
		m_commandService = commandService;
		m_scriptPath = scriptPath;
	}
	
	/**
	 * Setter
	 * @param scriptPath
	 */
	public void setScriptPath(String scriptPath) {
		m_scriptPath = scriptPath;
	}
	
	/**
	 * Getter
	 * @return script path
	 */
	public String getScriptPath() {
		return m_scriptPath;
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
		s_logger.info("Message arrived: {}, for switch: {}", switchValue, switchParams);
		
		try {
			// Create the command to be executed.
			String command = String.format("%s %s %s %s", 
					m_scriptPath, 
					switchParams[0], 
					switchParams[1], 
					switchValue
			);
			
			// Execute the command.
			s_logger.info("Executing: {}", command);
			m_commandService.execute(command);
		} catch (KuraException e) {
			e.printStackTrace();
			s_logger.error("Command failed.");
		}
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
