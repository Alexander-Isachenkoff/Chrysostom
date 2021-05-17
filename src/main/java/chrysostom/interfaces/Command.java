package chrysostom.interfaces;

public interface Command
{
    Command NO_COMMAND = () -> {};
    
    void execute();
}
