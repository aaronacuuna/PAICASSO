import type { Issue, File } from "./Analysis";

const userServiceFile: File = {
  name: "UserService.java",
  content: `public User getUserById(String id) {
  User user = userRepository.findById(id);
  return user.getName();
}`,
};

const dashboardFile: File = {
  name: "Dashboard.tsx",
  content: ` public boolean authenticate(String username, String password) {
    try {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
              
        // Se construye la consulta concatenando inputs
        String query = "SELECT id, role FROM users 
                         + "WHERE username = '" + username 
                         + "' AND password = '" + password + "';";
             
        ResultSet rs = stmt.executeQuery(query);
             
        if (rs.next()) {
            logger.info("Login exitoso");
            return true;
        }
        return false;
             
    } catch (SQLException e) {
        logger.error("Error durante el login", e);
        return false;
    }
}
    
public boolean register(String username, String password) {
    try {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        // Se construye la consulta concatenando inputs
        String query = "INSERT INTO users (username, password) VALUES ('" + username + "', '" + password + "');";
        int rowsAffected = stmt.executeUpdate(query);
        if (rowsAffected > 0) {
            logger.info("Registro exitoso");
            return true;
        }
        return false;
    } catch (SQLException e) {
        logger.error("Error durante el registro", e);
        return false;
    }
}
`,
};

export const mockIssues: Issue[] = [
  {
    id: 1,
    title: "Null Pointer Exception",
    severity: "high",
    file: userServiceFile,
    line: 2,
    type: "bug",
  },
  {
    id: 2,
    title: "Código duplicado",
    severity: "medium",
    file: dashboardFile,
    line: 7,
    type: "bug"
  },
];
