package net.vatri.inventory.services;

import net.vatri.inventory.models.*;

import java.util.List;
import java.util.Map;

public interface InventoryService {

    public User getUserByEmail(String email);

    public List<User> getUsers();

    public boolean saveUser(User user);

    public List<Product> getProducts();

    public Product getProduct(String id);

    public boolean saveProduct(Product product);


    public List<ProductGroup> getGroups();

    public ProductGroup getGroup(String id);

    public boolean saveGroup(ProductGroup group);

    public List<GroupVariant> getVariants();

    public GroupVariant getVariant(String id);

    public boolean saveVariant(GroupVariant variant);

    public List<Order> getOrders();

    public List<EmailModel> sendEmail();

    public void emailProcess();

    public List<Order> getOrders(Map<String, String> params);

    public Order getOrder(String id);

    public boolean saveOrder(Order order);

    public void removeOrderItem(OrderItem orderItem);

    public List<StockModel> getStock();

    public Map<String, String> getStats();
}