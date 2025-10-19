const { ObjectId } = require('mongodb');

class Restaurant {
    constructor(data) {
        this._id = data._id;
        this.nombre = data.nombre;
        this.tipoComida = data.tipoComida;
        this.municipio = data.municipio;
        this.direccion = data.direccion;
        this.calificacion = data.calificacion;
        this.precioPromedio = data.precioPromedio;
        this.horario = data.horario;
        this.ubicacion = data.ubicacion;
        this.telefono = data.telefono;
        this.especialidades = data.especialidades || [];
    }

    /**
     * Convert the Restaurant instance into a MongoDB document
     * consistent with constructor fields
     * @param {Restaurant} data
     */
    static toDatabase(data) {
        const doc = {
            nombre: data.nombre,
            tipoComida: data.tipoComida,
            municipio: data.municipio,
            direccion: data.direccion,
            calificacion: data.calificacion,
            precioPromedio: data.precioPromedio,
            horario: data.horario,
            ubicacion: data.ubicacion,
            telefono: data.telefono,
            especialidades: data.especialidades || []
        };

        if (data._id) {
            doc._id = new ObjectId(data._id);
        }

        return doc;
    }

    /**
     * Convert a MongoDB document into a Restaurant instance
     * @param {Object} doc
     */
    static fromDatabase(doc) {
        if (!doc) return null;

        return new Restaurant({
            _id: doc._id.toString(),
            nombre: doc.nombre,
            tipoComida: doc.tipoComida,
            municipio: doc.municipio,
            direccion: doc.direccion,
            calificacion: doc.calificacion,
            precioPromedio: doc.precioPromedio,
            horario: doc.horario,
            ubicacion: doc.ubicacion,
            telefono: doc.telefono,
            especialidades: doc.especialidades || []
        });
    }
}

module.exports = Restaurant;