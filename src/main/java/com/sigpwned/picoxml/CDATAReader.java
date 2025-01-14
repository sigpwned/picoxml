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


import java.io.Reader;
import java.io.IOException;


/**
 * This reader reads data from another reader until the end of a CDATA section
 * (]]&gt;) has been encountered.
 *
 */
class CDATAReader
   extends Reader
{

   /**
    * The encapsulated reader.
    */
   private IXMLReader reader;
    
    
   /**
    * Saved char.
    */
   private char savedChar;
    

   /**
    * True if the end of the stream has been reached.
    */
   private boolean atEndOfData;


   /**
    * Creates the reader.
    *
    * @param reader the encapsulated reader
    */
   CDATAReader(IXMLReader reader)
   {
      this.reader = reader;
      this.savedChar = 0;
      this.atEndOfData = false;
   }


   /**
    * Cleans up the object when it's destroyed.
    */
   protected void finalize()
      throws Throwable
   {
      this.reader = null;
      super.finalize();
   }
    

   /**
    * Reads a block of data.
    *
    * @param buffer where to put the read data
    * @param offset first position in buffer to put the data
    * @param size maximum number of chars to read
    *
    * @return the number of chars read, or -1 if at EOF
    *
    * @throws java.io.IOException
    *		if an error occurred reading the data
    */
   public int read(char[] buffer,
                   int    offset,
                   int    size)
      throws IOException
   {
      int charsRead = 0;

      if (this.atEndOfData) {
         return -1;
      }

      if ((offset + size) > buffer.length) {
         size = buffer.length - offset;
      }

      while (charsRead < size) {
         char ch = this.savedChar;

         if (ch == 0) {
            ch = this.reader.read();
         } else {
            this.savedChar = 0;
         }

         if (ch == ']') {
            char ch2 = this.reader.read();
            
            if (ch2 == ']') {
               char ch3 = this.reader.read();

               if (ch3 == '>') {
                  this.atEndOfData = true;
                  break;
               }

               this.savedChar = ch2;
               this.reader.unread(ch3);
            } else {
               this.reader.unread(ch2);
            }
         }
         buffer[charsRead] = ch;
         charsRead++;
      }

      if (charsRead == 0) {
         charsRead = -1;
      }

      return charsRead;
   }
    

   /**
    * Skips remaining data and closes the stream.
    *
    * @throws java.io.IOException
    *		if an error occurred reading the data
    */
   public void close()
      throws IOException
   {
      while (! this.atEndOfData) {
         char ch = this.savedChar;

         if (ch == 0) {
            ch = this.reader.read();
         } else {
            this.savedChar = 0;
         }

         if (ch == ']') {
            char ch2 = this.reader.read();

            if (ch2 == ']') {
               char ch3 = this.reader.read();

               if (ch3 == '>') {
                  break;
               }

               this.savedChar = ch2;
               this.reader.unread(ch3);
            } else {
               this.reader.unread(ch2);
            }
         }
      }

      this.atEndOfData = true;
   }

}
