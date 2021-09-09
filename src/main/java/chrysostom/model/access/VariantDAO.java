package chrysostom.model.access;

import chrysostom.model.entities.Variant;

public class VariantDAO extends DAO<Variant>
{
    protected Class<Variant> getLoadingClass() {
        return Variant.class;
    }
    
}
