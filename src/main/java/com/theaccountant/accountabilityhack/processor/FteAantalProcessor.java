package com.theaccountant.accountabilityhack.processor;

import com.theaccountant.accountabilityhack.data.SchoolEntry;
import com.theaccountant.accountabilityhack.data.SchoolRegistry;

import java.io.IOException;
import java.math.BigDecimal;

public final class FteAantalProcessor implements Processor {

    public void process(final SchoolRegistry registry) throws IOException {
        final CsvReader reader = new CsvReader("02.-po-fte-owtype-bestuur-brin-functie.csv");
        while (reader.next()) {
            final String brin = reader.getString("BRIN NUMMER");
            final SchoolEntry entry;
            if (registry.isSchoolPresent(brin)) {
                entry = registry.getSchoolByBrin(brin);
            } else {
                entry = SchoolEntry.builder().brin(brin).build();
                registry.addSchool(entry);
            }
            final String functie = reader.getString("FUNCTIEGROEP");
            final BigDecimal fte = reader.getBigDecimal("FTE'S 2016");
            switch (functie) {
            case "Directie":
                entry.setFteDirectie(fte);
                break;
            case "Onderwijsgevend personeel":
                entry.setFteLeerkrachten(fte);
                break;
            case "Onderwijsondersteunend personeel (OOP/OBP)":
                entry.setFteOndersteunend(fte);
                break;
            case "Leraren in opleiding (LIO)":
                entry.setFteInOpleiding(fte);
                break;
            case "Onbekend":
                entry.setFteOnbekend(fte);
                break;
            default:
                System.err.println("Unknown functie: " + functie);
                break;
            }
        }
        System.out.println("Done processing aantal FTE: " + registry.size());
    }
}
