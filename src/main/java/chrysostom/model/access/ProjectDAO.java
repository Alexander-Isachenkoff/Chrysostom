package chrysostom.model.access;

import chrysostom.model.entities.Project;

public class ProjectDAO extends DAO<Project>
{
    protected Class<Project> getLoadingClass() {
        return Project.class;
    }
}
