package io.elastest.demo.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory
            .getLogger(MessageController.class);

    private List<Message> messages = Collections
            .synchronizedList(new ArrayList<>());

    @GetMapping("/")
    public String showMessages(Model model) {

        model.addAttribute("messages", this.messages);

        return "index";
    }

    @PostMapping("/")
    public String newMessage(Message message) {

        messages.add(message);
        logger.info("Message added succesfully!");
        return "redirect:/";
    }

    @PostMapping("/clear")
    public String clearMessages() {

        this.messages = Collections.synchronizedList(new ArrayList<>());
        logger.info("All messages have been cleaned succesufully!");
        return "redirect:/";
    }

}
