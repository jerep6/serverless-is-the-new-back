
module.exports.handler = function (event, context, callback) {

  const hello = `Hello ${event.queryStringParameters.name || "inconnu"}`;
  callback(null, {
    statusCode: 200,
    body: hello,
    headers: {
      "X-Powered-By": "nodejs"
    }
  });
};
