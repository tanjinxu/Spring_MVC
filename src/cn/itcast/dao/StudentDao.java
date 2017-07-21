package cn.itcast.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.itcast.bean.Stu;

@Repository("stuDao")
public class StudentDao {
    @Resource(name = "jdbcTemplate")
    private JdbcTemplate JdbcTemplate;

    public List<Stu> getAll(String search) {

        String sql = (search == null) ? "select * from admin order by id desc"
                : "select * from admin where name like '%" + search + "%'  order by id desc";
        return this.JdbcTemplate.query(sql, new StuRowMapper());
    }

    public Stu findById(String id) {
        String sql = "select * from admin where id = ?";
        Stu stu = this.JdbcTemplate.queryForObject(sql, new StuRowMapper(), id);
        return stu;
    }

    class StuRowMapper implements RowMapper<Stu> {

        public Stu mapRow(ResultSet rs, int rowNum) throws SQLException {
            Stu stu = new Stu();
            stu.setId(rs.getString("id"));
            stu.setName(rs.getString("name"));
            stu.setAddress(rs.getString("address"));
            stu.setTel(rs.getString("tel"));
            stu.setAge(rs.getString("age"));
            stu.setSchool(rs.getString("school"));
            stu.setTest(rs.getString("test"));
            stu.setAddtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new java.util.Date(rs.getTimestamp("addtime").getTime())));
            return stu;
        }

    }

    public void delById(String stu_id) {

        String sql = "delete from admin where id = ?";
        this.JdbcTemplate.update(sql, stu_id);
    }

    public void saveEdit(Stu stu) {
        String sql = "update admin set name = ? ,address = ?,tel = ?, age = ?,school = ?,test = ? where id = ?";
        this.JdbcTemplate.update(sql, stu.getName(), stu.getAddress(), stu.getTel(), stu.getAge(), stu.getSchool(),
                stu.getTest(), stu.getId());
    }

    public void doAdd(Stu stu) {
        String sql = "insert into admin values(null,?,?,?,?,?,?,?)";
        this.JdbcTemplate.update(sql, stu.getName(), stu.getAddress(), stu.getTel(), stu.getAge(), stu.getSchool(),
                stu.getTest(), new Date());
        System.out.println(new Date());
    }

}
