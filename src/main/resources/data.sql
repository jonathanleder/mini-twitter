-- Inicialización de datos de prueba para la base de datos H2

-- Crear usuarios
INSERT INTO usuarios (username) VALUES ('juan_perez');
INSERT INTO usuarios (username) VALUES ('maria_garcia');
INSERT INTO usuarios (username) VALUES ('carlos_lopez');
INSERT INTO usuarios (username) VALUES ('ana_martinez');
INSERT INTO usuarios (username) VALUES ('luis_rodriguez');

-- Crear tweets normales
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (1, '¡Hola a todos! Este es mi primer tweet en la red social.', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (2, 'La programación es una pasión para mí. ¿A ustedes también les gusta?', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (3, 'Acabamos de lanzar nuestro nuevo proyecto. ¡Muy emocionante!', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (4, 'Bellas tardes para reflexionar sobre la vida y el futuro.', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (1, 'Java 21 trae muchas mejoras interesantes. Definitivamente versión recomendada.', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (5, '¿Alguien más sigue el partido de fútbol esta noche?', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (2, 'Spring Boot simplifica muchísimo el desarrollo de APIs REST.', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (3, 'El trabajo en equipo hace que todo sea posible. 🚀', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (4, 'Aprendiendo nuevas tecnologías cada día. Es un viaje emocionante.', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (1, '¿Cuál es su editor de código favorito? Yo soy fan de IntelliJ IDEA.', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (5, 'La inteligencia artificial está revolucionando el mundo.', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (2, 'Buenos días! Que sea un excelente día de trabajo.', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (3, 'Código limpio = Vida limpia. Siempre mantengo los estándares altos.', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (4, 'Mirando al atardecer, nada como la naturaleza para relajarse.', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (1, '¿Alguien se anima a un code challenge hoy?', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (5, 'La persistencia es la clave del éxito en la programación.', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (2, 'Compartiendo mi experiencia con microservicios en mi blog.', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (3, 'Feliz viernes a todos! A disfrutar del fin de semana.', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (4, 'La música me inspira mientras codifico.', CURRENT_TIMESTAMP);
INSERT INTO tweets (usuario_id, texto, fecha_creacion) VALUES (1, 'JUnit 5 es increíble. Testing made easy!', CURRENT_TIMESTAMP);

-- Crear retweets
-- Usuario 2 retweetea tweet 1 de usuario 1
INSERT INTO tweets (usuario_id, tweet_origen_id, fecha_creacion) VALUES (2, 1, CURRENT_TIMESTAMP);
-- Usuario 3 retweetea tweet 5 de usuario 1
INSERT INTO tweets (usuario_id, tweet_origen_id, fecha_creacion) VALUES (3, 5, CURRENT_TIMESTAMP);
-- Usuario 4 retweetea tweet 7 de usuario 2
INSERT INTO tweets (usuario_id, tweet_origen_id, fecha_creacion) VALUES (4, 7, CURRENT_TIMESTAMP);
-- Usuario 5 retweetea tweet 10 de usuario 1
INSERT INTO tweets (usuario_id, tweet_origen_id, fecha_creacion) VALUES (5, 10, CURRENT_TIMESTAMP);
-- Usuario 1 retweetea tweet 11 de usuario 5
INSERT INTO tweets (usuario_id, tweet_origen_id, fecha_creacion) VALUES (1, 11, CURRENT_TIMESTAMP);
