package ch.medidata.demo.impl;

import ch.medidata.demo.api.RestServiceApi;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestServiceImpl implements RestServiceApi {

   private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceImpl.class);
   private HttpServletResponse response;
   private HttpServletRequest request;

   @Context
   public void setResponse(final HttpServletResponse response) {
      this.response = response;
   }

   @Context
   public void setRequest(final HttpServletRequest request) {
      this.request = request;
   }

   public Response get() {
       
       System.out.println("get called");
      //todo real entity
      String entity = "{ \"message\": \"hello world!\" }";
      EntityTag etag = new EntityTag(String.valueOf(entity.hashCode()));

      String requestEtag = request.getHeader("If-None-Match");
      LOGGER.info("696c36bb entityEtag=[{}], requestEtag=[{}]", etag, requestEtag);
      if (etag.getValue().equals(requestEtag)) {
         response.setStatus(304);
      }

      response.addHeader("ETag", etag.getValue());

      return Response.status(200).entity(entity).tag(etag).cacheControl(CacheControl.valueOf("max-age=10")).build();
   }
}
