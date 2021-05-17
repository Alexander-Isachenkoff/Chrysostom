package chrysostom.model.utils;

import chrysostom.model.entities.Anaphora;
import chrysostom.model.entities.Project;
import chrysostom.model.entities.Variant;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil
{
    private static SessionFactory sessionFactory;
    
    private HibernateUtil() {}
    
    public static SessionFactory getSessionFactory() {
        sessionLazyInit();
        return sessionFactory;
    }
    
    private static void sessionLazyInit() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(Project.class);
            configuration.addAnnotatedClass(Anaphora.class);
            configuration.addAnnotatedClass(Variant.class);
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(builder.build());
        }
    }
}
