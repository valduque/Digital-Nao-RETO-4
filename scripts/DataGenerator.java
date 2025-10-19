package com.restaurantes.gdl.generator;

import com.restaurantes.gdl.model.Restaurante;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {

    private static final String[] MUNICIPIOS = {
            "Guadalajara", "Zapopan", "Tlaquepaque", "Tonalá", "Tlajomulco de Zúñiga"
    };

    private static final String[] TIPOS_COMIDA = {
            "Mexicana", "Italiana", "Japonesa", "China", "Tacos", "Mariscos",
            "Hamburguesas", "Pizza", "Sushi", "Parrilla", "Vegetariana",
            "Internacional", "Tortas", "Birria", "Pozole", "Comida Rápida"
    };

    private static final String[] PREFIJOS_NOMBRES = {
            "El", "La", "Los", "Las", "Don", "Doña", "Restaurante", "Mariscos",
            "Tacos", "Birriería", "Cenaduria", "Fonda", "Cocina"
    };

    private static final String[] NOMBRES_BASE = {
            "Tapatio", "Jalisciense", "Guadalajara", "Perla", "Estrella",
            "Corona", "Sol", "Luna", "Sabor", "Delicias", "Tradición",
            "Rincón", "Fogón", "Sazón", "Palacio", "Villa", "Hacienda"
    };

    private static final Map<String, double[]> COORDENADAS_MUNICIPIOS = Map.of(
            "Guadalajara", new double[]{-103.3467, 20.6767},
            "Zapopan", new double[]{-103.3925, 20.7214},
            "Tlaquepaque", new double[]{-103.3137, 20.6401},
            "Tonalá", new double[]{-103.2324, 20.6227},
            "Tlajomulco de Zúñiga", new double[]{-103.4425, 20.4742}
    );

    private static final Random random = new Random();

    public List<Restaurante> generarRestaurantes(int cantidad) {
        List<Restaurante> restaurantes = new ArrayList<>();
        Set<String> nombresUsados = new HashSet<>();

        for (int i = 0; i < cantidad; i++) {
            String nombre = generarNombreUnico(nombresUsados);
            String municipio = MUNICIPIOS[random.nextInt(MUNICIPIOS.length)];
            String tipoComida = TIPOS_COMIDA[random.nextInt(TIPOS_COMIDA.length)];

            Restaurante restaurante = Restaurante.builder()
                    .nombre(nombre)
                    .direccion(generarDireccion())
                    .municipio(municipio)
                    .tipoComida(tipoComida)
                    .calificacion(redondear(3.5 + random.nextDouble() * 1.5, 1))
                    .precioPromedio(redondear(100 + random.nextDouble() * 400, 2))
                    .horario(generarHorario())
                    .ubicacion(generarUbicacion(municipio))
                    .telefono(generarTelefono())
                    .especialidades(generarEspecialidades(tipoComida))
                    .build();

            restaurantes.add(restaurante);
        }

        return restaurantes;
    }

    private String generarNombreUnico(Set<String> nombresUsados) {
        String nombre;
        int intentos = 0;
        do {
            String prefijo = PREFIJOS_NOMBRES[random.nextInt(PREFIJOS_NOMBRES.length)];
            String base = NOMBRES_BASE[random.nextInt(NOMBRES_BASE.length)];
            nombre = prefijo + " " + base;

            if (random.nextDouble() > 0.7) {
                nombre += " " + (random.nextInt(3) + 1);
            }
            intentos++;
        } while (nombresUsados.contains(nombre) && intentos < 100);

        nombresUsados.add(nombre);
        return nombre;
    }

    private String generarDireccion() {
        String[] calles = {"Av. Chapultepec", "Av. Americas", "Calle Independencia",
                "Av. López Mateos", "Calle Morelos", "Av. Juárez", "Calle Hidalgo",
                "Av. Patria", "Calle Libertad", "Av. Vallarta"};

        int numero = 100 + random.nextInt(9900);
        String calle = calles[random.nextInt(calles.length)];

        return calle + " " + numero;
    }

    private String generarHorario() {
        String[] horarios = {
                "Lun-Dom 8:00-22:00",
                "Lun-Sab 9:00-21:00",
                "Lun-Dom 10:00-23:00",
                "Lun-Vie 11:00-20:00, Sab-Dom 10:00-22:00",
                "Lun-Dom 7:00-18:00",
                "Mar-Dom 12:00-22:00"
        };
        return horarios[random.nextInt(horarios.length)];
    }

    private Restaurante.Ubicacion generarUbicacion(String municipio) {
        double[] centro = COORDENADAS_MUNICIPIOS.get(municipio);
        double offsetLng = (random.nextDouble() - 0.5) * 0.1;
        double offsetLat = (random.nextDouble() - 0.5) * 0.1;

        return Restaurante.Ubicacion.builder()
                .type("Point")
                .coordinates(Arrays.asList(
                        redondear(centro[0] + offsetLng, 6),
                        redondear(centro[1] + offsetLat, 6)
                ))
                .build();
    }

    private String generarTelefono() {
        return String.format("33-%04d-%04d",
                random.nextInt(10000),
                random.nextInt(10000));
    }

    private List<String> generarEspecialidades(String tipoComida) {
        Map<String, List<String>> especialidadesPorTipo = Map.of(
                "Mexicana", Arrays.asList("Enchiladas", "Pozole", "Mole", "Chiles Rellenos"),
                "Tacos", Arrays.asList("Tacos al Pastor", "Tacos de Birria", "Tacos Dorados"),
                "Mariscos", Arrays.asList("Ceviche", "Camarones", "Pescado Zarandeado"),
                "Italiana", Arrays.asList("Pasta Carbonara", "Lasagna", "Risotto"),
                "Japonesa", Arrays.asList("Ramen", "Tempura", "Yakitori"),
                "Sushi", Arrays.asList("Nigiri", "Makis", "Sashimi")
        );

        List<String> especialidades = especialidadesPorTipo.getOrDefault(
                tipoComida,
                Arrays.asList("Platillo 1", "Platillo 2", "Platillo 3")
        );

        int cantidad = 2 + random.nextInt(2);
        Collections.shuffle(especialidades);
        return especialidades.subList(0, Math.min(cantidad, especialidades.size()));
    }

    private double redondear(double valor, int decimales) {
        double factor = Math.pow(10, decimales);
        return Math.round(valor * factor) / factor;
    }
}