package org.apache.maven.cli;

import org.apache.maven.Maven;
import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

import java.io.PrintStream;

/**
 * User: ugo
 * Date: 07/03/12
 */
public class FonkeyColorizedPrintStreamLogger extends AbstractLogger {
	public static final char ASCII_ESCAPE = ( char ) 27;
	public static final String ASCII_GREEN = ASCII_ESCAPE + "[32m";
	public static final String ASCII_RED =  ASCII_ESCAPE + "[31m";
	public static final String ASCII_FONKY = ASCII_ESCAPE + "[1m";
	public static final String ASCII_CLOSED = ( char ) 27 + "[0m";
	public static final String ASCII_ORANGE = ASCII_ESCAPE + "[33m";

	public static final String MAVEN_TRAIT_CONSOLE = "------------------------------------------------------------------------";
	public static final String BUILD_SUCCESS = "BUILD SUCCESS";

	private PrintStream stream;

	private Boolean before = false;
	private Boolean after = false;
	
	static interface Provider
	{
		PrintStream getStream();
	}

	private Provider provider;

	private static final String FATAL_ERROR = "[FATAL] ";

	private static final String ERROR = "[ERROR] ";

	private static final String WARNING = "[WARNING] ";

	private static final String INFO = "[INFO] ";

	private static final String DEBUG = "[DEBUG] ";

	public FonkeyColorizedPrintStreamLogger( Provider provider )
	{
		super( Logger.LEVEL_INFO, Maven.class.getName() );

		if ( provider == null )
		{
			throw new IllegalArgumentException( "output stream provider missing" );
		}
		this.provider = provider;
	}

	public FonkeyColorizedPrintStreamLogger( PrintStream out )
	{
		super( Logger.LEVEL_INFO, Maven.class.getName() );

		setStream( out );
	}

	public void setStream( final PrintStream out )
	{
		if ( out == null )
		{
			throw new IllegalArgumentException( "output stream missing" );
		}

		this.provider = new Provider()
		{
			public PrintStream getStream()
			{
				return out;
			}
		};
	}

	public void debug( String message, Throwable throwable )
	{
		if ( isDebugEnabled() )
		{
			PrintStream out = provider.getStream();

			out.print( DEBUG );
			out.println( message );

			if ( null != throwable )
			{
				throwable.printStackTrace( out );
			}
		}
	}

	public void info( String message, Throwable throwable )
	{
		if ( isInfoEnabled() )
		{
			PrintStream out = provider.getStream();

			if( message.equals( BUILD_SUCCESS ) ) {
				printMessageWithFonkyColor( INFO, MAVEN_TRAIT_CONSOLE, ASCII_GREEN );

				printMessageWithFonkyColor( INFO, message, ASCII_GREEN );

				printMessageWithFonkyColor( INFO, MAVEN_TRAIT_CONSOLE, ASCII_GREEN );

				before = false;
				after = true;
			} else if( message.equals( MAVEN_TRAIT_CONSOLE ) ) {
				before = true;
			} else {
			    if( before ) {
					if( after ) {
						after = false;
					} else {
						out.print( INFO );
						out.println( MAVEN_TRAIT_CONSOLE );
						before = false;
					}
				}

				displayFonkeyColorizedMessageAccordingToSuccessOrFailure( message, out );
			}

			if ( null != throwable )
			{
				throwable.printStackTrace( out );
			}
		}
	}

	public void warn( String message, Throwable throwable )
	{
		if ( isWarnEnabled() )
		{
			PrintStream out = provider.getStream();

			printMessageWithFonkyColor( WARNING, message, ASCII_ORANGE );

			if ( null != throwable )
			{
				throwable.printStackTrace( out );
			}
		}
	}

	public void error( String message, Throwable throwable )
	{
		if ( isErrorEnabled() )
		{
			PrintStream out = provider.getStream();

			printMessageWithFonkyColor( ERROR, message, ASCII_RED );

			if ( null != throwable )
			{
				throwable.printStackTrace( out );
			}
		}
	}

	public void fatalError( String message, Throwable throwable )
	{
		if ( isFatalErrorEnabled() )
		{
			PrintStream out = provider.getStream();

			out.print( FATAL_ERROR );
			out.println( message );

			if ( null != throwable )
			{
				throwable.printStackTrace( out );
			}
		}
	}

	public void close()
	{
		PrintStream out = provider.getStream();

		if ( out == System.out || out == System.err )
		{
			out.flush();
		}
		else
		{
			out.close();
		}
	}

	public Logger getChildLogger( String arg0 )
	{
		return this;
	}

	private void printMessageWithFonkyColor( String prefixMessage, String message, String color ) {
		PrintStream out = provider.getStream();
		
		out.print( ASCII_FONKY + color + prefixMessage );
		out.println( message + ASCII_CLOSED );
	}

	private void displayFonkeyColorizedMessageAccordingToSuccessOrFailure( String message, PrintStream out ) {
		if( message.contains( "SUCCESS" ) ) {
			printMessageWithFonkyColor( INFO, message, ASCII_GREEN );
		} else if( message.contains( "FAILURE" ) ) {
			printMessageWithFonkyColor( INFO, message, ASCII_RED );
		} else {
			out.print( INFO );
			out.println( message );
		}
	}
}