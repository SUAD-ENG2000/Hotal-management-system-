package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    
  
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_management" +
                                     "?useSSL=false" +
                                     "&serverTimezone=UTC" +
                                     "&autoReconnect=true" +
                                     "&maxReconnects=10" +
                                     "&connectTimeout=30000" +
                                     "&socketTimeout=30000" +
                                     "&characterEncoding=UTF8";
    
    private static final String USERNAME = "root";
    private static final String PASSWORD = "suad@262503SUAD";
    
    private DatabaseManager() {
        initializeConnection();
    }
    
    private void initializeConnection() {
        try {
            // تحميل Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
           
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            
           
            this.connection.setAutoCommit(true); 
            
           
            try {
               
                this.connection.setNetworkTimeout(
                    java.util.concurrent.Executors.newFixedThreadPool(1), 
                    30000
                );
            } catch (Exception e) {
                
                System.out.println("⚠️ Network timeout not supported, continuing...");
            }
            
            System.out.println("✅ تم الاتصال بقاعدة البيانات بنجاح!");
            
          
            testConnection();
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found!");
            System.err.println("قم بتحميل: mysql-connector-java-8.0.xx.jar");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ فشل الاتصال بقاعدة البيانات:");
            System.err.println("URL: " + URL);
            System.err.println("Username: " + USERNAME);
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            
            if (e.getErrorCode() == 0) {
                System.err.println("⚠️ تأكد من تشغيل MySQL Server!");
            } else if (e.getErrorCode() == 1045) {
                System.err.println("⚠️ تحقق من اسم المستخدم وكلمة المرور!");
            }
        }
    }
    
    private void testConnection() throws SQLException {
        try (var stmt = connection.createStatement();
             var rs = stmt.executeQuery("SELECT 1")) {
            if (rs.next()) {
                System.out.println("✅ اختبار الاتصال ناجح!");
            }
        }
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        // ⚠️ تحقق من أن الاتصال لا يزال مفتوحًا وصالحًا
        if (connection == null || connection.isClosed()) {
            System.out.println("🔄 إعادة إنشاء الاتصال (مغلق)...");
            initializeConnection();
        } else {
            // اختبار إذا كان الاتصال لا يزال يعمل
            try (var stmt = connection.createStatement()) {
                stmt.executeQuery("SELECT 1");
            } catch (SQLException e) {
                System.out.println("🔄 إعادة إنشاء الاتصال (غير صالح)...");
                initializeConnection();
            }
        }
        
        return connection;
    }
    
    
    public Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ تم إغلاق الاتصال بقاعدة البيانات");
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في إغلاق الاتصال: " + e.getMessage());
        }
    }
    
    
    public boolean isConnectionValid() {
        try {
            return connection != null && 
                   !connection.isClosed() && 
                   connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }
}