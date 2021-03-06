package com.theironyard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;

/**
 * Created by VeryBarry on 10/25/16.
 */
@Controller
public class BlogSpringController {
    @Autowired
    MessageRepository messages;

    @Autowired
    UserRepository users;

    @PostConstruct
    public void init() throws PasswordStorage.CannotPerformOperationException {
        User defaultUser = new User("Barton", PasswordStorage.createHash("1234"));
        Message defaultMessage = new Message("hello", defaultUser);
        if (users.findByUsername(defaultUser.username) == null) {
            users.save(defaultUser);
        }
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        User user = users.findByUsername(username);
        Iterable<Message> messagesIt = messages.findAll();
        for (Message m : messagesIt) {
            m.isMe = m.user.username.equals(username);
        }
        model.addAttribute("messages", messagesIt);
        model.addAttribute("username", user);
        return "home";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String userName, String userPass, HttpSession session) throws Exception {
        User user = users.findByUsername(userName);
        if (user == null) {
            user = new User(userName, PasswordStorage.createHash(userPass));
            users.save(user);
        }
        else if (!PasswordStorage.verifyPassword("password" , user.password)) {
            throw new Exception("Wrong password!");
        }
        session.setAttribute("username", userName);
        return "redirect:/";
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping(path = "/message", method = RequestMethod.POST)
    public String addMessage(String message, HttpSession session) throws Exception {
        String name = (String) session.getAttribute("username");
        User user = users.findByUsername(name);
        if (user == null) {
            throw new Exception("Not logged in.");
        }
        Message m = new Message(message, user);
        messages.save(m);
        return "redirect:/";
    }

    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public String deleteMessage(int id, HttpSession session) throws Exception {
        String name = (String) session.getAttribute("username");
        User user = users.findByUsername(name);
        Message m = messages.findOne(id);
        if (user == null) {
            throw new Exception("Log in first");
        }
        else if (!user.username.equals(m.user.username)) {
            throw new Exception("Ah Ah Ah, you didn't say the magic word");
        }
        messages.delete(m);
        return "redirect:/";
    }
    @RequestMapping(path = "/edit", method = RequestMethod.GET)
    public String editGet(Model model, HttpSession session, int id) throws Exception {
        String name = (String) session.getAttribute("username");
        User user = users.findByUsername(name);
        Message m = messages.findOne(id);
        if (user == null) {
            throw new Exception("you're not logged in.");
        }
        model.addAttribute("user", user);
        model.addAttribute("message", m);
        return "edit";
    }
    @RequestMapping(path = "/edit", method = RequestMethod.POST)
    public String edit(String text, Integer id, HttpSession session) throws Exception{
        String name = (String) session.getAttribute("username");
        User user = users.findByUsername(name);
        Message m = messages.findOne(id);
        if (user == null) {
            throw new Exception("Log in first");
        }
        else if (!user.username.equals(m.user.username)) {
            throw new Exception("Ah Ah Ah, you didn't say the magic word");
        }
        m.setText(text);
        messages.save(m);
        return "redirect:/";
    }
}
