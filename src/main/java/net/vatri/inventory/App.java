package net.vatri.inventory;

import javafx.scene.layout.AnchorPane;
import net.vatri.inventory.libs.*;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class App extends Application {

    protected static BorderPane mainPane = new BorderPane();
    private static Parent mainMenu;



    public Map<String, String> repository = new HashMap<String, String>();


    private static App instance = null;


    private SessionFactory sessionFactory = null;

    private FxPageSwitcher pageSwitcher;

    @Override
    public void start(Stage primaryStage) {
        getInstance().pageSwitcher = new FxPageSwitcher((node) -> mainPane.setCenter(node), Arrays.asList(
            new FxPage("login", "LoginView"),
            new FxPage("register", "RegisterView"),
            new FxPage("products", "ProductsView"), new FxPage("dashboard", "DashBoardView"),
            new FxPage("newProduct", "AddEditProductView"),
            new FxPage("groups", "GroupsView"),
            new FxPage("addEditGroup", "AddEditGroupView"),
            new FxPage("orders", "OrdersView"),
            new FxPage("addEditOrder", "AddEditOrderView"),
            new FxPage("stock", "StockView")
        ));

        mainMenu = new FxView("Menu").get();

        mainMenu.setVisible(false);
        ((AnchorPane)mainMenu).setPrefWidth(0);
        mainPane.setLeft(mainMenu);

        primaryStage.setScene(new Scene(mainPane, 800, 600));
        primaryStage.setTitle("Inventory Management");
        primaryStage.show();

        showPage("login");
    }

    @Override
    public void stop() throws Exception {
        getInstance().sessionFactory.close();
    }


    public static void showPage(String page){
        if(!Arrays.asList("register","login").contains(page)){
            ((AnchorPane)mainMenu).setPrefWidth(178);
            mainMenu.setVisible(true);
        }
        getInstance().pageSwitcher.showPage(page);
    }



    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure()
                    .build();
            try {
                sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            } catch (Exception e) {

                StandardServiceRegistryBuilder.destroy(registry);
                System.out.println(e.getMessage());
            }
        }
        return sessionFactory;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
