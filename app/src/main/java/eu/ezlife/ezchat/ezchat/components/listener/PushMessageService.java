package eu.ezlife.ezchat.ezchat.components.listener;

/**
 * Created by ajo on 11.04.17.
 */

public interface PushMessageService {

    PushMessageHandler handler = new PushMessageHandler();

    /**
     * update the observables
     */
    public void updateObservable();
}
