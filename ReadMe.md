# The mix drinks backend application

## Application setup

Environment variable

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

## API

Base url:
`https://api.mixdrinks.org/`

#### Search

`/cocktails/filter` - search by filters

Queries
| Name | Require |Type | Description |
|------|----------|-----------|------|
|query | No |String |The string to filter cocktail by name, filter return only cocktails which name contains `query` |
|tags | No |Array<Int> |The array of tag ids. The filter allow only cocktails, which connect to the one of tag |

Example
`/cocktails/filter?tags=60&query=ін` - Returns all cocktails with connect with tag {id:60, name:шоколадні} and
cocktail's name contains `ін`

Response
```json
[
  {
    "id": 245,
    "name": "Іноземний легіон",
    "images": [
      {
        "srcset": "https://image.mixdrinks.org/cocktails/245/origin/245.webp",
        "media": "screen and (min-width: 570px)",
        "type": "image/webp"
      },
      {
        "srcset": "https://image.mixdrinks.org/cocktails/245/560/245.webp",
        "media": "screen and (min-width: 410px)",
        "type": "image/webp"
      },
      {
        "srcset": "https://image.mixdrinks.org/cocktails/245/400/245.webp",
        "media": "screen and (min-width: 330px)",
        "type": "image/webp"
      },
      {
        "srcset": "https://image.mixdrinks.org/cocktails/245/320/245.webp",
        "media": "screen and (min-width: 0px)",
        "type": "image/webp"
      },
      {
        "srcset": "https://image.mixdrinks.org/cocktails/245/origin/245.jpg",
        "media": "screen and (min-width: 570px)",
        "type": "image/jpg"
      },
      {
        "srcset": "https://image.mixdrinks.org/cocktails/245/560/245.jpg",
        "media": "screen and (min-width: 410px)",
        "type": "image/jpg"
      },
      {
        "srcset": "https://image.mixdrinks.org/cocktails/245/400/245.jpg",
        "media": "screen and (min-width: 330px)",
        "type": "image/jpg"
      },
      {
        "srcset": "https://image.mixdrinks.org/cocktails/245/320/245.jpg",
        "media": "screen and (min-width: 0px)",
        "type": "image/jpg"
      }
    ],
    "goods": [
      {
        "name": "Апероль",
        "images": [
          {
            "srcset": "https://image.mixdrinks.org/goods/227/origin/227.webp",
            "media": "screen and (min-width: 570px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/227/560/227.webp",
            "media": "screen and (min-width: 410px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/227/400/227.webp",
            "media": "screen and (min-width: 330px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/227/320/227.webp",
            "media": "screen and (min-width: 0px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/227/origin/227.jpg",
            "media": "screen and (min-width: 570px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/227/560/227.jpg",
            "media": "screen and (min-width: 410px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/227/400/227.jpg",
            "media": "screen and (min-width: 330px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/227/320/227.jpg",
            "media": "screen and (min-width: 0px)",
            "type": "image/jpg"
          }
        ]
      },
      {
        "name": "Витриманий ром",
        "images": [
          {
            "srcset": "https://image.mixdrinks.org/goods/261/origin/261.webp",
            "media": "screen and (min-width: 570px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/261/560/261.webp",
            "media": "screen and (min-width: 410px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/261/400/261.webp",
            "media": "screen and (min-width: 330px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/261/320/261.webp",
            "media": "screen and (min-width: 0px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/261/origin/261.jpg",
            "media": "screen and (min-width: 570px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/261/560/261.jpg",
            "media": "screen and (min-width: 410px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/261/400/261.jpg",
            "media": "screen and (min-width: 330px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/261/320/261.jpg",
            "media": "screen and (min-width: 0px)",
            "type": "image/jpg"
          }
        ]
      },
      {
        "name": "Лимонна цедра",
        "images": [
          {
            "srcset": "https://image.mixdrinks.org/goods/56/origin/56.webp",
            "media": "screen and (min-width: 570px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/56/560/56.webp",
            "media": "screen and (min-width: 410px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/56/400/56.webp",
            "media": "screen and (min-width: 330px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/56/320/56.webp",
            "media": "screen and (min-width: 0px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/56/origin/56.jpg",
            "media": "screen and (min-width: 570px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/56/560/56.jpg",
            "media": "screen and (min-width: 410px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/56/400/56.jpg",
            "media": "screen and (min-width: 330px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/56/320/56.jpg",
            "media": "screen and (min-width: 0px)",
            "type": "image/jpg"
          }
        ]
      },
      {
        "name": "Херес Манзанілья",
        "images": [
          {
            "srcset": "https://image.mixdrinks.org/goods/242/origin/242.webp",
            "media": "screen and (min-width: 570px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/242/560/242.webp",
            "media": "screen and (min-width: 410px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/242/400/242.webp",
            "media": "screen and (min-width: 330px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/242/320/242.webp",
            "media": "screen and (min-width: 0px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/242/origin/242.jpg",
            "media": "screen and (min-width: 570px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/242/560/242.jpg",
            "media": "screen and (min-width: 410px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/242/400/242.jpg",
            "media": "screen and (min-width: 330px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/242/320/242.jpg",
            "media": "screen and (min-width: 0px)",
            "type": "image/jpg"
          }
        ]
      },
      {
        "name": "Лід в кубиках",
        "images": [
          {
            "srcset": "https://image.mixdrinks.org/goods/431/origin/431.webp",
            "media": "screen and (min-width: 570px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/431/560/431.webp",
            "media": "screen and (min-width: 410px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/431/400/431.webp",
            "media": "screen and (min-width: 330px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/431/320/431.webp",
            "media": "screen and (min-width: 0px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/431/origin/431.jpg",
            "media": "screen and (min-width: 570px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/431/560/431.jpg",
            "media": "screen and (min-width: 410px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/431/400/431.jpg",
            "media": "screen and (min-width: 330px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/431/320/431.jpg",
            "media": "screen and (min-width: 0px)",
            "type": "image/jpg"
          }
        ]
      },
      {
        "name": "Дюбонне",
        "images": [
          {
            "srcset": "https://image.mixdrinks.org/goods/554/origin/554.webp",
            "media": "screen and (min-width: 570px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/554/560/554.webp",
            "media": "screen and (min-width: 410px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/554/400/554.webp",
            "media": "screen and (min-width: 330px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/554/320/554.webp",
            "media": "screen and (min-width: 0px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/554/origin/554.jpg",
            "media": "screen and (min-width: 570px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/554/560/554.jpg",
            "media": "screen and (min-width: 410px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/554/400/554.jpg",
            "media": "screen and (min-width: 330px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/554/320/554.jpg",
            "media": "screen and (min-width: 0px)",
            "type": "image/jpg"
          }
        ]
      },
      {
        "name": "Ревеневий бітер",
        "images": [
          {
            "srcset": "https://image.mixdrinks.org/goods/3/origin/3.webp",
            "media": "screen and (min-width: 570px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/3/560/3.webp",
            "media": "screen and (min-width: 410px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/3/400/3.webp",
            "media": "screen and (min-width: 330px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/3/320/3.webp",
            "media": "screen and (min-width: 0px)",
            "type": "image/webp"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/3/origin/3.jpg",
            "media": "screen and (min-width: 570px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/3/560/3.jpg",
            "media": "screen and (min-width: 410px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/3/400/3.jpg",
            "media": "screen and (min-width: 330px)",
            "type": "image/jpg"
          },
          {
            "srcset": "https://image.mixdrinks.org/goods/3/320/3.jpg",
            "media": "screen and (min-width: 0px)",
            "type": "image/jpg"
          }
        ]
      }
    ],
    "tags": [
      {
        "id": 6,
        "name": "міцні"
      },
      {
        "id": 60,
        "name": "шоколадні"
      },
      {
        "id": 12,
        "name": "солодкі"
      },
      {
        "id": 20,
        "name": "трав'яні"
      },
      {
        "id": 21,
        "name": "на ромі"
      }
    ]
  }
]
```


#### Cocktails

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

#### Tags

Get list of tags

GET: `tags/all`

The response is array of tags.

```json
[
  {
    "id": 2,
    "name": "tag 1"
  },
  {
    "id": 2,
    "name": "tag 2"
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
