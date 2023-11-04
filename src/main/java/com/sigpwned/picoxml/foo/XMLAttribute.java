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
package com.sigpwned.picoxml.foo;


/**
 * An attribute in an XML element. This is an internal class.
 *
 * @see com.sigpwned.picoxml.foo.impl.DefaultXmlElement
 *
 */
public class XMLAttribute {

  /**
   * The full name of the attribute.
   */
  private String fullName;


  /**
   * The short name of the attribute.
   */
  private String name;


  /**
   * The namespace URI of the attribute.
   */
  private String namespace;


  /**
   * The value of the attribute.
   */
  private String value;


  /**
   * The type of the attribute.
   */
  private String type;


  /**
   * Creates a new attribute.
   *
   * @param fullName the non-null full name
   * @param name the non-null short name
   * @param namespace the namespace URI, which may be null
   * @param value the value of the attribute
   * @param type the type of the attribute
   */
  public XMLAttribute(String fullName, String name, String namespace, String value, String type) {
    this.fullName = fullName;
    this.name = name;
    this.namespace = namespace;
    this.value = value;
    this.type = type;
  }


  /**
   * Returns the full name of the attribute.
   */
  public String getFullName() {
    return this.fullName;
  }


  /**
   * Returns the short name of the attribute.
   */
  public String getName() {
    return this.name;
  }


  /**
   * Returns the namespace of the attribute.
   */
  public String getNamespace() {
    return this.namespace;
  }


  /**
   * Returns the value of the attribute.
   */
  public String getValue() {
    return this.value;
  }


  /**
   * Sets the value of the attribute.
   *
   * @param value the new value.
   */
  public void setValue(String value) {
    this.value = value;
  }


  /**
   * Returns the type of the attribute.
   *
   * @param type the new type.
   */
  public String getType() {
    return this.type;
  }

}
