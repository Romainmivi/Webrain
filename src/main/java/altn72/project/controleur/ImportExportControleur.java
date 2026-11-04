package altn72.project.controleur;

import altn72.project.service.ImportExportService;
import altn72.project.service.ImportExportService.ImportResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/api/import-export")
public class ImportExportControleur {

    private final ImportExportService importExportService;

    public ImportExportControleur(ImportExportService importExportService) {
        this.importExportService = importExportService;
    }

    @GetMapping("/import-stations")
    public String afficherPageImport() {
        return "import/importStations";
    }

    @PostMapping("/import-stations")
    public String importerStations(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("erreur", "Veuillez sélectionner un fichier CSV.");
            return "import/importStations";
        }

        try {
            ImportResult result = importExportService.importerStationsCsv(file.getInputStream());
            model.addAttribute("result", result);
        } catch (IOException e) {
            model.addAttribute("erreur", "Erreur lors de la lecture du fichier : " + e.getMessage());
        }

        return "import/importStations";
    }

}
