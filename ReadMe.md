# The mix drinks backend application

## Application setup

Environment variable

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

## API

Base url:
`https://api.mixdrinks.org/`

### Cocktails

`\cocktails`

#### Search

Queries

- `query`

Response

```json
[
  {
    "id": 1,
    "name": "Назва 1"
  },
  {
    "id": 2,
    "name": "Назва 2"
  }
]
```

Sample

```http
https://api.mixdrinks.org/cocktails?query=Лон

HTTP/1.1 200 OK
Content-Length: 55
Content-Type: application/json
Connection: keep-alive

[
  {
    "id": 322,
    "name": "Лонг айленд айс ті"
  }
]
```
