// src/repository/RestaurantRepository.js
const { ObjectId } = require('mongodb');
const MongoDatabase = require('../config/config'); 
const Restaurant = require('../models/Restaurants');

class RestaurantRepository {
  constructor() {
    this.collectionName = 'restaurantes';
  }

  getCollection() {
    const db = MongoDatabase.getDatabase();
    return db.collection(this.collectionName);
  }

  async create(data) {
    const doc = Restaurant.toDatabase(data);
    const result = await this.getCollection().insertOne(doc);
    return Restaurant.fromDatabase({ ...doc, _id: result.insertedId });
  }

  /**
   * findAll: filtros simples + paginación + ordenamiento
   * filters: { municipio, tipoComida, minRating }
   * options: { page, limit, sortBy, sortDir }
   */
  async findAll(filters = {}, options = {}) {
    const query = {};
    if (filters.municipio) query.municipio = filters.municipio;
    if (filters.tipoComida) query.tipoComida = filters.tipoComida;
    if (filters.minRating) query.calificacion = { $gte: Number(filters.minRating) };

    // búsqueda por texto parcial en nombre (opcional)
    if (filters.q) {
      query.nombre = { $regex: filters.q, $options: 'i' };
    }

    const page = Math.max(1, Number(options.page) || 1);
    const limit = Math.max(1, Number(options.limit) || 20);
    const skip = (page - 1) * limit;

    const sortBy = options.sortBy || 'nombre';
    const sortDir = options.sortDir === 'desc' ? -1 : 1;
    const cursor = this.getCollection()
      .find(query)
      .sort({ [sortBy]: sortDir })
      .skip(skip)
      .limit(limit);

    const results = await cursor.toArray();
    const total = await this.getCollection().countDocuments(query);

    return {
      data: results.map(Restaurant.fromDatabase),
      meta: { total, page, limit, pages: Math.ceil(total / limit) }
    };
  }

  async findById(id) {
    if (!ObjectId.isValid(id)) return null;
    const doc = await this.getCollection().findOne({ _id: new ObjectId(id) });
    return Restaurant.fromDatabase(doc);
  }

  async update(id, data) {
    if (!ObjectId.isValid(id)) throw new Error('Invalid id');
    const doc = Restaurant.toDatabase(data);
    // no reemplazamos _id en el $set
    delete doc._id;
    const result = await this.getCollection().findOneAndUpdate(
      { _id: new ObjectId(id) },
      { $set: doc },
      { returnDocument: 'after' }
    );
    return Restaurant.fromDatabase(result.value);
  }

  async delete(id) {
    if (!ObjectId.isValid(id)) throw new Error('Invalid id');
    const result = await this.getCollection().deleteOne({ _id: new ObjectId(id) });
    return result.deletedCount === 1;
  }
}

module.exports = new RestaurantRepository();