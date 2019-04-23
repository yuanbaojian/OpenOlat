/**
* OLAT - Online Learning and Training<br>
* http://www.olat.org
* <p>
* Licensed under the Apache License, Version 2.0 (the "License"); <br>
* you may not use this file except in compliance with the License.<br>
* You may obtain a copy of the License at
* <p>
* http://www.apache.org/licenses/LICENSE-2.0
* <p>
* Unless required by applicable law or agreed to in writing,<br>
* software distributed under the License is distributed on an "AS IS" BASIS, <br>
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
* See the License for the specific language governing permissions and <br>
* limitations under the License.
* <p>
* Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
* University of Zurich, Switzerland.
* <hr>
* <a href="http://www.openolat.org">
* OpenOLAT - Online Learning and Training</a><br>
* This file has been modified by the OpenOLAT community. Changes are licensed
* under the Apache 2.0 license as the original file.  
* <p>
*/ 

package org.olat.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Mike Stock Comment:
 */
public class ObjectCloner {

	//so that nobody can accidentally create an ObjectCloner object
	private ObjectCloner() {
	//
	}

	/**
	 * returns a deep copy of an object (must all be Serializable)
	 * 
	 * @param oldObj
	 * @return copied object
	 * @throws RuntimeException
	 */
	static public Object deepCopy(Object oldObj) throws RuntimeException {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			// serialize and pass the object
			oos.writeObject(oldObj);
			oos.flush();
			ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
			ois = new ObjectInputStream(bin);
			// return the new object
			return ois.readObject();
		} catch (Exception e) {
			throw new RuntimeException("Exception in ObjectCloner = ", e);
		} finally {
			try {
				oos.close();
				ois.close();
			} catch (Exception e) {
				//
			}
		}
	}
		
	/**
   * Determines the size of an object in bytes when it is serialized.
   * <br>
   * This should not be used for anything other than optimization
   * testing since it can be memory and processor intensive.
   * <br>
   * @author http://churchillobjects.com/c/13029.html
   */
  public static int getObjectSize(Object object){
    if(object==null){
      return -1;
    }
    try{
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(object);
      byte[] bytes = baos.toByteArray();
      oos.close();
      baos.close();
      return bytes.length;
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return -1;
  }  
	
	
	
}