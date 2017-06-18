package controller.events;

/**
 * Created by Gandalf on 21/2/2016.
 */
public interface ChannelEventVisitor {
    public void visit(CreateChannel event);
    public void visit(RemoveChannel event);
    public void visit(SubscribeChannel event);
    public void visit(UnsubscribeChannel event);
}
