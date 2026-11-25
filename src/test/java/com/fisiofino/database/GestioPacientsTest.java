package com.fisiofino.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fisiofino.model.MalaltiaPacient;
import com.fisiofino.model.Pacient;

/**
 * Tests mínims de la base de dades.
 * Les taules les crea docker compose (schema.sql) en arrencar postgres-test.
 * Abans de cada test es buiden, així sempre es parteix de taules buides.
 * Fa servir conn.test.properties (arrel del projecte): una base de dades de
 * PROVES
 */
class GestioPacientsTest {

    // Dades d'exemple del XML (fictícies)
    private static final String DNI_LAURA = "88999121H";
    private static final String DNI_PERE = "44999121S";
    private static final String HIPERTENSIO = "38341003"; // codi SNOMED
    private static final String ASMA = "195967001"; // codi SNOMED

    GestioPacients gestio = new GestioPacients();

    @BeforeAll
    static void iniciaElPool() throws Exception {
        Connexio.iniciaConnexio("conn.test.properties");
    }

    @AfterAll
    static void tancaElPool() {
        Connexio.tancaConnexio();
    }

    @BeforeEach
    void estatInicial() throws Exception {
        // Les taules ja les ha creat docker compose (schema.sql); només les buidem
        executa("TRUNCATE patient_disease RESTART IDENTITY CASCADE");
        executa("TRUNCATE disease RESTART IDENTITY CASCADE");
        executa("TRUNCATE patient RESTART IDENTITY CASCADE");
    }

    @Test
    void createPatientInsereixUnPacient() throws Exception {
        gestio.CreatePatient(laura());

        assertEquals(1, compta("patient"));
    }

    @Test
    void findBydniNieTrobaElPacient() throws Exception {
        gestio.CreatePatient(laura());

        Pacient p = gestio.findBydniNie(DNI_LAURA);

        assertNotNull(p, "Hauria de trobar el pacient");
        assertEquals("Laura", p.getGivenName());
    }

    @Test
    void findBydniNieRetornaNullSiNoExisteix() throws Exception {
        assertNull(gestio.findBydniNie("00000000X"));
    }

    @Test
    void findAllPacientsRetornaTotsElsPacients() throws Exception {
        gestio.CreatePatient(laura());
        gestio.CreatePatient(pere());

        assertEquals(2, gestio.findAllPacients().size());
    }

    // ---------- findMalaltia / CreateMalaltia ----------

    @Test
    void createMalaltiaInsereixLaMalaltia() throws Exception {
        assertTrue(gestio.CreateMalaltia(hipertensio(DNI_LAURA)));

        assertEquals(1, compta("disease"));
    }

    @Test
    void findMalaltiaTrobaUnaMalaltiaCreada() throws Exception {
        gestio.CreateMalaltia(hipertensio(DNI_LAURA));

        assertTrue(gestio.findMalaltia(HIPERTENSIO));
        assertFalse(gestio.findMalaltia(ASMA), "Només s'ha creat la hipertensió");
    }

    // ---------- CreateMalaltiaPacient / findMalaltiaPacient ----------

    @Test
    void createMalaltiaPacientRelacionaElPacientAmbLaMalaltia() throws Exception {
        int idLaura = gestio.CreatePatient(laura());
        gestio.CreateMalaltia(hipertensio(DNI_LAURA));

        assertTrue(gestio.CreateMalaltiaPacient(hipertensio(DNI_LAURA)));

        assertEquals(1, compta("patient_disease"));
        assertEquals("Activo", estat(idLaura, HIPERTENSIO));
    }

    @Test
    void findMalaltiaPacientRetornaCertSiLaTeActiva() throws Exception {
        gestio.CreatePatient(laura());
        donaMalaltia(hipertensio(DNI_LAURA));

        assertTrue(gestio.findMalaltiaPacient(DNI_LAURA, HIPERTENSIO, "Activo"));
    }

    @Test
    void findMalaltiaPacientRetornaFalsSiNoTeAquestEstat() throws Exception {
        gestio.CreatePatient(laura());
        donaMalaltia(hipertensio(DNI_LAURA));

        assertFalse(gestio.findMalaltiaPacient(DNI_LAURA, HIPERTENSIO, "Resuelto"));
    }

    @Test
    void findMalaltiaPacientRetornaFalsPerAUnAltrePacient() throws Exception {
        gestio.CreatePatient(laura());
        gestio.CreatePatient(pere());
        donaMalaltia(hipertensio(DNI_LAURA));

        assertFalse(gestio.findMalaltiaPacient(DNI_PERE, HIPERTENSIO, "Activo"));
    }

    // ---------- findDiseaseBydniNie ----------

    @Test
    void findDiseaseBydniNieRetornaLesMalaltiesDelPacient() throws Exception {
        gestio.CreatePatient(laura());
        gestio.CreatePatient(pere());
        donaMalaltia(hipertensio(DNI_LAURA));
        donaMalaltia(asmaLaura());
        donaMalaltia(hipertensio(DNI_PERE));

        assertEquals(2, gestio.findDiseaseBydniNie(DNI_LAURA).size());
        assertEquals(1, gestio.findDiseaseBydniNie(DNI_PERE).size());
    }

    @Test
    void findDiseaseBydniNieRetornaCodiNomEstatITractament() throws Exception {
        gestio.CreatePatient(laura());
        donaMalaltia(asmaLaura());

        MalaltiaPacient asma = gestio.findDiseaseBydniNie(DNI_LAURA).get(0);

        assertEquals(ASMA, asma.getCode());
        assertEquals("Asma", asma.getName());
        assertEquals("Controlado", asma.getStatus());
        assertEquals(asmaLaura().getTreatment(), asma.getTreatment());
    }

    // ---------- updateStatus ----------

    @Test
    void updateStatusModificaLEstat() throws Exception {
        int idLaura = gestio.CreatePatient(laura());
        donaMalaltia(hipertensio(DNI_LAURA));

        assertTrue(gestio.updateStatus(idLaura, HIPERTENSIO, "Resuelto"));

        assertEquals("Resuelto", estat(idLaura, HIPERTENSIO));
    }

    @Test
    void updateStatusRetornaFalsSiElPacientNoTeLaMalaltia() throws Exception {
        int idLaura = gestio.CreatePatient(laura());

        assertFalse(gestio.updateStatus(idLaura, HIPERTENSIO, "Resuelto"));
    }

    @Test
    void updateStatusNoModificaLaMalaltiaDUnAltrePacient() throws Exception {
        int idLaura = gestio.CreatePatient(laura());
        int idPere = gestio.CreatePatient(pere());
        donaMalaltia(hipertensio(DNI_LAURA));
        donaMalaltia(hipertensio(DNI_PERE));

        gestio.updateStatus(idLaura, HIPERTENSIO, "Resuelto");

        assertEquals("Activo", estat(idPere, HIPERTENSIO), "Només s'ha de canviar l'estat de Laura");
    }

    // ---------- Ajudes: dades de prova ----------

    // Mateixes dades que pacientes_bluebutton.xml (pacients 1 i 2), sense llegir el
    // fitxer.

    private static Pacient laura() {
        return pacient(DNI_LAURA, "Laura", "Exemple Roca", "F", LocalDate.of(1985, 3, 14),
                "Carrer de l'Exemple, 12, 3r 1a", "Barcelona", "08001", "+34-600-000-000",
                "laura.martinez@example.com");
    }

    private static Pacient pere() {
        return pacient(DNI_PERE, "Pere", "Lopez Vagafa", "M", LocalDate.of(1972, 11, 2),
                "Avinguda Fictícia, 45", "Girona", "17001", "+34-611-111-111", "pere.vidal@example.com");
    }

    private static MalaltiaPacient hipertensio(String dni) {
        return malaltia(dni, HIPERTENSIO, "Hipertensión arterial", "Activo", "Enalapril 10 mg, Cada 24 h");
    }

    private static MalaltiaPacient asmaLaura() {
        return malaltia(DNI_LAURA, ASMA, "Asma", "Controlado",
                "Budesonida inhalada 200 mcg, Cada 12 h; Salbutamol inhalado 100 mcg, A demanda");
    }

    private static Pacient pacient(String dni, String nom, String cognom, String genere, LocalDate naixement,
            String adreca, String ciutat, String codiPostal, String telefon, String email) {
        Pacient p = new Pacient();
        p.setDni(dni);
        p.setGivenName(nom);
        p.setFamilyName(cognom);
        p.setGender(genere);
        p.setBirthDate(naixement);
        p.setAddress(adreca);
        p.setCity(ciutat);
        p.setPostalCode(codiPostal);
        p.setCountry("ES");
        p.setTelephone(telefon);
        p.setEmail(email);
        return p;
    }

    private static MalaltiaPacient malaltia(String dni, String codi, String nom, String estat, String tractament) {
        MalaltiaPacient mp = new MalaltiaPacient();
        mp.setDniPacient(dni);
        mp.setCode(codi);
        mp.setName(nom);
        mp.setStatus(estat);
        mp.setTreatment(tractament);
        return mp;
    }

    /** Desa la malaltia (si no hi és) i la relaciona amb el seu pacient. */
    private void donaMalaltia(MalaltiaPacient mp) throws Exception {
        if (!gestio.findMalaltia(mp.getCode())) {
            gestio.CreateMalaltia(mp);
        }
        gestio.CreateMalaltiaPacient(mp);
    }

    // ---------- Ajudes: SQL directe a la BD de proves ----------

    private static Connection obreConnexio() throws Exception {
        Properties p = new Properties();
        try (InputStream is = Files.newInputStream(Path.of("conn.test.properties"))) {
            p.load(is);
        }
        return DriverManager.getConnection(p.getProperty("DB_URL"), p.getProperty("DB_USER"),
                p.getProperty("DB_PASSWORD"));
    }

    /**
     * Executa una sentència; si falla (p. ex. la taula encara no existeix) no fa
     * res.
     */
    private static void executa(String sql) throws Exception {
        try (Connection c = obreConnexio(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException e) {
            // ignorem l'error
        }
    }

    /**
     * Executa una consulta que retorna un sol valor (o null si no hi ha cap fila).
     */
    private static Object valor(String sql) throws Exception {
        try (Connection c = obreConnexio(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            return rs.next() ? rs.getObject(1) : null;
        }
    }

    private static int compta(String taula) throws Exception {
        return ((Number) valor("SELECT count(*) FROM " + taula)).intValue();
    }

    private static String estat(int idPacient, String codi) throws Exception {
        return (String) valor("SELECT status FROM patient_disease WHERE id_patient = " + idPacient
                + " AND snomed_code = '" + codi + "'");
    }
}
