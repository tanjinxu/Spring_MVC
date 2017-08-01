package cn.itcast.handler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import cn.itcast.bean.User;
import cn.itcast.service.LoginService;

@Controller
public class LoginController {
    @Resource(name = "loginService")
    private LoginService login;

    @RequestMapping(value = "/doLogin.action", method = RequestMethod.POST)
    public ModelAndView doLogin(User user, HttpServletRequest req) {
        ModelAndView modelAndView = new ModelAndView();
        if (user.getName().equals("") || user.getPass().equals("")) {
            modelAndView.addObject("message", "请认真填写信息！～");
            modelAndView.setViewName("index");
            return modelAndView;
        }
        User u = null;
        u = login.doLogin(user);

        if (u == null) {// 空则说明没有查到
            modelAndView.addObject("message", "用户名密码不存在");
            modelAndView.setViewName("index");
        } else {
            // String url = request.getContextPath();

            // https
            req.getSession().setAttribute("user", user);
            return new ModelAndView("redirect:http://localhost:8080/zjsun_zspring_mvc/getAll.action");
            // return new ModelAndView("redirect:https://127.0.0.1:8443/zjsun_zspring_mvc/getAll.action");
        }
        return modelAndView;

    }
}
