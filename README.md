Protools
==============
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**Basé sur Flowable Engine 🦊**

# Podman en local
### Création de l'image
> podman build -t protoolsbo .  

### Lancement de l'image

On passe les properties par variables d'environnement.<BR>
La proposition ci-dessous s'appuie sur un fichier *.\secrets\secrets_protools_properties.properties* qui va contenir les
mots de passe des comptes keycloak.

Un exemple *secrets_protools_properties-example.properties* est présent dans ce dossier.

>podman run -p 8080:8080 --env-file .\src\main\resources\application-dev.properties --env-file .\secrets\secrets_protools_properties.properties localhost/protoolsbo:latest
