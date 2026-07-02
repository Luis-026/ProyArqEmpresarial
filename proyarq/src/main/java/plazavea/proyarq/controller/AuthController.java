package plazavea.proyarq.controller;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import plazavea.proyarq.entity.Usuario;
import plazavea.proyarq.service.UsuarioService;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    @Value("${google.recaptcha.secret-key}")
    private String recaptchaSecret;

    @Value("${google.recaptcha.site-key}")
    private String recaptchaSiteKey;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping({"/", "/login"})
    public String loginPage(Model model) {
        model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam(value = "g-recaptcha-response", required = false) String recaptchaResponse,
            HttpSession session,
            Model model) {

        model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);

        if (recaptchaResponse == null || recaptchaResponse.isBlank()) {
            model.addAttribute("error", "Por favor verifica que no eres un robot.");
            return "login";
        }

        if (!verifyRecaptcha(recaptchaResponse)) {
            model.addAttribute("error", "La verificación reCAPTCHA falló. Intenta de nuevo.");
            return "login";
        }

        Usuario usuario = usuarioService.findByEmail(email).orElse(null);

        if (usuario != null && usuario.getPassword().equals(password) && usuario.getRol().equalsIgnoreCase(role)) {
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setAttribute("usuarioRol", usuario.getRol());
            if (role.equalsIgnoreCase("Administrador")) {
                return "redirect:/dashboard";
            }
            if (role.equalsIgnoreCase("Operario")) {
                return "redirect:/pedidos";
            }
            if (role.equalsIgnoreCase("Jefe de Tienda")) {
                return "redirect:/reportes";
            }
            return "redirect:/dashboard";
        }

        model.addAttribute("error", "Correo, contraseña o rol incorrecto");
        return "login";
    }

    private boolean verifyRecaptcha(String recaptchaResponse) {
        try {
            String url = "https://www.google.com/recaptcha/api/siteverify";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setDoOutput(true);

            String params = "secret=" + URLEncoder.encode(recaptchaSecret, StandardCharsets.UTF_8)
                    + "&response=" + URLEncoder.encode(recaptchaResponse, StandardCharsets.UTF_8);
            try (OutputStream os = con.getOutputStream()) {
                os.write(params.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int responseCode = con.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response.toString());
                return jsonNode.path("success").asBoolean(false);
            }
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
