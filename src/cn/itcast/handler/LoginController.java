package cn.itcast.handler;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public ModelAndView doLogin(@ModelAttribute("user") User user) {
        User u = null;
        u = login.doLogin(user);

        ModelAndView modelAndView = new ModelAndView();
        if (u == null) {// 空则说明没有查到
            modelAndView.addObject("message", "用户名密码不存在");
            modelAndView.setViewName("index");
        } else {
            // String url = request.getContextPath();
            return new ModelAndView("redirect:http://localhost:8080/zjsun_zspring_mvc/getAll.action");
        }
        return modelAndView;

    }
}
