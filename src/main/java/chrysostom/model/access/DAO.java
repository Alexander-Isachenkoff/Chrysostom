package chrysostom.model.access;

import chrysostom.model.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public abstract class DAO<T>
{
    Session openSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }
    
    public T findById(int id) {
        Session session = openSession();
        T obj = session.get(getLoadingClass(), id);
        session.close();
        return obj;
    }
    
    protected abstract Class<T> getLoadingClass();
    
    public void save(T obj) {
        Session session = openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(obj);
        tx1.commit();
        session.close();
    }
    
    public void update(T obj) {
        Session session = openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(obj);
        tx1.commit();
        session.close();
    }
    
    public void saveOrUpdate(T obj) {
        Session session = openSession();
        Transaction tx1 = session.beginTransaction();
        session.saveOrUpdate(obj);
        tx1.commit();
        session.close();
    }
    
    public void delete(T obj) {
        Session session = openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(obj);
        tx1.commit();
        session.close();
    }
    
    public List<T> selectAll() {
        Session session = openSession();
        List<T> list = session.createQuery("From " + getLoadingClass().getSimpleName()).list();
        session.close();
        return list;
    }
}
