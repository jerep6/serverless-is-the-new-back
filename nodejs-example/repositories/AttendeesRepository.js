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