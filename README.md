# 🧱 Shinederu PingMod

![Build](https://img.shields.io/github/actions/workflow/status/Shinederu/MC-Ananas-PingMods/build.yml?branch=main)
![Minecraft](https://img.shields.io/badge/minecraft-1.20.1-blue?style=flat-square)
![License](https://img.shields.io/github/license/Shinederu/MC-Ananas-PingMods)

---

## ✨ Description

> Petit mod Fabric qui envoie une requête HTTP vers une API dès que le serveur Minecraft est entièrement démarré.
> Parfait pour synchroniser des services externes, ping un dashboard web, ou faire des easter eggs 😄

---

## ⚙️ Fonctionnement

Dès que le serveur est **complètement lancé**, le mod envoie une requête GET vers :

```
https://api.play.shinederu.lol/server/serveroff
```

- Si l’API répond avec succès (code HTTP 200), le message retourné est affiché dans la console.
- En cas d’erreur (4xx / 5xx) ou de timeout, un message clair est aussi affiché.

---

## 🧹 Installation

1. Télécharge [`fabric-api`](https://modrinth.com/mod/fabric-api) pour Minecraft `1.20.1`
2. Glisse ce mod (`shinederu_pingmod-x.x.x.jar`) dans le dossier `mods/`
3. Démarre ton serveur Fabric !

---

## 🔧 Compilation locale

```bash
./gradlew build
```

Le fichier sera généré dans `build/libs/`.

---

## 📆 API pingée

Tu peux consulter ou tester l’API appelée ici :  
[https://api.play.shinederu.lol/server/serveroff](https://api.play.shinederu.lol/server/serveroff)

---

## 📜 Licence

Ce projet est sous licence **MIT** — fais-en ce que tu veux, mais pense à créditer 💙

---

*Made with 🍍 by Shinederu*

