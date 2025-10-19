// src/controllers/RestaurantController.js
const RestaurantRepository = require('../repository/RestaurantsRepository');

const RestaurantController = {
  async create(req, res, next) {
    try {
      const payload = req.body;
      const created = await RestaurantRepository.create(payload);
      res.status(201).json(created);
    } catch (err) {
      next(err);
    }
  },

  async list(req, res, next) {
    try {
      const filters = {
        municipio: req.query.municipio,
        tipoComida: req.query.tipoComida,
        minRating: req.query.minRating,
        q: req.query.q
      };
      const options = {
        page: req.query.page,
        limit: req.query.limit,
        sortBy: req.query.sortBy,
        sortDir: req.query.sortDir
      };
      const result = await RestaurantRepository.findAll(filters, options);
      res.json(result);
    } catch (err) {
      next(err);
    }
  },

  async getById(req, res, next) {
    try {
      const { id } = req.params;
      const restaurant = await RestaurantRepository.findById(id);
      if (!restaurant) return res.status(404).json({ message: 'Restaurant not found' });
      res.json(restaurant);
    } catch (err) {
      next(err);
    }
  },

  async update(req, res, next) {
    try {
      const { id } = req.params;
      const payload = req.body;
      const updated = await RestaurantRepository.update(id, payload);
      if (!updated) return res.status(404).json({ message: 'Restaurant not found' });
      res.json(updated);
    } catch (err) {
      next(err);
    }
  },

  async remove(req, res, next) {
    try {
      const { id } = req.params;
      const deleted = await RestaurantRepository.delete(id);
      if (!deleted) return res.status(404).json({ message: 'Restaurant not found' });
      res.json({ message: 'Restaurant deleted' });
    } catch (err) {
      next(err);
    }
  }
};

module.exports = RestaurantController;
