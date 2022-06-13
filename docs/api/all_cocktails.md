## All cocktails

**Deprecated**, use the [meta](meta.md) endpoint instead

`cocktails/all` - return the list of all cocktails, with short information id and name.

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
