package unrn.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@SpringBootTest(classes = unrn.main.Main.class)
@AutoConfigureMockMvc

class TweetControllerWebIT {
        private static final String USERNAME = "tweetuser";
        private static final String RETWEETER = "retweeter";
        private static final String TEXTO_TWEET = "Hola Twitter!";
        private static final String TEXTO_ORIGINAL = "Original";

        @Autowired
        private MongoTemplate mongoTemplate;

        @Autowired
        private MockMvc mockMvc;

        private Long usuarioId;

        @BeforeEach
        void setUp() throws Exception {
                mongoTemplate.getDb().drop();
                usuarioId = crearUsuarioYObtenerId(USERNAME);
        }

        private Long crearUsuarioYObtenerId(String username) throws Exception {
                var createResult = mockMvc.perform(post("/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"" + username + "\"}"))
                                .andReturn();
                int status = createResult.getResponse().getStatus();
                if (status != 201) {
                        throw new RuntimeException("No se pudo crear el usuario: "
                                        + createResult.getResponse().getContentAsString());
                }
                var result = mockMvc.perform(get("/usuarios/" + username)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andReturn();
                String json = result.getResponse().getContentAsString();
                Number idNum = JsonPath.read(json, "$.id");
                return idNum.longValue();
        }

        private Long crearTweetYObtenerId(Long usuarioId, String texto) throws Exception {
                var tweetResult = mockMvc.perform(post("/tweets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"usuarioId\":" + usuarioId + ",\"texto\":\"" + texto + "\"}"))
                                .andReturn();
                assertEquals(201, tweetResult.getResponse().getStatus(), "El tweet no se creó correctamente");
                // Obtener id del tweet creado
                var listarResult = mockMvc.perform(get("/tweets/usuario/" + usuarioId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andReturn();
                String tweetsJson = listarResult.getResponse().getContentAsString();
                Number idNum = JsonPath.read(tweetsJson, "$.tweets[0].id");
                return idNum.longValue();
        }

        @Test
        @DisplayName("POST /tweets crea un tweet y lo lista correctamente")
        void crearTweet_yListarTweets() throws Exception {
                // Crear tweet
                Long tweetId = crearTweetYObtenerId(usuarioId, TEXTO_TWEET);
                // Listar tweets del usuario y verificar campos
                var listarResult = mockMvc.perform(get("/tweets/usuario/" + usuarioId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andReturn();
                String tweetsJson = listarResult.getResponse().getContentAsString();
                List<?> tweets = JsonPath.read(tweetsJson, "$.tweets[*]");
                assertAll("Verificar tweet listado",
                                () -> assertFalse(tweets.isEmpty(), "La lista de tweets no debe estar vacía"),
                                () -> {
                                        Number idNum = JsonPath.read(tweetsJson, "$.tweets[0].id");
                                        assertEquals(tweetId, idNum.longValue());
                                },
                                () -> assertEquals(TEXTO_TWEET, JsonPath.read(tweetsJson, "$.tweets[0].texto")),
                                () -> assertNull(JsonPath.read(tweetsJson, "$.tweets[0].origenId"),
                                                "No debe tener origenId"));
        }

        @Test
        @DisplayName("POST /tweets/retweet crea un retweet correctamente")
        void crearRetweet() throws Exception {
                // Crear tweet original
                Long tweetId = crearTweetYObtenerId(usuarioId, TEXTO_ORIGINAL);
                // Crear otro usuario
                Long retweeterId = crearUsuarioYObtenerId(RETWEETER);
                // Crear retweet
                mockMvc.perform(post("/tweets/retweet")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"usuarioId\":" + retweeterId + ",\"tweetOrigenId\":" + tweetId + "}"))
                                .andExpect(status().isCreated());
                // Verificar que el retweet aparece en la lista del retweeter
                var listarResult = mockMvc.perform(get("/tweets/usuario/" + retweeterId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andReturn();

                // Debugging: print status and content if not 200
                int status = listarResult.getResponse().getStatus();
                String tweetsJson = listarResult.getResponse().getContentAsString();

                assertEquals(200, status, "Expected 200 but got " + status + " with content: " + tweetsJson);

                assertAll("Verificar retweet listado",
                                () -> {
                                        Number origenIdNum = JsonPath.read(tweetsJson, "$.tweets[0].origenId");
                                        assertEquals(tweetId, origenIdNum.longValue());
                                        // Obtener el texto del tweet original usando el id de origen
                                        var originalResult = mockMvc.perform(get("/tweets/usuario/" + usuarioId)
                                                        .accept(MediaType.APPLICATION_JSON))
                                                        .andExpect(status().isOk())
                                                        .andReturn();
                                        String originalTweetsJson = originalResult.getResponse().getContentAsString();
                                        String textoOriginal = JsonPath.read(originalTweetsJson, "$.tweets[0].texto");
                                        assertEquals(TEXTO_ORIGINAL, textoOriginal);
                                },
                                () -> assertNull(JsonPath.read(tweetsJson, "$.tweets[0].texto"),
                                                "El texto del retweet debe ser null"));
        }

        @Test
        @DisplayName("POST /tweets con usuario inexistente retorna error")
        void crearTweet_usuarioInexistente_error() throws Exception {
                mockMvc.perform(post("/tweets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"usuarioId\":9999,\"texto\":\"fail\"}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("application/json"));
        }
}
