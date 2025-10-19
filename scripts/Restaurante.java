package com.restaurantes.gdl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurante {
    private ObjectId id;
    private String nombre;
    private String direccion;
    private String municipio;
    private String tipoComida;
    private double calificacion;
    private double precioPromedio;
    private String horario;
    private Ubicacion ubicacion;
    private String telefono;
    private List<String> especialidades;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ubicacion {
        private String type;
        private List<Double> coordinates; // [longitude, latitude]
    }

    public Document toDocument() {
        Document ubicacionDoc = new Document()
                .append("type", ubicacion.getType())
                .append("coordinates", ubicacion.getCoordinates());

        Document doc = new Document()
                .append("nombre", nombre)
                .append("direccion", direccion)
                .append("municipio", municipio)
                .append("tipoComida", tipoComida)
                .append("calificacion", calificacion)
                .append("precioPromedio", precioPromedio)
                .append("horario", horario)
                .append("ubicacion", ubicacionDoc)
                .append("telefono", telefono)
                .append("especialidades", especialidades);

        if (id != null) {
            doc.append("_id", id);
        }

        return doc;
    }

    public static Restaurante fromDocument(Document doc) {
        Document ubicacionDoc = doc.get("ubicacion", Document.class);
        Ubicacion ubicacion = Ubicacion.builder()
                .type(ubicacionDoc.getString("type"))
                .coordinates(ubicacionDoc.getList("coordinates", Double.class))
                .build();

        return Restaurante.builder()
                .id(doc.getObjectId("_id"))
                .nombre(doc.getString("nombre"))
                .direccion(doc.getString("direccion"))
                .municipio(doc.getString("municipio"))
                .tipoComida(doc.getString("tipoComida"))
                .calificacion(doc.getDouble("calificacion"))
                .precioPromedio(doc.getDouble("precioPromedio"))
                .horario(doc.getString("horario"))
                .ubicacion(ubicacion)
                .telefono(doc.getString("telefono"))
                .especialidades(doc.getList("especialidades", String.class))
                .build();
    }
}