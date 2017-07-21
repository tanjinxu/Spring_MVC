package cn.itcast.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bean.Stu;
import cn.itcast.dao.StudentDao;

@Service("stuService")
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, readOnly = false)
public class StuService {
    @Resource(name = "stuDao")
    private StudentDao stuDao;

    public List<Stu> getAll(String search) {
        return stuDao.getAll(search);
    }

    public void delById(String stu_id) {
        stuDao.delById(stu_id);
    }

    public Stu findById(String id) {
        return stuDao.findById(id);
    }

    public void saveEdit(Stu stu) {
        stuDao.saveEdit(stu);
    }

    public void doAdd(Stu stu) {
        stuDao.doAdd(stu);
    }

}
