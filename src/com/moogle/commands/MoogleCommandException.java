// $Id$
/* 
 */

package com.moogle.commands;

public class MoogleCommandException extends Exception
{
    /**
     * Needed for some dumb reason
     */
    private static final long serialVersionUID = -6657073414531719934L;
    private String WhatTheFuckIsWrong = "No Idea";

    public MoogleCommandException()
    {
        super();
    }

    public MoogleCommandException(String message)
    {
        super(message);
        WhatTheFuckIsWrong = message;
    }

    public MoogleCommandException(Throwable t)
    {
        super(t);
    }

    public String getWhatTheFuckIsWrong()
    {
        return super.getMessage();
    }

}
