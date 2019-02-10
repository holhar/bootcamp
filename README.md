# Running Cloud Foundry ITs

In order to run the integration tests that use Cloud Foundry services make sure to configure the information necessary
to work with the *cf API* and that the JARs of the applications to be deployed are present. It might be possible, that
the service names and/or plans of the configured cf services in ITs have changed over time. Check out the *Cloud Foundry
 Marketplace* to find out about any changes and at up to date services.

## Cloud Foundry Properties

The props must be set either in the *application.properties* of the app containing the ITs or, when using a
*CloudFoundryOperations* Bean for accessing the Cloud Foundry API, as dedicated method params.

### application.properties Setup

```
cf.org=<CF-ORGANIZATION-HERE>
cf.space=<CF-SPACE-HERE>
cf.api=https://api.run.pivotal.io
cf.user=<CF-USERNAME-HERE>
cf.password=<CF-PASSWORD-HERE>
```

### CloudFoundryOperations Setup

```Java
@Bean
CloudFoundryOperations cloudFoundryOperations() {
    DefaultConnectionContext connectionContext = DefaultConnectionContext.builder()
            .apiHost("https://api.run.pivotal.io")
            .build();

    TokenProvider tokenProvider = PasswordGrantTokenProvider.builder()
            .password("<CF-PASSWORD-HERE>")
            .username("<CF-USERNAME-HERE>")
            .build();

    ReactorCloudFoundryClient reactorClient = ReactorCloudFoundryClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();

    CloudFoundryOperations cloudFoundryOperations = DefaultCloudFoundryOperations.builder()
            .cloudFoundryClient(reactorClient)
            .organization("<CF-ORGANIZATION-HERE>")
            .space("<CF-SPACE-HERE>")
            .build();

    return cloudFoundryOperations;
}
```