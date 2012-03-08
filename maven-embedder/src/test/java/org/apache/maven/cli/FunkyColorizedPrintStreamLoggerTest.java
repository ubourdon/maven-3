package org.apache.maven.cli;

import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;

import static org.apache.maven.cli.FonkeyColorizedPrintStreamLogger.*;
import static org.mockito.Mockito.*;

/**
 * User: ugo
 * Date: 07/03/12
 */
public class FunkyColorizedPrintStreamLoggerTest {

	PrintStream stream;
	private FonkeyColorizedPrintStreamLogger fonkeyLogger;

	@Before
	public void init() {
		stream = spy( new PrintStream( System.out ) );
		fonkeyLogger = new FonkeyColorizedPrintStreamLogger( stream );
	}

	@Test
	public void should_print_hello() {
		fonkeyLogger.info( "hello" );

		verify( stream ).println( "hello" );
	}

	@Test
	public void should_print_hello_in_red_qui_pete() {
		fonkeyLogger.error( "hello" );

		verify( stream ).print( ASCII_FONKY + ASCII_RED + "[ERROR] " );
		verify( stream ).println( "hello" + ASCII_CLOSED );
	}

	@Test
	public void should_print_BUILD_SUCCESS_in_green_qui_pete() {
		fonkeyLogger.info( "BUILD SUCCESS" );

		verify( stream, times( 3 ) ).print( ASCII_FONKY + ASCII_GREEN + "[INFO] " );
		verify( stream ).println( "BUILD SUCCESS" + ASCII_CLOSED );
	}

	@Test
	public void should_print_Trait_in_normal_color_when_not_follow_by_build_success() {
		fonkeyLogger.info( MAVEN_TRAIT_CONSOLE );
		fonkeyLogger.info( "bla" );

		verify( stream, times( 2 ) ).print( "[INFO] " );
		verify( stream ).println( MAVEN_TRAIT_CONSOLE );
		verify( stream ).println( "bla" );
	}

	@Test
	public void should_print_Trait_in_green_if_follow_by_build_success() {
		fonkeyLogger.info( MAVEN_TRAIT_CONSOLE );
		fonkeyLogger.info( "BUILD SUCCESS" );

		verify( stream, times( 3 ) ).print( ASCII_FONKY + ASCII_GREEN + "[INFO] " );
		verify( stream, times( 2 ) ).println( MAVEN_TRAIT_CONSOLE + ASCII_CLOSED );
	}

	@Test
	public void should_print_message_in_green_if_contains_SUCCESS() {
		fonkeyLogger.info( "mon module....... SUCCESS [1.300s]" );

		verify( stream ).print( ASCII_FONKY + ASCII_GREEN + "[INFO] " );
		verify( stream ).println( "mon module....... SUCCESS [1.300s]" + ASCII_CLOSED );
	}

	@Test
	public void should_print_message_in_red_if_contains_FAILURE() {
		fonkeyLogger.info( "mon module....... FAILURE [1.300s]" );

		verify( stream ).print( ASCII_FONKY + ASCII_RED + "[INFO] " );
		verify( stream ).println( "mon module....... FAILURE [1.300s]" + ASCII_CLOSED );
	}
}
