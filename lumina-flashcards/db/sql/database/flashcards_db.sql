CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY ,
    nome_usuario VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP WITH TIME ZONE DEFAULT  CURRENT_TIMESTAMP
);

CREATE TABLE flashcards(
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL,
    frente TEXT NOT NULL,
    verso TEXT NOT NULL,
    data_criacao TIMESTAMP WITH TIME ZONE DEFAULT  CURRENT_TIMESTAMP,
    data_ultima_revisao TIMESTAMP WITH TIME ZONE,
    intervalo_revisao INTEGER DEFAULT 1,
    fator_facilidade REAL DEFAULT 2.5,
    CONSTRAINT fk_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_flashcards_usuario_id ON flashcards(usuario_id);
CREATE INDEX  idx_usuarios_email ON usuarios(email);