
-- Datos de prueba para Mini Twitter

-- Usuarios

INSERT INTO usuarios (id, username) VALUES (1, 'alicia');
INSERT INTO usuarios (id, username) VALUES (2, 'roberto');
INSERT INTO usuarios (id, username) VALUES (3, 'charlie789');
INSERT INTO usuarios (id, username) VALUES (4, 'daniela');
INSERT INTO usuarios (id, username) VALUES (5, 'emiliano');

-- Tweets normales

INSERT INTO tweets (id, usuario_id, texto) VALUES (1, 1, 'Hola mundo!');
INSERT INTO tweets (id, usuario_id, texto) VALUES (2, 2, 'Primer tweet de Bob');
INSERT INTO tweets (id, usuario_id, texto) VALUES (3, 3, 'Charlie presente');
INSERT INTO tweets (id, usuario_id, texto) VALUES (4, 4, 'Daniela saluda');
INSERT INTO tweets (id, usuario_id, texto) VALUES (5, 5, 'Emiliano reportando');

-- Retweets

INSERT INTO tweets (id, usuario_id, texto, tweet_origen_id) VALUES (6, 2, null, 1); -- Bob retweetea a Alice
INSERT INTO tweets (id, usuario_id, texto, tweet_origen_id) VALUES (7, 3, null, 2); -- Charlie retweetea a Bob
INSERT INTO tweets (id, usuario_id, texto, tweet_origen_id) VALUES (8, 4, null, 3); -- Daniela retweetea a Charlie
INSERT INTO tweets (id, usuario_id, texto, tweet_origen_id) VALUES (9, 5, null, 4); -- Emiliano retweetea a Daniela