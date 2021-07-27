#maven Build
mvn clean install

#install in karaf
install --start mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.javax-cache-api/1.0.0_1
install --start mvn:org.ehcache/ehcache/3.2.3
install --start mvn:ch.medidata.demo/cxf-clientcache/1.0.0-SNAPSHOT

If error occurs: Caused by: java.lang.ClassNotFoundException: javax.cache.configuration.Configuration not found by org.apache.cxf.cxf-rt-rs-client [140]
refresh 140

This ist a start-level problem, because ehcache bundle should be started before cxf-rt-rs-client. For the sample this is irrelevant.

During startup of cxf-clientcache, there should be a "fcd0e875 enabling cache control feature" indecationg that CacheControlFeatureWrapper.configure was called.
Unfortunately, this never happened.

------------------------------------------------------------------------------------------------------------------------------------------------------------------------
The ultimate goal of cxf CacheControlFeature is that the entity and etag get cached and multiple calls on
http://localhost:8181/cxf/client/democlient
would not result in multiple calls to the server interface
http://localhost:8181/cxf/server/demoservice. (because the entity is cached locally for some time)

Also,  every call to http://localhost:8181/cxf/server/demoservice will be called with the current cached etag as "If-None-Match" header.
On the server side the "If-None-Match" etag will be compared with the current entity etag. If equal, the server will send a "304 Not Modified" Response,
which the CacheControlFeature would handle.
------------------------------------------------------------------------------------------------------------------------------------------------------------------------
