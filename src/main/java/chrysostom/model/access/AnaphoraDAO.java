package chrysostom.model.access;

import chrysostom.model.entities.Anaphora;

public class AnaphoraDAO extends DAO<Anaphora>
{
    protected Class<Anaphora> getLoadingClass() {
        return Anaphora.class;
    }
}
