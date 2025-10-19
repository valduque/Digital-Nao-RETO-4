package com.restaurantes.gdl;

import com.restaurantes.gdl.generator.DataGenerator;
import com.restaurantes.gdl.model.Restaurante;
import com.restaurantes.gdl.service.BackupService;
import com.restaurantes.gdl.service.MongoDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    // Configuración
    private static final String MONGO_URI = "mongodb+srv://ValDuque:Duquechita26!@challenge4.kana6ol.mongodb.net/";
    private static final String DATABASE_NAME = "restaurantes_gdl";
    private static final String COLLECTION_NAME = "restaurantes";
    private static final int CANTIDAD_RESTAURANTES = 100;

    public static void main(String[] args) {
        logger.info("==============================================");
        logger.info("  SISTEMA DE RESTAURANTES - GDL  ");
        logger.info("  Área Metropolitana de Guadalajara, México");
        logger.info("==============================================\n");

        try (MongoDBService mongoService = new MongoDBService(MONGO_URI, DATABASE_NAME, COLLECTION_NAME)) {

            // Mostrar menú interactivo
            mostrarMenu(mongoService);

        } catch (Exception e) {
            logger.error("Error fatal en la aplicación", e);
            System.exit(1);
        }
    }

    private static void mostrarMenu(MongoDBService mongoService) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║         MENÚ PRINCIPAL                 ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 1. Generar e importar datos            ║");
            System.out.println("║ 2. Crear índices                       ║");
            System.out.println("║ 3. Realizar backup (mongodump)         ║");
            System.out.println("║ 4. Mostrar estadísticas                ║");
            System.out.println("║ 5. Limpiar colección                   ║");
            System.out.println("║ 6. Ejecutar todo (1 + 2 + 3)           ║");
            System.out.println("║ 0. Salir                               ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("\nSelecciona una opción: ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir salto de línea

                switch (opcion) {
                    case 1:
                        generarEImportarDatos(mongoService);
                        break;
                    case 2:
                        mongoService.crearIndices();
                        break;
                    case 3:
                        realizarBackup();
                        break;
                    case 4:
                        mongoService.mostrarEstadisticas();
                        break;
                    case 5:
                        confirmarYLimpiar(mongoService, scanner);
                        break;
                    case 6:
                        ejecutarTodo(mongoService);
                        break;
                    case 0:
                        continuar = false;
                        logger.info("¡Hasta luego!");
                        break;
                    default:
                        logger.warn("Opción no válida");
                }
            } catch (Exception e) {
                logger.error("Error al procesar la opción", e);
                scanner.nextLine(); // Limpiar buffer
            }
        }
        scanner.close();
    }

    private static void generarEImportarDatos(MongoDBService mongoService) {
        logger.info("\n>>> Generando {} restaurantes...", CANTIDAD_RESTAURANTES);

        DataGenerator generator = new DataGenerator();
        List<Restaurante> restaurantes = generator.generarRestaurantes(CANTIDAD_RESTAURANTES);

        logger.info("✓ {} restaurantes generados", restaurantes.size());
        logger.info("Ejemplos generados:");
        for (int i = 0; i < Math.min(3, restaurantes.size()); i++) {
            Restaurante r = restaurantes.get(i);
            logger.info("  - {} ({}) en {} - Calificación: {}",
                    r.getNombre(), r.getTipoComida(), r.getMunicipio(), r.getCalificacion());
        }

        mongoService.importarRestaurantes(restaurantes);
        logger.info("✓ Datos importados exitosamente a MongoDB");
    }

    private static void realizarBackup() {
        BackupService backupService = new BackupService(MONGO_URI, DATABASE_NAME);
        backupService.realizarBackup();
    }

    private static void confirmarYLimpiar(MongoDBService mongoService, Scanner scanner) {
        long count = mongoService.contarDocumentos();
        if (count == 0) {
            logger.info("La colección ya está vacía");
            return;
        }

        System.out.println("\n⚠️  ADVERTENCIA: Se eliminarán " + count + " documentos");
        System.out.print("¿Estás seguro? (S/N): ");
        String respuesta = scanner.nextLine().trim().toUpperCase();

        if (respuesta.equals("S") || respuesta.equals("SI")) {
            mongoService.limpiarColeccion();
            logger.info("✓ Colección limpiada");
        } else {
            logger.info("Operación cancelada");
        }
    }

    private static void ejecutarTodo(MongoDBService mongoService) {
        logger.info("\n╔════════════════════════════════════════╗");
        logger.info("║   EJECUTANDO PROCESO COMPLETO          ║");
        logger.info("╚════════════════════════════════════════╝\n");

        // Paso 1: Generar e importar datos
        logger.info("PASO 1: Generación e importación de datos");
        logger.info("─────────────────────────────────────────");
        generarEImportarDatos(mongoService);

        // Paso 2: Crear índices
        logger.info("\nPASO 2: Creación de índices");
        logger.info("─────────────────────────────────────────");
        mongoService.crearIndices();

        // Paso 3: Realizar backup
        logger.info("\nPASO 3: Backup de la base de datos");
        logger.info("─────────────────────────────────────────");
        realizarBackup();

        // Paso 4: Mostrar estadísticas
        logger.info("\nPASO 4: Estadísticas finales");
        logger.info("─────────────────────────────────────────");
        mongoService.mostrarEstadisticas();

        logger.info("\n╔════════════════════════════════════════╗");
        logger.info("║   ✓ PROCESO COMPLETADO EXITOSAMENTE    ║");
        logger.info("╚════════════════════════════════════════╝\n");
    }
}