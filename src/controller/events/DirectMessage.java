package controller.events;

/**
 * Created by tomi on 20/06/17.
 */
public class DirectMessage implements ChannelEventAcceptor {

    String channel;
    String from;
    String to;
    String message;

    public DirectMessage(String channel, String from, String to, String message) {
        this.channel = channel;
        this.from = from;
        this.to = to;
        this.message = message;
    }

    public String getChannel() {
        return this.channel;
    }

    public String getFrom() {
        return this.from;
    }

    public String getTo() {
        return this.to;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public void accept(ChannelEventVisitor visitor) {
        visitor.visit(this);
    }
}
