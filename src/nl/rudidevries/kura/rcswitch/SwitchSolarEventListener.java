package nl.rudidevries.kura.rcswitch;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.CloudClient;

import nl.rudidevries.kura.solarevent.api.SolarEvent;
import nl.rudidevries.kura.solarevent.api.SolarEventListener;

/**
 * Registers as a SolarEventListener with a SolarEventService.
 * 
 * It will then publish the solar events on the applications
 * CloudClient, so switch actions can be registered with 
 * these events. 
 * 
 * @author rudidevries
 *
 */
class SwitchSolarEventListener implements SolarEventListener {

	private CloudClient m_cloudClient;
	
	/**
	 * Constructor
	 * @param cloudClient The applications CloudClient instance.
	 */
	SwitchSolarEventListener(CloudClient cloudClient) {
		m_cloudClient = cloudClient;
	}
	
	/**
	 * Publish a sun/rise topic on this applications cloudClient.
	 */
	@Override
	public void sunrise(SolarEvent e) {
		try {
			m_cloudClient.publish("sun/rise", e.getTime().toString().getBytes(), 0, false, 10);
		} catch (KuraException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Publish a sun/set topic on this applications cloudClient.
	 */
	@Override
	public void sunset(SolarEvent e) {
		try {
			m_cloudClient.publish("sun/set", e.getTime().toString().getBytes(), 0, false, 10);
		} catch (KuraException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
