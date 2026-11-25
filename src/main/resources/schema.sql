-- Model relacional de la pràctica (Patient, Patient_disease, Disease).

CREATE TABLE IF NOT EXISTS patient (
    id_patient   SERIAL PRIMARY KEY,
    given_name   VARCHAR(100) NOT NULL,
    family_name  VARCHAR(150) NOT NULL,
    nif          VARCHAR(20)  NOT NULL,
    birth_date   DATE,
    gender       VARCHAR(5),
    address      VARCHAR(200),
    city         VARCHAR(100),
    postal_code  VARCHAR(10),
    country      VARCHAR(5),
    telephone    VARCHAR(30),
    email        VARCHAR(150)
);
