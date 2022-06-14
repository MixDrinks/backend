#### Visit

##### Cocktails

POST: `cocktails/visit`

Queries

| Name | Require | Type   | Description     |
|------|---------|--------|-----------------|
| id   | Yes     | String | The cocktail id |

##### Items

POST: `items/visit`

Queries

| Name | Require | Type   | Description     |
|------|---------|--------|-----------------|
| id   | Yes     | String | The cocktail id |

---


Note:
The endpoints just notify the server. We cannot use the exists GET endpoints for count visits, because the get response
has
caches.