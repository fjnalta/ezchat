package eu.ezlife.ezchat.ezchat.components.listener;

/**
 * Created by ajo on 11.04.17.
 */

public interface ListenerService {

    MessageHandler handler = new MessageHandler();

    /**
     * update the observables
     */
    public void updateObservable();
}
