# Protools - Flowable Engine 🦊
[Demo Link](https://protools.dev.insee.io/)

This prototype was build with Flowable Engine. At the moment, it only serves as a demonstration support.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Installation
[Link to swagger](https://protools-back-office.demo.insee.io/)

### Installation via Docker (Recommended)

```bash
docker pull docker pull inseefr/protools-back-office
docker run -d --name protoolsflowable -p 8080:8080  docker pull inseefr/protools-back-office:experimental
```
### Manual Install
``` bash
git clone git@github.com:InseeFr/Protools-Back-Office.git
cd Protools-Back-Office
./mvnw spring-boot:run -Dmaven.test.skip
```





