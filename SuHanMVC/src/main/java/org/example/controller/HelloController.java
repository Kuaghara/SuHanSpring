package org.example.controller;


import org.example.core.annotation.Controller;
import org.example.core.annotation.RequestMapping;
import org.example.core.annotation.RequestMethod;
import org.example.core.annotation.ResponseBody;
import org.example.core.httpHandle.ModelAndView;

@Controller
public class HelloController {


    @ResponseBody
    @RequestMapping(path = "/hello", method = RequestMethod.GET)
    public ModelAndView hello() {
        ModelAndView modelAndView = new ModelAndView("hello");
        modelAndView.addJsonMessage("Message", "Hello World");
        return modelAndView;
    }
}
