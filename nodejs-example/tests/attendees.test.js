const
  expect = require('chai').expect
  utils = require('./test.utils')
  handler = require('../handlers/AttendeesHandler'),
  bluebird = require('bluebird');

const get = bluebird.promisify(handler.get);
const list = bluebird.promisify(handler.list);

describe('Attendees', () => {
  before(utils.beforeHook);
  after(utils.afterHook);

  it('should read an attendee', done => {
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
  });

});