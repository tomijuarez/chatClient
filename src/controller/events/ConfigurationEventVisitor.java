package controller.events;

/**
 * Created by tomi on 25/05/17.
 */
public interface ConfigurationEventVisitor {
    public void visit(CreateUser event);
    public void visit(RemoveUser event);
}
