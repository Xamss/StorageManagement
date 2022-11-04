package net.vatri.inventory.services;

import net.vatri.inventory.models.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.persistence.Query;
import java.util.*;


public class InventoryServiceHibernate implements InventoryService {

    private SessionFactory sessionFactory;

    private static final String TBL_PRODUCTS = "products";
    private static final String TBL_PRODUCT_GROUPS = "product_groups";
    private static final String TBL_ORDERS = "orders";

    public InventoryServiceHibernate(SessionFactory sessionFactory) {
        this.setSessionFactory(sessionFactory);
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private List list(String name) {
        Session session = getSessionFactory().openSession();
        try {
            return session.createQuery("FROM " + name).list();
        } finally {
            session.close();
        }
    }

    private Object get(Class entity, String id) {

        Session session = getSessionFactory().openSession();
        try {
            return session.get(entity, Integer.parseInt(id));
        } finally {
            session.close();
        }
    }

    private boolean save(Object entity, Integer id) {

        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        try {
            if (id != null && id > 0) {
                session.merge(entity);
            } else {
                session.persist(entity);
            }
            session.getTransaction().commit();
            return true;
        } catch (HibernateException e) {
            System.out.print(e.getMessage());
            session.getTransaction().rollback();
            return false;
        } finally {
            session.close();
        }
    }


    @Override
    public User getUserByEmail(String email) {
        Session session = getSessionFactory().openSession();
        User user = (User) session.createQuery("FROM User WHERE email = :email")
                .setParameter("email", email)
                .uniqueResult();

        session.close();
        return user;
    }

    public boolean saveUser(User user){
        return this.save(user, user.getId());
    }

    @Override
    public List<User> getUsers() {
        List<User> list = (List<User>) this.list("User");
        return list;
    }

    @Override
    public List<Product> getProducts() {
        List<Product> list = (List<Product>) this.list("Product");
        return list;
    }

    @Override
    public Product getProduct(String id) {
        return (Product) this.get(Product.class, id);
    }

    @Override
    public boolean saveProduct(Product product) {
        return this.save(product, product.getId());
    }

    @Override
    public List<ProductGroup> getGroups() {
        return (List<ProductGroup>) this.list("ProductGroup");
    }

    @Override
    public ProductGroup getGroup(String id) {
        return (ProductGroup) this.get(ProductGroup.class, id);
    }

    @Override
    public boolean saveGroup(ProductGroup group) {
        return this.save(group, group.getId());
    }

    @Override
    public List<GroupVariant> getVariants() {
        return (List<GroupVariant>) this.list("GroupVariant");
    }

    @Override
    public GroupVariant getVariant(String id) {
        return (GroupVariant) this.get(GroupVariant.class, id);
    }

    @Override
    public boolean saveVariant(GroupVariant variant) {
        return this.save(variant, variant.getId());
    }

    @Override
    public List<Order> getOrders() {
        return (List<Order>) this.list("Order");
    }

    @Override
    public List<Order> getOrders(Map<String, String> params) {

        Session session = getSessionFactory().openSession();

        Query query = session.createQuery("FROM Order");

        Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> par = it.next();
            String key = par.getKey();
            String val = par.getValue();

            query.setParameter(key, val);
        }

        List<Order> list = (List<Order>) query.getResultList();
        session.close();

        return list;
    }

    @Override
    public Order getOrder(String id) {
        return (Order) this.get(Order.class, id);
    }

    @Override
    public boolean saveOrder(Order order) {
        return this.save(order, order.getId());
    }

    @Override
    public void removeOrderItem(OrderItem orderItem) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        try {
            session.remove(orderItem);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    @Override
    public List<StockModel> getStock() {
        String strQuery = "SELECT DISTINCT"
                + "	p.name as product_name "
                + "	, gv.variant_name "
                + " ,(select buy_cnt  - sell_cnt "
                + " FROM ( "
                + " SELECT "
                + "( "
                + " select count(o.id) "
                + " from orders o "
                + " LEFT JOIN order_items oi on oi.order_id = o.id "
                + " WHERE o.type = 'buy' and oi.product_id = p.id AND oi.product_variant_id = gv.id "
                + " ) as buy_cnt "
                + " ,( "
                + " select count(o2.id) "
                + " from orders o2 "
                + " LEFT JOIN order_items oi on oi.order_id = o2.id "
                + " WHERE o2.type = 'sell' and oi.product_id = p.id AND oi.product_variant_id = gv.id "
                + " ) as sell_cnt "
                + " ) as tbl "
                + " ) as `stock` "
                + " FROM products p "
                + " LEFT JOIN group_variants gv ON gv.group_id = p.group_id "
                + " ORDER BY product_name ";

        Session session = getSessionFactory().openSession();
        List<Object[]> tmpList = session.getEntityManagerFactory()
                .createEntityManager()
                .createNativeQuery(strQuery)
                .getResultList();


        List<StockModel> res = new ArrayList<>();
        tmpList.stream().forEach(row -> {
            if (row.length > 2) {
                String productName = (String) row[0];
                String variantName = (String) row[1];
                String qty = row[2].toString();
                res.add(new StockModel(productName, variantName, qty));
            } else {
                System.out.println("Can't initialize stock model");
            }
        });

        session.close();
        return res;
    }

    @Override
    public List<EmailModel> sendEmail(){
        String strQuery = "select p.name, orders.type, count(*) quantity from orders join order_items oi on orders.id = oi.order_id\n" +
                "join products p on oi.product_id = p.id group by p.name, type limit 5;";

        Session session = getSessionFactory().openSession();
        List<Object[]> tmpList = session.getEntityManagerFactory()
                .createEntityManager()
                .createNativeQuery(strQuery)
                .getResultList();

        List<EmailModel> res = new ArrayList<>();
        tmpList.stream().forEach(row -> {
            if (row.length > 2) {
                String productName = (String) row[0];
                String typeName = (String) row[1];
                String qty = row[2].toString();
                res.add(new EmailModel(productName, typeName, qty));
            } else {
                System.out.println("Can't initialize stock model");
            }
        });
        return res;
    }

    @Override
    public void emailProcess(){
        List<EmailModel> emailModels = sendEmail();
        List<User> users = getUsers();
        for (User user: users) {
            EmailNotify.sendEmail(user.getEmail(), emailModels);
        }
    }

    @Override
    public Map<String, String> getStats() {

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM");

        String month1 = formatter.print(LocalDate.now().minusMonths(3));
        String month2 = formatter.print(LocalDate.now().minusMonths(2));
        String month3 = formatter.print(LocalDate.now().minusMonths(1));
        String month4 = formatter.print(LocalDate.now());

        String strQuery = "SELECT"
                + " (SELECT count(*) from " + TBL_PRODUCTS + ") as products_number "
                + " ,(SELECT count(*) from " + TBL_ORDERS + ") as orders_number "
                + " ,(SELECT count(*) FROM " + TBL_PRODUCT_GROUPS + " ) as groups_number "
                + " , (SELECT count(*) FROM " + TBL_ORDERS + " where created LIKE '%" + month1 + "%' ) as orders1"
                + " , (SELECT count(*) FROM " + TBL_ORDERS + " where created LIKE '%" + month2 + "%' ) as orders2"
                + " , (SELECT count(*) FROM " + TBL_ORDERS + " where created LIKE '%" + month3 + "%' ) as orders3"
                + " , (SELECT count(*) FROM " + TBL_ORDERS + " where created LIKE '%" + month4 + "%' ) as orders4";

        Session session = getSessionFactory().openSession();
        Object[] res = (Object[]) session.getEntityManagerFactory()
                .createEntityManager()
                .createNativeQuery(strQuery)
                .getSingleResult();

        session.close();

        Map<String, String> out = new HashMap<String, String>();
        out.put("products_number", res[0].toString());
        out.put("orders_number", res[1].toString());
        out.put("groups_number", res[2].toString());
        out.put("orders1", res[3].toString());
        out.put("orders2", res[4].toString());
        out.put("orders3", res[5].toString());
        out.put("orders4", res[6].toString());

        return out;
    }



}
