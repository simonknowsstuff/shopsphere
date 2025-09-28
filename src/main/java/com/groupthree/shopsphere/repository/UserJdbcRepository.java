package com.groupthree.shopsphere.repository;

import com.groupthree.shopsphere.models.User;
import org.springframework.stereotype.Repository;

import java.sql.*;
@Repository
public class UserJdbcRepository {

    public User findByEmail(String email) throws SQLException {
        String url = "jdbc:h2:mem:testdb";
        String user = "sa";
        String pass = "";

        Connection conn = DriverManager.getConnection(url, user, pass);
        String sql = "SELECT * FROM users WHERE email = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, email);

        ResultSet rs = stmt.executeQuery();
        User u = null;
        if (rs.next()) {
            u = new User(
                    rs.getLong("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
            );
        }
        rs.close();
        stmt.close();
        conn.close();
        return u;
    }

    public void save(User user) throws SQLException {
        String url = "jdbc:h2:mem:testdb";
        String dbUser = "sa";
        String dbPass = "";

        Connection conn = DriverManager.getConnection(url, dbUser, dbPass);
        String sql = "INSERT INTO users (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, user.getFirstName());
        stmt.setString(2, user.getLastName());
        stmt.setString(3, user.getEmail());
        stmt.setString(4, user.getPassword());

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }
}
