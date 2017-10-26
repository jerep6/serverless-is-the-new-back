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