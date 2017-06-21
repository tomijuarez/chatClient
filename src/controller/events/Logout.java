package controller.events;

/**
 * Created by tomi on 20/06/17.
 */
public class Logout implements ChannelEventAcceptor {

    String userName;

    public Logout(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    @Override
    public void accept(ChannelEventVisitor visitor) {
        visitor.visit(this);
    }
}
