package ch.medidata.demo.impl;

import ch.medidata.demo.api.RestServiceApi;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestServiceImpl implements RestServiceApi {

   private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceImpl.class);
   private MessageContext jaxrsContext;

   
   
   @Context
   public void setMessageContext(MessageContext messageContext) {
       this.jaxrsContext = messageContext;
   }


    public Response get() {

        System.out.println("get called\n");
        // todo real entity
        String entity = "{ \"message\": \"hello world!\" }";
        EntityTag etag = new EntityTag(String.valueOf(entity.hashCode()));

        
        
        //use JAXRS API to check etag and return 304 if necessary
        ResponseBuilder rb = this.jaxrsContext.getRequest().evaluatePreconditions(etag);
        
        if (rb == null) {
            return Response.status(200).entity(entity).tag(etag).cacheControl(CacheControl.valueOf("max-age=10")).build();
        } else {
            //this will return 304
            return rb.cacheControl(CacheControl.valueOf("max-age=10")).build();
        }

    }
}
