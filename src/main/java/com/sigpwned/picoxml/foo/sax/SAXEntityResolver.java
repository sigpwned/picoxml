/*-
 * =================================LICENSE_START==================================
 * picoxml
 * ====================================SECTION=====================================
 * Copyright (C) 2023 Andy Boothe
 * ====================================SECTION=====================================
 * This file is part of PicoXML 2 for Java.
 * 
 * Copyright (C) 2000-2002 Marc De Scheemaecker, All Rights Reserved.
 * Copyright (C) 2020-2020 Saúl Hidalgo, All Rights Reserved.
 * Copyright (C) 2023-2023 Andy Boothe, All Rights Reserved.
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.picoxml.foo.sax;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import com.sigpwned.picoxml.foo.IXMLReader;
import com.sigpwned.picoxml.foo.XMLEntityResolver;


/**
 * SAXEntityResolver is a subclass of XMLEntityResolver that supports the
 * SAX EntityResolver listener.
 *
 * @see com.sigpwned.picoxml.foo.IXMLEntityResolver
 *
 */
public class SAXEntityResolver
   extends XMLEntityResolver
{

   /**
    * The SAX EntityResolver listener.
    */
   private EntityResolver saxEntityResolver;


   /**
    * Creates the resolver.
    */
   public SAXEntityResolver()
   {
      this.saxEntityResolver = null;
   }


   /**
    * Cleans up the object when it's destroyed.
    */
   protected void finalize()
      throws Throwable
   {
      this.saxEntityResolver = null;
      super.finalize();
   }


   /**
    * Sets the SAX EntityResolver listener.
    *
    * @param resolver the entity resolver
    */
   public void setEntityResolver(EntityResolver resolver)
   {
      this.saxEntityResolver = resolver;
   }


   /**
    * Opens an external entity.
    *
    * @param xmlReader the current XML reader
    * @param publicID the public ID, which may be null
    * @param systemID the system ID
    *
    * @return the reader, or null if the reader could not be created/opened
    */
   protected Reader openExternalEntity(IXMLReader xmlReader,
                                       String     publicID,
                                       String     systemID)
   {
      try {
         URL url = new URL(xmlReader.getSystemID());
         url = new URL(url, systemID);

         if (this.saxEntityResolver != null) {
            InputSource source
            = this.saxEntityResolver
            .resolveEntity(publicID, url.toString());

            if (source != null) {
               Reader reader = source.getCharacterStream();

               if (reader != null) {
                  return reader;
               }

               InputStream stream = source.getByteStream();

               if (stream == null) {
                  publicID = source.getPublicId();
                  systemID = source.getSystemId();
               } else {
                  String encoding = source.getEncoding();

                  if (encoding != null) {
                     return new InputStreamReader(stream, encoding);
                  } else { // if encoding == null
                     return new InputStreamReader(stream);
                  }
               }
            }
         }

         return super.openExternalEntity(xmlReader, publicID, systemID);
      } catch (Exception e) {
         return null;
      }
   }

}
