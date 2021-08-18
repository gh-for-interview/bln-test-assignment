# Тестовое приложение

### Требования

openjdk 8, sbt, docker

### Запуск

```
sbt clean assembly
docker build -f build-image.dockerfile -t app .
docker-compose up -d
```

Порты:

* Kafka: `127.0.0.1:9092`
* Zookeeper: `127.0.0.1:2181`
* App: `127.0.0.1:29303`
* Postgre: `127.0.0.1:5432`
* Adminer: `127.0.0.1:8080`

API:

* создание сущности - PUT [http://localhost:29303/api/v1/webhook/](http://localhost:29303/api/v1/webhook/)
* изменение сущности - PATCH [http://localhost:29303/api/v1/webhook/id](http://localhost:29303/api/v1/webhook/id)
* удаление сущности - DELETE [http://localhost:29303/api/v1/webhook/id](http://localhost:29303/api/v1/webhook/id)
* получение сущности - GET [http://localhost:29303/api/v1/webhook/id](http://localhost:29303/api/v1/webhook/id)

### Проверить покрытие тестами

```
sbt clean coverage test
sbt coverageReport 
```
Отчет лежит в `target/scala-2.13/coverage-report`

Текущее покрытие - 67.90%

### Запросы для проверки API

Создание webhook
```
curl --header "Content-Type: application/json" \
  --request PUT \
  --data '{"url":"http://some-url.com","event":"creation"}' \
  http://localhost:29303/api/v1/webhook/
```

Получение webhook
```
curl http://localhost:29303/api/v1/webhook/id
```

Изменение webhook
```
curl --header "Content-Type: application/json" \
  --request PATCH \
  --data '{"url":"http://some-url.com","event":"deletion"}' \
  http://localhost:29303/api/v1/webhook/id
```

Удаление webhook
```
curl --request DELETE http://localhost:29303/api/v1/webhook/id
```

Docker host network не виден на Mac, для тестирования на Mac надо зайти внутрь контейнера
```
docker exec -it testassignmentbln_app_1 /bin/bash
```
