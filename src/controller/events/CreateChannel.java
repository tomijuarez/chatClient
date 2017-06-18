package controller.events;

/**
 * Created by Gandalf on 21/2/2016.
 */
public class CreateChannel implements ChannelEventAcceptor {

    private String name;

    public CreateChannel(String name) {
        this.name = name;
    }

    public String getChannelName() {
        return this.name;
    }

    @Override
    public void accept(ChannelEventVisitor visitor) {
        visitor.visit(this);
    }
}
