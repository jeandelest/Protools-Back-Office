# Protools POC - Flowable Engine 🦊
[Demo Link 😉](https://protools.dev.insee.io/)

This POC was build to test the Flowable Engine. It only serves as a demonstration support.

Current WIP & experimentations can be found on the protools-wip branch.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Installation
[Link to swagger](https://protools-flowable-demo.dev.insee.io/)

### Installation via Docker (Recommended)

```bash
docker pull docker pull inseefr/protools-back-office
docker run -d --name protoolsflowable -p 8080:8080  docker pull inseefr/protools-back-office:experimental
```
### Manual Install
``` bash
git clone git@github.com:InseeFr/Protools-Back-Office.git
cd Protools-Back-Office
git checkout demo
./mvnw spring-boot:run -Dmaven.test.skip
```





