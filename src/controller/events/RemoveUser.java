package controller.events;

/**
 * Created by tomi on 25/05/17.
 */
public class RemoveUser implements ConfigurationEventAcceptor {

    @Override
    public void accept(ConfigurationEventVisitor visitor) {
        visitor.visit(this);
    }
}

