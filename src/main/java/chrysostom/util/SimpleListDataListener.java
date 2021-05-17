package chrysostom.util;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public interface SimpleListDataListener extends ListDataListener
{
    @Override
    default void intervalAdded(ListDataEvent e) {
        dataChanged(e);
    }
    
    @Override
    default void intervalRemoved(ListDataEvent e) {
        dataChanged(e);
    }
    
    @Override
    default void contentsChanged(ListDataEvent e) {
        dataChanged(e);
    }
    
    void dataChanged(ListDataEvent e);
}
