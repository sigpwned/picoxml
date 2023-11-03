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
package com.sigpwned.picoxml;


import java.util.Hashtable;
import java.io.Reader;
import java.io.StringReader;


/**
 * An IXMLEntityResolver resolves entities.
 *
 */
public interface IXMLEntityResolver
{

   /**
    * Adds an internal entity.
    *
    * @param name  the name of the entity.
    * @param value the value of the entity.
    */
   public void addInternalEntity(String name,
                                 String value);


   /**
    * Adds an external entity.
    *
    * @param name     the name of the entity.
    * @param publicID the public ID of the entity, which may be null.
    * @param systemID the system ID of the entity.
    */
   public void addExternalEntity(String name,
                                 String publicID,
                                 String systemID);


   /**
    * Returns a Java reader containing the value of an entity.
    *
    * @param xmlReader the current NanoXML reader.
    * @param name      the name of the entity.
    *
    * @return the reader, or null if the entity could not be resolved.
    *
    * @throws com.sigpwned.picoxml.XMLParseException
    *     If an exception occurred while resolving the entity.
    */
   public Reader getEntity(IXMLReader xmlReader,
                           String     name)
      throws XMLParseException;


   /**
    * Returns true if an entity is external.
    *
    * @param name the name of the entity.
    */
   public boolean isExternalEntity(String name);

}
