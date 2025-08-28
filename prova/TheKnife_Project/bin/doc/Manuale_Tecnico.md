# Manuale Tecnico — TheKnife

## Architettura
- **Model**: User, Restaurant, Review, Role, Location
- **Repo**: accesso CSV (UserRepository, RestaurantRepository, ReviewRepository, FavoritesRepository, DataStore)
- **Service**: regole di business (UserService, RestaurantService)
- **Controller**: flussi CLI (UserController, RestaurantController)
- **Util**: CsvUtils, SecurityUtils, ConsoleUtils

## Dati (CSV)
- restaurants.csv: id,name,country,city,address,lat,lon,avgPrice,delivery,booking,cuisine,ownerUsername
- users.csv: username,passwordHash,name,surname,birthdate,domicile,role
- reviews.csv: id,restaurantId,username,stars,text,reply
- favorites.csv: username,restaurantId

## Build
- Maven (Java 17), main class `theknife.TheKnife`.
- Jar: `mvn package` -> `target/TheKnife-1.0.0.jar`

## Estensioni possibili
- Persistenza su DB, GUI, geocoding, validazioni estese, pagination ricerca.
