# Protools POC - Flowable Engine 🦊

[Demo Link 😉](https://protools.dev.insee.io/)

This POC was build to test the Flowable Engine. This branch is dedicated to various experiments and tests.

Currently testing advanced features for l'Enquête Famille

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Installation

[Link to swagger](https://protools-flowable.dev.insee.io/)

### Installation via Docker (Recommended)

```bash
docker pull mailinenguyen/protools-flowable
docker run -d --name protoolsflowable -p 8080:8080  mailinenguyen/protools-flowable:latest
```

### Manual Install

```bash
git clone git@github.com:POCProtools/Protools-Flowable.git
cd Protools-Flowable
git checkout protools-wip
./mvnw spring-boot:run
```
