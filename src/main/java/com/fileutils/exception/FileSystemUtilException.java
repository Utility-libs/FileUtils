package com.fileutils.exception;

import org.slf4j.event.Level;

public class FileSystemUtilException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The error code. */
	String errorCode;

	/** The error message. */
	String errorMessage;

	/** The object. */
	Object[] object;

	/** The exception. */
	Throwable exception;

	/** The level. */
	Level level = Level.ERROR;

	/** The flag. */
	boolean flag = false;

	/**
	 * 
	 * Constructor "ZHFileException" is used for
	 */
	public FileSystemUtilException() {
		super();
	}

	/**
	 * 
	 * @param errorMessageConstructor
	 *            "ZHFileException" is used for
	 */
	public FileSystemUtilException(String errorMessage) {
		super(errorMessage);
		this.errorCode = errorMessage;
	}

	/**
	 * 
	 * @param tConstructor
	 *            "ZHFileException" is used for
	 */
	public FileSystemUtilException(Throwable t) {
		super(t);
	}

	/**
	 * 
	 * @param errorMessage
	 * @param tConstructor
	 *            "ZHFileException" is used for
	 */
	public FileSystemUtilException(String errorCode, Throwable t) {
		super(errorCode,t);
		this.errorCode = errorCode;
		this.exception = t;
	}
	
	 /**
     * Instantiates a new pre publisher exception.
     *
     * @param code the code
     * @param ex the ex
     * @param object the object
     * @param flag the flag
     */
    public FileSystemUtilException(String code,Throwable ex, Object[] object, boolean flag)
    {
        this.errorCode = code;
        this.exception = ex;
        this.object = object; 
        this.flag = flag;
    }
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
     * Instantiates a new pre publisher exception.
     *
     * @param code the code
     * @param object the object
     * @param level the level
     * @param ex the ex
     */
    public FileSystemUtilException(String code, Object[] object, Level level, Throwable ex)
    {
        this.errorCode = code;
        this.exception = ex;
        this.object = object; 
        this.level = level;
    }
	
	 /**
     * Returns the error code associated with the exception.
     *
     * @return the error code
     */
    public String getErrorCode() 
    {
        return this.errorCode;
    }
	
	/**
     * Gets the object.
     *
     * @return Returns the object.
     */
    public Object[] getObject() {
        return object;
    }
    
    /**
     * Gets the exception.
     *
     * @return Returns the exception.
     */
    public Throwable getException() {
        return exception;
    }
    
    /**
     * Gets the level.
     *
     * @return Returns the level.
     */
    public Level getLevel() {
        return level;
    }
    
    /**
     * Sets the level.
     *
     * @param level The level to set.
     */
    public void setLevel(Level level) {
        this.level = level;
    }

}
