
# Shopko – Pametna Android aplikacija za optimizaciju kupovine

**Shopko** je mobilna Android aplikacija razvijena u Kotlinu koja korisnicima pomaže u organizaciji kupovine. Omogućuje skeniranje artikala, usporedbu cijena i pronalazak najpovoljnijih trgovina na temelju lokacije.

## Ključne funkcionalnosti

- Skeniranje i ručni unos artikala
- Izrada i uređivanje popisa za kupovinu
- Usporedba cijena proizvoda među trgovinama
- Prikaz najbližih i najpovoljnijih trgovina (lokacijski)
- Filtriranje po brendovima, kategorijama i preferencijama

## Tehnologije i alati

- **Jezik:** Kotlin
- **IDE:** Android Studio
- **Arhitektura:** MVVM
- **Mape i lokacija:** Google Maps API
- **CI/CD:** GitHub Actions
- **JSON komunikacija:** Retrofit + Moshi/Gson

## Pokretanje projekta lokalno

1. Kloniraj repozitorij:
   ```bash
   git clone https://github.com/svincek/Shopko_vecznas.git
Otvori projekt u Android Studio.

Dodaj local.properties datoteku:

properties
Copy
Edit
sdk.dir=/Users/korisnik/Library/Android/sdk
API_KEY= # kontaktirati na: simon.vincek@gmail.com za api ključ
Pokreni:

Build > Make Project

ili preko CLI: ./gradlew build

Komunikacija s backendom
Aplikacija se spaja na Spring Boot REST API (hostiran posebno), preko sljedećih ruta:

GET /articles?page=X&size=Y – artikli

GET /stores – trgovine s geolokacijama

Primjeri commit poruka
bash
Copy
Edit
feat: Implementiran prikaz trgovina na mapi
fix: Ispravljen crash prilikom skeniranja praznog bar koda

👤 Autori
Simon Vincek – simon.vincek@gmail.com

Dino Huđ – dhud61@gmail.com

Lara Šljivić – lara.sljivic@gmail.com

GitHub: github.com/svincek
