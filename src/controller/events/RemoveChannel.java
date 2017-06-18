package controller.events;

/**
 * Created by tomi on 25/05/17.
 */
public class RemoveChannel implements ChannelEventAcceptor {

    private String channelName;

    public RemoveChannel(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelName() {
        return this.channelName;
    }

    @Override
    public void accept(ChannelEventVisitor visitor) {
        visitor.visit(this);
    }


}
