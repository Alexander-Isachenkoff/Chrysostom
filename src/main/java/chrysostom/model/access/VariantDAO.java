package chrysostom.model.access;

import chrysostom.model.entities.Anaphora;
import chrysostom.model.entities.Variant;

public class VariantDAO
{
    protected Class<Variant> getLoadingClass() {
        return Variant.class;
    }
    
}
