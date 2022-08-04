# The backend service for [mixdrinks.org](https://mixdrinks.org/). The service for home cocktails cooking.

[Our website](https://mixdrinks.org/)

### The API provides rest api about cocktails, receipts, goods and tools which your need to create the cocktails.

The service provide api for

* Cocktails
* Goods
* Tools
* Tags
* Filter by tags, goods, tools

### Using

[REST API](https://mixdrinks.github.io/docs/api)

### Deploy your own instance of service by digital ocean app platform

[![Deploy to DO](https://www.deploytodo.com/do-btn-blue.svg)](https://cloud.digitalocean.com/apps/new?repo=https://github.com/MixDrinks/backend/tree/main)

*Not fully tested at the moment*

## Install & Run

The app require the postgres database. Run the postgres database and provide the following environment variables:

* DB_URL - the url to data, include `sslmode=require` if your install of database needs it.
* DB_USER - the username. The user must have read access to all database
* DB_PASSWORD - the password

[More info](https://mixdrinks.github.io/docs/backend/)

## Find a bug?

If you found an issue or would like to submit an improvement to this project, please submit an issue using the issues
tab above. If you would like to submit a PR with a fix, reference the issue you created!
