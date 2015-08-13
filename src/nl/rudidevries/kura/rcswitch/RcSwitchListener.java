package nl.rudidevries.kura.rcswitch;

import java.util.Map;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.CloudClient;
import org.eclipse.kura.cloud.CloudService;
import org.eclipse.kura.command.CommandService;
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Subscribe to a MQTT endpoint and listen for commands. These commands will 
 * be interpreted and execute a command line script through the command service.
 * 
 * The script that will be executed can be set in the configuration options 
 * of this component.
 * 
 * The script should take three commandline parameters. The first two will be collected
 * from the topic, and identify the device to be called. The third is the message payload,
 * and carries the on or off status.
 * 
 * @author Rudi de Vries
 *
 */
public class RcSwitchListener implements ConfigurableComponent  {
	private static final Logger s_logger = LoggerFactory.getLogger(RcSwitchListener.class);
	
	private static final String PROPERTIES_SCRIPT_PATH = "script.path";
	private static final String APP_ID = "switch";
	
	private Map<String, Object> m_properties;
	private CloudService m_cloudService;
	private CloudClient m_cloudClient;
	private CommandService m_commandService;
	
	private SwitchCloudClientListener m_cloudClientListener;
	
	/**
	 * DI
	 * @param cloudService injected cloud service
	 */
	public void setCloudService(CloudService cloudService) {
	    m_cloudService = cloudService;
	}

	/**
	 * DI
	 * @param cloudService injected cloud service
	 */
	public void unsetCloudService(CloudService cloudService) {
	    m_cloudService = null;
	}
	
	/**
	 * DI
	 * @param commandService injected command service
	 */
	public void setCommandService(CommandService commandService) {
		m_commandService = commandService;
	}
	
	/**
	 * DI
	 * @param commandService injected command service
	 */
	public void unsetCommandService(CommandService commandService) {
		m_commandService = null;
	}
	
	/**
	 * Activate the component.
	 * 
	 * @param componentContext
	 * @param properties
	 */
	protected void activate(ComponentContext componentContext, Map<String,Object> properties) {
		// Set the component configuration properties.
		this.m_properties = properties;
		
		// Acquire a Cloud Application Client for this Application
		s_logger.info("Getting CloudClient for {}...", APP_ID);
		try {
			m_cloudClient = m_cloudService.newCloudClient(APP_ID);
		}
		catch (Exception e) {
			s_logger.info("Failed retrieving new CloudClient for {}...", APP_ID);
			throw new ComponentException(e);
		}
		
		// subscribe the client to the broker.
		intializeSubscription();
		s_logger.info("Activating {}... Done.", APP_ID);
	}
	
	/**
	 * Deactivate the component.
	 * 
	 * @param componentContext
	 */
	protected void deactivate(ComponentContext componentContext) {
		s_logger.info("Deactivating {}...", APP_ID);
		m_cloudClient.release();
	}
	
	/**
	 * Called when the component configuration has changed.
	 * @param properties component configuration settings.
	 */
	public void updated(Map<String, Object> properties) {
		this.m_properties = properties;
		
		// Change script path of listener.
		m_cloudClientListener.setScriptPath((String) m_properties.get(PROPERTIES_SCRIPT_PATH));
	}
	
	/**
	 * Initialize the subscription and register the listener.
	 * Should only be executed at activation time.
	 */
	private void intializeSubscription() {
		try {
			m_cloudClient.subscribe("#", 1);
			
			m_cloudClientListener = new SwitchCloudClientListener(
					m_commandService,
					(String) m_properties.get(PROPERTIES_SCRIPT_PATH)
			);
			m_cloudClient.addCloudClientListener(m_cloudClientListener);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			s_logger.error("Subscribe failed.");
		}
	}
}
