package com.exerciseBeta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserController {

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/userCreate")
  public String createUserForm() {
    return "userCreate";
  }

  @PostMapping("/userCreate")
  public RedirectView createUser(@RequestParam String username, @RequestParam String password) {
    UserDetails user = User.withUsername(username)
        .password(passwordEncoder.encode(password))
        .roles("USER")
        .build();

    if (userDetailsService instanceof InMemoryUserDetailsManager) {
      ((InMemoryUserDetailsManager) userDetailsService).createUser(user);
    } else {
      throw new RuntimeException("UserDetailsService must be an instance of InMemoryUserDetailsManager");
    }

    return new RedirectView("/login");
  }

  @GetMapping("/userInfo")
  public ModelAndView userInfo(Authentication authentication) {
    String username = authentication.getName();
    UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(username);
    ModelAndView modelAndView = new ModelAndView("userInfo");
    modelAndView.addObject("username", username);
    modelAndView.addObject("password", userDetails.getPassword());
    return modelAndView;
  }
}
