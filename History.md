0.5.0 / 2016-04-23
==================

  * Release 0.5.0
  * Update README.md
  * Remove sbt-buildinfo
  * Cleanup not aggregated coverage results from upload
  * Put BuildInfo object in package
  * Aggregate coverage at the end of build
  * migrate mock server to integer-based entity ids
  * remove assumption that ids are UUIDs in tests
  * IsImportant could be sent as text/plain true or false too
  * Remove support for /contact/{id}/message
  * add test for update message without admin token
  * add category message management
  * store category messages with ids

0.4.2 / 2016-04-21
==================

  * Update README.md
  * Add easier to understand tck-runner parameter requirement
  * decouple admin credentials from sys.env in TCK
  * decouple admin credentials from sys.env
  * use env for TCK admin credentials
  * display admin login/pass on server startup
  * get mock admin user/pass from env
  * Provide buildinfo on Server and tck-runner startup
  * Enable sbt-buildinfo plugin
  * Replace isBatch=true with isIndividual=false

0.4.1 / 2016-04-21
==================

  * inline admin token
  * add authorization to category post, delete
  * clean up validators
  * Make phone in Contact optional
  * Ensure latest docker images are pulled before running
  * Update readme for version 0.4.0

0.4.0 / 2016-04-21
==================

  * Fix gatling compilation, generate random uuid for category
  * Filtering of Contacts by category in MockServer
  * Tests for filtering Contacts by category
  * Hide contactResponseFormat as it could expose sensitive data
  * Ensure Contact belongs to valid category during creation
  * Category fields validation
  * Use EntityCreated as result of postCategoryRequest
  * Replace raw responses with case classes and proper marshallers
  * Add tests for no auth admin perations returning Unauthorized
  * Nicer error message for category with isBatch = false in notify
  * Move things into packages to simplify navigation
  * remove admin logout endpoint
  * move authorization to Auth trait
  * Make sure returned domofon.yml has host: pointing to the mock server
  * add test for operations always allowed for admin
  * add admin authentication
  * pass secret in tests modifing the contact
  * Bypass swagger.editor.io proxy, as we support CORS
  * require secret as Bearer token, pass it as cookie on creation
  * remove unneeded ContactCreateResponse
  * add secret to contact
  * Split MockServer trait into smaller traits
  * Add support for Categories endpoint
  * On Success return OperationSuccessful which also supports application/json
  * Add missing accept json

0.3.0 / 2016-04-19
==================

  * Add History.md using `git changelog -t 0.3.0 --all -p -n`
  * Add AUTHORS from `git authors`
  * Bump version in README.md  to 0.3.0
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

n.n.n / 2016-04-24
==================



0.5.0 / 2016-04-23
==================

  * Merge pull request #72 from blstream/release.0.5.0
  * Release 0.5.0
  * Merge pull request #71 from blstream/lustefaniak-patch-1
  * Update README.md
  * Merge pull request #70 from blstream/fix-codecov
  * Remove sbt-buildinfo
  * Cleanup not aggregated coverage results from upload
  * Put BuildInfo object in package
  * Aggregate coverage at the end of build
  * Merge pull request #69 from blstream/remove-uuid-assumption
  * migrate mock server to integer-based entity ids
  * remove assumption that ids are UUIDs in tests
  * Merge pull request #65 from blstream/fix-get-important
  * IsImportant could be sent as text/plain true or false too
  * Merge pull request #64 from blstream/remove-messages-from-contact
  * Remove support for /contact/{id}/message
  * Merge pull request #63 from blstream/category-messages
  * add test for update message without admin token
  * add category message management
  * store category messages with ids

0.4.2 / 2016-04-21
==================

  * Merge pull request #62 from blstream/lustefaniak-patch-1
  * Update README.md
  * Merge pull request #58 from blstream/get-credentials-from-environment
  * Add easier to understand tck-runner parameter requirement
  * decouple admin credentials from sys.env in TCK
  * decouple admin credentials from sys.env
  * use env for TCK admin credentials
  * display admin login/pass on server startup
  * get mock admin user/pass from env
  * Merge pull request #60 from blstream/buildinfo-for-easier-debug
  * Provide buildinfo on Server and tck-runner startup
  * Enable sbt-buildinfo plugin
  * Merge pull request #59 from blstream/filip-isbatch-meaning
  * Replace isBatch=true with isIndividual=false

0.4.1 / 2016-04-21
==================

  * Merge pull request #56 from blstream/authorize-categories
  * inline admin token
  * add authorization to category post, delete
  * clean up validators
  * Merge pull request #54 from blstream/optional-phone-number
  * Make phone in Contact optional
  * Merge pull request #53 from blstream/docker-pull-in-readme
  * Ensure latest docker images are pulled before running
  * Merge pull request #52 from blstream/release-0.4.0
  * Update readme for version 0.4.0

0.4.0 / 2016-04-21
==================

  * Merge pull request #51 from blstream/contact-in-category
  * Fix gatling compilation, generate random uuid for category
  * Filtering of Contacts by category in MockServer
  * Tests for filtering Contacts by category
  * Hide contactResponseFormat as it could expose sensitive data
  * Ensure Contact belongs to valid category during creation
  * Merge pull request #49 from blstream/category-validations
  * Category fields validation
  * Merge pull request #48 from blstream/some-cleanups
  * Use EntityCreated as result of postCategoryRequest
  * Replace raw responses with case classes and proper marshallers
  * Add tests for no auth admin perations returning Unauthorized
  * Nicer error message for category with isBatch = false in notify
  * Merge pull request #45 from blstream/moving-things-around
  * Move things into packages to simplify navigation
  * Merge pull request #46 from blstream/authorization
  * Merge branch 'authorization' of https://github.com/blstream/domofon-tck into authorization
  * remove admin logout endpoint
  * Merge branch 'master' into authorization
  * Merge pull request #47 from blstream/swagger-editor-link
  * move authorization to Auth trait
  * Make sure returned domofon.yml has host: pointing to the mock server
  * add test for operations always allowed for admin
  * add admin authentication
  * pass secret in tests modifing the contact
  * Bypass swagger.editor.io proxy, as we support CORS
  * require secret as Bearer token, pass it as cookie on creation
  * remove unneeded ContactCreateResponse
  * add secret to contact
  * Merge pull request #44 from blstream/categories
  * Split MockServer trait into smaller traits
  * Add support for Categories endpoint
  * Merge pull request #42 from blstream/respect-accept-headers-always
  * On Success return OperationSuccessful which also supports application/json
  * Add missing accept json

0.3.0 / 2016-04-19
==================

  * Merge pull request #41 from blstream/relases-0.3.0
  * Add History.md using `git changelog -t 0.3.0 --all -p -n`
  * Add AUTHORS from `git authors`
  * Bump version in README.md  to 0.3.0
  * Merge pull request #40 from blstream/validation-with-jjg-tests
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
  * Merge pull request #38 from blstream/sse-fixes
  * Upgrade akka to version 2.4.4, it fixes issues with GET /contacts/sse
  * Merge pull request #37 from blstream/notification-emails-to-authors
  * Send notifications only to author and commiter
  * Merge pull request #36 from blstream/lustefaniak-patch-2
  * Update README.md

0.2.2 / 2016-04-18
==================

  * Merge pull request #35 from blstream/lustefaniak-patch-1
  * Update README.md
  * Update README.md
  * Merge pull request #34 from blstream/build-docker-images
  * Deploy docker images to registry
  * Publish on Travis to docker registry
  * Build docker images for tck-runner and akka-http-mock-server
  * Rely on manual termination of the Server
  * Merge pull request #32 from blstream/always-deploy-to-bintray
  * Always publish packages to bintray
  * Merge pull request #31 from blstream/lustefaniak-patch-1
  * Update README.md

0.2.1 / 2016-04-18
==================

  * Deploy packaged ZIP to Github Releases and use deploy support of travis for bintray publishing
  * Merge pull request #29 from blstream/debug-request-and-response
  * Add some tests to verify if on error BaseTckTest prints Request and Response
  * Simplify Request and Response printing, use StringBuilder
  * Explicit result types in BaseTckTest
  * Port tests to use ~~> helper method
  * Disable fullstacks in tck-runner
  * Helper methods to simplify calling domofonRoute and asserting response with some debug on error
  * Gattling (#15)
  * Merge pull request #26 from blstream/contact-id-message-tests
  * Ensure returned Contact has no message
  * Add GET /contacts/{id}/message endpoint
  * Fix order of matchers, ensure default get contact is not used for other paths
  * add basic tests for contact message put
  * Merge pull request #28 from blstream/cherrys-from-lukasz-indykiewicz
  * Remove message fields from GetContact
  * Merge pull request #27 from blstream/cherrys-from-lukasz-indykiewicz
  * Fix tests for POST,GET /contacts - accept headers, optional fields
  * Merge pull request #25 from blstream/admin-email-test
  * If adminEmail was posted it is saved
  * Ensure adminEmail is always set to notifyEmail if it wasn't set
  * Merge pull request #24 from blstream/post-contact-array
  * Forbid posting JsArray to /contacts
  * Merge pull request #22 from blstream/cleanup-tck
  * We don't need JsonWriters in DomofonMarshalling
  * Remove dead code in tck
  * Merge pull request #21 from blstream/cors-put
  * Allow PUT in CORS
  * Add links to swagger editor on mock startup
  * Merge pull request #19 from blstream/notify-endpoint
  * Add /contacts/{id}/notify endpoint
  * Merge pull request #18 from blstream/missing-fields-validation-spray
  * Report missing fields both as Json as plain text
  * Provide information about missing fields in Contact request
  * Merge pull request #17 from blstream/delet-contact
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
  * Merge pull request #4 from blstream/fixes-to-unhandled-requests-in-external
  * Add name to SSE event
  * Call terminate on system in mock Server
  * More reliable proxyResult in ExternalServer
  * Print fullstacks on exceptions in Runner
  * Guard tests execution in Try
  * (rename)
  * Ensure UUID is properly obtained in postContactRequest
  * Merge pull request #3 from blstream/split-into-subprojects
  * Split into separate modules with tck,tck-runner, akka-http-mock and akka-http-mock-server
  * Merge pull request #2 from blstream/lustefaniak-patch-1
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
