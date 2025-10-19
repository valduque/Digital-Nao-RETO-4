# RETO-4 Version   1.0.0
Non-Relational Databases for Storing JSON Data

# Tattler Revamp Project

##  Overview
Tattler is a Mexican app that lists restaurants all over the country.  
Recently, user traffic dropped a lot, so the goal of this project is to **bring it back to life** with a more modern and personalized version.

The story starts with **Alejandra**, a project manager, and **Elian**, a developer.  
They team up to make Tattler smarter — an app that gives **personalized restaurant recommendations**, lets users **add comments and ratings**, and always stays up to date.

---

##  Objectives

### General Objective
Turn Tattler into a web app that gives users **personalized experiences** using current restaurant data and user preferences.

### Specific Objectives
- Use **MongoDB** to store and manage restaurant data.  
- Build a **REST API with Express.js** to handle user interactions.  
- Add endpoints for **comments**, **ratings**, and **restaurant registration**.  
- Include a **search and filter system** to find restaurants easily.  
- Add a simple **analytics dashboard** to track how people use the app.  

---

##  Technologies
- **Node.js** and **Express.js** for the API  
- **MongoDB** for the database  
- **Mongoose** for connecting to MongoDB  
- **Postman** for testing the API  
- **Git & GitHub** for version control  

---

## 🧠 Features
- Personalized restaurant recommendations  
- Add and view comments and ratings  
- Search and filter by food type or area  
- Analytics dashboard to see user activity

## 🗂️ Project Structure

\`\`\`
restaurantes-mongodb/
├── pom.xml
├── README.md
├── .gitignore
├── run.sh (Linux/Mac)
├── run.bat (Windows)
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── restaurantes/
│       │           └── gdl/
│       │               ├── Main.java
│       │               ├── model/
│       │               │   └── Restaurante.java
│       │               ├── generator/
│       │               │   └── DataGenerator.java
│       │               └── service/
│       │                   ├── MongoDBService.java
│       │                   └── BackupService.java
│       └── resources/
│           └── simplelogger.properties
└── backups/ (created automatically)
\`\`\`

## 🚀 Prerequisites

### 1. Java 17
\`\`\`bash
java -version
# Should display: java version "17.x.x"
\`\`\`
**Download:** https://adoptium.net/

### 2. Maven
\`\`\`bash
mvn -version
# Should display: Apache Maven 3.x.x
\`\`\`
**Download:** https://maven.apache.org/download.cgi

### 3. MongoDB Database Tools (for backups)

**Windows:**
- Download: https://www.mongodb.com/try/download/database-tools
- Add to PATH

**macOS:**
\`\`\`bash
brew install mongodb-database-tools
\`\`\`

**Linux (Ubuntu/Debian):**
\`\`\`bash
sudo apt-get install mongodb-database-tools
\`\`\`

**Verify:**
\`\`\`bash
mongodump --version
\`\`\`

## 📦 Installation

### Step 1: Create folder structure

\`\`\`bash
mkdir -p restaurantes-mongodb/src/main/java/com/restaurantes/gdl/model
mkdir -p restaurantes-mongodb/src/main/java/com/restaurantes/gdl/generator
mkdir -p restaurantes-mongodb/src/main/java/com/restaurantes/gdl/service
mkdir -p restaurantes-mongodb/src/main/resources
cd restaurantes-mongodb
\`\`\`

### Step 2: Copy the files

Place each file in its location:

- \`pom.xml\` → project root
- \`.gitignore\` → project root
- \`run.sh\` and \`run.bat\` → project root
- \`Main.java\` → \`src/main/java/com/restaurantes/gdl/\`
- \`Restaurante.java\` → \`src/main/java/com/restaurantes/gdl/model/\`
- \`DataGenerator.java\` → \`src/main/java/com/restaurantes/gdl/generator/\`
- \`MongoDBService.java\` → \`src/main/java/com/restaurantes/gdl/service/\`
- \`BackupService.java\` → \`src/main/java/com/restaurantes/gdl/service/\`
- \`simplelogger.properties\` → \`src/main/resources/\`

### Step 3: Grant permissions to script (Linux/Mac)

\`\`\`bash
chmod +x run.sh
\`\`\`

### Step 4: Compile the project

\`\`\`bash
mvn clean compile
\`\`\`

## ▶️ Execution

### Option 1: Use the scripts

**Linux/Mac:**
\`\`\`bash
./run.sh
\`\`\`

**Windows:**
\`\`\`cmd
run.bat
\`\`\`

### Option 2: With Maven directly

\`\`\`bash
mvn exec:java -Dexec.mainClass="com.restaurantes.gdl.Main"
\`\`\`

### Option 3: From your IDE

- Open the project in IntelliJ IDEA, Eclipse or VS Code
- Run the \`Main.java\` class

## 🎮 Using the System

When running, you'll see this menu:

\`\`\`
╔════════════════════════════════════════╗
║         MAIN MENU                      ║
╠════════════════════════════════════════╣
║ 1. Generate and import data            ║
║ 2. Create indexes                      ║
║ 3. Perform backup (mongodump)          ║
║ 4. Show statistics                     ║
║ 5. Clear collection                    ║
║ 6. Execute all (1 + 2 + 3)             ║
║ 0. Exit                                ║
╚════════════════════════════════════════╝
\`\`\`

### Recommended Flow (First Time):

1. Select **option 6** - Executes the entire process automatically:
   - Generates 100 restaurants
   - Imports them to MongoDB
   - Creates indexes
   - Performs backup
   - Shows statistics

2. Then you can use other options individually as needed

## 📊 Generated Data

### Information for each restaurant:
- **Unique name** (e.g., "Tacos El Tapatio")
- **Address** with real streets from GDL
- **Municipality**: Guadalajara, Zapopan, Tlaquepaque, Tonalá, Tlajomulco
- **Food type**: Mexican, Italian, Japanese, Tacos, Seafood, etc.
- **Rating**: 3.5 - 5.0
- **Average price**: $100 - $500 MXN
- **Operating hours**
- **Geographic coordinates** (GeoJSON format)
- **Phone number** in GDL format
- **Specialties** according to food type

### MongoDB document example:

\`\`\`json
{
  "_id": ObjectId("..."),
  "nombre": "Tacos El Tapatio",
  "direccion": "Av. Chapultepec 2345",
  "municipio": "Guadalajara",
  "tipoComida": "Tacos",
  "calificacion": 4.7,
  "precioPromedio": 150.50,
  "horario": "Lun-Dom 10:00-23:00",
  "ubicacion": {
    "type": "Point",
    "coordinates": [-103.3467, 20.6767]
  },
  "telefono": "33-1234-5678",
  "especialidades": ["Tacos al Pastor", "Tacos de Birria"]
}
\`\`\`

## 🔍 Created Indexes

The system automatically creates:

1. **idx_tipo_comida** - Search by food type
2. **idx_calificacion** - Sort by best rating
3. **idx_municipio** - Filter by municipality
4. **idx_municipio_tipo** - Combined search
5. **idx_ubicacion_geo** - Geospatial searches (near you)

## 💾 Backups

Backups are saved in:
\`\`\`
backups/backup_YYYYMMDD_HHMMSS/
\`\`\`

Each backup includes MongoDB \`.bson\` and \`.metadata.json\` files.

## 🔧 Configuration

To change the MongoDB URI, edit \`Main.java\`:

\`\`\`java
private static final String MONGO_URI = "your_uri_here";
private static final String DATABASE_NAME = "restaurantes_gdl";
private static final String COLLECTION_NAME = "restaurantes";
private static final int CANTIDAD_RESTAURANTES = 100;
\`\`\`

## 🐛 Troubleshooting

### Error: "mongodump is not available"
- Install MongoDB Database Tools
- Verify it's in PATH: \`mongodump --version\`
- **Note**: Backup is optional, other functions will work

### MongoDB connection error
- Verify the URI is correct
- Check your internet connection
- Make sure your IP is on the MongoDB Atlas whitelist

### Compilation error
\`\`\`bash
mvn clean install -U
\`\`\`

### Error: "package lombok does not exist"
\`\`\`bash
mvn clean compile
\`\`\`
If it persists, verify you have Java 17 and Maven correctly installed.

## 📝 Project Dependencies

- MongoDB Java Driver 4.11.1
- Lombok 1.18.30 (reduces boilerplate code)
- SLF4J 2.0.9 (logging)
- Gson 2.10.1 (JSON handling)

## 🌟 Key Features

- ✅ 100% realistic data from Guadalajara metropolitan area
- ✅ Real geographic coordinates by municipality
- ✅ Guaranteed unique names
- ✅ Contextual specialties according to food type
- ✅ Optimized indexes for common queries
- ✅ Automatic timestamped backup
- ✅ Real-time statistics
- ✅ User-friendly interface
- ✅ Robust error handling

## 📚 MongoDB Query Examples

\`\`\`javascript
// Search for tacos in Zapopan
db.restaurantes.find({ 
  municipio: "Zapopan", 
  tipoComida: "Tacos" 
})

// Top rated
db.restaurantes.find().sort({ calificacion: -1 }).limit(10)

// Restaurants near a point (5km)
db.restaurantes.find({
  ubicacion: {
    $near: {
      $geometry: { type: "Point", coordinates: [-103.3467, 20.6767] },
      $maxDistance: 5000
    }
  }
})
\`\`\`

## 👨‍💻 Development

To contribute or modify:

1. Clone the repository
2. Make your changes
3. Compile: \`mvn clean compile\`
4. Test: \`mvn exec:java -Dexec.mainClass="com.restaurantes.gdl.Main"\`

## 📄 License

This project is open source under the MIT license.

---

**Need help?** Check the logs or contact the development team.

Enjoy managing restaurants! 🎉🌮🍕
`;
