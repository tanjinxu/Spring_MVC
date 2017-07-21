package cn.itcast.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.itcast.bean.User;

@Repository("loginDao")
public class LoginDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    public User doLogin(User user) {
        String sql = "select * from user where uname=? and pass=?";
        User u = null;
        try {
            u = this.getJdbcTemplate().queryForObject(sql, new UserRowMapper(), user.getName(), user.getPass());
        } catch (Exception e) {
            return null;
        }

        return u;
    }

    class UserRowMapper implements RowMapper<User> {
        /**
         * rs:结果集. rowNum:行号
         */
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setPass(rs.getString("pass"));
            user.setName(rs.getString("uname"));
            return user;
        }

    }
}
