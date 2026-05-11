package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.AnalisisDTO;
import es.aaracubel.paicasso.backend.entities.Analisis;
import es.aaracubel.paicasso.backend.entities.EstadoAnalisis;
import es.aaracubel.paicasso.backend.entities.Metrica;
import es.aaracubel.paicasso.backend.entities.Repositorio;
import es.aaracubel.paicasso.backend.mappers.AnalisisMapper;
import es.aaracubel.paicasso.backend.repositories.AnalisisRepository;
import es.aaracubel.paicasso.backend.repositories.MetricaRepository;
import es.aaracubel.paicasso.backend.repositories.RepositorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalisisService {

    private final AnalisisRepository analisisRepository;
    private final RepositorioRepository repositorioRepository;
    private final MetricaRepository metricaRepository;
    private final AnalisisMapper analisisMapper;
    private final SonarService sonarService;

    @Value("${sonarqube.url}")
    private String sonarUrl;

    @Value("${sonarqube.security.token}")
    private String sonarToken;

    public AnalisisDTO obtenerUltimoAnalisis(Long repoId) {
        return analisisRepository.findFirstByRepositorioIdOrderByFechaEjecucionDesc(repoId)
                .map(analisisMapper::toDTO)
                .orElse(null);
    }

    @Transactional
    public AnalisisDTO iniciarAnalisis(Long repoId) {
        Repositorio repo = repositorioRepository.findById(repoId)
                .orElseThrow(() -> new RuntimeException("Repositorio no encontrado"));

        Analisis nuevoAnalisis = new Analisis();
        nuevoAnalisis.setRepositorio(repo);
        nuevoAnalisis.setEstado(EstadoAnalisis.EN_PROGRESO);
        nuevoAnalisis.setFechaEjecucion(LocalDateTime.now());

        Analisis guardado = analisisRepository.save(nuevoAnalisis);
        return analisisMapper.toDTO(guardado);
    }

    @Async("sonarTaskExecutor")
    public void ejecutarAnalisisEnSegundoPlano(Long analisisId, Long repoId) {
        Path directorioTemporal = null;

        try {
            Repositorio repo = repositorioRepository.findById(repoId).orElseThrow();

            directorioTemporal = Files.createTempDirectory("paicasso-repo-" + repoId + "-");
            System.out.println("Carpeta temporal creada en: " + directorioTemporal.toAbsolutePath());

            String tokenGithub = repo.getUsuario().getTokenAcceso();
            String repoUrl = repo.getUrl();
            String authUrl = repoUrl.replace("https://", "https://" + tokenGithub + "@");

            System.out.println("Descargando código de GitHub...");
            ProcessBuilder gitBuilder = new ProcessBuilder(
                    "git", "clone", "--depth", "1", authUrl, directorioTemporal.toString());

            gitBuilder.environment().put("GIT_LFS_SKIP_SMUDGE", "1");
            gitBuilder.redirectErrorStream(true);
            Process gitProcess = gitBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(gitProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[GIT] " + line);
                }
            }

            int gitExitCode = gitProcess.waitFor();

            if (gitExitCode != 0) {
                throw new RuntimeException("Error al clonar el repositorio de GitHub. Código: " + gitExitCode);
            }

            boolean esProyectoMaven = Files.exists(directorioTemporal.resolve("pom.xml"));
            if (esProyectoMaven) {
                System.out.println("Proyecto Maven detectado. Ejecutando compilación previa...");

                List<String> mavenCmd = construirComandoMaven(directorioTemporal);
                ProcessBuilder mavenBuilder = new ProcessBuilder(mavenCmd);
                mavenBuilder.directory(directorioTemporal.toFile());
                mavenBuilder.redirectErrorStream(true);

                try {
                    Process mavenProcess = mavenBuilder.start();

                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(mavenProcess.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println("[MAVEN] " + line);
                        }
                    }

                    int mavenExitCode = mavenProcess.waitFor();
                    if (mavenExitCode != 0) {
                        System.err.println(
                                "Advertencia: Falló la compilación de Maven. El análisis de Sonar podría perder precisión en Java. Código: "
                                        + mavenExitCode);
                    } else {
                        System.out.println("Compilación Maven exitosa.");
                    }
                } catch (IOException e) {
                    System.err.println(
                            "Advertencia: No se pudo lanzar Maven (" + e.getMessage()
                                    + "). Continuando sin compilar; el análisis de Java perderá precisión.");
                }
            }

            System.out.println("Ejecutando SonarScanner...");
            String projectKey = "paicasso_" + repo.getId();

            boolean esWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String sonarCmd = esWindows ? "sonar-scanner.bat" : "sonar-scanner";

            ProcessBuilder sonarBuilder;
            if (esWindows) {
                sonarBuilder = new ProcessBuilder(
                        "cmd.exe", "/c", sonarCmd,
                        "-Dsonar.projectKey=" + projectKey,
                        "-Dsonar.projectName=" + repo.getNombre(),
                        "-Dsonar.sources=.",
                        esProyectoMaven ? "-Dsonar.java.binaries=target/classes" : "-Dsonar.java.binaries=.",
                        "-Dsonar.host.url=" + sonarUrl,
                        "-Dsonar.token=" + sonarToken);
            } else {
                sonarBuilder = new ProcessBuilder(
                        sonarCmd,
                        "-Dsonar.projectKey=" + projectKey,
                        "-Dsonar.projectName=" + repo.getNombre(),
                        "-Dsonar.sources=.",
                        esProyectoMaven ? "-Dsonar.java.binaries=target/classes" : "-Dsonar.java.binaries=.",
                        "-Dsonar.host.url=" + sonarUrl,
                        "-Dsonar.token=" + sonarToken);
            }

            sonarBuilder.directory(directorioTemporal.toFile());
            sonarBuilder.redirectErrorStream(true);
            Process sonarProcess = sonarBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(sonarProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[SONAR] " + line);
                }
            }

            int sonarExitCode = sonarProcess.waitFor();

            if (sonarExitCode != 0) {
                throw new RuntimeException("Error ejecutando sonar-scanner. Código: " + sonarExitCode);
            }

            System.out.println("Esperando a que SonarQube termine de procesar el reporte...");
            int intentos = 0;
            int maxIntentos = 60;

            while (intentos < maxIntentos) {
                if (sonarService.procesamientoCompletado(projectKey)) {
                    System.out.println("¡SonarQube ha finalizado el procesamiento en el servidor!");
                    break;
                }

                Thread.sleep(2000);
                intentos++;
            }

            if (intentos == maxIntentos) {
                System.err.println("Advertencia: Se agotó el tiempo máximo de espera de SonarQube.");
            }

            System.out.println("Recopilando métricas...");
            AnalisisDTO analisisDTO = sonarService.obtenerMetricas(projectKey);

            Analisis analisis = analisisRepository.findById(analisisId).orElseThrow();

            if (analisisDTO != null) {
                analisis.setTotalBugs(analisisDTO.getBugs());
                analisis.setTotalVulnerabilidades(analisisDTO.getVulnerabilidades());
                analisis.setTotalCodeSmells(analisisDTO.getCodeSmells());
                analisis.setLineasCodigo(analisisDTO.getLineasCodigo());
                analisis.setCobertura(analisisDTO.getCobertura());
                analisis.setDuplicaciones(analisisDTO.getDuplicaciones());
            }

            System.out.println("Guardando detalle de incidencias...");
            List<Metrica> incidencias = sonarService.obtenerDetalleIncidencias(projectKey, analisis);

            if (!incidencias.isEmpty()) {
                metricaRepository.saveAll(incidencias);
            }
            System.out.println("Se han guardado " + incidencias.size() + " incidencias detalladas.");

            analisis.setEstado(EstadoAnalisis.COMPLETADO);
            analisisRepository.save(analisis);
            System.out.println("¡Análisis completado con éxito para el repo " + repo.getNombre() + "!");
        } catch (Exception e) {
            System.err.println("Error crítico en análisis asíncrono: " + e.getMessage());
            analisisRepository.findById(analisisId).ifPresent(analisis -> {
                analisis.setEstado(EstadoAnalisis.ERROR);
                analisisRepository.save(analisis);
            });
        } finally {
            borrarDirectorio(directorioTemporal);
        }
    }

    private List<String> construirComandoMaven(Path directorioProyecto) {
        boolean esWindows = System.getProperty("os.name").toLowerCase().contains("win");
        String wrapper = esWindows ? "mvnw.cmd" : "mvnw";
        boolean tieneWrapper = Files.exists(directorioProyecto.resolve(wrapper));

        if (tieneWrapper) {
            System.out.println("Usando Maven Wrapper del repositorio (" + wrapper + ").");
            return esWindows
                    ? List.of("cmd.exe", "/c", wrapper, "clean", "compile")
                    : List.of("./" + wrapper, "clean", "compile");
        }

        System.out.println("Maven Wrapper no encontrado. Usando Maven del sistema.");
        return esWindows
                ? List.of("cmd.exe", "/c", "mvn.cmd", "clean", "compile")
                : List.of("mvn", "clean", "compile");
    }

    private void borrarDirectorio(Path path) {
        if (path == null || !Files.exists(path))
            return;

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    file.toFile().setWritable(true);
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    dir.toFile().setWritable(true);
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("Código temporal borrado del disco.");
        } catch (Exception e) {
            System.err.println("Fallo crítico forzando el borrado de la carpeta temporal: " + e.getMessage());
        }
    }
}
