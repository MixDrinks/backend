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

[Link to docker hub](https://hub.docker.com/r/vovochkastelmashchuk/mixdrinks)
```bash
docker pull vovochkastelmashchuk/mixdrinks:tagname
```

`tagname` - the tag of the image. Can be one of the following:

- `latest` - the production version of the app
- pr-{{pull_request_number}}, example: `pr-1`
- sha-{{commit_short_sha}}, example: `sha-1234567`
- {{branch_name}} - the latest commit from the main branch, example: `main`

## Environment variables

The app require the postgres database. Run the postgres database and provide the following environment variables:

* DB_URL - the url to data, include `sslmode=require` if your install of database needs it.
* DB_USER - the username. The user must have read access to all database
* DB_PASSWORD - the password

[More info](https://mixdrinks.github.io/docs/backend/)

## Find a bug?

If you found an issue or would like to submit an improvement to this project, please submit an issue using the issues
tab above. If you would like to submit a PR with a fix, reference the issue you created!

## CI/CD

The project use github actions for CI/CD. The CI/CD pipelines is described in the folder `.github/workflows/`
The job verify the code style, run the tests and build the docker image for each pull request.
After push to the main branch the job build the docker image with tags latest and sha-{short_commit_sha} and push it to
the docker hub. After the push the job trigger the digital ocean app platform to deploy the new version of the app, we
use the sha for identify the version, we cannot use the latest tag because the latest tag is not immutable, and be
doesn't have opportunity to rollback.

### Deploy process

All changes from main branch immediately deploy to production. The project doesn't have stage environment I trust the
tests and the deployment workflow.
