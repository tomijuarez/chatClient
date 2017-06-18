package controller.events;

/**
 * Created by tomi on 25/05/17.
 */
public class UnsubscribeChannel implements ChannelEventAcceptor {

    @Override
    public void accept(ChannelEventVisitor visitor) {
        visitor.visit(this);
    }
}