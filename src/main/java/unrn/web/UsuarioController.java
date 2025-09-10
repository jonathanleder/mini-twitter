package unrn.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unrn.DTOs.NuevoUsuario;
import unrn.DTOs.UsuarioDto;
import unrn.service.TwitterService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final TwitterService service;

    public UsuarioController(TwitterService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody NuevoUsuario nuevoUsuario) {
        try {
            service.crearUsuario(nuevoUsuario.username());
            return ResponseEntity.status(201).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listarUsuarios() {
        var usuarios = service.listarUsuarios()
                .stream()
                .map(u -> new UsuarioDto(u.getId(), u.obtenerUserName()))
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> buscarUsuario(@PathVariable String username) {
        return service.buscarUsuarioPorUserName(username)
                .map(u -> ResponseEntity.ok(new UsuarioDto(u.getId(), u.obtenerUserName())))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            service.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
