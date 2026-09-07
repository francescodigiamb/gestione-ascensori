package com.francesco.gestione_ascensori.service;

import com.francesco.gestione_ascensori.model.*;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class RapportinoWordServiceTest {

    private final RapportinoWordService service = new RapportinoWordService();

    private Intervento esempio() {
        Luogo luogo = new Luogo("Pescara", "Zona centro");
        Impianto impianto = new Impianto("ASC-PE-001", "Via Roma 10", luogo, StatoImpianto.ATTIVO, null);
        impianto.setMatricola("PE-2024-0187");

        Utente tecnico = new Utente("mrossi", "x", "Mario Rossi", RuoloUtente.OPERATORE);

        Intervento i = new Intervento(impianto, TipoIntervento.MANUTENZIONE, StatoIntervento.COMPLETATO,
                LocalDate.of(2026, 9, 1), "Manutenzione ordinaria semestrale dell'impianto.");
        i.setId(12L);
        i.setAssegnatario(tecnico);
        i.setDataEsecuzione(LocalDate.of(2026, 9, 3));
        i.setInizioIntervento(LocalTime.of(8, 30));
        i.setFineIntervento(LocalTime.of(10, 15));
        i.setNoteTecnico("Verificati funi e paracadute.\nSostituita lampada di cabina.\nIngrassate le guide.");
        i.setCosto(new BigDecimal("120.00"));
        return i;
    }

    @Test
    void generaUnDocumentoWordLeggibile() throws Exception {
        byte[] docx = service.genera(esempio());

        assertTrue(docx.length > 0, "il documento non deve essere vuoto");

        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(docx));
             XWPFWordExtractor estrattore = new XWPFWordExtractor(doc)) {

            String testo = estrattore.getText();
            assertTrue(testo.contains("RAPPORTINO DI INTERVENTO"));
            assertTrue(testo.contains("ASC-PE-001"));
            assertTrue(testo.contains("PE-2024-0187"));
            assertTrue(testo.contains("Mario Rossi"));
            assertTrue(testo.contains("Sostituita lampada di cabina."));
            assertTrue(testo.contains("120,00 €"));
            assertFalse(doc.getAllPictures().isEmpty(), "il logo deve essere incorporato");
        }

        // copia di controllo per l'ispezione manuale
        Files.write(Path.of("target", "rapportino-esempio.docx"), docx);
    }

    @Test
    void componeIlNomeFileConImpiantoEData() {
        assertEquals("Rapportino_ASC-PE-001_03-09-2026.docx", service.nomeFile(esempio()));
    }
}
