// app.js
const express = require('express');
const app = express();
const port = process.env.PORT || 3000;
const MongoDatabase = require('./config/config'); // tu archivo actual
const restaurantsRouter = require('./routes/restaurants');

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Rutas base
app.use('/restaurants', restaurantsRouter);

// middleware simple para errores
app.use((err, req, res, next) => {
  console.error(err);
  res.status(err.status || 500).json({ error: err.message || 'Internal Server Error' });
});

(async function start() {
  try {
    await MongoDatabase.connect();
    app.listen(port, () => {
      console.log(`Server listening on port ${port}`);
    });
  } catch (err) {
    console.error('No fue posible iniciar la app:', err);
    process.exit(1);
  }
})();