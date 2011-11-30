package org.obm.annotations.transactional;

import java.util.Collection;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Guice;

@RunWith(Arquillian.class)
public class TemperatureConverterTest {

   private TemperatureConverter converter;

   @Before
   public void setup() {
	   converter = Guice.createInjector(new TransactionalModule()).getInstance(TemperatureConverter.class);
   }
   
   @Deployment
   public static WebArchive createTestArchive() {
	   
	   Collection<GenericArchive> deps = new MavenImpl()
	   		.dependencies("com.google.inject:guice:3.0",
	   				"org.slf4j:slf4j-api:1.6.2",
	   				"aopalliance:aopalliance:jar:1.0",
					"javax.inject:javax.inject:1");
	   
	   return ShrinkWrap.create(WebArchive.class, "test.war")
			   .addAsLibraries(deps)
			   .addClass(TemperatureConverter.class)
			   .addPackage(Package.getPackage("org.obm.annotations.transactional"));
   }


   @Test

   public void testConvertToCelsius() {
      Assert.assertEquals(converter.convertToCelsius(32d), 0d, 0.1d);
      Assert.assertEquals(converter.convertToCelsius(212d), 100d, 0.1d);
   }


   @Test
   public void testConvertToFarenheit() {
      Assert.assertEquals(converter.convertToFarenheit(0d), 32d, 0.1d);
      Assert.assertEquals(converter.convertToFarenheit(100d), 212d, 0.1d);
   }

}
