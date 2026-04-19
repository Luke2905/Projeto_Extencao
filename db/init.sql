CREATE DATABASE IF NOT EXISTS monitoramento_represa;
USE monitoramento_represa;

CREATE TABLE REPRESA (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100)
);

CREATE TABLE MONITORAMENTO (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_represa INT,
    data DATE,
    chuva_mm DOUBLE,
    volume_util_percent DOUBLE,
    chuva_acumulada_mes_mm DOUBLE,
    FOREIGN KEY (id_represa) REFERENCES REPRESA(id)
);

-- Dados de teste
INSERT INTO REPRESA (nome) VALUES ('Represa A'), ('Represa B');

INSERT INTO MONITORAMENTO (id_represa, data, chuva_mm, volume_util_percent, chuva_acumulada_mes_mm)
VALUES
(1, '2024-01-01', 10.5, 75.2, 100.0),
(2, '2024-01-02', 5.0, 60.0, 80.0);