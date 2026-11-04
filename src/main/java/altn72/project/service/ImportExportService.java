package altn72.project.service;

import altn72.project.modele.Station;
import altn72.project.modele.StationRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportExportService {

    public record ImportResult(
            int creees,
            int misesAJour,
            int erreurs,
            List<String> erreursDetails
    ) {
    }

    private final StationRepository stationRepository;

    public ImportExportService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public ImportResult importerStationsCsv(InputStream inputStream) {
        int creees = 0;
        int misesAJour = 0;
        List<String> erreursDetails = new ArrayList<>();

        CSVFormat format = CSVFormat.Builder.create()
                .setDelimiter(';')
                .setHeader("id", "nom", "adresse", "ville", "latitude", "longitude")
                .setSkipHeaderRecord(true)
                .build();

        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser csvParser = format.parse(reader)) {

            for (CSVRecord record : csvParser) {
                try {
                    int id = Integer.parseInt(record.get("id").trim());
                    String nom = record.get("nom").trim();
                    String adresse = record.get("adresse").trim();
                    String ville = record.get("ville").trim();

                    double latitude = 0.0;
                    double longitude = 0.0;

                    String latStr = record.get("latitude");
                    String lonStr = record.get("longitude");
                    if (latStr != null && !latStr.isBlank()) {
                        latitude = Double.parseDouble(latStr.trim());
                    }
                    if (lonStr != null && !lonStr.isBlank()) {
                        longitude = Double.parseDouble(lonStr.trim());
                    }

                    Station existingStation = stationRepository.findById(id).orElse(null);
                    if (existingStation != null) {
                        existingStation.setNom(nom);
                        existingStation.setAdresse(adresse);
                        existingStation.setVille(ville);
                        stationRepository.save(existingStation);
                        misesAJour++;
                    } else {
                        Station station = new Station(id, nom, latitude, longitude, adresse, ville);
                        stationRepository.save(station);
                        creees++;
                    }
                } catch (Exception e) {
                    erreursDetails.add("Ligne " + (record.getRecordNumber() + 1) + " : " + e.getMessage());
                }
            }
        } catch (Exception e) {
            erreursDetails.add("Erreur de lecture du fichier : " + e.getMessage());
        }

        return new ImportResult(creees, misesAJour, erreursDetails.size(), erreursDetails);
    }

    public void exporterStationsCsv(OutputStream outputStream) {
        CSVFormat format = CSVFormat.Builder.create()
                .setDelimiter(';')
                .setHeader("id", "nom", "adresse", "ville", "latitude", "longitude")
                .build();

        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, format)) {

            for (Station station : stationRepository.findAll()) {
                csvPrinter.printRecord(
                        station.getId(),
                        station.getNom(),
                        station.getAdresse(),
                        station.getVille(),
                        station.getLatitude(),
                        station.getLongitude()
                );
            }
            csvPrinter.flush();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export CSV : " + e.getMessage(), e);
        }
    }
}
