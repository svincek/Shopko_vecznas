# Shopko – Pametna aplikacija za optimizaciju kupovine namirnica

**Shopko** je mobilna Android aplikacija razvijena u Kotlinu, s ciljem pomoći korisnicima u organizaciji i optimizaciji kupovine namirnica. Omogućuje skeniranje i izradu digitalnih popisa za kupovinu, usporedbu cijena između trgovina i odabir preferencija.

---

## Ključne funkcionalnosti

- Skeniranje i ručni unos popisa za kupovinu
- Prikaz najpovoljnijih trgovina u blizini s aktualnim popustima
- Filtriranje proizvoda prema brendovima i preferencijama korisnika

---

## 🧱 Tehnologije i alati

- **Jezik:** Kotlin
- **IDE:** Android Studio
- **Arhitektura:** MVVM
- **Mape i lokacija:** Google Maps API
- **CI/CD:** GitHub Actions

---

## 🛠️ Kako pokrenuti projekt lokalno

1. Kloniraj repozitorij:
   ```bash
   git clone https://github.com/svincek/Shopko_vecznas.git
   ```

2. Otvori projekt u **Android Studiju**.

3. Dodaj `local.properties` datoteku u root projekta:
   ```properties
   sdk.dir=/Users/korisnik/Library/Android/sdk
   API_KEY=xxx123
   ```

4. Pokreni `Build > Make Project` ili `./gradlew build`

---

## 📌 Napomena o commit porukama

Napomena: Stariji commitovi nisu u potpunosti dokumentirani jer je projekt prvotno razvijan bez formalnih commit pravila. Od 1.5.2025. commit poruke prate preporučenu praksu.
Od 1.5.2025., commit poruke prate standardizirani format:
```
feat: Dodana funkcija za filtriranje trgovina po udaljenosti
fix: Ispravljen bug pri skeniranju bar koda
```

---

## 📫 Kontakt

Autor: [Simon Vincek]  
Email: [simon.vincek@email.com]  
GitHub: [github.com/svincek]
