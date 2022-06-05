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
- `mode` (variants: `MINI`, `COMPACT`). Default: MINI
    - MINI - just an id and a name
    - COMPACT - the entity with main info such as id, description, etc, with short information about ingredients.

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

## Images

Base url
`https://image.mixdrinks.org`

Supported size:

- 320
- 400
- 560
- origin

Supported size:

- webp
- jpg

### Cocktails

`/cocktails/{id}/{size}/{id}.{format}`
{id} - id of cocktail

Sample: `/cocktails/104/origin/104.webp`

### Goods

`/goods/{id}/{size}/{id}.{format}`
{id} - id of good

Sample: `/cocktails/104/origin/104.webp`
