
0.3.0 / 2016-04-19
==================

  * add tests for date validation
  * add ValidationError response
  * Extract duplicated code to private method
  * Remove tests for null value in JSON
  * Make mock return 422 on malformed PUT to /contact/{id}/important
  * Test  malformed PUT requests to /contact/{id}/important
  * Test  malformed POST requests to /contacts
  * add ValidationError response
  * add validation for optional values
  * validate phone to be nonEmpty
  * move Error algebra to Validators object
  * make message a simple String
  * add field names to validation
  * add validations for contact request
  * Upgrade akka to version 2.4.4, it fixes issues with GET /contacts/sse
  * Send notifications only to author and commiter
  * Update README.md

0.2.2 / 2016-04-18
==================

  * Update README.md
  * Update README.md
  * Deploy docker images to registry
  * Publish on Travis to docker registry
  * Build docker images for tck-runner and akka-http-mock-server
  * Rely on manual termination of the Server
  * Always publish packages to bintray
  * Update README.md

0.2.1 / 2016-04-18
==================

  * Deploy packaged ZIP to Github Releases and use deploy support of travis for bintray publishing
  * Add some tests to verify if on error BaseTckTest prints Request and Response
  * Simplify Request and Response printing, use StringBuilder
  * Explicit result types in BaseTckTest
  * Port tests to use ~~> helper method
  * Disable fullstacks in tck-runner
  * Helper methods to simplify calling domofonRoute and asserting response with some debug on error
  * Gattling (#15)
  * Ensure returned Contact has no message
  * Add GET /contacts/{id}/message endpoint
  * Fix order of matchers, ensure default get contact is not used for other paths
  * add basic tests for contact message put
  * Remove message fields from GetContact
  * Fix tests for POST,GET /contacts - accept headers, optional fields
  * If adminEmail was posted it is saved
  * Ensure adminEmail is always set to notifyEmail if it wasn't set
  * Forbid posting JsArray to /contacts
  * We don't need JsonWriters in DomofonMarshalling
  * Remove dead code in tck
  * Allow PUT in CORS
  * Add links to swagger editor on mock startup
  * Add /contacts/{id}/notify endpoint
  * Report missing fields both as Json as plain text
  * Provide information about missing fields in Contact request
  * Fix typo
  * Format file
  * Add remove contact test
  * Add remove contact
  * Update README.md

0.2.0 / 2016-04-13
==================

  * Bump versions in README.md
  * Add support for /contacts/{id}/important and /domofon.yaml (#10)
  * Add option to change listening port (#9)
  * Update README.md (#8)
  * Update README.md (#7)
  * Merge pull request #6 from blstream/lustefaniak-patch-1
  * 404 contact tests (#5)
  * Add name to SSE event
  * Call terminate on system in mock Server
  * More reliable proxyResult in ExternalServer
  * Print fullstacks on exceptions in Runner
  * Guard tests execution in Try
  * (rename)
  * Ensure UUID is properly obtained in postContactRequest
  * Split into separate modules with tck,tck-runner, akka-http-mock and akka-http-mock-server
  * Update README.md
  * Update README.md
  * Create README.md

0.1.0 / 2016-04-12
==================

  * Travis and Bintray settings
  * /contact/{id}/deputy endpoint and TCK
  * GET /contacts/sse
  * GET /contacts GET /contacts/{id} tests
  * Tests for PUT /contacts with mock server
  * Initial commit
