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


/**
 * An XMLParseException is thrown when the XML passed to the XML parser is not
 * well-formed.
 *
 */
public class XMLParseException
   extends XMLException
{

   /**
    * Creates a new exception.
    *
    * @param msg the message of the exception.
    */
   public XMLParseException(String msg)
   {
      super(msg);
   }


   /**
    * Creates a new exception.
    *
    * @param systemID the system ID from where the data came
    * @param lineNr   the line number in the XML data where the exception
    *                 occurred.
    * @param msg      the message of the exception.
    */
   public XMLParseException(String systemID,
                            int    lineNr,
                            String msg)
   {
      super(systemID, lineNr, null, msg, true);
   }

}
