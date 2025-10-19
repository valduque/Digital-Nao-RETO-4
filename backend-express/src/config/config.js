const { MongoClient } = require('mongodb');
class MongoDB {
    constructor(){
        this.client = null;
        this.db = null
    }
    
    async connect() {
        try {
            if (this.client && this.db) {
                console.log('Ya se encuentra conectado a una base de datos.');
                return this.db;
            }


            // Crear cliente
            this.client = new MongoClient("mongodb+srv://ValDuque:Duquechita26!@challenge4.kana6ol.mongodb.net/")

            // Conectar
            await this.client.connect();

            // Obtener instancia de base de datos
            this.db = this.client.db("restaurantes_gdl");

            // Verificar conexión
            await this.db.command({ ping: 1 });

            console.log("Se ha conectado a la base de datos");

            return this.db;
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }
    getDatabase() {
        if (!this.db) {
            throw new Error('Database not connected. Call connect() first.');
        }
        return this.db;
    }
     getCollection(collectionName) {
        return this.getDatabase().collection(collectionName);
    }
}

module.exports = new MongoDB();