
package unrn.web;

import org.junit.jupiter.api.BeforeEach;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityManagerFactory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = unrn.main.Main.class)
@AutoConfigureMockMvc
class UsuarioControllerWebIT {

    @Autowired
    private EntityManagerFactory emf;
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() throws Exception {
        // Crea un usuario antes de cada test
        emf.getSchemaManager().truncate();
        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"usuarioTest\"}"));
    }

    @Test
    @DisplayName("GET /usuarios retorna lista de usuarios en JSON")
    void getUsuarios_retornaListaUsuariosJson() throws Exception {
        // Setup: (La BD debe tener datos de usuarios)
        // Ejercitación y Verificación:
        mockMvc.perform(get("/usuarios")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username").exists());
    }

    @Test
    @DisplayName("GET /usuarios/{id} retorna 404 si no existe el usuario")
    void getUsuarioPorId_usuarioNoExiste_retorna404() throws Exception {
        // Setup: (ID que no existe)
        // Ejercitación y Verificación:
        mockMvc.perform(get("/usuarios/9999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
