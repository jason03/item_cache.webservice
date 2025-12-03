### Manual Tests ItemController Endpoints

#### Get all items
#### GET /items
```
curl -v -X GET 'http://localhost:8080/api/v1/items' \
-H 'Accept: application/json' \
-H 'Content-Type: application/json' \
| jq .
```


#### Get item by id
#### GET /items/{n}
```
curl -v -X GET 'http://localhost:8080/api/v1/items/1' \
-H 'Accept: application/json' \
-H 'Content-Type: application/json' \
| jq .
```

#### Create item
#### POST /items
```
curl -X POST 'http://localhost:8080/api/v1/items' \
-H 'Accept: application/json' \
-H 'Content-Type: application/json' \
-d '{
  "name": "Item 1",
  "status": "CURRENT",
  "description": "Item 1 summary"
}' | jq .
```

#### Update existing item by id
#### PUT /items/{n}
```
curl -X PUT 'http://localhost:8080/api/v1/items/1' \
-H 'Accept: application/json' \
-H 'Content-Type: application/json' \
-d '{
  "name": "Item 1",
  "status": "DISCONTINUED",
  "description": "Item 1 summary"
}' | jq .
```

#### Update existing item status by id
#### PUT /items/{n}
status = current | discontinued
```
curl -X PUT 'http://localhost:8080/api/v1/items/1?status=discontinued' \
-H 'Accept: application/json' \
-H 'Content-Type: application/json' | jq .
```

#### Delete an existing item by id
#### DELETE /items/{n}
```
curl -v -X DELETE 'http://localhost:8080/api/v1/items' \
-H 'Accept: application/json' \
-H 'Content-Type: application/json' | jq .
```
