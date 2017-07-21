package cn.itcast.handler;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import cn.itcast.bean.Stu;
import cn.itcast.service.StuService;
import net.sf.json.JSONObject;

@Controller
public class StudentController {

    @Resource(name = "stuService")
    private StuService stuService;

    @RequestMapping("getAll.action")
    public ModelAndView getAll(@RequestParam(value = "search", defaultValue = "", required = false) String search,
            ModelAndView mv) {
        List<Stu> list = stuService.getAll(search);
        mv.addObject("list", list);
        mv.addObject("search", search);
        mv.setViewName("list");
        return mv;
    }

    @RequestMapping(value = "del.action", method = RequestMethod.POST)
    public void del(@RequestParam("stu_id") String stu_id) {
        stuService.delById(stu_id);
    }

    @RequestMapping(value = "doEdit", method = RequestMethod.GET)
    public ModelAndView doEdit(@RequestParam("id") String id) {
        Stu stu = stuService.findById(id);
        ModelAndView mv = new ModelAndView();
        mv.addObject("info", stu);
        mv.setViewName("action");
        return mv;
    }

    @RequestMapping("saveEdit")
    public ModelAndView saveEdit(Stu stu) {

        stuService.saveEdit(stu);
        System.out.println(stu.getName());
        ModelAndView mv = new ModelAndView("redirect:/getAll.action");
        return mv;
    }

    @RequestMapping("add")
    public ModelAndView add() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("action");
        return mv;
    }

    @RequestMapping("doAdd")
    public ModelAndView doAdd(Stu stu) {
        stuService.doAdd(stu);
        ModelAndView mv = new ModelAndView("redirect:/getAll.action");
        return mv;
    }

    @RequestMapping(value = "getJson", method = RequestMethod.POST)
    public void getJson(@RequestParam(value = "stu_id") String stu_id, HttpServletResponse res) {
        Stu stu = stuService.findById(stu_id);
        String json = JSONObject.fromObject(stu).toString();
        try {
            res.setContentType("text/html;charset=UTF-8");
            res.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
