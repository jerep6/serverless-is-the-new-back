const
  dynamodbLocal = require('dynamodb-localhost'),
  dynamodbsdk = require('../repositories/dynamodbsdk').DynamoDB
  dynamodbdoc = require('../repositories/dynamodbsdk').DocumentClient;

// dynamodbLocal.install();

module.exports = {
  beforeHook,
  afterHook,
  insertTable: populateTable
}


function beforeHook () {
  this.timeout(50000);

  startDynamoDB();
  return createTable();

}

function afterHook() {
  console.log('Stop dynamolocal');
  return dynamodbLocal.stop("8888");
}

function startDynamoDB() {
  console.log('Start dynamolocal');
  return dynamodbLocal.start({
    port: 8888,
    inMemory: true,
  });
}

function createTable() {
  console.log('Create tables');



  const params = {
    AttributeDefinitions: [ { AttributeName: "id", AttributeType: "S" } ],
    KeySchema: [ { AttributeName: "id", KeyType: "HASH" } ],
    ProvisionedThroughput: {
      ReadCapacityUnits: 5,
      WriteCapacityUnits: 5
    },
    TableName: "attendees"
  };

  return dynamodbsdk.createTable(params).promise()
}

function populateTable(tableName, content) {

  const params = {
    TableName : tableName,
    Item: content
  };


  return dynamodbdoc.put(params).promise();

}