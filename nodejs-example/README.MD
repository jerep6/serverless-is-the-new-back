
# Pré-requis
Dans le terminal
```bash
export AWS_PROFILE=xebia-dev
```

# DEMO Lambda Node.js

0. Création de la table DynamoDB **"attendees"** dans la console AWS
Item 1 :
```
{
  "firstName": "Gerome",
  "id": "1",
  "job": "developer",
  "lastName": "Egron"
}
```

Item 2 :
```
{
  "firstName": "Jeremy",
  "id": "2",
  "job": "developer",
  "lastName": "Pinsolle"
}
```

1. Présentation **package.json**

2. Création **repositories/AttendeesRepository.js**
```javascript
const dynamo = require('./dynamodbsdk').DocumentClient;

module.exports.list = function () {
  return dynamo.scan({TableName : 'attendees'})
    .promise()
    .then(result => result.Items);
};

module.exports.get = function (id) {
  return dynamo.get({
    TableName : 'attendees',
    Key: { id: id }}
    )
    .promise()
    .then(result => result.Item);
};
```

3. Création **handlers/AttendeesHandler.js**
```javascript
const repo = require('../repositories/AttendeesRepository');

module.exports.list = function (event, context, callback) {
  repo.list().then(result => {
    callback(null, {
      statusCode: 200,
      body: JSON.stringify(result)
    });
  });
};

module.exports.get = function (event, context, callback) {
  repo.get(event.pathParameters.id).then(result => {
    let response;
    if (!result) {
      response = {
        statusCode: 404,
        body: undefined
      };
    } else {
      response = {
        statusCode: 200,
        body: JSON.stringify(result)
      };
    }

    callback(null, response);
  });
};
```
4. Création **serverless.yml**
```yaml
service: node-bdxio

provider:
  name: aws
  runtime: nodejs6.10
  region: eu-west-1
  tracing: true

  iamRoleStatements:
    - Effect: Allow
      Action: "dynamodb:*"
      Resource: "arn:aws:dynamodb:eu-west-1:*:table/attendees"


functions:

  attendee-list:
    handler: handlers/AttendeesHandler.list
    name: bdx-attendee-list
    events:
      - http:
          path: /attendees
          method: get

  attendee-get:
    handler: handlers/AttendeesHandler.get
    name: bdx-attendee-get
    events:
      - http:
          path: /attendees/{id}
          method: get

```

5. Ajouter les plugins
```yaml
plugins:
  # Run local API gateway
  - serverless-offline
```

6. Deployer en local
```bash
serverless offline start
```

7. Deployer sur AWS
```bash
serverless deploy
```

8. Tester : remplacer le TODO dans le fichier **tests/attendees.test.js**
```javascript
utils.insertTable('attendees', {id: "test1", name: "john"})
      .then(() => {

        const events = {
          pathParameters: {
            id: "test1"
          }
        };

        get(events, null)
          .then(attendee => {
            expect(attendee).to.be.not.undefined;
            expect(attendee.name).to.be.equal('john');
          })

      })
      .then(() => done())
      .catch(err => done(err));
```

