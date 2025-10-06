package cloud.luigi99.solar.playground.app.presentation.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("title", "Dashboard");
        return "dashboard";
    }

    @GetMapping("/chat")
    public String chat(Model model) {
        model.addAttribute("title", "Chat");
        return "chat";
    }

    @GetMapping("/files")
    public String files(Model model) {
        model.addAttribute("title", "Files");
        return "files";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("title", "Profile");
        return "profile";
    }
}
