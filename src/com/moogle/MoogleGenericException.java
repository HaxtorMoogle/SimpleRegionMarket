// $Id$
/* 
*/

package com.moogle;

public class MoogleGenericException extends Exception 
{
    
        /**
	 *   Needed for some dumb reason
	 */
        private static final long serialVersionUID = -8872837315202055280L;
	private String WhatTheFuckIsWrong = "Generic Moogle Exception Error";

	public MoogleGenericException()
	{
        super();
    }

    public MoogleGenericException(String message) 
    {
        super(message);
        WhatTheFuckIsWrong = message;
    }

    public MoogleGenericException(Throwable t) 
    {
        super(t);
    }

	public String getWhatTheFuckIsWrong() 
	{
		return WhatTheFuckIsWrong;
	}

}
