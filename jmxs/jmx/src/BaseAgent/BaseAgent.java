package BaseAgent;/*
 * @(#)files      BaseAgent.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.10
 * @(#)date      02/10/01
 *
 * Copyright 2000-2002 Sun Microsystems, Inc.  All rights reserved.
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 * 
 * Copyright 2000-2002 Sun Microsystems, Inc.  Tous droits r�serv�s.
 * Ce logiciel est propriet� de Sun Microsystems, Inc.
 * Distribu� par des licences qui en restreignent l'utilisation. 
 */


// java imports
//
import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

// RI imports
//



public class BaseAgent {

    /*
     * ------------------------------------------
     *  CONSTRUCTORS
     * ------------------------------------------
     */
    
    public BaseAgent() {
    
    }

    /*
     * ------------------------------------------
     *  PUBLIC METHODS
     * ------------------------------------------
     */
  
    public static void main(String[] args) {
        
	// CREATE the MBeanServer
	//
	System.out.println("\n\tCREATE the MBeanServer.");
	MBeanServer server = MBeanServerFactory.createMBeanServer();
        
	// CREATE and START a new HTML adaptor
	//
	System.out.println("\n\tCREATE, REGISTER and START a new HTML adaptor:");
	HtmlAdaptorServer html = new HtmlAdaptorServer();
	ObjectName html_name = null;
	try {
	    html_name = new ObjectName("Adaptor:name=html,port=8082");
	    System.out.println("\tOBJECT NAME           = " + html_name);
	    server.registerMBean(html, html_name);
	} catch(Exception e) {
	    System.out.println("\t!!! Could not create the HTML adaptor !!!");
	    e.printStackTrace();
	    return;
	}
	html.start();    
    }   
    
}
