package controller.events;

/**
 * Created by tomi on 25/05/17.
 */
public interface ConfigurationEventAcceptor {
    void accept(ConfigurationEventVisitor visitor);

}
