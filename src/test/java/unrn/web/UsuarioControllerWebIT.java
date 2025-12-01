package unrn.web;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import unrn.service.TwitterService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = unrn.main.Main.class)
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test-integracion")
public class UsuarioControllerWebIT {

        @Autowired
        private EntityManagerFactory emf;
        @Autowired
        private TwitterService twitterService;
        @Autowired
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
        @DisplayName("POST /usuarios con username duplicado retorna error 400")
        void postUsuario_usernameDuplicado_error400() throws Exception {
                // Setup: ya existe usuarioTest por @BeforeEach
                mockMvc.perform(post("/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"usuarioTest\"}"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /usuarios con username vacío retorna error 400 y texto plano")
        void postUsuario_usernameVacio_error400() throws Exception {
                mockMvc.perform(post("/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"\"}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("text/plain;charset=UTF-8"));
        }

        @Test
        @DisplayName("POST /usuarios con JSON inválido retorna error 400 y texto plano")
        void postUsuario_jsonInvalido_error400() throws Exception {
                mockMvc.perform(post("/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("text/plain;charset=UTF-8"));
        }

        @Test
        @DisplayName("DELETE /usuarios/{id} elimina usuario existente")
        void deleteUsuario_existente_noContent() throws Exception {
                // Buscar el usuario para obtener el id
                var result = mockMvc.perform(get("/usuarios/usuarioTest")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andReturn();
                String json = result.getResponse().getContentAsString();
                Number idNum = com.jayway.jsonpath.JsonPath.read(json, "$.id");
                Long usuarioId = idNum.longValue();
                // Eliminar usuario
                mockMvc.perform(delete("/usuarios/" + usuarioId))
                                .andExpect(status().isNoContent());
                // Verificar que ya no existe
                mockMvc.perform(get("/usuarios/" + usuarioId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("DELETE /usuarios/{id} con id inexistente retorna 204 No Content")
        void deleteUsuario_inexistente_noContent() throws Exception {
                mockMvc.perform(delete("/usuarios/9999"))
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("GET /usuarios/{username} retorna usuario existente")
        void getUsuarioPorUsername_existente_ok() throws Exception {
                mockMvc.perform(get("/usuarios/usuarioTest")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("usuarioTest"));
        }

        @Test
        @DisplayName("GET /usuarios/{username} retorna 404 si no existe el usuario")
        void getUsuarioPorUsername_noExiste_404() throws Exception {
                mockMvc.perform(get("/usuarios/noexiste")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /usuarios retorna lista de usuarios en JSON")
        void ListarUsuarios() throws Exception {
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
