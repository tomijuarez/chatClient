package controller.events;

/**
 * Created by tomi on 20/06/17.
 */
public class Clear implements ChannelEventAcceptor {

    public Clear() {
        //BLANK [...]
    }

    @Override
    public void accept(ChannelEventVisitor visitor) {
        visitor.visit(this);
    }
}
