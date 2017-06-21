package controller.events;

/**
 * Created by tomi on 25/05/17.
 */
public class UnsubscribeChannel implements ChannelEventAcceptor {

    private String channelName;
    private String userName;

    public UnsubscribeChannel(String channelName, String userName) {
        this.channelName = channelName;
        this.userName = userName;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public String getUserName() {
        return this.userName;
    }

    @Override
    public void accept(ChannelEventVisitor visitor) {
        visitor.visit(this);
    }
}
