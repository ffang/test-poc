package ch.medidata.demo.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import javax.cache.spi.CachingProvider;

import org.apache.cxf.jaxrs.client.cache.CacheControlClientReaderInterceptor;
import org.apache.cxf.jaxrs.client.cache.CacheControlClientRequestFilter;
import org.apache.cxf.jaxrs.client.cache.Entry;
import org.apache.cxf.jaxrs.client.cache.Key;

public class CacheProvidersFactory {

   private CachingProvider provider;
   private CacheManager manager;
   private Cache<Key, Entry> cache;
   private boolean cacheResponseInputStream;

   public CacheControlClientRequestFilter createCacheControlClientRequestFilter() {
       if (cache == null) {
           System.getProperties().setProperty(Caching.JAVAX_CACHE_CACHING_PROVIDER, "org.ehcache.jsr107.EhcacheCachingProvider");
           cache = createCache(new HashMap<String, Object>());
       }
       return new CacheControlClientRequestFilter(cache);
   }
   
   public CacheControlClientReaderInterceptor createCacheControlClientReaderInterceptor() {
       if (cache == null) {
           System.getProperties().setProperty(Caching.JAVAX_CACHE_CACHING_PROVIDER, "org.ehcache.jsr107.EhcacheCachingProvider");
           cache = createCache(new HashMap<String, Object>());
       }
       CacheControlClientReaderInterceptor reader = new CacheControlClientReaderInterceptor(cache);
       reader.setCacheResponseInputStream(cacheResponseInputStream);
       return reader;
   }

   private Cache<Key, Entry> createCache(final Map<String, Object> properties) {
       final Properties props = new Properties();
       props.putAll(properties);

       final String prefix = this.getClass().getName() + ".";
       final String uri = props.getProperty(prefix + "config-uri");
       final String name = props.getProperty(prefix + "name", this.getClass().getName());

       final ClassLoader contextClassLoader = CacheProvidersFactory.class.getClassLoader();

       provider = Caching.getCachingProvider(contextClassLoader);
       try {
           manager = provider.getCacheManager(
                   uri == null ? provider.getDefaultURI() : new URI(uri),
                   contextClassLoader,
                   props);

           final MutableConfiguration<Key, Entry> configuration = new MutableConfiguration<Key, Entry>()
               .setReadThrough("true".equalsIgnoreCase(props.getProperty(prefix + "readThrough", "false")))
               .setWriteThrough("true".equalsIgnoreCase(props.getProperty(prefix + "writeThrough", "false")))
               .setManagementEnabled(
                   "true".equalsIgnoreCase(props.getProperty(prefix + "managementEnabled", "false")))
               .setStatisticsEnabled(
                   "true".equalsIgnoreCase(props.getProperty(prefix + "statisticsEnabled", "false")))
               .setStoreByValue("true".equalsIgnoreCase(props.getProperty(prefix + "storeByValue", "false")));

           final String loader = props.getProperty(prefix + "loaderFactory");
           if (loader != null) {
               @SuppressWarnings("unchecked")
               Factory<? extends CacheLoader<Key, Entry>> f = newInstance(contextClassLoader, loader, Factory.class);
               configuration.setCacheLoaderFactory(f);
           }
           final String writer = props.getProperty(prefix + "writerFactory");
           if (writer != null) {
               @SuppressWarnings("unchecked")
               Factory<? extends CacheWriter<Key, Entry>> f = newInstance(contextClassLoader, writer, Factory.class);
               configuration.setCacheWriterFactory(f);
           }
           final String expiry = props.getProperty(prefix + "expiryFactory");
           if (expiry != null) {
               @SuppressWarnings("unchecked")
               Factory<? extends ExpiryPolicy> f = newInstance(contextClassLoader, expiry, Factory.class);
               configuration.setExpiryPolicyFactory(f);
           }

           cache = manager.createCache(name, configuration);
           return cache;
       } catch (final URISyntaxException e) {
           throw new IllegalArgumentException(e);
       }
   }

   @SuppressWarnings("unchecked")
   private static <T> T newInstance(final ClassLoader contextClassLoader, final String clazz, final Class<T> cast) {
       try {
           return (T) contextClassLoader.loadClass(clazz).newInstance();
       } catch (final Exception e) {
           throw new IllegalArgumentException(e);
       }
   }

   public CacheProvidersFactory(boolean cacheStream) {
       this.cacheResponseInputStream = cacheStream;
   }
}
