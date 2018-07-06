package MonitorMBean;/*
 * @(#)files      StandardObservedObjectMBean.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   4.5
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

// RI imports
//
import javax.management.*;

/**
 * @version     4.5     02/28/02
 * @author      Sun Microsystems, Inc
 */

public interface StandardObservedObjectMBean {

    /*
     * ------------------------------------------
     *  PUBLIC METHODS
     * ------------------------------------------
     */    
    
    // GETTERS AND SETTERS
    //--------------------
    
    public Integer getNbObjects();
}
