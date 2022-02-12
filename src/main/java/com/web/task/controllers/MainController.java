package com.web.task.controllers;

import com.web.task.User;
import com.web.task.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class MainController {

    @Autowired  //object loads automatically(doesn't need initializing)
    private UserRepository repo;

    @GetMapping("/")
    public String loginFormStart(){
        return "login";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/process_register")
    public String processRegistration(User user, Model model){
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setRegistrationDate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        user.setLastLoginDate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        user.setStatus("Unblocked");

        if(repo.findByEmail(user.getEmail()) == null){
            repo.save(user);
            model.addAttribute("register", "Successful registration!");
        }
        else model.addAttribute("register", "User with this e-mail has already been registered!");

        return "registration_success";
    }

    @GetMapping("/users_list")
    public String userList(Model model){
        List<User> users = repo.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    @RequestMapping("/user_control")
    public String checkboxActions(@RequestParam(name = "checkbox") List<Long> ID, @RequestParam(name = "button") String button, Authentication auth) {
        if(button.equals("Block")){
            ID.forEach(x -> repo.blockById(x));
            return ID.contains(repo.findByEmail(auth.getName()).getId()) ? "redirect:/" : "redirect:/users_list";
        }
        else if(button.equals("Unblock")){
            ID.forEach(x -> repo.unblockById(x));
        }
        else{
            ID.forEach(x -> repo.deleteById(x));
            return (repo.findByEmail(auth.getName()) == null) ? "redirect:/" : "redirect:/users_list";
        }
        return "redirect:/users_list";
    }
}
