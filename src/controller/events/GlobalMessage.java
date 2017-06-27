package controller.events;

/**
 * Created by tomi on 20/06/17.
 */
public class GlobalMessage implements ChannelEventAcceptor {

    String channel;
    String from;
    String message;

    public GlobalMessage(String channel, String from, String message) {
        this.channel = channel;
        this.from = from;
        this.message = message;
    }

    public String getChannel() {
        return this.channel;
    }

    public String getFrom() {
        return this.from;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public void accept(ChannelEventVisitor visitor) {
        visitor.visit(this);
    }
}
