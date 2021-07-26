package ch.medidata.demo.client;

import javax.cache.Caching;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.cache.CacheControlFeature;

import ch.medidata.demo.api.DemoRestApi;


public class RestClient implements DemoRestApi {

   private CacheControlFeature feature;
   private WebTarget target;
   private String etag;
   private String previousResult;
   private boolean notModified;
   
   public RestClient() {
       
   }

   public String get() {
       
       try {
                  
           if (feature == null) {
               System.getProperties().setProperty(Caching.JAVAX_CACHE_CACHING_PROVIDER, "org.ehcache.jsr107.EhcacheCachingProvider");
               feature = new CacheControlFeature();
               feature.setCacheResponseInputStream(true);
               Client client = ClientBuilder.newBuilder()
                   .register(feature)
                   .build();
               target = client.target("http://localhost:8181/cxf/server/demoservice");
           }
           
           Response response  = null;
           if (etag == null) {
               response = target.request(MediaType.APPLICATION_JSON).header(HttpHeaders.CACHE_CONTROL, "public").get();
           } else {
               response = target.request(MediaType.APPLICATION_JSON).header(HttpHeaders.CACHE_CONTROL, "public").header(HttpHeaders.IF_NONE_MATCH, etag).get();
           }
           etag = response.getHeaderString("ETag");
           etag = etag.substring(1, etag.length() - 1);
           if (response.getStatus() == 304) {
               response.readEntity(String.class);
               //"304 Not Modified" 
               this.notModified = true;
           } else {
               if (!this.notModified) {
                   previousResult = response.readEntity(String.class);
               }
           }
           return this.previousResult;
           
       } finally {
           
       }    
   }
}
