package nl.rudidevries.kura.rcswitch;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.CloudClient;
import org.eclipse.kura.cloud.CloudClientListener;
import org.eclipse.kura.cloud.CloudService;
import org.eclipse.kura.command.CommandService;
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.eclipse.kura.message.KuraPayload;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RcSwitchListener implements ConfigurableComponent  {
	private static final String PROPERTIES_SCRIPT_PATH = "script.path";
	
	private static final Logger s_logger = LoggerFactory.getLogger(RcSwitchListener.class);
	private static final String APP_ID = "switch";
	
	private Map<String, Object> m_properties;

	private CloudService m_cloudService;
	private CloudClient m_cloudClient;
	private CommandService m_commandService;
	
	public void setCloudService(CloudService cloudService) {
	    m_cloudService = cloudService;
	}

	public void unsetCloudService(CloudService cloudService) {
	    m_cloudService = null;
	}
	
	public void setCommandService(CommandService commandService) {
		m_commandService = commandService;
	}
	
	public void unsetCommandService(CommandService commandService) {
		m_commandService = null;
	}
	
	protected void activate(ComponentContext componentContext, Map<String,Object> properties) {
		m_properties = new HashMap<String, Object>();
		updated(properties);
		
		// Acquire a Cloud Application Client for this Application
		s_logger.info("Getting CloudClient for {}...", APP_ID);
		
		try {
			m_cloudClient = m_cloudService.newCloudClient(APP_ID);
		}
		catch (Exception e) {
			s_logger.info("Failed retrieving new CloudClient for {}...", APP_ID);
			throw new ComponentException(e);
		}
		

		subcribeToBroker();
		s_logger.info("Activating {}... Done.", APP_ID);
	}
	
	protected void deactivate(ComponentContext componentContext) {
		s_logger.info("Deactivating {}...", APP_ID);
		m_cloudClient.release();
	}
	
	public void updated(Map<String, Object> properties) {
		this.m_properties = properties;
	}
	
	public void subcribeToBroker() {
		try {
			m_cloudClient.subscribe("#", 1);
			m_cloudClient.addCloudClientListener(new CloudClientListener() {

				@Override
				public void onControlMessageArrived(String deviceId, String appTopic, KuraPayload msg, int qos,
						boolean retain) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onMessageArrived(String deviceId, String appTopic, KuraPayload msg, int qos,
						boolean retain) {
					// TODO Auto-generated method stub
					String switchValue = new String(msg.getBody());
					String[] switchParams = appTopic.split("/");
					
					s_logger.info("Message arrived: {}, for switch: {}", switchValue, switchParams);
					
					try {
						String command = String.format("%s %s %s %s", 
								m_properties.get(PROPERTIES_SCRIPT_PATH), 
								switchParams[0], 
								switchParams[1], 
								switchValue
						);
						
						s_logger.info("Executing: {}", command);
						m_commandService.execute(command);
					} catch (KuraException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						s_logger.error("Command failed.");
					}
				}

				@Override
				public void onConnectionLost() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onConnectionEstablished() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onMessageConfirmed(int messageId, String appTopic) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onMessagePublished(int messageId, String appTopic) {
					// TODO Auto-generated method stub
					
				}
			});
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			s_logger.error("Subscribe failed.");
		}
	}
}
