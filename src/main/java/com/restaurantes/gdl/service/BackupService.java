package com.restaurantes.gdl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BackupService {

    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);
    private final String connectionString;
    private final String databaseName;

    public BackupService(String connectionString, String databaseName) {
        this.connectionString = connectionString;
        this.databaseName = databaseName;
    }

    public void realizarBackup() {
        try {
            // Verificar si mongodump está disponible
            if (!verificarMongoDump()) {
                logger.error("mongodump no está disponible en el sistema");
                logger.info("Para instalar MongoDB Database Tools:");
                logger.info("  - Windows/Mac: https://www.mongodb.com/try/download/database-tools");
                logger.info("  - Linux: sudo apt-get install mongodb-database-tools");
                return;
            }

            // Crear carpeta de backups si no existe
            File backupDir = new File("backups");
            if (!backupDir.exists()) {
                backupDir.mkdirs();
                logger.info("Carpeta 'backups' creada");
            }

            // Generar nombre con timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String outputPath = "backups/backup_" + timestamp;

            logger.info("Iniciando backup de la base de datos '{}'...", databaseName);
            logger.info("Destino: {}", outputPath);

            // Construir comando mongodump
            List<String> command = new ArrayList<>();
            command.add("mongodump");
            command.add("--uri=" + connectionString);
            command.add("--db=" + databaseName);
            command.add("--out=" + outputPath);

            // Ejecutar comando
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Leer salida del comando
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("mongodump: {}", line);
                }
            }

            // Esperar a que termine el proceso
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                logger.info("✓ Backup completado exitosamente");
                logger.info("Ubicación: {}", new File(outputPath).getAbsolutePath());

                // Mostrar tamaño del backup
                mostrarTamañoBackup(outputPath);
            } else {
                logger.error("✗ El backup falló con código de salida: {}", exitCode);
            }

        } catch (IOException e) {
            logger.error("Error de I/O al ejecutar mongodump", e);
        } catch (InterruptedException e) {
            logger.error("El proceso de backup fue interrumpido", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Error inesperado durante el backup", e);
        }
    }

    private boolean verificarMongoDump() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("mongodump", "--version");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && line.contains("mongodump version")) {
                    logger.info("mongodump encontrado: {}", line);
                    return true;
                }
            }

            process.waitFor();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void mostrarTamañoBackup(String path) {
        try {
            File backupDir = new File(path);
            if (backupDir.exists() && backupDir.isDirectory()) {
                long tamaño = calcularTamañoDirectorio(backupDir);
                double tamañoMB = tamaño / (1024.0 * 1024.0);
                logger.info("Tamaño del backup: {:.2f} MB", tamañoMB);
            }
        } catch (Exception e) {
            logger.warn("No se pudo calcular el tamaño del backup", e);
        }
    }

    private long calcularTamañoDirectorio(File directorio) {
        long tamaño = 0;
        File[] archivos = directorio.listFiles();
        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isFile()) {
                    tamaño += archivo.length();
                } else if (archivo.isDirectory()) {
                    tamaño += calcularTamañoDirectorio(archivo);
                }
            }
        }
        return tamaño;
    }

    public void restaurarBackup(String backupPath) {
        try {
            logger.info("Iniciando restauración desde: {}", backupPath);

            List<String> command = new ArrayList<>();
            command.add("mongorestore");
            command.add("--uri=" + connectionString);
            command.add("--db=" + databaseName);
            command.add(backupPath + "/" + databaseName);
            command.add("--drop"); // Elimina la colección antes de restaurar

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("mongorestore: {}", line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                logger.info("✓ Restauración completada exitosamente");
            } else {
                logger.error("✗ La restauración falló con código de salida: {}", exitCode);
            }

        } catch (Exception e) {
            logger.error("Error durante la restauración", e);
        }
    }
}