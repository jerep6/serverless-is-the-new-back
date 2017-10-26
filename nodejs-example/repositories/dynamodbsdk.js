const AWS = require('aws-sdk');
AWS.config.update({region: "eu-west-1"});

let config = {};
if(process.env.NODE_ENV === 'test') {
  config = { endpoint: new AWS.Endpoint('http://localhost:8888') };
}

module.exports.DynamoDB = new AWS.DynamoDB(config);
module.exports.DocumentClient = new AWS.DynamoDB.DocumentClient(config);
