package controller.events;

/**
 * Created by tomi on 25/05/17.
 */
public class CreateUser implements ConfigurationEventAcceptor {

    @Override
    public void accept(ConfigurationEventVisitor visitor) {
        visitor.visit(this);
    }
}
