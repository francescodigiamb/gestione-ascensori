package com.francesco.gestione_ascensori.service;

import com.francesco.gestione_ascensori.model.Intervento;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Genera il rapportino di un intervento in formato Word (.docx),
 * impaginato con logo e colori aziendali.
 */
@Service
public class RapportinoWordService {

    private static final String BLU           = "214F8D";
    private static final String ROSSO         = "C73834";
    private static final String GRIGIO        = "6B7280";
    private static final String GRIGIO_CHIARO = "9CA3AF";
    private static final String NERO          = "111111";
    private static final String BORDO         = "E5E7EB";
    private static final String SFONDO_LABEL  = "F4F6F9";
    private static final String FONT          = "Calibri";

    private static final String LOGO = "static/img/logo-chiavaroli-ascensori.png";

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter ORA  = DateTimeFormatter.ofPattern("HH:mm");
    private static final DecimalFormat EURO =
            new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.ITALY));

    /** Costruisce il .docx del rapportino. */
    public byte[] genera(Intervento i) {
        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            impostaMargini(doc);
            intestazione(doc);
            titolo(doc, i);

            titoloSezione(doc, "Impianto");
            List<String[]> impianto = new ArrayList<>();
            impianto.add(new String[]{"Impianto", valore(i.getImpianto().getNome())});
            if (nonVuoto(i.getImpianto().getMatricola())) {
                impianto.add(new String[]{"Matricola", i.getImpianto().getMatricola()});
            }
            impianto.add(new String[]{"Indirizzo", valore(i.getImpianto().getIndirizzo())});
            if (i.getImpianto().getLuogo() != null) {
                impianto.add(new String[]{"Località", i.getImpianto().getLuogo().getNome()});
            }
            tabellaDati(doc, impianto);

            titoloSezione(doc, "Intervento");
            List<String[]> intervento = new ArrayList<>();
            intervento.add(new String[]{"Tipo", i.getTipo().getLabel()});
            intervento.add(new String[]{"Stato", i.getStato().getLabel()});
            intervento.add(new String[]{"Data programmata", data(i.getDataProgrammata())});
            intervento.add(new String[]{"Data esecuzione", data(i.getDataEsecuzione())});
            intervento.add(new String[]{"Orario", orario(i)});
            intervento.add(new String[]{"Tecnico",
                    i.getAssegnatario() != null ? i.getAssegnatario().getNomeCompleto() : "—"});
            if (i.getCosto() != null) {
                intervento.add(new String[]{"Costo", EURO.format(i.getCosto()) + " €"});
            }
            tabellaDati(doc, intervento);

            titoloSezione(doc, "Descrizione del lavoro");
            testo(doc, valore(i.getDescrizione()));

            titoloSezione(doc, "Lavori eseguiti");
            testo(doc, nonVuoto(i.getNoteTecnico())
                    ? i.getNoteTecnico()
                    : "Rapportino non ancora compilato.");

            firme(doc);
            piePagina(doc);

            doc.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new IllegalStateException("Impossibile generare il rapportino Word", e);
        }
    }

    /** Nome file suggerito, es. Rapportino_ASC-PE-001_03-09-2026.docx */
    public String nomeFile(Intervento i) {
        LocalDate riferimento = i.getDataEsecuzione() != null
                ? i.getDataEsecuzione() : i.getDataProgrammata();
        String suffisso = riferimento != null
                ? DateTimeFormatter.ofPattern("dd-MM-yyyy").format(riferimento)
                : String.valueOf(i.getId());
        return "Rapportino_" + pulisci(i.getImpianto().getNome()) + "_" + suffisso + ".docx";
    }

    // ── blocchi del documento ─────────────────────────────

    private void impostaMargini(XWPFDocument doc) {
        CTPageMar margini = doc.getDocument().getBody().addNewSectPr().addNewPgMar();
        margini.setTop(BigInteger.valueOf(1000));
        margini.setBottom(BigInteger.valueOf(1000));
        margini.setLeft(BigInteger.valueOf(1000));
        margini.setRight(BigInteger.valueOf(1000));
    }

    private void intestazione(XWPFDocument doc) {
        XWPFParagraph logo = doc.createParagraph();
        logo.setSpacingAfter(0);
        aggiungiLogo(logo.createRun());

        XWPFParagraph nome = doc.createParagraph();
        nome.setSpacingBefore(60);
        nome.setSpacingAfter(0);
        run(nome, "CHIAVAROLI ASCENSORI", 13, true, BLU);

        XWPFParagraph sottotitolo = doc.createParagraph();
        sottotitolo.setSpacingAfter(160);
        run(sottotitolo, "Manutenzione e assistenza impianti elevatori", 8, false, GRIGIO);
        bordoInferiore(sottotitolo, ROSSO, 12);
    }

    private void aggiungiLogo(XWPFRun run) {
        try (InputStream perMisura = new ClassPathResource(LOGO).getInputStream();
             InputStream perImmagine = new ClassPathResource(LOGO).getInputStream()) {

            BufferedImage img = ImageIO.read(perMisura);
            double altezza = 38;
            double larghezza = (img != null && img.getHeight() > 0)
                    ? altezza * img.getWidth() / img.getHeight()
                    : 120;
            run.addPicture(perImmagine, XWPFDocument.PICTURE_TYPE_PNG, "logo.png",
                    Units.toEMU(larghezza), Units.toEMU(altezza));

        } catch (Exception ignored) {
            // logo non disponibile: il documento resta valido anche senza immagine
        }
    }

    private void titolo(XWPFDocument doc, Intervento i) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(240);
        p.setSpacingAfter(0);
        run(p, "RAPPORTINO DI INTERVENTO", 17, true, BLU);

        XWPFParagraph sotto = doc.createParagraph();
        sotto.setSpacingAfter(60);
        run(sotto, "N. " + i.getId() + "  ·  " + i.getTipo().getLabel()
                + "  ·  " + i.getStato().getLabel(), 9, false, GRIGIO);
    }

    private void titoloSezione(XWPFDocument doc, String testo) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(280);
        p.setSpacingAfter(80);
        XWPFRun r = run(p, testo.toUpperCase(Locale.ITALY), 8, true, GRIGIO_CHIARO);
        r.setCharacterSpacing(20);
    }

    private void tabellaDati(XWPFDocument doc, List<String[]> righe) {
        XWPFTable tabella = doc.createTable(righe.size(), 2);
        tabella.setWidth("100%");
        bordiTabella(tabella);

        for (int r = 0; r < righe.size(); r++) {
            XWPFTableRow riga = tabella.getRow(r);

            XWPFTableCell etichetta = riga.getCell(0);
            etichetta.setWidth("32%");
            sfondo(etichetta, SFONDO_LABEL);
            cella(etichetta, righe.get(r)[0], true, GRIGIO);

            XWPFTableCell valore = riga.getCell(1);
            valore.setWidth("68%");
            cella(valore, righe.get(r)[1], false, NERO);
        }
    }

    private void testo(XWPFDocument doc, String contenuto) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingAfter(0);
        p.setSpacingBetween(1.25);
        XWPFRun r = p.createRun();
        stile(r, 10, false, NERO);
        String[] righe = contenuto.split("\\r?\\n");
        for (int k = 0; k < righe.length; k++) {
            if (k > 0) r.addBreak();
            r.setText(righe[k]);
        }
    }

    private void firme(XWPFDocument doc) {
        doc.createParagraph().setSpacingBefore(600);

        XWPFTable tabella = doc.createTable(2, 2);
        tabella.setWidth("100%");
        senzaBordi(tabella);

        String[] etichette = {"Firma del tecnico", "Firma del cliente"};
        for (int c = 0; c < 2; c++) {
            XWPFTableCell linea = tabella.getRow(0).getCell(c);
            linea.setWidth("50%");
            XWPFParagraph pLinea = linea.getParagraphs().get(0);
            pLinea.setSpacingAfter(0);
            run(pLinea, "", 10, false, NERO);
            bordoInferiore(pLinea, GRIGIO_CHIARO, 6);

            XWPFTableCell didascalia = tabella.getRow(1).getCell(c);
            didascalia.setWidth("50%");
            XWPFParagraph pTesto = didascalia.getParagraphs().get(0);
            pTesto.setSpacingBefore(60);
            run(pTesto, etichette[c], 8, false, GRIGIO);
        }
    }

    private void piePagina(XWPFDocument doc) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(600);
        p.setAlignment(ParagraphAlignment.CENTER);
        run(p, "Documento generato il "
                + DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm").format(LocalDateTime.now())
                + " — Chiavaroli Ascensori", 7, false, GRIGIO_CHIARO);
    }

    // ── helper di formattazione ───────────────────────────

    private XWPFRun run(XWPFParagraph p, String testo, int puntiFont, boolean grassetto, String colore) {
        XWPFRun r = p.createRun();
        stile(r, puntiFont, grassetto, colore);
        r.setText(testo);
        return r;
    }

    private void stile(XWPFRun r, int puntiFont, boolean grassetto, String colore) {
        r.setFontFamily(FONT);
        r.setFontSize(puntiFont);
        r.setBold(grassetto);
        r.setColor(colore);
    }

    private void cella(XWPFTableCell cella, String testo, boolean grassetto, String colore) {
        XWPFParagraph p = cella.getParagraphs().get(0);
        p.setSpacingBefore(40);
        p.setSpacingAfter(40);
        run(p, testo, 9, grassetto, colore);
        cella.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
    }

    private void sfondo(XWPFTableCell cella, String colore) {
        CTTcPr pr = cella.getCTTc().isSetTcPr() ? cella.getCTTc().getTcPr() : cella.getCTTc().addNewTcPr();
        CTShd shd = pr.isSetShd() ? pr.getShd() : pr.addNewShd();
        shd.setVal(STShd.CLEAR);
        shd.setColor("auto");
        shd.setFill(colore);
    }

    private void bordiTabella(XWPFTable t) {
        t.setTopBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, BORDO);
        t.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, BORDO);
        t.setLeftBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, BORDO);
        t.setRightBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, BORDO);
        t.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, BORDO);
        t.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, BORDO);
    }

    private void senzaBordi(XWPFTable t) {
        t.setTopBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
        t.setBottomBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
        t.setLeftBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
        t.setRightBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
        t.setInsideHBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
        t.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
    }

    private void bordoInferiore(XWPFParagraph p, String colore, int spessore) {
        CTPPr pr = p.getCTP().isSetPPr() ? p.getCTP().getPPr() : p.getCTP().addNewPPr();
        CTPBdr bordi = pr.isSetPBdr() ? pr.getPBdr() : pr.addNewPBdr();
        CTBorder bordo = bordi.isSetBottom() ? bordi.getBottom() : bordi.addNewBottom();
        bordo.setVal(STBorder.SINGLE);
        bordo.setSz(BigInteger.valueOf(spessore));
        bordo.setSpace(BigInteger.valueOf(1));
        bordo.setColor(colore);
    }

    private String data(LocalDate d) {
        return d != null ? DATA.format(d) : "—";
    }

    private String orario(Intervento i) {
        if (i.getInizioIntervento() == null && i.getFineIntervento() == null) return "—";

        StringBuilder sb = new StringBuilder();
        sb.append(i.getInizioIntervento() != null ? ORA.format(i.getInizioIntervento()) : "—");
        sb.append(" → ");
        sb.append(i.getFineIntervento() != null ? ORA.format(i.getFineIntervento()) : "—");

        if (i.getInizioIntervento() != null && i.getFineIntervento() != null) {
            Duration d = Duration.between(i.getInizioIntervento(), i.getFineIntervento());
            if (!d.isNegative() && !d.isZero()) {
                long ore = d.toHours();
                sb.append("   (").append(ore > 0 ? ore + "h " : "").append(d.toMinutesPart()).append("m)");
            }
        }
        return sb.toString();
    }

    private boolean nonVuoto(String s) {
        return s != null && !s.isBlank();
    }

    private String valore(String s) {
        return nonVuoto(s) ? s : "—";
    }

    private String pulisci(String s) {
        return nonVuoto(s) ? s.trim().replaceAll("[^A-Za-z0-9._-]+", "-") : "intervento";
    }
}
