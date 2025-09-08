
-- Datos de prueba para Mini Twitter

-- Usuarios
INSERT INTO usuarios (id, username) VALUES (1, 'alice123');
INSERT INTO usuarios (id, username) VALUES (2, 'bob456');
INSERT INTO usuarios (id, username) VALUES (3, 'charlie789');

-- Tweets normales
INSERT INTO tweets (id, usuario_id, texto) VALUES (1, 1, 'Hola mundo!');
INSERT INTO tweets (id, usuario_id, texto) VALUES (2, 2, 'Primer tweet de Bob');
INSERT INTO tweets (id, usuario_id, texto) VALUES (3, 3, 'Charlie presente');

-- Retweets
INSERT INTO tweets (id, usuario_id, texto, tweet_origen_id) VALUES (4, 2, null, 1); -- Bob retweetea a Alice
INSERT INTO tweets (id, usuario_id, texto, tweet_origen_id) VALUES (5, 3, null, 2); -- Charlie retweetea a Bob