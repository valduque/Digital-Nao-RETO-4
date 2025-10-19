// src/routes/restaurants.js
const express = require('express');
const router = express.Router();
const RestaurantController = require('../controllers/RestaurantController');

// GET /restaurants           -> lista (con filtros / paginación)
router.get('/', RestaurantController.list);

// GET /restaurants/:id       -> obtener 1 por id
router.get('/:id', RestaurantController.getById);

// POST /restaurants          -> crear
router.post('/', RestaurantController.create);

// PUT /restaurants/:id       -> actualizar (reemplaza campos indicados)
router.put('/:id', RestaurantController.update);

// DELETE /restaurants/:id    -> eliminar
router.delete('/:id', RestaurantController.remove);

module.exports = router;
