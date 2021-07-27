package ch.medidata.demo.client;

import ch.medidata.demo.api.DemoRestApi;
import ch.medidata.demo.api.RestServiceApi;

public class RestClient implements DemoRestApi {

   private RestServiceApi proxy;

   public RestClient(final RestServiceApi proxy) {
      this.proxy = proxy;
   }

   public String get() {
      return proxy.get().readEntity(String.class);
   }
}
