Feature: Resource Service Messaging Flow
  As a client of the resource service
  I want to upload audio resources and have them processed
  So that they can be stored and metadata can be extracted

  Scenario: Upload a resource and verify the messaging flow
    Given the resource service is running with RabbitMQ
    When I upload a valid audio file
    Then the resource should be saved successfully
    And a message with the resource ID should be published to RabbitMQ
    And the resource processor should be able to retrieve the resource
