/**
 * Escaper.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util.xml;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class XMLUtil
{
	
//	public static String escape( String s ) {
//		StringBuffer sb = new StringBuffer();
//		for ( int i = 0; i < s.length(); i++ ) {
//			char c=s.charAt(i);
//			if(c>0x1F)
//			{
//				switch (c ) {
//				case '&': sb.append("&amp;");
//					break;
//				case '<': sb.append("&lt;");
//					break;
//				case '>': sb.append("&gt;");
//					break;
//				case 0xffff: sb.append("*BADCHAR*");
//					break;
//				case 0x08:
//					break;
//				case 0x19:
//					sb.append("'");
//					break;
//				default: sb.append( s.charAt(i) );
//				}
//			}
//		}
//		return sb.toString();
//	}
//
//	public static String convert(byte[] bytes)
//	{
//		Charset charset=Charset.forName("cp1252");
//		CharsetDecoder decoder=charset.newDecoder();
//
//		try
//		{
//			ByteBuffer bbuf=ByteBuffer.wrap(bytes);
//			CharBuffer cbuf=decoder.decode(bbuf);
//			String s=cbuf.toString();
//			return s;
//		}
//		catch (java.nio.charset.CharacterCodingException e) { e.printStackTrace(); }
//
//		return null;
//	}
//
//	public static String convert(String intext)
//	{
//		Charset charset=Charset.forName("cp1252");
//		CharsetDecoder decoder=charset.newDecoder();
//		CharsetEncoder encoder=charset.newEncoder();
//		try
//		{
//			ByteBuffer bbuf=encoder.encode(CharBuffer.wrap(intext));
//			CharBuffer cbuf=decoder.decode(bbuf);
//			String s=cbuf.toString();
//			return s;
//		}
//		catch (java.nio.charset.CharacterCodingException e) {
//					System.out.println("Errror "+e+" converting '"+intext+"'");
//					}
//
//		return null;
//	}
//
}

