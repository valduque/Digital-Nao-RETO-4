package com.restaurantes.gdl.service;

import com.mongodb.client.*;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.restaurantes.gdl.model.Restaurante;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MongoDBService implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(MongoDBService.class);

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    public MongoDBService(String connectionString, String databaseName, String collectionName) {
        try {
            this.mongoClient = MongoClients.create(connectionString);
            this.database = mongoClient.getDatabase(databaseName);
            this.collection = database.getCollection(collectionName);
            logger.info("Conectado exitosamente a MongoDB: {}", databaseName);
        } catch (Exception e) {
            logger.error("Error al conectar a MongoDB", e);
            throw new RuntimeException("No se pudo conectar a MongoDB", e);
        }
    }

    public void importarRestaurantes(List<Restaurante> restaurantes) {
        try {
            logger.info("Importando {} restaurantes...", restaurantes.size());

            List<Document> documentos = new ArrayList<>();
            for (Restaurante restaurante : restaurantes) {
                documentos.add(restaurante.toDocument());
            }

            collection.insertMany(documentos);
            logger.info("✓ Se importaron {} restaurantes exitosamente", restaurantes.size());
        } catch (Exception e) {
            logger.error("Error al importar restaurantes", e);
            throw new RuntimeException("Error en la importación", e);
        }
    }

    public void crearIndices() {
        try {
            logger.info("Creando índices...");

            // Índice para tipo de comida
            collection.createIndex(
                    Indexes.ascending("tipoComida"),
                    new IndexOptions().name("idx_tipo_comida")
            );
            logger.info("✓ Índice creado: idx_tipo_comida");

            // Índice para calificación (descendente para búsquedas de mejores calificaciones)
            collection.createIndex(
                    Indexes.descending("calificacion"),
                    new IndexOptions().name("idx_calificacion")
            );
            logger.info("✓ Índice creado: idx_calificacion");

            // Índice para municipio
            collection.createIndex(
                    Indexes.ascending("municipio"),
                    new IndexOptions().name("idx_municipio")
            );
            logger.info("✓ Índice creado: idx_municipio");

            // Índice compuesto: municipio + tipo de comida
            collection.createIndex(
                    Indexes.compoundIndex(
                            Indexes.ascending("municipio"),
                            Indexes.ascending("tipoComida")
                    ),
                    new IndexOptions().name("idx_municipio_tipo")
            );
            logger.info("✓ Índice compuesto creado: idx_municipio_tipo");

            // Índice geoespacial para búsquedas por ubicación
            collection.createIndex(
                    Indexes.geo2dsphere("ubicacion"),
                    new IndexOptions().name("idx_ubicacion_geo")
            );
            logger.info("✓ Índice geoespacial creado: idx_ubicacion_geo");

            logger.info("Todos los índices fueron creados exitosamente");
        } catch (Exception e) {
            logger.error("Error al crear índices", e);
            throw new RuntimeException("Error en la creación de índices", e);
        }
    }

    public void limpiarColeccion() {
        try {
            long count = collection.countDocuments();
            if (count > 0) {
                logger.info("Limpiando colección ({} documentos)...", count);
                collection.drop();
                logger.info("✓ Colección limpiada");
            }
        } catch (Exception e) {
            logger.error("Error al limpiar colección", e);
        }
    }

    public long contarDocumentos() {
        return collection.countDocuments();
    }

    public void mostrarEstadisticas() {
        try {
            logger.info("\n=== ESTADÍSTICAS DE LA COLECCIÓN ===");
            logger.info("Total de restaurantes: {}", collection.countDocuments());

            // Contar por municipio
            logger.info("\nRestaurantes por municipio:");
            AggregateIterable<Document> porMunicipio = collection.aggregate(List.of(
                    new Document("$group", new Document("_id", "$municipio")
                            .append("count", new Document("$sum", 1))),
                    new Document("$sort", new Document("count", -1))
            ));
            porMunicipio.forEach(doc ->
                    logger.info("  {}: {}", doc.getString("_id"), doc.getInteger("count")));

            // Contar por tipo de comida
            logger.info("\nRestaurantes por tipo de comida:");
            AggregateIterable<Document> porTipo = collection.aggregate(List.of(
                    new Document("$group", new Document("_id", "$tipoComida")
                            .append("count", new Document("$sum", 1))),
                    new Document("$sort", new Document("count", -1))
            ));
            porTipo.forEach(doc ->
                    logger.info("  {} : {}", doc.getString("_id"), doc.getInteger("count")));

            // Calificación promedio
            AggregateIterable<Document> calificacionPromedio = collection.aggregate(List.of(
                    new Document("$group", new Document("_id", null)
                            .append("avgCalificacion", new Document("$avg", "$calificacion")))
            ));
            Document avg = calificacionPromedio.first();
            if (avg != null) {
                logger.info("\nCalificación promedio: {}",
                        String.format("%.2f", avg.getDouble("avgCalificacion")));
            }

            logger.info("=====================================\n");
        } catch (Exception e) {
            logger.error("Error al mostrar estadísticas", e);
        }
    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            logger.info("Conexión a MongoDB cerrada");
        }
    }
}